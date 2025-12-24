package com.example.philoxapp.component.sidepanel;

/**
 * Represents a navigation item in the side panel
 */
public class NavItem {
    private String title;
    private String icon;
    private String controller;

    // Default constructor for JSON deserialization
    public NavItem() {}

    public NavItem(String title, String icon, String controller) {
        this.title = title;
        this.icon = icon;
        this.controller = controller;
    }

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getController() { return controller; }
    public void setController(String controller) { this.controller = controller; }

    @Override
    public String toString() {
        return "NavItem{title='" + title + "', icon='" + icon + "', controller='" + controller + "'}";
    }
}
