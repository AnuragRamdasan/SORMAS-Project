package de.symeda.sormas.backend.disease;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.disease.DiseaseConfigurationFacade;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "DiseaseConfigurationFacade")
public class DiseaseConfigurationFacadeEjb implements DiseaseConfigurationFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	@EJB
	private DiseaseConfigurationService service;
	@EJB
	private UserService userService;

	private List<Disease> activeDiseases = new ArrayList<>();
	private List<Disease> inactiveDiseases = new ArrayList<>();
	private List<Disease> primaryDiseases = new ArrayList<>();
	private List<Disease> nonPrimaryDiseases = new ArrayList<>();
	private List<Disease> caseBasedDiseases = new ArrayList<>();
	private List<Disease> aggregateDiseases = new ArrayList<>();
	private List<Disease> followUpEnabledDiseases = new ArrayList<>();
	private Map<Disease, Integer> followUpDurations = new HashMap<>();

	@Override
	public List<DiseaseConfigurationDto> getAllAfter(Date date) {
		return service.getAllAfter(date, null)
				.stream()
				.map(d -> toDto(d))
				.collect(Collectors.toList());
	}

	@Override
	public List<DiseaseConfigurationDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids)
				.stream()
				.map(d -> toDto(d))
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {
		return service.getAllUuids(null);
	}

	@Override
	public boolean isActiveDisease(Disease disease) {
		return activeDiseases.contains(disease);
	}

	@Override
	public List<Disease> getAllDiseases(Boolean active, Boolean primary, Boolean caseBased) {
		User currentUser = userService.getCurrentUser();

		Set<Disease> diseases = new HashSet<>();

		if (Boolean.TRUE.equals(active)) {
			if (currentUser.getLimitedDisease() != null && activeDiseases.contains(currentUser.getLimitedDisease())) {
				diseases.add(currentUser.getLimitedDisease());
			} else {
				diseases.addAll(activeDiseases);
			}
		} else if (Boolean.FALSE.equals(active)) {
			if (currentUser.getLimitedDisease() != null && inactiveDiseases.contains(currentUser.getLimitedDisease())) {
				diseases.add(currentUser.getLimitedDisease());
			} else {
				diseases.addAll(inactiveDiseases);
			}
		}

		if (Boolean.TRUE.equals(primary)) {
			if (currentUser.getLimitedDisease() != null && primaryDiseases.contains(currentUser.getLimitedDisease())) {
				diseases.add(currentUser.getLimitedDisease());
			} else {
				diseases.addAll(primaryDiseases);
			}
		} else if (Boolean.FALSE.equals(primary)) {
			if (currentUser.getLimitedDisease() != null && nonPrimaryDiseases.contains(currentUser.getLimitedDisease())) {
				diseases.add(currentUser.getLimitedDisease());
			} else {
				diseases.addAll(nonPrimaryDiseases);
			}
		}

		if (Boolean.TRUE.equals(caseBased)) {
			if (currentUser.getLimitedDisease() != null && caseBasedDiseases.contains(currentUser.getLimitedDisease())) {
				diseases.add(currentUser.getLimitedDisease());
			} else {
				diseases.addAll(caseBasedDiseases);
			}
		} else if (Boolean.FALSE.equals(caseBased)) {
			if (currentUser.getLimitedDisease() != null && aggregateDiseases.contains(currentUser.getLimitedDisease())) {
				diseases.add(currentUser.getLimitedDisease());
			} else {
				diseases.addAll(aggregateDiseases);
			}
		}

		diseases.removeIf(d -> Boolean.TRUE.equals(active) && inactiveDiseases.contains(d)
				|| Boolean.FALSE.equals(active) && activeDiseases.contains(d)
				|| Boolean.TRUE.equals(primary) && nonPrimaryDiseases.contains(d)
				|| Boolean.FALSE.equals(primary) && primaryDiseases.contains(d)
				|| Boolean.TRUE.equals(caseBased) && aggregateDiseases.contains(d)
				|| Boolean.FALSE.equals(caseBased) && caseBasedDiseases.contains(d));

		List<Disease> diseaseList = new ArrayList<>(diseases);
		diseaseList.sort((d1, d2) -> d1.toString().compareTo(d2.toString()));
		return diseaseList;
	}

	@Override
	public List<Disease> getAllActiveDiseases() {
		User currentUser = userService.getCurrentUser();
		if (currentUser.getLimitedDisease() != null) {
			return activeDiseases.stream().filter(d -> d == currentUser.getLimitedDisease()).collect(Collectors.toList());
		} else {
			return activeDiseases;
		}
	}

	@Override
	public boolean isPrimaryDisease(Disease disease) {
		return primaryDiseases.contains(disease);
	}

	@Override
	public List<Disease> getAllPrimaryDiseases() {
		User currentUser = userService.getCurrentUser();
		if (currentUser.getLimitedDisease() != null) {
			return primaryDiseases.stream().filter(d -> d == currentUser.getLimitedDisease()).collect(Collectors.toList());
		} else {
			return primaryDiseases;
		}
	}

	@Override
	public boolean hasFollowUp(Disease disease) {
		return followUpEnabledDiseases.contains(disease);
	}

	@Override
	public List<Disease> getAllDiseasesWithFollowUp() {
		User currentUser = userService.getCurrentUser();
		if (currentUser.getLimitedDisease() != null) {
			return followUpEnabledDiseases.stream().filter(d -> d == currentUser.getLimitedDisease()).collect(Collectors.toList());
		} else {
			return followUpEnabledDiseases;
		}
	}

	@Override
	public int getFollowUpDuration(Disease disease) {
		return followUpDurations.get(disease);
	}

	public static DiseaseConfigurationDto toDto(DiseaseConfiguration source) {
		if (source == null) {
			return null;
		}

		DiseaseConfigurationDto target = new DiseaseConfigurationDto();
		DtoHelper.fillDto(target, source);

		target.setDisease(source.getDisease());
		target.setActive(source.getActive());
		target.setPrimaryDisease(source.getPrimaryDisease());
		target.setCaseBased(source.getCaseBased());
		target.setFollowUpEnabled(source.getFollowUpEnabled());
		target.setFollowUpDuration(source.getFollowUpDuration());

		return target;
	}

	public DiseaseConfiguration fromDto(@NotNull DiseaseConfigurationDto source) {
		DiseaseConfiguration target = service.getByUuid(source.getUuid());
		if (target == null) {
			target = new DiseaseConfiguration();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setDisease(source.getDisease());
		target.setActive(source.getActive());
		target.setPrimaryDisease(source.getPrimaryDisease());
		target.setCaseBased(source.getCaseBased());
		target.setFollowUpEnabled(source.getFollowUpEnabled());
		target.setFollowUpDuration(source.getFollowUpDuration());

		return target;
	}

	private DiseaseConfigurationDto getDiseaseConfiguration(Disease disease) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DiseaseConfiguration> cq = cb.createQuery(DiseaseConfiguration.class);
		Root<DiseaseConfiguration> root = cq.from(DiseaseConfiguration.class);

		Predicate filter = cb.equal(root.get(DiseaseConfiguration.DISEASE), disease);
		if (filter == null) {
			return null;
		} else {
			cq.where(filter);
		}

		cq.select(root);

		try {
			DiseaseConfigurationDto diseaseConfiguration = toDto(em.createQuery(cq).getSingleResult());
			return diseaseConfiguration;
		} catch (NoResultException e) {
			return null;
		}
	}

	@PostConstruct
	private void loadData() {
		activeDiseases.clear();
		inactiveDiseases.clear();
		primaryDiseases.clear();
		nonPrimaryDiseases.clear();
		caseBasedDiseases.clear();
		aggregateDiseases.clear();
		followUpEnabledDiseases.clear();
		followUpDurations.clear();

		for (Disease disease : Disease.values()) {
			DiseaseConfigurationDto configuration = getDiseaseConfiguration(disease);

			if (Boolean.TRUE.equals(configuration.getActive()) 
					|| (configuration.getActive() == null && disease.isDefaultActive())) {
				activeDiseases.add(disease);
			} else {
				inactiveDiseases.add(disease);
			}
			if (Boolean.TRUE.equals(configuration.getPrimaryDisease())
					|| (configuration.getPrimaryDisease() == null && disease.isDefaultPrimary())) {
				primaryDiseases.add(disease);
			} else {
				nonPrimaryDiseases.add(disease);
			}
			if (Boolean.TRUE.equals(configuration.getCaseBased())
					|| (configuration.getCaseBased() == null && disease.isDefaultCaseBased())) {
				caseBasedDiseases.add(disease);
			} else {
				aggregateDiseases.add(disease);
			}
			if (Boolean.TRUE.equals(configuration.getFollowUpEnabled()) 
					|| (configuration.getFollowUpEnabled() == null && disease.isDefaultFollowUpEnabled())) {
				followUpEnabledDiseases.add(disease);
			}
			if (configuration.getFollowUpDuration() != null) {
				followUpDurations.put(disease, configuration.getFollowUpDuration());
			} else {
				followUpDurations.put(disease, disease.getDefaultFollowUpDuration());
			}
		}
	}

	@LocalBean
	@Stateless
	public static class DiseaseConfigurationFacadeEjbLocal extends DiseaseConfigurationFacadeEjb {

	}

}
