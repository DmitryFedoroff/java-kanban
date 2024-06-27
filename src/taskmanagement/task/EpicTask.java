package taskmanagement.task;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends BaseTask {
    private final List<Integer> subtaskIds;

    public EpicTask(String title, String description) {
        super(title, description);
        this.subtaskIds = new ArrayList<>();
    }

    public List<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void addSubtask(int subtaskId) {
        if (subtaskId == this.id) {
            throw new IllegalArgumentException("Эпик не может быть добавлен в виде подзадачи к самому себе");
        }
        subtaskIds.add(subtaskId);
    }

    public void removeSubtask(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }

    @Override
    public String toString() {
        return String.join(",",
                String.valueOf(getId()),
                "EPIC",
                getTitle(),
                getStatus().name(),
                getDescription()
        );
    }
}