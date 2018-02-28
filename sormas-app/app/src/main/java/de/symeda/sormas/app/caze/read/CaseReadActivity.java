package de.symeda.sormas.app.caze.read;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.caze.edit.CaseEditActivity;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.util.NavigationHelper;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.app.backend.caze.Case;

/**
 * Created by Orson on 06/01/2018.
 */

public class CaseReadActivity  extends BaseReadActivity {

    private static final int MENU_INDEX_CASE_INFO = 0;
    private static final int MENU_INDEX_PATIENT_INFO = 1;
    private static final int MENU_INDEX_HOSPITALIZATION = 2;
    private static final int MENU_INDEX_SYMPTOMS = 3;
    private static final int MENU_INDEX_EPIDEMIOLOGICAL_DATA = 4;
    private static final int MENU_INDEX_CONTACTS = 5;
    private static final int MENU_INDEX_SAMPLES = 6;
    private static final int MENU_INDEX_TASKS = 7;

    private final String DATA_XML_PAGE_MENU = "xml/data_read_page_case_menu.xml";

    //private InvestigationStatus filterStatus = null;
    private CaseClassification pageStatus = null;
    private String recordUuid = null;
    private BaseReadActivityFragment activeFragment = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //SaveFilterStatusState(outState, filterStatus);
        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializeActivity(Bundle arguments) {
        //filterStatus = (InvestigationStatus) getFilterStatusArg(arguments);
        pageStatus = (CaseClassification) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    public BaseReadActivityFragment getActiveReadFragment() throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(
                    CaseReadActivity.this, recordUuid).setReadPageStatus(pageStatus);
            activeFragment = CaseReadFragment.newInstance(dataCapsule);
        }

        return activeFragment;
    }

    @Override
    public boolean showStatusFrame() {
        return true;
    }

    @Override
    public boolean showTitleBar() {
        return true;
    }

    @Override
    public boolean showPageMenu() {
        return true;
    }

    @Override
    public Enum getPageStatus() {
        if (pageStatus == null) {
            pageStatus = (CaseClassification) getPageStatusArg(getIntent().getExtras());;
        }

        return pageStatus;
    }

    @Override
    public String getPageMenuData() {
        return DATA_XML_PAGE_MENU;
    }

    @Override
    public boolean onLandingPageMenuClick(AdapterView<?> parent, View view, LandingPageMenuItem menuItem, int position, long id) throws IllegalAccessException, InstantiationException {
        setActiveMenu(menuItem);

        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(
                CaseReadActivity.this, recordUuid).setReadPageStatus(pageStatus);

        if (menuItem.getKey() == MENU_INDEX_CASE_INFO) {
            activeFragment = CaseReadFragment.newInstance(dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_PATIENT_INFO) {
            activeFragment = CaseReadPatientInfoFragment.newInstance(dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_HOSPITALIZATION) {
            activeFragment = CaseReadHospitalizationFragment.newInstance(dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_SYMPTOMS) {
            activeFragment = CaseReadSymptomsFragment.newInstance(dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_EPIDEMIOLOGICAL_DATA) {
            activeFragment = CaseReadEpidemiologicalDataFragment.newInstance(dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_CONTACTS) {
            activeFragment = CaseReadContactsFragment.newInstance(dataCapsule);
            replaceFragment(activeFragment);
        }else if (menuItem.getKey() == MENU_INDEX_SAMPLES) {
            activeFragment = CaseReadSamplesFragment.newInstance(dataCapsule);
            replaceFragment(activeFragment);
        } else if (menuItem.getKey() == MENU_INDEX_TASKS) {
            activeFragment = CaseReadTasksFragment.newInstance(dataCapsule);
            replaceFragment(activeFragment);
        }

        updateSubHeadingTitle();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_action_menu, menu);

        MenuItem readMenu = menu.findItem(R.id.action_edit);
        //readMenu.setVisible(false);
        readMenu.setTitle(R.string.action_edit_case);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavigationHelper.navigateUpFrom(this);
                return true;

            case R.id.action_edit:
                gotoEditView();
                return true;

            case R.id.option_menu_action_sync:
                //synchronizeChangedData();
                return true;

            case R.id.option_menu_action_markAllAsRead:
                /*CaseDao caseDao = DatabaseHelper.getCaseDao();
                PersonDao personDao = DatabaseHelper.getPersonDao();
                List<Case> cases = caseDao.queryForAll();
                for (Case caseToMark : cases) {
                    caseDao.markAsRead(caseToMark);
                }

                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof CasesListFragment) {
                        fragment.onResume();
                    }
                }*/
                return true;

            // Report problem button
            case R.id.action_report:
                /*UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                AlertDialog dialog = userReportDialog.create();
                dialog.show();*/

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level3_case_read;
    }

    private void gotoEditView() {
        if (activeFragment == null)
            return;

        Case record = (Case)activeFragment.getRecord();

        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(CaseReadActivity.this,
                record.getUuid()).setEditPageStatus(record.getInvestigationStatus());
        CaseEditActivity.goToActivity(this, dataCapsule);
    }

    public static void goToActivity(Context fromActivity, CaseFormNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, CaseReadActivity.class, dataCapsule);
    }
}