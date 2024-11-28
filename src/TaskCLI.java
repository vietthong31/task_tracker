import java.util.*;

public class TaskCLI {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GRAY = "\u001B[90m";

    private TaskTracker taskTracker;

    public static void main(String[] args) {
        TaskCLI cli = new TaskCLI();
        if (args.length == 0) {
            System.out.println(HelpCommand.getUsage("TaskCLI"));
        } else {
            String command = args[0];
            String[] arguments = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
            try {
                cli.run(command, arguments);
            } catch (Exception e) {
                System.out.printf("%serror%s: %s\n", ANSI_RED, ANSI_RESET, e.getMessage());
            }
        }
    }

    public void run(String command, String[] args) throws Exception {
        if (!command.equals("help")) taskTracker = new TaskTracker();

        switch (command) {
            case "add":
            case "delete":
            case "mark-in-progress":
            case "mip":
            case "mark-done":
            case "md":
                if (args.length == 0) {
                    throw new Exception("Missing argument(s)");
                }
                break;
            case "update":
                if (args.length == 1) {
                    throw new Exception("Missing argument(s)");
                }
        }
        switch (command) {
            case "add" -> {
                int count = 0;
                for (String arg : args) {
                    if (taskTracker.add(arg)) count++;
                }
                System.out.printf("Added %s%d%s task(s) successfully\n", ANSI_BLUE, count, ANSI_RESET);
            }
            case "delete" -> {
                Set<Integer> deletedTaskIds = new HashSet<>();
                Set<Integer> notFoundIds = new HashSet<>();
                for (String arg : args) {
                    if (taskTracker.delete(Integer.parseInt(arg))) deletedTaskIds.add(Integer.parseInt(arg));
                    else notFoundIds.add(Integer.parseInt(arg));
                }
                if (!deletedTaskIds.isEmpty()) System.out.println("Deleted task(s): " + deletedTaskIds);
                if (!notFoundIds.isEmpty()) System.out.println("Not found id(s): " + notFoundIds);
            }
            case "update" -> taskTracker.update(Integer.parseInt(args[0]), args[1]);
            case "mark-in-progress", "mip" ->
                    taskTracker.changeStatus(Integer.parseInt(args[0]), TaskStatus.IN_PROGRESS);
            case "mark-done", "md" -> taskTracker.changeStatus(Integer.parseInt(args[0]), TaskStatus.DONE);
            case "list" -> {
                if (args.length == 1) {
                    taskTracker.list(args[0]);
                } else {
                    taskTracker.list();
                }
            }
            case "help" -> {
                if (args.length == 0) {
                    System.out.println(HelpCommand.getUsage("TaskCLI"));
                } else {
                    String usage = HelpCommand.getUsage(args[0]);
                    if (usage == null) {
                        throw new Exception("Unknown command " + args[0]);
                    } else {
                        System.out.println(usage);
                    }
                }
            }
            default -> throw new Exception("Unknown command: " + command);
        }

        if (!command.equals("list") && !command.equals("help")) {
            taskTracker.saveTasks();
        }
    }

}

class HelpCommand {
    private static final Map<String, String> commandUsages = new HashMap<>();

    static {
        // @formatter:off
        commandUsages.put("TaskCLI", String.format(
                """
                %1$sTaskCLI%2$s is a task tracker command line interface.
                
                Usage: TaskCLI <command> [...args]
                
                Commands:
                  add               Add a new task
                  update            Update a task
                  delete            Delete a task
                  mark-in-progress  Mark a task in progress %3$s(TaskCLI mip)%2$s
                  mark-done         Mark a task done %3$s(TaskCLI md)%2$s
                  list              List all tasks
                  help [command]    Show usages
                """, TaskCLI.ANSI_BLUE, TaskCLI.ANSI_RESET, TaskCLI.ANSI_GRAY));
        //formatter:on
        commandUsages.put("add", """
                Usage: TaskCLI add <task description>
                
                Examples:
                  TaskCLI add work
                  TaskCLI add "finish project" "play game"
                """);
        commandUsages.put("update", """
                Usage: TaskCLI update <id> <task description>
                
                Example: TaskCLI update 1 "cook dinner"
                """);
        commandUsages.put("delete", """
                Usage: TaskCLI delete <id>
                
                Examples:
                  - TaskCLI delete 1
                  - TaskCLI delete 2 3
                """);
        commandUsages.put("mark-in-progress", """
                Usage: TaskCLI mark-in-progress <id>
                
                Example: TaskCLI mark-in-progress 1
                """);
        commandUsages.put("mark-done", """
                Usage: TaskCLI mark-done <id>
                
                Example: TaskCLI mark-done 1
                """);
        commandUsages.put("list", """
                Usage: TaskCLI list [option]
                
                Options:
                  - todo          Tasks that are not done
                  - in-progress   Tasks that are in progress
                  - done          Tasks that are done
                
                Examples:
                  - TaskCLI list
                  - TaskCLI list done
                """);
        commandUsages.put("help", "Usage: TaskCLI help [command]");
    }

    public static String getUsage(String command) {
        return commandUsages.get(command);
    }
}
