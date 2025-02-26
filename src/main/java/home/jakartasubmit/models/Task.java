package home.jakartasubmit.models;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID taskId;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @Column(nullable = false)
    private String courseName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime deadline;

    public Task() {}

    public Task(User instructor, String courseName, String description, LocalDateTime deadline) {
        this.instructor = instructor;
        this.courseName = courseName;
        this.description = description;
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return String.format("Task{id=%s, instructorId=%s, course=%s, deadline=%s}",
                taskId,
                instructor != null ? instructor.getUserId() : "null",
                courseName,
                deadline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return Objects.equals(taskId, task.taskId);
    }

    public UUID getTaskId() { return taskId; }
    public User getInstructor() { return instructor; }
    public String getCourseName() { return courseName; }
    public String getDescription() { return description; }
    public LocalDateTime getDeadline() { return deadline; }

    public void setTaskId(UUID taskId) { this.taskId = taskId; }
    public void setInstructor(User instructor) { this.instructor = instructor; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public void setDescription(String description) { this.description = description; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
}