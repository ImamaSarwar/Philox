package factory;

import entity.Volunteer;

public class VolunteerFactory {
    public static Volunteer createVolunteer(String name, String email, String password, String phone, String cnic, int age, String city) {
        Volunteer volunteer = new Volunteer();
        volunteer.setName(name);
        volunteer.setEmail(email);
        volunteer.setPassword(password);
        volunteer.setPhone(phone);
        volunteer.setCnic(cnic);
        volunteer.setAge(age);
        volunteer.setCity(city);
        // Set other default values if needed
        return volunteer;
    }
}

