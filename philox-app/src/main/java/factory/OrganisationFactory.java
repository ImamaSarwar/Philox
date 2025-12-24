package factory;

import entity.Organisation;

import java.time.LocalDate;

public class OrganisationFactory {
    public static Organisation createOrganisation(
            int organisationId,
            String name,
            String email,
            String password,
            String repName,
            String repCnic,
            String repContactNumber,
            String repEmail,
            String contactNumber,
            String mission,
            String address,
            String website,
            String ntn,
            String registrationAuthority,
            String registrationNumber,
            LocalDate issueDate,
            String registrationProofPath,
            String taxDocumentPath,
            String cnicProofPath,
            String instagramLink,
            String facebookLink,
            String linkedInLink
    ) {
        Organisation organisation = new Organisation();
        organisation.setOrganisationId(organisationId);
        organisation.setName(name);
        organisation.setEmail(email);
        organisation.setPassword(password);
        organisation.setStatus(1); // for now
        organisation.setRepName(repName);
        organisation.setRepCnic(repCnic);
        organisation.setRepContactNumber(repContactNumber);
        organisation.setRepEmail(repEmail);
        organisation.setContactNumber(contactNumber);
        organisation.setMission(mission);
        organisation.setAddress(address);
        organisation.setWebsite(website);
        organisation.setNtn(ntn);
        organisation.setRegistrationAuthority(registrationAuthority);
        organisation.setRegistrationNumber(registrationNumber);
        organisation.setIssueDate(issueDate);
        organisation.setRegistrationProofPath(registrationProofPath);
        organisation.setTaxDocumentPath(taxDocumentPath);
        organisation.setCnicProofPath(cnicProofPath);
        organisation.setInstagramLink(instagramLink);
        organisation.setFacebookLink(facebookLink);
        organisation.setLinkedInLink(linkedInLink);
        return organisation;
    }
};
