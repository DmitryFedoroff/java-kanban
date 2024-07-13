package taskmanagement.task;

import taskmanagement.manager.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EpicTask extends BaseTask {
    private final List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public EpicTask(String title, String description) {
        super(title, description);
    }

    public List<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void addSubtask(int subtaskId) {
        if (subtaskId == this.id) {
            throw new IllegalArgumentException("Эпик не может быть добавлен в виде подзадачи к самому себе");
        }
        subtaskIds.add(subtaskId);
        recalculateDurationAndTime();
    }

    public void removeSubtask(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
        recalculateDurationAndTime();
    }

    private void recalculateDurationAndTime() {
        this.duration = subtaskIds.stream()
                .map(subtaskId -> (Subtask) Managers.getDefault().getTaskById(subtaskId))
                .filter(subtask -> subtask != null && subtask.getStartTime() != null)
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        this.startTime = subtaskIds.stream()
                .map(subtaskId -> (Subtask) Managers.getDefault().getTaskById(subtaskId))
                .filter(subtask -> subtask != null && subtask.getStartTime() != null)
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        this.endTime = subtaskIds.stream()
                .map(subtaskId -> (Subtask) Managers.getDefault().getTaskById(subtaskId))
                .filter(subtask -> subtask != null && subtask.getStartTime() != null)
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return String.join(",",
                String.valueOf(getId()),
                "EPIC",
                getTitle(),
                getStatus().name(),
                getDescription(),
                getStartTimeToString(),
                String.valueOf(getDuration().toMinutes()),
                getEndTimeToString());
    }
}
