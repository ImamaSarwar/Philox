package entity;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private int userId;
    private String message;
    private LocalDateTime timestamp;
    private boolean read;

    // Constructors, getters, setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId;}
    public void setUserId(int userId) { this.userId = userId;}
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
