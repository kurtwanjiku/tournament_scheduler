package tournament.scheduler.masterdata;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CountryManagerGUI extends Application {
    private MasterDataManager dataManager;
    private TextField isoCodeField;
    private TextField nameField;
    private TableView<Country> countryTable;
    private ObservableList<Country> countryData;
    
    @Override
    public void start(Stage primaryStage) {
        dataManager = new MasterDataManager();
        
        primaryStage.setTitle("Country Manager");
        
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
        
        // ISO Code input
        Label isoLabel = new Label("ISO Code:");
        isoCodeField = new TextField();
        isoCodeField.setPromptText("e.g., DE");
        isoCodeField.setPrefWidth(100);
        
        // Country name input
        Label nameLabel = new Label("Country Name:");
        nameField = new TextField();
        nameField.setPromptText("e.g., Germany");
        nameField.setPrefWidth(200);
        
        // Add button
        Button addButton = new Button("Add Country");
        addButton.setOnAction(e -> addCountry());
        
        // Layout input fields
        inputGrid.add(isoLabel, 0, 0);
        inputGrid.add(isoCodeField, 1, 0);
        inputGrid.add(nameLabel, 2, 0);
        inputGrid.add(nameField, 3, 0);
        inputGrid.add(addButton, 4, 0);
        
        // Create table
        countryTable = new TableView<>();
        countryData = FXCollections.observableArrayList();
        countryTable.setItems(countryData);
        
        // Create columns
        TableColumn<Country, String> isoColumn = new TableColumn<>("ISO Code");
        isoColumn.setCellValueFactory(new PropertyValueFactory<>("isoCode"));
        isoColumn.setPrefWidth(100);
        
        TableColumn<Country, String> nameColumn = new TableColumn<>("Country Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);
        
        // Add delete button column
        TableColumn<Country, Void> deleteColumn = new TableColumn<>("");
        deleteColumn.setPrefWidth(100);
        deleteColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            
            {
                deleteButton.setOnAction(e -> {
                    Country country = getTableRow().getItem();
                    if (country != null) {
                        deleteCountry(country);
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
        
        countryTable.getColumns().addAll(isoColumn, nameColumn, deleteColumn);
        
        // Add components to main layout
        mainLayout.getChildren().addAll(
            menuBar,
            new Label("Add New Country"),
            inputGrid,
            new Separator(),
            new Label("Existing Countries"),
            countryTable
        );
        
        // Load existing countries
        refreshCountryList();
        
        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void addCountry() {
        String isoCode = isoCodeField.getText().trim().toUpperCase();
        String name = nameField.getText().trim();
        
        // Validate input
        if (isoCode.isEmpty() || name.isEmpty()) {
            showAlert("Input Error", "Both ISO code and country name are required.");
            return;
        }
        
        if (isoCode.length() != 2) {
            showAlert("Input Error", "ISO code must be exactly 2 characters.");
            return;
        }
        
        // Check if already exists
        if (dataManager.isIsoCodeExists(isoCode)) {
            showAlert("Duplicate Entry", "A country with ISO code '" + isoCode + "' already exists.");
            return;
        }
        
        if (dataManager.isCountryNameExists(name)) {
            showAlert("Duplicate Entry", "A country named '" + name + "' already exists.");
            return;
        }
        
        // Save country
        try {
            Country country = new Country(isoCode, name);
            dataManager.saveCountry(country);
            refreshCountryList();
            
            // Clear input fields
            isoCodeField.clear();
            nameField.clear();
        } catch (Exception e) {
            showAlert("Error", "Failed to save country: " + e.getMessage());
        }
    }
    
    private void deleteCountry(Country country) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete " + country.getName() + "?");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                dataManager.deleteCountry(country.getId());
                refreshCountryList();
            } catch (Exception e) {
                showAlert("Error", "Failed to delete country: " + e.getMessage());
            }
        }
    }
    
    private void refreshCountryList() {
        countryData.clear();
        countryData.addAll(dataManager.getAllCountries());
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