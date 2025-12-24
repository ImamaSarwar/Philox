package service;

import db.repository.UserRepository;
import factory.VolunteerFactory;
import entity.Volunteer;
import factory.OrganisationFactory;
import entity.Organisation;

import db.repository.VolunteerRepository;
import db.repository.OrganisationRepository;

import org.mindrot.jbcrypt.BCrypt;
import utils.PasswordUtil;

import java.time.LocalDate;

public class RegistrationService {

    private RegistrationService() {} // Prevent instantiation

    // volunteer registration
    public static boolean registerVolunteer(String name,String email,String password,String phone,String cnic,int age,String city) {
        // for testing purposes, we are not hashing the password
        //String hashedPwd = password;
        String hashedPwd = PasswordUtil.hash(password);
        Volunteer volunteer = VolunteerFactory.createVolunteer(name, email, hashedPwd, phone, cnic, age, city);
        volunteer.setRegistrationDate(LocalDate.now());
        volunteer.setStatus(1); // active status
        return VolunteerRepository.save(volunteer);
    }

    // organisation registration
    public static boolean registerOrganisation( String name, String email, String password, String repName,
                                                String repCnic, String repContactNumber, String repEmail, String contactNumber, String mission, String address,
                                                String website, String ntn, String registrationAuthority, String registrationNumber,
                                                String registrationProofPath, String taxDocumentPath, String cnicProofPath,
                                                LocalDate issueDate, String instagramLink, String facebookLink, String linkedInLink) {
        String hashedPwd = PasswordUtil.hash(password);
        Organisation organisation = OrganisationFactory.createOrganisation(
                0,
                name,
                email,
                hashedPwd,
                repName,
                repCnic,
                repContactNumber,
                repEmail,
                contactNumber,
                mission,
                address,
                website,
                ntn,
                registrationAuthority,
                registrationNumber,
                issueDate,
                registrationProofPath,
                taxDocumentPath,
                cnicProofPath,
                instagramLink,
                facebookLink,
                linkedInLink
        );
        organisation.setRegistrationDate(LocalDate.now());
        return OrganisationRepository.save(organisation);
    }

    public static boolean registerOrganisation(Organisation organisation){
        String hashedPwd = PasswordUtil.hash(organisation.getPassword());
        organisation.setPassword(hashedPwd);
        organisation.setRegistrationDate(LocalDate.now());
        organisation.setStatus(0); // pending status
        return OrganisationRepository.save(organisation);
    }

    // uniqueness checks
    public static boolean userEmailExists(String email) {
        return UserRepository.existsByEmail(email);
    }

    public static boolean volunteerCnicExists(String cnic) {
        return VolunteerRepository.existsByCnic(cnic);
    }

    public static boolean organisationRegistrationNumberExists(String registrationNumber) {
        return OrganisationRepository.existsByRegistrationNumber(registrationNumber);
    }

    public static boolean organisationNtnExists(String ntn) {
        return OrganisationRepository.existsByNtn(ntn);
    }



}
