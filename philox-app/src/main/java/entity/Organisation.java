package entity;

import java.time.LocalDate;

public class Organisation extends User {
    // ORGANISATION table attributes
    //private int organisationId;
    private String contactNumber;
    private String mission;
    private String address;
    private String website;

    private String repName;
    private String repCnic;
    private String repContactNumber;
    private String repEmail;

    // ORGANISATION_LICENSE table attributes
    private String registrationAuthority;
    private String registrationNumber;
    private LocalDate issueDate;
    private String ntn;

    private String registrationProofPath;
    private String taxDocumentPath;
    private String cnicProofPath;

    // ORGANISATION_SOCIAL_MEDIA table attributes
    private String instagramLink;
    private String facebookLink;
    private String linkedInLink;

    public Organisation() {
        super();
    }

    // Getters and setters
    public String getRepName() { return repName; }
    public void setRepName(String repName) { this.repName = repName; }

    public String getRepCnic() { return repCnic; }
    public void setRepCnic(String repCnic) { this.repCnic = repCnic; }

    public String getRepContactNumber() { return repContactNumber; }
    public void setRepContactNumber(String repContactNumber) { this.repContactNumber = repContactNumber; }

    public String getRepEmail() { return repEmail; }
    public void setRepEmail(String repEmail) { this.repEmail = repEmail; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getMission() { return mission; }
    public void setMission(String mission) { this.mission = mission; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getNtn() { return ntn; }
    public void setNtn(String ntn) { this.ntn = ntn; }

    public int getOrganisationId() { return getUserId(); }
    public void setOrganisationId(int organisationId) { setUserId(organisationId); }

    public String getRegistrationAuthority() { return registrationAuthority; }
    public void setRegistrationAuthority(String registrationAuthority) { this.registrationAuthority = registrationAuthority; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public String getRegistrationProofPath() { return registrationProofPath; }
    public void setRegistrationProofPath(String registrationProofPath) { this.registrationProofPath = registrationProofPath; }

    public String getTaxDocumentPath() { return taxDocumentPath; }
    public void setTaxDocumentPath(String taxDocumentPath) { this.taxDocumentPath = taxDocumentPath; }

    public String getCnicProofPath() { return cnicProofPath; }
    public void setCnicProofPath(String cnicProofPath) { this.cnicProofPath = cnicProofPath; }

    // Social media getters and setters
    public String getInstagramLink() { return instagramLink; }
    public void setInstagramLink(String instagramLink) { this.instagramLink = instagramLink; }

    public String getFacebookLink() { return facebookLink; }
    public void setFacebookLink(String facebookLink) { this.facebookLink = facebookLink; }

    public String getLinkedInLink() { return linkedInLink; }
    public void setLinkedInLink(String linkedInLink) { this.linkedInLink = linkedInLink; }
}
