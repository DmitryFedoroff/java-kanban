package taskmanagement.task;

public class SimpleTask extends BaseTask {
    public SimpleTask(String title, String description) {
        super(title, description);
    }

    @Override
    public String toString() {
        return String.join(",",
                String.valueOf(getId()),
                "TASK",
                getTitle(),
                getStatus().name(),
                getDescription(),
                getStartTimeToString(),
                String.valueOf(getDuration().toMinutes())
        );
    }
}
