# Tournament Scheduler

A JavaFX application for scheduling round-robin tournaments with 8 teams. The application uses SQLite for persistent storage of team information.

## Features

- Input and store 8 team names
- Generate a double round-robin tournament schedule
- Each team plays against every other team twice (home and away)
- Persistent storage of team data using SQLite
- Modern JavaFX user interface

## Requirements

- Java 24.0.1 or later
- JavaFX SDK 24.0.1
- SQLite JDBC Driver (automatically downloaded)
- SLF4J API and Simple binding (automatically downloaded)

## Project Structure

```
tournament_scheduler/
├── src/
│   └── tournament/
│       └── scheduler/
│           ├── DatabaseManager.java
│           ├── Team.java
│           └── TournamentSchedulerGUI.java
├── compile_and_run.bat
├── .gitignore
└── README.md
```

## How to Run

1. Ensure Java 24.0.1 and JavaFX SDK 24.0.1 are installed
2. Update the paths in `compile_and_run.bat` if necessary:
   - `JAVA_HOME`: Path to your Java installation
   - `JAVAFX_HOME`: Path to your JavaFX SDK installation
3. Run the application:
   ```bash
   .\compile_and_run.bat
   ```

## Development

The project uses:
- Java for the core logic
- JavaFX for the GUI
- SQLite for data persistence
- Git for version control

## Database Schema

```sql
CREATE TABLE teams (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE
);
``` 