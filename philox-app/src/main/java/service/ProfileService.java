package service;

import db.repository.BadgeRepository;
import db.repository.OrganisationRepository;
import db.repository.UserRepository;
import db.repository.VolunteerRepository;
import entity.Badge;
import entity.Organisation;
import entity.Volunteer;

import java.util.List;
import java.util.Map;

public class ProfileService {

    public static boolean updateVolunteerInfo(Volunteer volunteer) {
        return VolunteerRepository.updateVolunteerInfo(volunteer.getVolunteerId(),volunteer.getName(),volunteer.getPhone(), volunteer.getCnic(),
                volunteer.getAge(), volunteer.getCity());
    }

    public static boolean updateVolunteerPortfolio(Volunteer volunteer) {
        return VolunteerRepository.updateVolunteerPortfolio(volunteer.getVolunteerId(), volunteer.getBio(), volunteer.getSkills());
    }

    public static Map<String,Integer> getVolunteerStats(int volunteerId) {
        return VolunteerRepository.getVolunteerStats(volunteerId);
    }

    public static boolean updateOrganisationProfile(Organisation organisation){
        return OrganisationRepository.updateOrganisationProfile(organisation.getOrganisationId(), organisation.getMission(), organisation.getContactNumber(),
                organisation.getAddress(), organisation.getWebsite(), organisation.getRepName(), organisation.getRepContactNumber(), organisation.getRepCnic(), organisation.getRepEmail());
    }

    public static Map<String,Integer> getOrganisationStats(int organisationId) {
        return OrganisationRepository.getOrganisationStats(organisationId);
    }

    public static List<Badge> getVolunteerBadges(int volunteerId) {
        return BadgeRepository.getVolunteerBadges(volunteerId);
    }

}
