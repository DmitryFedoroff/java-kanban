package taskmanagement.task;

import taskmanagement.status.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public abstract class BaseTask {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");
    protected int id;
    protected String title;
    protected String description;
    protected TaskStatus status;
    protected Duration duration = Duration.ZERO;
    protected LocalDateTime startTime;

    public BaseTask(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plusMinutes(duration.toMinutes());
    }

    public String getStartTimeToString() {
        if (startTime == null) {
            return "null";
        }
        return startTime.format(DATE_TIME_FORMATTER);
    }

    public String getEndTimeToString() {
        if (startTime == null) {
            return "null";
        }
        return getEndTime().format(DATE_TIME_FORMATTER);
    }

    public boolean isOverlapping(BaseTask other) {
        if (this.startTime == null || other.getStartTime() == null) {
            return false;
        }
        return !(this.getEndTime().isBefore(other.getStartTime()) || this.getStartTime().isAfter(other.getEndTime()));
    }

    @Override
    public String toString() {
        return String.join(",",
                String.valueOf(id),
                this.getClass().getSimpleName().toUpperCase(),
                title,
                status.name(),
                description,
                (startTime == null) ? "null" : startTime.format(DATE_TIME_FORMATTER),
                String.valueOf(duration.toMinutes())
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseTask baseTask = (BaseTask) o;
        return id == baseTask.id &&
                Objects.equals(title, baseTask.title) &&
                Objects.equals(description, baseTask.description) &&
                status == baseTask.status &&
                Objects.equals(duration, baseTask.duration) &&
                Objects.equals(startTime, baseTask.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status, duration, startTime);
    }
}
