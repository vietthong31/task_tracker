import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

public class Task {

    private int id;
    private String description;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Task() {
    }

    public Task(int id, String description) {
        var now = LocalDateTime.now();
        this.id = id;
        this.description = description;
        this.status = TaskStatus.TODO;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static Optional<Task> fromJson(String json) throws Exception {
        json = json.trim().replace("{", "").replace("}", "");
        if (json.isBlank()) return Optional.empty();

        String[] keyValuePairs = json.split(",");

        Class<Task> clazz = Task.class;
        Task task = clazz.getDeclaredConstructor().newInstance();

        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split("(?<=\"):");
            String key = keyValue[0].trim().replaceAll("\"", "");
            String value = keyValue[1].trim().replaceAll("\"", "");

            Field field;
            try {
                field = clazz.getDeclaredField(key);
                field.setAccessible(true);
            } catch (NoSuchFieldException e) {
                continue;
            }

            switch (field.getName()) {
                case "id":
                    field.set(task, Integer.parseInt(value));
                    break;
                case "description":
                    field.set(task, value);
                    break;
                case "status":
                    field.set(task, TaskStatus.valueOf(value.toUpperCase()));
                    break;
                case "createdAt":
                case "updatedAt":
                    field.set(task, LocalDateTime.parse(value));
                    break;
            }
        }
        return Optional.of(task);
    }

    public String toJson() {
        return String.format("""
                {
                  "id": %d,
                  "description": "%s",
                  "status": "%s",
                  "createdAt": "%s",
                  "updatedAt": "%s"
                }""", id, description, status, createdAt, updatedAt);
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return id + " " + description + " " + status + " " + createdAt + " " + updatedAt;
    }

}