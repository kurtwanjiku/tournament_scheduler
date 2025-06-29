package tournament.scheduler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tournament.scheduler.masterdata.CountryManagerGUI;
import tournament.scheduler.masterdata.LeagueManagerGUI;

public class MainApplication extends Application {
    private Stage mainStage;
    
    @Override
    public void start(Stage primaryStage) {
        this.mainStage = primaryStage;
        primaryStage.setTitle("Tournament Scheduler");
        
        // Create menu bar
        MenuBar menuBar = new MenuBar();
        
        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> Platform.exit());
        fileMenu.getItems().add(exitItem);
        
        // Data menu
        Menu dataMenu = new Menu("Data");
        MenuItem countryManagerItem = new MenuItem("Country Manager");
        MenuItem leagueManagerItem = new MenuItem("League Manager");
        
        countryManagerItem.setOnAction(e -> openCountryManager());
        leagueManagerItem.setOnAction(e -> openLeagueManager());
        
        dataMenu.getItems().addAll(countryManagerItem, leagueManagerItem);
        
        // Add menus to menu bar
        menuBar.getMenus().addAll(fileMenu, dataMenu);
        
        // Create main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(menuBar);
        
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void openCountryManager() {
        try {
            Stage countryStage = new Stage();
            // Make the window modal
            countryStage.initModality(Modality.APPLICATION_MODAL);
            countryStage.initOwner(mainStage);
            
            CountryManagerGUI countryManager = new CountryManagerGUI();
            countryManager.start(countryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void openLeagueManager() {
        try {
            Stage leagueStage = new Stage();
            // Make the window modal
            leagueStage.initModality(Modality.APPLICATION_MODAL);
            leagueStage.initOwner(mainStage);
            
            LeagueManagerGUI leagueManager = new LeagueManagerGUI();
            leagueManager.start(leagueStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
} 