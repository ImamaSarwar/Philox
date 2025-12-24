package service;

import java.util.List;
import java.util.Map;
import db.repository.OpportunityRepository;
import entity.Opportunity;

public class OpportunityService {

    public static boolean postOpportunity(Opportunity opportunity) {
        return OpportunityRepository.save(opportunity);
    }
    public static Map<Integer, String> getOpportunityCategories(){
        return OpportunityRepository.getOpportunityCategories();
    }

    public static List<Opportunity> getAllOpportunities() {
        return OpportunityRepository.getAllOpportunities();
    }

    public static List<Opportunity> getAllOpenOpportunities() {
        return OpportunityRepository.getAllOpenOpportunities();
    }

    public static List<Opportunity> getAllOpportunitiesByOrganisation(int organisationId) {
        return OpportunityRepository.getAllOpportunitiesByOrganisation(organisationId);
    }

    public static boolean closeOpportunity(int opportunityId) {
        return OpportunityRepository.closeOpportunity(opportunityId);
    }

    public static boolean cancelOpportunity(int opportunityId) {
        return OpportunityRepository.cancelOpportunity(opportunityId);
    }

    public static int closeExpiredOpportunities() {
        return OpportunityRepository.closeExpiredOpportunities();
    }
}
