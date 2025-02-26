package home.jakartasubmit.models;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
public class Submission implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID submissionId;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    public Submission() {}

    public Submission(User student, Task task, String filePath) {
        this.student = student;
        this.task = task;
        this.filePath = filePath;
        this.submittedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("Submission{id=%s, studentId=%s, taskId=%s, filePath=%s, submittedAt=%s}",
                submissionId,
                student != null ? student.getUserId() : "null",
                task != null ? task.getTaskId() : "null",
                filePath,
                submittedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(submissionId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Submission that = (Submission) obj;
        return Objects.equals(submissionId, that.submissionId);
    }

    public UUID getSubmissionId() { return submissionId; }
    public User getStudent() { return student; }
    public Task getTask() { return task; }
    public String getFilePath() { return filePath; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }

    public void setSubmissionId(UUID submissionId) { this.submissionId = submissionId; }
    public void setStudent(User student) { this.student = student; }
    public void setTask(Task task) { this.task = task; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}