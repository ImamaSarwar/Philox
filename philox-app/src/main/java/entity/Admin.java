package entity;

public class Admin extends User{

    String role;

    public Admin(int userId, String name, String email, String password, int status, String role) {
        super(userId, name, email, password, status, null);
        this.role = role;
    }

    public Admin() {
        super();
        this.setName("Admin");
        this.setEmail("admin@philox.com");
        this.setStatus(1);
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

}
