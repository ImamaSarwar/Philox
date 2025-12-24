package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Opportunity {

    private int opportunityId;
    private int organisationId;
    private String title;
    private int categoryId;
    private String category;
    private String description;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate closeDate;
    private LocalTime startTime;
    private int duration; // in hours
    private int capacity;
    private int status; // 1: Open, 0: Closed, -1: Cancelled, -2: Flagged
    private LocalDateTime createdAt;

    private Organisation organisation;

    // Getters and Setters
    public int getOpportunityId() { return opportunityId; }
    public void setOpportunityId(int opportunityId) { this.opportunityId = opportunityId;}
    public int getOrganisationId() { return organisationId; }
    public void setOrganisationId(int organisationId) { this.organisationId = organisationId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public LocalDate getCloseDate() { return closeDate; }
    public void setCloseDate(LocalDate closeDate) { this.closeDate = closeDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public void setAsOpen() { this.status = 1; }
    public void setAsClosed() { this.status = 0; }
    public void setAsCancelled() { this.status = -1; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Organisation getOrganisation() { return organisation; }
    public void setOrganisation(Organisation organisation) { this.organisation = organisation; }

}
