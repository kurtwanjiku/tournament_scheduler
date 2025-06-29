package tournament.scheduler;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.util.*;

public class TournamentSchedulerGUI extends Application {
    private static final int NUM_TEAMS = 8;
    private int currentTeamIndex = 0;
    private DatabaseManager dbManager;
    private TextField teamNameField;
    private Label promptLabel;
    private Button nextButton;
    private TextArea scheduleArea;
    private VBox mainLayout;
    private ListView<String> existingTeamsListView;
    
    @Override
    public void start(Stage primaryStage) {
        dbManager = new DatabaseManager();
        
        primaryStage.setTitle("Tournament Scheduler");

        // Create main layout
        mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.setAlignment(Pos.CENTER);

        // Check for existing teams
        List<Team> existingTeams = dbManager.getAllTeams();
        
        if (!existingTeams.isEmpty()) {
            showExistingTeamsScreen(existingTeams);
        } else {
            showNewTeamInputScreen();
        }

        // Create scene
        Scene scene = new Scene(mainLayout, 500, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showExistingTeamsScreen(List<Team> existingTeams) {
        // Clear previous content
        mainLayout.getChildren().clear();

        // Add header label
        Label headerLabel = new Label("Existing Teams Found");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Create ListView for existing teams
        existingTeamsListView = new ListView<>();
        existingTeamsListView.setPrefHeight(200);
        for (Team team : existingTeams) {
            existingTeamsListView.getItems().add(team.getName());
        }

        // Create buttons
        Button useExistingButton = new Button("Use Existing Teams");
        Button startFreshButton = new Button("Start Fresh");
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(useExistingButton, startFreshButton);

        // Add components to layout
        mainLayout.getChildren().addAll(
            headerLabel,
            new Label("Found " + existingTeams.size() + " teams in database:"),
            existingTeamsListView,
            buttonBox
        );

        // Button handlers
        useExistingButton.setOnAction(e -> {
            if (existingTeams.size() == NUM_TEAMS) {
                generateAndDisplaySchedule();
            } else {
                showAlert("Incorrect number of teams", 
                         "Found " + existingTeams.size() + " teams, but need exactly " + NUM_TEAMS + " teams.\n" +
                         "Please start fresh to enter " + NUM_TEAMS + " teams.");
            }
        });

        startFreshButton.setOnAction(e -> {
            dbManager.deleteAllTeams();
            showNewTeamInputScreen();
        });
    }

    private void showNewTeamInputScreen() {
        // Clear previous content
        mainLayout.getChildren().clear();

        // Create input section
        promptLabel = new Label("Enter name for Team 1:");
        teamNameField = new TextField();
        teamNameField.setMaxWidth(200);

        nextButton = new Button("Next");
        nextButton.setOnAction(e -> processTeamName());

        // Create schedule display area
        scheduleArea = new TextArea();
        scheduleArea.setEditable(false);
        scheduleArea.setPrefRowCount(20);
        scheduleArea.setPrefColumnCount(40);
        scheduleArea.setWrapText(true);
        scheduleArea.setVisible(false);

        // Add components to layout
        mainLayout.getChildren().addAll(
            promptLabel,
            teamNameField,
            nextButton,
            scheduleArea
        );
    }

    private void processTeamName() {
        String teamName = teamNameField.getText().trim();
        
        if (teamName.isEmpty()) {
            showAlert("Please enter a team name");
            return;
        }

        // Save team to database
        Team team = new Team(teamName);
        try {
            dbManager.saveTeam(team);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                showAlert("Team name '" + teamName + "' already exists. Please use a different name.");
                return;
            }
            throw e;
        }
        
        currentTeamIndex++;
        teamNameField.clear();

        if (currentTeamIndex < NUM_TEAMS) {
            promptLabel.setText("Enter name for Team " + (currentTeamIndex + 1) + ":");
        } else {
            generateAndDisplaySchedule();
        }
    }

    private void generateAndDisplaySchedule() {
        // Hide input controls and show schedule
        mainLayout.getChildren().clear();

        // Add header
        Label headerLabel = new Label("Tournament Schedule");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        mainLayout.getChildren().add(headerLabel);

        // Create and show schedule area
        scheduleArea = new TextArea();
        scheduleArea.setEditable(false);
        scheduleArea.setPrefRowCount(20);
        scheduleArea.setPrefColumnCount(40);
        scheduleArea.setWrapText(true);
        scheduleArea.setVisible(true);
        mainLayout.getChildren().add(scheduleArea);

        // Get teams from database
        List<Team> teams = dbManager.getAllTeams();
        String[] teamNames = teams.stream()
                                 .map(Team::getName)
                                 .toArray(String[]::new);
        
        List<Match> schedule = generateSchedule(teamNames);
        displaySchedule(schedule);

        // Add a "New Tournament" button
        Button newTournamentButton = new Button("Start New Tournament");
        newTournamentButton.setOnAction(e -> {
            dbManager.deleteAllTeams();
            currentTeamIndex = 0;
            showNewTeamInputScreen();
        });
        mainLayout.getChildren().add(newTournamentButton);
    }

    private void displaySchedule(List<Match> schedule) {
        StringBuilder sb = new StringBuilder();
        schedule.sort((a, b) -> a.round - b.round);

        int currentRound = 0;
        for (Match match : schedule) {
            if (currentRound != match.round) {
                currentRound = match.round;
                sb.append("\nRound ").append(currentRound).append(":\n");
                sb.append("-------------------\n");
            }
            sb.append(match.homeTeam).append(" vs ").append(match.awayTeam).append("\n");
        }

        scheduleArea.setText(sb.toString());
    }

    private void showAlert(String message) {
        showAlert("Warning", message);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    static class Match {
        String homeTeam;
        String awayTeam;
        int round;

        Match(String homeTeam, String awayTeam, int round) {
            this.homeTeam = homeTeam;
            this.awayTeam = awayTeam;
            this.round = round;
        }
    }

    private List<Match> generateSchedule(String[] teams) {
        List<Match> schedule = new ArrayList<>();
        
        // First half of the season (each team plays against others once)
        for (int round = 0; round < NUM_TEAMS - 1; round++) {
            for (int i = 0; i < NUM_TEAMS / 2; i++) {
                int team1 = i;
                int team2 = NUM_TEAMS - 1 - i;
                
                // Rotate teams except the first one
                if (i > 0) {
                    team1 = (round + i) % (NUM_TEAMS - 1);
                    team2 = (round + NUM_TEAMS - 1 - i) % (NUM_TEAMS - 1);
                }
                
                if (team2 == 0) team2 = NUM_TEAMS - 1;
                
                schedule.add(new Match(teams[team1], teams[team2], round + 1));
            }
        }

        // Second half of the season (reverse home/away teams)
        int firstHalfSize = schedule.size();
        for (int i = 0; i < firstHalfSize; i++) {
            Match match = schedule.get(i);
            schedule.add(new Match(
                match.awayTeam,
                match.homeTeam,
                match.round + NUM_TEAMS - 1
            ));
        }

        return schedule;
    }

    public static void main(String[] args) {
        launch(args);
    }
} 