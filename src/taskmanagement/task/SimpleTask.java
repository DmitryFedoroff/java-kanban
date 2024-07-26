package taskmanagement.task;

import java.time.Duration;
import java.time.LocalDateTime;

public class SimpleTask extends BaseTask {
    public SimpleTask(String title, String description) {
        super(title, description);
    }

    public SimpleTask(String title, String description, LocalDateTime startTime, Duration duration) {
        super(title, description);
        this.startTime = startTime;
        this.duration = duration;
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
