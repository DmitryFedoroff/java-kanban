package taskmanagement.task;

public class Subtask extends BaseTask {
    private final int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        if (this.epicId == id) {
            throw new IllegalArgumentException("Подзадача не может быть своим же эпиком");
        }
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + getEpicId() +
                '}';
    }
}
