package service;

import db.repository.ApplicationRepository;
import entity.Application;
import entity.Opportunity;

import java.util.List;

public class ApplicationService {

    public static boolean applyForOpportunity(Application application) {
        application.setStatus(0); // Set status to 0 (pending)
        return ApplicationRepository.save(application);
    }

    public static List<Application> getApplicationsForOrganisation(int organisationId) {
        return ApplicationRepository.getApplicationsForOrganisation(organisationId);
    }

    public static List<Application> getApplicationsForOpportunity(int opportunityId) {
        return ApplicationRepository.getApplicationsForOpportunity(opportunityId);
    }

    public static List<Application> getApplicationsForVolunteer(int volunteerId) {
        return ApplicationRepository.getApplicationsForVolunteer(volunteerId);
    }

    public static boolean updateApplicationStatus(Application application) {
        return ApplicationRepository.updateApplicationStatus(application.getApplicationId(), application.getStatus());
    }

    public static List<Opportunity> findCompletedByVolunteer(int volunteerId){
        return ApplicationRepository.findCompletedByVolunteer(volunteerId);
    }

    public static int getApplicationCountByOpportunity(int opportunityId) {
        return ApplicationRepository.getApplicationCountByOpportunity(opportunityId);
    }

    public static boolean hasVolunteerApplied(int volunteerId, int opportunityId) {
        return ApplicationRepository.hasVolunteerApplied(volunteerId, opportunityId);
    }

    public static int finalizeExpiredApplications() {
        return ApplicationRepository.finalizeExpiredApplications();
    }
}
