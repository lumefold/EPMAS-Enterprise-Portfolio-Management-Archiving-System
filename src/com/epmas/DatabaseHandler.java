package com.epmas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * CHAPTER 7: Database Programming (JDBC Persistence Ledger)
 * This utility class implements the complete 4-step JDBC workflow:
 * 1. Load database driver properties dynamically (Class.forName).
 * 2. Establish database connections securely (DriverManager.getConnection).
 * 3. Execute queries and statements (Statement & PreparedStatement).
 * 4. Process query results (ResultSet cursors mapping values back to models).
 * 
 * Secure practices: Uses parameterized PreparedStatement to prevent SQL injection.
 * Resilience: Implements defensive catches with a robust fallback to a simulated
 * in-memory SQL transaction ledger if database drivers or servers are offline.
 */
public class DatabaseHandler {

    // Target database URL for SQLite (local file database)
    private static final String DB_URL = "jdbc:sqlite:epmas_portfolio.db";
    private static final String DRIVER_CLASS = "org.sqlite.JDBC";
    
    // In-memory simulated database representing the transaction ledger if JDBC is offline
    private static final List<Asset> mockDatabaseLedger = new ArrayList<>();
    private static boolean useSimulationFallback = false;

    /**
     * CHAPTER 7.1: Driver Initialization and Setup (Step 1 & 2 of JDBC)
     * Dynamically loads the JDBC driver and creates the schema tables.
     */
    public static void initializeDatabase() {
        System.out.println("\n--- CHAPTER 7: JDBC DATABASE INITIALIZATION ---");
        
        try {
            // STEP 1: Load database driver dynamically
            System.out.println("[JDBC Step 1] Dynamically loading driver: " + DRIVER_CLASS);
            Class.forName(DRIVER_CLASS);
            
            // STEP 2: Establish connection
            System.out.println("[JDBC Step 2] Connecting to database: " + DB_URL);
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 Statement stmt = conn.createStatement()) {
                
                // STEP 3: Execute SQL statement to create the tables
                String sql = "CREATE TABLE IF NOT EXISTS assets (" +
                             "symbol TEXT PRIMARY KEY, " +
                             "name TEXT NOT NULL, " +
                             "price REAL NOT NULL, " +
                             "quantity REAL NOT NULL, " +
                             "type TEXT NOT NULL, " +
                             "special_field TEXT" +
                             ");";
                stmt.execute(sql);
                System.out.println("[JDBC Setup] SQLite Database tables initialized successfully.");
                useSimulationFallback = false;
            }
        } catch (ClassNotFoundException e) {
            System.out.println("[JDBC Warning] SQLite Driver class not found in classpath.");
            System.out.println("[JDBC Warning] EPMAS will automatically fall back to the In-Memory SQL Simulated Ledger.");
            useSimulationFallback = true;
        } catch (SQLException e) {
            System.out.println("[JDBC Warning] SQL Database Connection failed: " + e.getMessage());
            System.out.println("[JDBC Warning] EPMAS will automatically fall back to the In-Memory SQL Simulated Ledger.");
            useSimulationFallback = true;
        }
        
        if (useSimulationFallback) {
            System.out.println("[JDBC Simulator] In-Memory Ledger Database is ONLINE and ready.");
            // Populate mock database with some initial seed assets
            mockDatabaseLedger.clear();
            mockDatabaseLedger.add(new Asset("USD", "US Dollar Cash Seed", 1.0, 1000.0));
            mockDatabaseLedger.add(new CryptoAsset("BTC", "Bitcoin Seed", 65000.0, 0.05, "Bitcoin Network"));
        }
    }

    /**
     * CHAPTER 7.2: Parameterized PreparedStatement Updates
     * Inserts or replaces assets in the database securely to avoid SQL Injection.
     */
    public static void saveAssetsToDatabase(List<Asset> assets) {
        System.out.println("\n--- CHAPTER 7: SAVING ASSETS VIA JDBC ---");
        
        if (useSimulationFallback) {
            System.out.println("[JDBC Simulator] Intercepting request to insert " + assets.size() + " assets.");
            for (Asset asset : assets) {
                // Simulate parameterized compilation
                System.out.println("[JDBC Simulator SQL Execute] INSERT OR REPLACE INTO assets(symbol, name, price, quantity, type, special_field) "
                                   + "VALUES(?, ?, ?, ?, ?, ?);");
                System.out.println("   -> Param 1 (Symbol): " + asset.getSymbol());
                System.out.println("   -> Param 2 (Name): " + asset.getName());
                System.out.println("   -> Param 3 (Price): " + asset.getPrice());
                System.out.println("   -> Param 4 (Quantity): " + asset.getQuantity());
                
                String type = (asset instanceof CryptoAsset) ? "CRYPTO" : "BASE";
                String special = (asset instanceof CryptoAsset) ? ((CryptoAsset) asset).getBlockchainNetwork() : "N/A";
                System.out.println("   -> Param 5 (Type): " + type);
                System.out.println("   -> Param 6 (Special Field): " + special);
                
                // Update local simulation repository
                // Remove existing if symbol matches to simulate primary key replace
                mockDatabaseLedger.removeIf(a -> a.getSymbol().equalsIgnoreCase(asset.getSymbol()));
                mockDatabaseLedger.add(asset);
            }
            System.out.println("[JDBC Simulator] Simulated database commit complete.");
            return;
        }

        // Standard JDBC Pipeline execution
        String insertSQL = "INSERT OR REPLACE INTO assets(symbol, name, price, quantity, type, special_field) VALUES(?, ?, ?, ?, ?, ?);";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            
            conn.setAutoCommit(false); // Enable manual transaction block checks
            
            for (Asset asset : assets) {
                // CHAPTER 7: Exclusively set parameters securely via PreparedStatement
                pstmt.setString(1, asset.getSymbol());
                pstmt.setString(2, asset.getName());
                pstmt.setDouble(3, asset.getPrice());
                pstmt.setDouble(4, asset.getQuantity());
                
                if (asset instanceof CryptoAsset) {
                    pstmt.setString(5, "CRYPTO");
                    pstmt.setString(6, ((CryptoAsset) asset).getBlockchainNetwork());
                } else {
                    pstmt.setString(5, "BASE");
                    pstmt.setString(6, "N/A");
                }
                pstmt.addBatch(); // Batch execution for optimized speed
            }
            pstmt.executeBatch();
            conn.commit(); // Commit transaction safely
            System.out.println("[JDBC] Successfully inserted/updated " + assets.size() + " records in SQLite DB.");
            
        } catch (SQLException e) {
            System.err.println("[JDBC Error] Failed to persist data: " + e.getMessage());
        }
    }

    /**
     * CHAPTER 7.3: Reading database records using ResultSet cursor mapping.
     */
    public static List<Asset> loadAssetsFromDatabase() {
        System.out.println("\n--- CHAPTER 7: LOADING ASSETS VIA JDBC ---");
        List<Asset> assets = new ArrayList<>();
        
        if (useSimulationFallback) {
            System.out.println("[JDBC Simulator] Reading simulated SELECT * FROM assets; query.");
            System.out.println("[JDBC Simulator] Initializing mock ResultSet cursor loop.");
            for (Asset asset : mockDatabaseLedger) {
                System.out.println("[JDBC Simulator Cursor Row] Symbol: " + asset.getSymbol() + " | Name: " + asset.getName());
                assets.add(asset);
            }
            System.out.println("[JDBC Simulator] Read " + assets.size() + " records from simulated ResultSet.");
            return assets;
        }

        // Standard JDBC Pipeline execution
        String querySQL = "SELECT symbol, name, price, quantity, type, special_field FROM assets;";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(querySQL);
             ResultSet rs = pstmt.executeQuery()) {
            
            // CHAPTER 7: Extract structural records systematically using ResultSet cursor loop
            while (rs.next()) {
                String symbol = rs.getString("symbol");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                double quantity = rs.getDouble("quantity");
                String type = rs.getString("type");
                String special = rs.getString("special_field");
                
                if ("CRYPTO".equalsIgnoreCase(type)) {
                    assets.add(new CryptoAsset(symbol, name, price, quantity, special));
                } else {
                    assets.add(new Asset(symbol, name, price, quantity));
                }
            }
            System.out.println("[JDBC] Successfully retrieved " + assets.size() + " records from SQLite DB.");
            
        } catch (SQLException e) {
            System.err.println("[JDBC Error] Failed to query records: " + e.getMessage());
        }
        return assets;
    }
}
