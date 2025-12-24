package session;

import entity.User;

// Singleton class to manage user session
public class Session {
    private static Session instance;
    private User currentUser;

    private Session() {}

    public static Session getSession() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
