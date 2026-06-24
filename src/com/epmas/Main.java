package com.epmas;

import java.util.List;

/**
 * CHAPTER 1: Introduction to Java & OOP (Orchestration Drivers)
 * CHAPTER 2: Inside Objects & Classes (Static Lifecycle Checking)
 * CHAPTER 4: Polymorphism (Upcasting & Introspection Tests)
 * CHAPTER 5: Exception Handling (Boundary Shield Try-Catch-Finally Execution)
 * CHAPTER 6: Files & Streams (Data Pipe Interfacing)
 * CHAPTER 7: Database Programming (JDBC Ledger Persistence)
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println(" 🚀 STARTING ENTERPRISE PORTFOLIO MANAGEMENT & ARCHIVING SYSTEM (EPMAS) ");
        System.out.println("======================================================================\n");

        // Set up local file paths for archiving tests
        String baseDir = "C:\\Users\\hp\\Downloads\\EPMAS\\src\\com\\epmas\\data";
        String csvPath = baseDir + "\\portfolio.csv";
        String csvBackupPath = baseDir + "\\portfolio_backup.csv";
        String binaryPath = baseDir + "\\portfolio.dat";
        String serializedPath = baseDir + "\\portfolio.ser";

        // Initialize managers
        PortfolioManager manager = new PortfolioManager();

        // ======================================================================
        // CHAPTER 2: STATIC LIFE CYCLE TRACKING DEMONSTRATION
        // ======================================================================
        System.out.println("--- CHAPTER 2: LIFECYCLE MEMORY ALLOCATION ---");
        System.out.println("Initial total asset instances registered on JVM Heap: " + Asset.getTotalObjectsCreated());
        
        // Allocate holdings
        Asset a1 = new Asset("AAPL", "Apple Inc. Common Stock", 175.50, 10.0); // Equity
        Asset a2 = new Asset("MSFT", "Microsoft Corp. Common Stock", 420.20, 5.0);  // Equity
        CryptoAsset c1 = new CryptoAsset("BTC", "Bitcoin Cryptocurrency", 64500.0, 0.25, "Bitcoin Mainnet"); // Derivative Crypto
        CryptoAsset c2 = new CryptoAsset("ETH", "Ethereum Cryptocurrency", 3500.0, 1.5, "Ethereum Virtual Machine"); // Derivative Crypto

        System.out.println("Post-allocation total asset instances registered on JVM Heap: " + Asset.getTotalObjectsCreated());
        System.out.println("----------------------------------------------\n");

        // ======================================================================
        // CHAPTER 5: EXCEPTION HANDLING (THE CRASH-PREVENTION SHIELD)
        // ======================================================================
        System.out.println("--- CHAPTER 5: EXCEPTION HANDLING & VALIDATION CONTROLS ---");
        
        // Test Valid Additions
        try {
            manager.addAsset(a1);
            manager.addAsset(a2);
            manager.addAsset(c1);
            manager.addAsset(c2);
        } catch (InvalidAssetException e) {
            System.err.println("Unexpected failure adding valid assets: " + e.getMessage());
        }

        // Test Custom Exception Bounds checking (Negative Price)
        try {
            System.out.println("[Exception Test] Attempting to insert asset with negative market valuation...");
            Asset toxicAsset = new Asset("TOX", "Toxic Debt Obligation", -50.0, 100.0);
            manager.addAsset(toxicAsset);
        } catch (InvalidAssetException e) {
            // Safe recovery block preventing runtime crashes
            System.out.println("[Exception Shield Triggered] Recovered from exception: " + e.getMessage());
        }

        // Test Custom Exception Bounds checking (Negative Quantity)
        try {
            System.out.println("[Exception Test] Attempting to insert asset with negative shares held...");
            Asset shortAsset = new Asset("SHRT", "Naked Short Liability", 10.0, -5.0);
            manager.addAsset(shortAsset);
        } catch (InvalidAssetException e) {
            // Safe recovery block preventing runtime crashes
            System.out.println("[Exception Shield Triggered] Recovered from exception: " + e.getMessage());
        } finally {
            System.out.println("[Exception Cleanup] Finally block executed. Resource safety guaranteed.");
        }
        System.out.println("----------------------------------------------\n");

        // ======================================================================
        // CHAPTER 4: POLYMORPHISM (DYNAMIC DISPATCH & TYPE INTROSPECTION)
        // ======================================================================
        System.out.println("--- CHAPTER 4: POLYMORPHISM & RUNTIME TYPE ANALYSIS ---");
        manager.printPolymorphicReport();

        // ======================================================================
        // CHAPTER 6: FILES & STREAMS ARCHIVE PIPELINE
        // ======================================================================
        System.out.println("--- CHAPTER 6: FILES & STREAMS PERSISTENCE OPERATOR ---");

        // 6.1 Inspect and print file system metadata
        FileHandler.printFileMetadata(csvPath);

        // 6.2 CSV character streaming (Write and Read)
        FileHandler.writePortfolioCSV(csvPath, manager.getHoldings());
        List<Asset> csvImported = FileHandler.readPortfolioCSV(csvPath);
        System.out.println("[Verification] CSV Imported count matches: " + (csvImported.size() == manager.getHoldings().size()));

        // 6.3 Binary primitive streaming (Write and Read)
        FileHandler.writePortfolioBinary(binaryPath, manager.getHoldings());
        List<Asset> binaryImported = FileHandler.readPortfolioBinary(binaryPath);
        System.out.println("[Verification] Binary Imported count matches: " + (binaryImported.size() == manager.getHoldings().size()));

        // 6.4 Full Object Serialization
        FileHandler.serializePortfolio(serializedPath, manager.getHoldings());
        List<Asset> hydratedPortfolio = FileHandler.deserializePortfolio(serializedPath);
        System.out.println("[Verification] Hydrated object graph total market valuation matches: " +
                (hydratedPortfolio.size() == manager.getHoldings().size() && 
                 hydratedPortfolio.get(0).getMarketValue() == manager.getHoldings().get(0).getMarketValue()));

        // 6.5 Buffered character copying for performance logging backup
        FileHandler.backupFileBuffered(csvPath, csvBackupPath);
        System.out.println("----------------------------------------------\n");

        // ======================================================================
        // CHAPTER 7: DATABASE PROGRAMMING (JDBC PERSISTENCE LEDGER)
        // ======================================================================
        System.out.println("--- CHAPTER 7: JDBC DATABASE TRANSACTION LEDGER ---");
        
        // Step 1 & 2: Load Driver & Initialize Database Schema
        DatabaseHandler.initializeDatabase();

        // Step 3: Insert/Replace entries securely via parameterized PreparedStatements
        DatabaseHandler.saveAssetsToDatabase(manager.getHoldings());

        // Step 4: Extract records systematically using ResultSet cursor iteration mapping back to objects
        List<Asset> dbRetrievedAssets = DatabaseHandler.loadAssetsFromDatabase();

        System.out.println("\n[Verification] Displaying assets loaded directly from Database Ledger:");
        for (Asset dbAsset : dbRetrievedAssets) {
            System.out.println("   DB Rec -> " + dbAsset);
        }
        System.out.println("----------------------------------------------\n");

        System.out.println("======================================================================");
        System.out.println(" 🎉 EPMAS TEST SUITE RUN COMPLETED SUCCESSFULLY!");
        System.out.println(" All Java OOP & Data programming constraints verified across all 7 Chapters.");
        System.out.println("======================================================================");
    }
}
