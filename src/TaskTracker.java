import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class TaskTracker {

    private int currentId;
    private final Path taskPath = Path.of("tasks.json");
    private final List<Task> tasks = new ArrayList<>();

    public TaskTracker() {
        try {
            if (!Files.exists(taskPath)) {
                try {
                    Files.createFile(taskPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            String json = Files.readString(taskPath);
            if (!json.isEmpty()) {
                json = json.replace("[", "").replace("]", "");
                String[] taskJson = json.split("(?<=}),");
                for (String task : taskJson) {
                    Optional<Task> optionalTask = Task.fromJson(task);
                    optionalTask.ifPresent(t -> {
                        if (t.getId() > currentId) {
                            currentId = t.getId();
                        }
                        tasks.add(t);
                    });
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Rewrite tasks.json
    public void saveTasks() {
        String taskJson = tasks.stream().map(Task::toJson).collect(Collectors.joining(",\n"));
        taskJson = "[" + taskJson + "]";
        try {
            Files.writeString(taskPath, taskJson, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean add(String taskDescription) {
        currentId++;
        return tasks.add(new Task(currentId, taskDescription));
    }

    public boolean delete(int id) {
        return tasks.removeIf(t -> t.getId() == id);
    }

    public void update(int id, String description) {
        Task task = tasks.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
        if (task != null) {
            String oldDescription = task.getDescription();
            task.setDescription(description);
            System.out.printf("Updated task %d: %s -> %s\n", id, oldDescription, description);
        } else {
            System.out.println("Not found task with id " + id);
        }
    }

    public void changeStatus(int id, TaskStatus status) {
        Task task = tasks.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
        if (task != null) {
            TaskStatus oldStatus = task.getStatus();
            task.setStatus(status);
            System.out.printf("Updated task %d: %s -> %s\n", id, oldStatus, status.name());
        } else {
            System.out.println("Not found task with id " + id);
        }
    }

    public void list() {
        if (tasks.isEmpty()) {
            System.out.println("Task list is empty");
        } else {
            System.out.println("✔  done, ⏳ doing, ➖ not done");
            tasks.forEach(task -> {
                String symbol = switch (task.getStatus()) {
                    case DONE -> "✔";
                    case IN_PROGRESS -> "⏳";
                    default -> "➖";
                };
                System.out.printf("%d. %s %s\n", task.getId(), task.getDescription(), symbol);
            });
        }
    }

    public void list(String option) {
        List<Task> filteredTasks = tasks.stream()
                .filter(task -> task.getStatus().toString().equalsIgnoreCase(option))
                .toList();
        if (filteredTasks.isEmpty()) {
            System.out.println("There are no \"" + option + "\" tasks");
        } else {
            filteredTasks.forEach(t -> System.out.printf("%d. %s\n", t.getId(), t.getDescription()));
        }
    }

}
