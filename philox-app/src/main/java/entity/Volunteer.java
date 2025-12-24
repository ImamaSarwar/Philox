package entity;

public class Volunteer extends User{
    //private int volunteerId;
    private String phone;
    private String cnic;
    private int age;
    private String city;
    private int availability;
    private float rating;
    private String bio;

    private java.util.List<String> skills = new java.util.ArrayList<>();

    public Volunteer() {
        super();
        this.availability = 1;
    }

    public Volunteer(String name, String email, String password, String phone, String cnic, int age, String city) {
        super(0, name, email, password, 1, null);
        this.phone = phone;
        this.cnic = cnic;
        this.age = age;
        this.city = city;
        this.availability = 1;
    }

    public int getVolunteerId() {
        return getUserId();
    }
    public void setVolunteerId(int volunteerId) {
        setUserId(volunteerId);
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCnic() {
        return cnic;
    }
    public void setCnic(String cnic) {
        this.cnic = cnic;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public int getAvailability() {
        return availability;
    }
    public void setAvailability(int availability) { this.availability = availability; }
    public void setAvailable() {
        this.availability = 1;
    }
    public void setUnavailable(){this.availability = 0;}

    public float getRating() { return this.rating;}
    public void setRating(float rating) { this.rating = rating; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public java.util.List<String> getSkills() {
        return skills == null ? new java.util.ArrayList<>() : new java.util.ArrayList<>(skills);
    }
    public void setSkills(java.util.List<String> skills) {
        if (skills == null) {
            this.skills = new java.util.ArrayList<>();
        } else {
            this.skills = new java.util.ArrayList<>(skills);
        }
    }
    public void addSkill(String skill) {
        if (this.skills == null) this.skills = new java.util.ArrayList<>();
        this.skills.add(skill);
    }
}
