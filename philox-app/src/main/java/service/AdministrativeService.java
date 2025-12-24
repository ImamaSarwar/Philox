package service;

import db.repository.AdminRepository;
import db.repository.BadgeRepository;
import db.repository.OpportunityRepository;
import db.repository.OrganisationRepository;
import entity.Admin;
import entity.Badge;

import java.util.List;

public class AdministrativeService {

    public static boolean registerAdmin(Admin admin) {
        return AdminRepository.save(admin);
    }

    // stats for dashboard
    public static int getPendingOrganisationApprovalsCount() {
        return AdminRepository.getPendingOrganisationApprovalsCount();
    }
    public static int getTotalBadgesCount(){
        return AdminRepository.getTotalBadgesCount();
    }
    public static int getTotalActiveUsersCount(){
        return AdminRepository.getTotalActiveUsersCount();
    }

    public static boolean flagOpportunityPost(int opportunityId) {
        return OpportunityRepository.flagOpportunity(opportunityId);
    }

    public static boolean approveOrRejectOrganisation(int organisationId, boolean approve) {
        if (approve){
            return OrganisationRepository.approveOrganisation(organisationId);
        }
        else {
            return OrganisationRepository.rejectOrganisation(organisationId);
        }
    }

    public static boolean createNewBadge(Badge badge){
        try {
            // First, create the badge in database to get the generated ID
            if (BadgeRepository.createBadge(badge)) {
                // Now we have the badgeId from the database
                int badgeId = badge.getBadgeId();

                // If there's an icon to save
                if (badge.getIconPath() != null && !badge.getIconPath().isEmpty()) {
                    // Create uploads/badges directory if it doesn't exist
                    java.nio.file.Path uploadsDir = java.nio.file.Paths.get("uploads/badges");
                    if (!java.nio.file.Files.exists(uploadsDir)) {
                        java.nio.file.Files.createDirectories(uploadsDir);
                    }

                    String destinationPath = "uploads/badges/" + badgeId + ".png";

                    // Convert URI string back to proper file path
                    java.net.URI sourceUri = java.net.URI.create(badge.getIconPath());
                    java.nio.file.Path sourcePath = java.nio.file.Paths.get(sourceUri);

                    // Copy the icon file
                    java.nio.file.Files.copy(
                            sourcePath,
                            java.nio.file.Paths.get(destinationPath),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );

                    // Update badge with new icon path
                    badge.setIconPath(destinationPath);
                    // Optionally update the database with new icon path
                    BadgeRepository.updateBadgeIconPath(badgeId, destinationPath);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean deleteBadge(int badgeId){
        return BadgeRepository.deleteBadge(badgeId);
    }

    public static List<Badge> getAllBadges(){
        return BadgeRepository.getAllBadges();
    }

}
