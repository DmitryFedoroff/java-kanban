package taskmanagement.task;

import taskmanagement.status.TaskStatus;

import java.util.Objects;

public abstract class BaseTask {
    protected int id;
    protected String title;
    protected String description;
    protected TaskStatus status;

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

    @Override
    public String toString() {
        return "BaseTask{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseTask baseTask = (BaseTask) o;
        return id == baseTask.id &&
                Objects.equals(title, baseTask.title) &&
                Objects.equals(description, baseTask.description) &&
                status == baseTask.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status);
    }
}
