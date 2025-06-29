package tournament.scheduler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:tournament.db";
    
    static {
        try {
            // Register JDBC driver
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading SQLite JDBC driver: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    public DatabaseManager() {
        initializeDatabase();
    }
    
    private void initializeDatabase() {
        String createTableSQL = 
            "CREATE TABLE IF NOT EXISTS teams (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL UNIQUE" +
            ")";
            
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    public void saveTeam(Team team) {
        String sql = "INSERT INTO teams (name) VALUES (?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, team.getName());
            pstmt.executeUpdate();
            
            // Get the auto-generated ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    team.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving team: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    public List<Team> getAllTeams() {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT id, name FROM teams";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                teams.add(new Team(
                    rs.getInt("id"),
                    rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving teams: " + e.getMessage());
            throw new RuntimeException(e);
        }
        
        return teams;
    }
    
    public void deleteAllTeams() {
        String sql = "DELETE FROM teams";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error deleting teams: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
} 