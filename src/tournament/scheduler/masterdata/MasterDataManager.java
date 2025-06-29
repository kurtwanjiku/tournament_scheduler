package tournament.scheduler.masterdata;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MasterDataManager {
    private static final String DB_URL = "jdbc:sqlite:tournament.db";
    
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading SQLite JDBC driver: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    public MasterDataManager() {
        initializeDatabase();
    }
    
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Create countries table if it doesn't exist
            String createCountriesTable = """
                CREATE TABLE IF NOT EXISTS countries (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    iso_code TEXT UNIQUE NOT NULL,
                    name TEXT UNIQUE NOT NULL
                )
            """;
            
            // Create leagues table if it doesn't exist
            String createLeaguesTable = """
                CREATE TABLE IF NOT EXISTS leagues (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    country_code TEXT NOT NULL,
                    name TEXT NOT NULL,
                    hierarchy_level INTEGER NOT NULL,
                    UNIQUE(country_code, name),
                    FOREIGN KEY(country_code) REFERENCES countries(iso_code)
                )
            """;
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createCountriesTable);
                stmt.execute(createLeaguesTable);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    // Country management methods
    public void saveCountry(Country country) {
        String sql = "INSERT INTO countries (iso_code, name) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, country.getIsoCode());
            pstmt.setString(2, country.getName());
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    country.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save country", e);
        }
    }
    
    public List<Country> getAllCountries() {
        List<Country> countries = new ArrayList<>();
        String sql = "SELECT * FROM countries ORDER BY name";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                countries.add(new Country(
                    rs.getInt("id"),
                    rs.getString("iso_code"),
                    rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get countries", e);
        }
        
        return countries;
    }
    
    public void deleteCountry(int id) {
        String sql = "DELETE FROM countries WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete country", e);
        }
    }
    
    public boolean isIsoCodeExists(String isoCode) {
        String sql = "SELECT COUNT(*) FROM countries WHERE iso_code = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, isoCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check ISO code existence", e);
        }
    }
    
    public boolean isCountryNameExists(String name) {
        String sql = "SELECT COUNT(*) FROM countries WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check country name existence", e);
        }
    }
    
    // League management methods
    public void saveLeague(League league) {
        String sql = "INSERT INTO leagues (country_code, name, hierarchy_level) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, league.getCountryCode());
            pstmt.setString(2, league.getName());
            pstmt.setInt(3, league.getHierarchyLevel());
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    league.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save league", e);
        }
    }
    
    public List<League> getAllLeagues() {
        List<League> leagues = new ArrayList<>();
        String sql = "SELECT * FROM leagues ORDER BY country_code, hierarchy_level";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                leagues.add(new League(
                    rs.getInt("id"),
                    rs.getString("country_code"),
                    rs.getString("name"),
                    rs.getInt("hierarchy_level")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get leagues", e);
        }
        
        return leagues;
    }
    
    public void deleteLeague(int id) {
        String sql = "DELETE FROM leagues WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete league", e);
        }
    }
    
    public boolean isLeagueExists(String countryCode, String name) {
        String sql = "SELECT COUNT(*) FROM leagues WHERE country_code = ? AND name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, countryCode);
            pstmt.setString(2, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check league existence", e);
        }
    }
} 