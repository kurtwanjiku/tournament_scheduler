package tournament.scheduler.masterdata;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LeagueManagerGUI extends Application {
    private MasterDataManager dataManager;
    private ComboBox<Country> countryComboBox;
    private TextField nameField;
    private Spinner<Integer> hierarchyLevelSpinner;
    private TableView<League> leagueTable;
    private ObservableList<League> leagueData;
    
    @Override
    public void start(Stage primaryStage) {
        dataManager = new MasterDataManager();
        
        primaryStage.setTitle("League Manager");
        
        // Create main layout
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        
        // Create menu bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> primaryStage.close());
        fileMenu.getItems().add(exitItem);
        menuBar.getMenus().add(fileMenu);
        
        // Create input section
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(5);
        inputGrid.setPadding(new Insets(10));
        
        // Country selection
        Label countryLabel = new Label("Country:");
        countryComboBox = new ComboBox<>();
        countryComboBox.setPromptText("Select Country");
        countryComboBox.setPrefWidth(200);
        
        // Load countries into combo box
        ObservableList<Country> countries = FXCollections.observableArrayList(dataManager.getAllCountries());
        countryComboBox.setItems(countries);
        
        // Custom cell factory to display country name with ISO code
        countryComboBox.setCellFactory(lv -> new ListCell<Country>() {
            @Override
            protected void updateItem(Country country, boolean empty) {
                super.updateItem(country, empty);
                if (empty || country == null) {
                    setText(null);
                } else {
                    setText(country.getName() + " (" + country.getIsoCode() + ")");
                }
            }
        });
        
        // League name input
        Label nameLabel = new Label("League Name:");
        nameField = new TextField();
        nameField.setPromptText("e.g., Bundesliga");
        nameField.setPrefWidth(200);
        
        // Hierarchy level input
        Label hierarchyLabel = new Label("Hierarchy Level:");
        hierarchyLevelSpinner = new Spinner<>(1, 10, 1);
        hierarchyLevelSpinner.setEditable(true);
        hierarchyLevelSpinner.setPrefWidth(100);
        
        // Add button
        Button addButton = new Button("Add League");
        addButton.setOnAction(e -> addLeague());
        
        // Layout input fields
        inputGrid.add(countryLabel, 0, 0);
        inputGrid.add(countryComboBox, 1, 0);
        inputGrid.add(nameLabel, 2, 0);
        inputGrid.add(nameField, 3, 0);
        inputGrid.add(hierarchyLabel, 0, 1);
        inputGrid.add(hierarchyLevelSpinner, 1, 1);
        inputGrid.add(addButton, 3, 1);
        
        // Create table
        leagueTable = new TableView<>();
        leagueData = FXCollections.observableArrayList();
        leagueTable.setItems(leagueData);
        
        // Create columns
        TableColumn<League, String> countryColumn = new TableColumn<>("Country");
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("countryCode"));
        countryColumn.setPrefWidth(100);
        
        TableColumn<League, String> nameColumn = new TableColumn<>("League Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);
        
        TableColumn<League, Integer> hierarchyColumn = new TableColumn<>("Level");
        hierarchyColumn.setCellValueFactory(new PropertyValueFactory<>("hierarchyLevel"));
        hierarchyColumn.setPrefWidth(100);
        
        // Add delete button column
        TableColumn<League, Void> deleteColumn = new TableColumn<>("");
        deleteColumn.setPrefWidth(100);
        deleteColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            
            {
                deleteButton.setOnAction(e -> {
                    League league = getTableRow().getItem();
                    if (league != null) {
                        deleteLeague(league);
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
        
        leagueTable.getColumns().addAll(countryColumn, nameColumn, hierarchyColumn, deleteColumn);
        
        // Add components to main layout
        mainLayout.getChildren().addAll(
            menuBar,
            new Label("Add New League"),
            inputGrid,
            new Separator(),
            new Label("Existing Leagues"),
            leagueTable
        );
        
        // Load existing leagues
        refreshLeagueList();
        
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void addLeague() {
        Country selectedCountry = countryComboBox.getValue();
        String name = nameField.getText().trim();
        Integer hierarchyLevel = hierarchyLevelSpinner.getValue();
        
        // Validate input
        if (selectedCountry == null) {
            showAlert("Input Error", "Please select a country.");
            return;
        }
        
        if (name.isEmpty()) {
            showAlert("Input Error", "League name is required.");
            return;
        }
        
        // Check if league already exists for this country
        if (dataManager.isLeagueExists(selectedCountry.getIsoCode(), name)) {
            showAlert("Duplicate Entry", "This league already exists for the selected country.");
            return;
        }
        
        // Save league
        try {
            League league = new League(selectedCountry.getIsoCode(), name, hierarchyLevel);
            dataManager.saveLeague(league);
            refreshLeagueList();
            
            // Clear input fields
            nameField.clear();
            hierarchyLevelSpinner.getValueFactory().setValue(1);
        } catch (Exception e) {
            showAlert("Error", "Failed to save league: " + e.getMessage());
        }
    }
    
    private void deleteLeague(League league) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete " + league.getName() + "?");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                dataManager.deleteLeague(league.getId());
                refreshLeagueList();
            } catch (Exception e) {
                showAlert("Error", "Failed to delete league: " + e.getMessage());
            }
        }
    }
    
    private void refreshLeagueList() {
        leagueData.clear();
        leagueData.addAll(dataManager.getAllLeagues());
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
} 