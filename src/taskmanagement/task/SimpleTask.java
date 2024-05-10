package taskmanagement.task;

public class SimpleTask extends BaseTask {

    public SimpleTask(String title, String description) {
        super(title, description);
    }

    @Override
    public String toString() {
        return "SimpleTask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
