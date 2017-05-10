package de.symeda.sormas.app.event;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoGetInterface;
import de.symeda.sormas.app.backend.common.AdoDtoHelper.DtoPostInterface;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.EventDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import retrofit2.Call;

/**
 * @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 */
public class SyncEventsTask extends AsyncTask<Void, Void, Void> {

    private final Context context;

    public SyncEventsTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            new EventDtoHelper().pullEntities(new DtoGetInterface<EventDto>() {
                @Override
                public Call<List<EventDto>> getAll(long since) {

                    User user = ConfigProvider.getUser();
                    if (user != null) {
                        Call<List<EventDto>> all = RetroProvider.getEventFacade().getAll(user.getUuid(), since);
                        return all;
                    }
                    return null;
                }
            }, DatabaseHelper.getEventDao());

            new EventDtoHelper().pushEntities(new DtoPostInterface<EventDto>() {
                @Override
                public Call<Long> postAll(List<EventDto> dtos) {
                    return RetroProvider.getEventFacade().postAll(dtos);
                }
            }, DatabaseHelper.getEventDao());
        } catch (DaoException | SQLException | IOException e) {
            Log.e(getClass().getName(), "Error while synchronizing alerts", e);
            SormasApplication application = (SormasApplication) context.getApplicationContext();
            Tracker tracker = application.getDefaultTracker();
            ErrorReportingHelper.sendCaughtException(tracker, this.getClass().getSimpleName(), e, null, true);
        }
        return null;
    }

    public static void syncEvents(Context context, final FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            syncEvents(context, new Callback() {
                @Override
                public void call() {
                    if (fragmentManager.getFragments() != null) {
                        for (Fragment fragement : fragmentManager.getFragments()) {
                            if (fragement instanceof EventsListFragment) {
                                fragement.onResume();
                            }
                        }
                    }
                }
            });
        } else {
            syncEvents(context, (Callback)null);
        }
    }

    public static void syncEvents(final FragmentManager fragmentManager, Context context, SwipeRefreshLayout refreshLayout) {
        syncEvents(context, fragmentManager);
        refreshLayout.setRefreshing(false);
    }

    public static void syncEventsWithProgressDialog(Context context, final Callback callback) {

        final ProgressDialog progressDialog = ProgressDialog.show(context, "Event synchronization",
                "Events are being synchronized...", true);

        syncEvents(context, new Callback() {
            @Override
            public void call() {
                progressDialog.dismiss();
                callback.call();
            }
        });
    }

    public static void syncEvents(final Context context, final Callback callback) {
        new SyncEventsTask(context) {
            @Override
            protected void onPostExecute(Void aVoid) {
                SyncEventParticipantsTask.syncEventParticipants(context, new Callback() {
                    @Override
                    public void call() {
                        if (callback != null) {
                            callback.call();
                        }
                    }
                });
            }
        }.execute();
    }
}