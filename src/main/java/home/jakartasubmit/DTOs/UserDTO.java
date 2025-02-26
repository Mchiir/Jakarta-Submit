package home.jakartasubmit.DTOs;

import home.jakartasubmit.models.User.Role;
import home.jakartasubmit.models.User;

public class UserDTO {
    private String fullName;
    private String email;
    private Role role;

    public UserDTO() {}

    public UserDTO(String fullName, String email, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    // Getters
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public User.Role getRole() { return role; }

    // Setters
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(User.Role role) { this.role = role; }
}
