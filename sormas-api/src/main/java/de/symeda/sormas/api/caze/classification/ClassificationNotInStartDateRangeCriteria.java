package de.symeda.sormas.api.caze.classification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.utils.DateHelper;

public class ClassificationNotInStartDateRangeCriteria extends ClassificationCaseCriteria {

	private static final long serialVersionUID = -8817472226784147694L;
	
	private final int daysBeforeStartDate;
	
	public ClassificationNotInStartDateRangeCriteria(String propertyId, int daysBeforeStartDate) {
		super(propertyId);
		this.daysBeforeStartDate = daysBeforeStartDate;
	}
	
	@Override
	public boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests) {
		try {
			Method method = getInvokeClass().getMethod("get" + propertyId.substring(0, 1).toUpperCase() + propertyId.substring(1));
			Object value = method.invoke(getInvokeObject(caze));
			if (value instanceof Date) {
				Date startDate = CaseLogic.getStartDate(caze.getSymptoms().getOnsetDate(), caze.getReceptionDate(), caze.getReportDate());
				Date lowerThresholdDate = DateHelper.subtractDays(startDate, daysBeforeStartDate);
				
				return !(((Date) value).equals(lowerThresholdDate) 
						|| ((Date) value).equals(startDate)
						|| (((Date) value).after(lowerThresholdDate) 
								&& ((Date) value).before(startDate)));
			} else {
				return true;
			}
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String buildDescription() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, propertyId));
		stringBuilder.append(" ").append(I18nProperties.getText("notWithin")).append(" ").append(daysBeforeStartDate).append(" ").append(I18nProperties.getText("daysBeforeCaseStart"));
		return stringBuilder.toString();
	}
	
}