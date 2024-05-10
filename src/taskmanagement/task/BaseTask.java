package taskmanagement.task;

import taskmanagement.status.TaskStatus;

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

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Task {" +
                "ID=" + getId() +
                ", Title='" + getTitle() + '\'' +
                ", Description='" + getDescription() + '\'' +
                ", Status=" + getStatus() +
                '}';
    }
}
