# Task Tracker CLI

Solution to [Task Tracker](https://roadmap.sh/projects/task-tracker), a simple command line interface (CLI) to track what you need to do, what you have done, and what you are currently 
working on.

## Features

- Add, Update and Delete tasks.
- Mark a task as in progress or done.
- List all tasks.
- List tasks by status.

## Installation

1. Clone the repository

```
git clone https://github.com/vietthong31/task_tracker.git
cd task_tracker/src/
```

2. Compile the source code

```
javac -d out Task.java TaskCLI.java TaskStatus.java TaskTracker.java
```

3. Run the CLI

```
java -cp out TaskCLI
```

## Usage

```text
Usage: TaskCLI <command> [...args]

Commands:
  add               Add a new task
  update            Update a task
  delete            Delete a task
  mark-in-progress  Mark a task in progress %3$s(TaskCLI mip)%2$s
  mark-done         Mark a task done %3$s(TaskCLI md)%2$s
  list              List all tasks
  help [command]    Show usages
```
