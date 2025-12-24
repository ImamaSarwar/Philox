package entity;

import java.time.LocalDateTime;

public class Application {
    private int applicationId;
    private String applicationComment;
    private int opportunityId;
    private int volunteerId;
    private LocalDateTime applicationDate; //createdAt
    private int status; //0: Pending, 1: Accepted, -1: Rejected, 2: finalised

    Volunteer volunteer; // Associated Volunteer object
    Opportunity opportunity; // Associated Opportunity object

    public int getApplicationId() {
        return applicationId;
    }
    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationComment() {
        return applicationComment;
    }
    public void setApplicationComment(String applicationComment) {
        this.applicationComment = applicationComment;
    }

    public int getOpportunityId() {
        return opportunityId;
    }
    public void setOpportunityId(int opportunityId) {
        this.opportunityId = opportunityId;
    }

    public int getVolunteerId() {
        return volunteerId;
    }
    public void setVolunteerId(int volunteerId) {
        this.volunteerId = volunteerId;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }
    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public Volunteer getVolunteer() {
        return volunteer;
    }
    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }
    public Opportunity getOpportunity() {
        return opportunity;
    }
    public void setOpportunity(Opportunity opportunity) {
        this.opportunity = opportunity;
    }

    public void acceptApplication(){
        this.status = 1; // Accepted
    }
    public void rejectApplication(){
        this.status = -1; // Rejected
    }

}
