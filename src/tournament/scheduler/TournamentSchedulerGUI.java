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
    
    @Override
    public void start(Stage primaryStage) {
        dbManager = new DatabaseManager();
        // Clear any existing teams when starting fresh
        dbManager.deleteAllTeams();
        
        primaryStage.setTitle("Tournament Scheduler");

        // Create main layout
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.setAlignment(Pos.CENTER);

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

        // Create scene
        Scene scene = new Scene(mainLayout, 500, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void processTeamName() {
        String teamName = teamNameField.getText().trim();
        
        if (teamName.isEmpty()) {
            showAlert("Please enter a team name");
            return;
        }

        // Save team to database
        Team team = new Team(teamName);
        dbManager.saveTeam(team);
        
        currentTeamIndex++;
        teamNameField.clear();

        if (currentTeamIndex < NUM_TEAMS) {
            promptLabel.setText("Enter name for Team " + (currentTeamIndex + 1) + ":");
        } else {
            // All teams entered, generate and display schedule
            generateAndDisplaySchedule();
        }
    }

    private void generateAndDisplaySchedule() {
        // Hide input controls and show schedule
        promptLabel.setVisible(false);
        teamNameField.setVisible(false);
        nextButton.setVisible(false);
        scheduleArea.setVisible(true);

        // Get teams from database
        List<Team> teams = dbManager.getAllTeams();
        String[] teamNames = teams.stream()
                                 .map(Team::getName)
                                 .toArray(String[]::new);
        
        List<Match> schedule = generateSchedule(teamNames);
        displaySchedule(schedule);
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
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
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