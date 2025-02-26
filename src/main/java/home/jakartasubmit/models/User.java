package home.jakartasubmit.models;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.mindrot.jbcrypt.BCrypt;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password; // Stored as hashed password

    public enum Role { STUDENT, INSTRUCTOR, ADMIN }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public User() {}

    public User(String fullName, String email, String password, Role role) {
        this.fullName = fullName;
        this.email = email;
        setPassword(password);// Using setter to ensure hashing on create and edit
        this.role = role;
    }

    @PrePersist
    public void hashPassword() {
        if (password != null && !password.isEmpty()) {
            int workFactor = Integer.parseInt("10");
            this.password = BCrypt.hashpw(password, BCrypt.gensalt(workFactor));
        }
    }

    @Override
    public String toString() {
        return String.format("User{id=%s, fullName=%s, email=%s, role=%s}",
                userId,
                fullName,
                email,
                role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(userId, user.userId);
    }

    // Getters
    public UUID getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }

    // Setters
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }

    // Hashing passwords properly
    public void setPassword(String password) {
        if (password != null && !password.isEmpty()) {
            this.password = BCrypt.hashpw(password, BCrypt.gensalt(10));
        }
    }

    public void setRole(Role role) { this.role = role; }
}