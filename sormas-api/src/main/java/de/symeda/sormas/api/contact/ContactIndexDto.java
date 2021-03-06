/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;

public class ContactIndexDto implements Serializable {

	private static final long serialVersionUID = 7511900591141885152L;

	public static final String I18N_PREFIX = "Contact";
	
	public static final String UUID = "uuid";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String PERSON = "person";
	public static final String CAZE = "caze";
	public static final String DISEASE = "disease";
	public static final String CASE_PERSON = "casePerson";
	public static final String CASE_REGION_UUID = "caseRegionUuid";
	public static final String CASE_DISTRICT_UUID = "caseDistrictUuid";
	public static final String CASE_HEALTH_FACILITY_UUID = "caseHealthFacilityUuid";
	public static final String LAST_CONTACT_DATE = "lastContactDate";
	public static final String CONTACT_PROXIMITY = "contactProximity";
	public static final String CONTACT_CLASSIFICATION = "contactClassification";
	public static final String CONTACT_STATUS = "contactStatus";
	public static final String FOLLOW_UP_STATUS = "followUpStatus";
	public static final String FOLLOW_UP_UNTIL = "followUpUntil";
	public static final String CONTACT_OFFICER_UUID = "contactOfficerUuid";
	public static final String QUARANTINE_TO = "quarantineTo";
	public static final String CONTACT_CATEGORY = "contactCategory";

	private String uuid;
	private PersonReferenceDto person;
	private CaseReferenceDto caze;
	private Disease disease;
	private String diseaseDetails;
	private String caseRegionUuid;
	private String caseDistrictUuid;
	private String caseHealthFacilityUuid;
	private Date lastContactDate;
	private ContactProximity contactProximity;
	private ContactClassification contactClassification;
	private ContactStatus contactStatus;
	private FollowUpStatus followUpStatus;
	private Date followUpUntil;
	private String contactOfficerUuid;
	private Date reportDateTime;
	private Date quarantineTo;
	private ContactCategory contactCategory;
	
	public ContactIndexDto(String uuid, String personUuid, String personFirstName, String personLastName, String cazeUuid,
			Disease disease, String diseaseDetails, String casePersonUuid, String caseFirstName, String caseLastName, String caseRegionUuid,
			String caseDistrictUuid, String caseHealthFacilityUuid, Date lastContactDate,
			ContactCategory contactCategory, ContactProximity contactProximity,
			ContactClassification contactClassification, ContactStatus contactStatus, FollowUpStatus followUpStatus, 
			Date followUpUntil, String contactOfficerUuid, Date reportDateTime, Date quarantineTo) {
		this.uuid = uuid;
		this.person = new PersonReferenceDto(personUuid, personFirstName, personLastName);
		this.caze = new CaseReferenceDto(cazeUuid, caseFirstName, caseLastName);
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.caseRegionUuid = caseRegionUuid;
		this.caseDistrictUuid = caseDistrictUuid;
		this.caseHealthFacilityUuid = caseHealthFacilityUuid;
		this.lastContactDate = lastContactDate;
		this.contactCategory = contactCategory;
		this.contactProximity = contactProximity;
		this.contactClassification = contactClassification;
		this.contactStatus = contactStatus;
		this.followUpStatus = followUpStatus;
		this.followUpUntil = followUpUntil;
		this.contactOfficerUuid = contactOfficerUuid;
		this.reportDateTime = reportDateTime;
		this.quarantineTo = quarantineTo;
	}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public PersonReferenceDto getPerson() {
		return person;
	}
	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}
	public CaseReferenceDto getCaze() {
		return caze;
	}
	public void setCaze(CaseReferenceDto caze) {
		this.caze = caze;
	}
	public Disease getDisease() {
		return disease;
	}
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	public String getDiseaseDetails() {
		return diseaseDetails;
	}
	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}
	public Date getLastContactDate() {
		return lastContactDate;
	}
	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}
	public ContactProximity getContactProximity() {
		return contactProximity;
	}
	public void setContactProximity(ContactProximity contactProximity) {
		this.contactProximity = contactProximity;
	}
	public ContactClassification getContactClassification() {
		return contactClassification;
	}
	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}
	public ContactStatus getContactStatus() {
		return contactStatus;
	}
	public void setContactStatus(ContactStatus contactStatus) {
		this.contactStatus = contactStatus;
	}
	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}
	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}
	public Date getFollowUpUntil() {
		return followUpUntil;
	}
	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
	}
	public String getCaseRegionUuid() {
		return caseRegionUuid;
	}
	public void setCaseRegionUuid(String caseRegionUuid) {
		this.caseRegionUuid = caseRegionUuid;
	}
	public String getCaseDistrictUuid() {
		return caseDistrictUuid;
	}
	public void setCaseDistrictUuid(String caseDistrictUuid) {
		this.caseDistrictUuid = caseDistrictUuid;
	}
	public String getCaseHealthFacilityUuid() {
		return caseHealthFacilityUuid;
	}
	public void setCaseHealthFacilityUuid(String caseHealthFacilityUuid) {
		this.caseHealthFacilityUuid = caseHealthFacilityUuid;
	}
	public String getContactOfficerUuid() {
		return contactOfficerUuid;
	}
	public void setContactOfficerUuid(String contactOfficerUuid) {
		this.contactOfficerUuid = contactOfficerUuid;
	}
	public Date getReportDateTime() {
		return reportDateTime;
	}
	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}
	public Date getQuarantineTo() {
		return quarantineTo;
	}
	public void setQuarantineTo(Date quarantineTo) {
		this.quarantineTo = quarantineTo;
	}

	public ContactCategory getContactCategory() {
		return contactCategory;
	}
	public void setContactCategory(ContactCategory contactCategory) {
		this.contactCategory = contactCategory;
	}

	public ContactReferenceDto toReference() {
		return new ContactReferenceDto(uuid);
	}
	
}
