package com.epmas;

import java.util.ArrayList;
import java.util.List;

/**
 * CHAPTER 4: Polymorphism (Dynamic Binding & Interfaces)
 * - Upcasting & Collections: Stores both general Asset objects and specialized 
 *   CryptoAsset child objects inside a single unified List<Asset> collection.
 * - Type Introspection: Navigates the inheritance hierarchy safely using the 
 *   instanceof operator to check elements before attempting specialized downcasting operations.
 * 
 * CHAPTER 5: Exception Handling (The Crash-Prevention Shield)
 * - Custom Assertions: Throws InvalidAssetException if asset prices, quantities,
 *   or descriptors violate financial ledger rules.
 */
public class PortfolioManager {

    // CHAPTER 4: Unified collection containing upcasted Asset base and derived instances
    private final List<Asset> holdings;

    public PortfolioManager() {
        this.holdings = new ArrayList<>();
    }

    /**
     * CHAPTER 5: Throws InvalidAssetException on validation failures
     */
    public void addAsset(Asset asset) throws InvalidAssetException {
        if (asset == null) {
            throw new InvalidAssetException("Asset cannot be null.");
        }
        
        // Data boundaries checks
        if (asset.getSymbol() == null || asset.getSymbol().trim().isEmpty()) {
            throw new InvalidAssetException("Asset symbol cannot be null or empty.");
        }
        if (asset.getName() == null || asset.getName().trim().isEmpty()) {
            throw new InvalidAssetException("Asset name cannot be null or empty.");
        }
        if (asset.getPrice() < 0.0) {
            throw new InvalidAssetException(String.format(
                    "Invalid GPA/Price Boundary Check failed. Price must be positive. Provided: %.2f for %s", 
                    asset.getPrice(), asset.getSymbol()));
        }
        if (asset.getQuantity() < 0.0) {
            throw new InvalidAssetException(String.format(
                    "Invalid Quantity Boundary Check failed. Quantity must be non-negative. Provided: %.4f for %s", 
                    asset.getQuantity(), asset.getSymbol()));
        }

        // Additional checked exception trigger for crypto specific rules
        if (asset instanceof CryptoAsset) {
            CryptoAsset crypto = (CryptoAsset) asset;
            if (crypto.getBlockchainNetwork() == null || crypto.getBlockchainNetwork().trim().isEmpty()) {
                throw new InvalidAssetException("Cryptocurrency asset must have a valid blockchain network.");
            }
        }

        holdings.add(asset);
        System.out.println("[PortfolioManager] Successfully validated and added: " + asset.getSymbol());
    }

    // Retrieve active list of holdings
    public List<Asset> getHoldings() {
        return holdings;
    }

    // Clear active holdings in memory
    public void clearHoldings() {
        holdings.clear();
    }

    /**
     * Calculates the total value of the portfolio.
     */
    public double calculateTotalPortfolioValue() {
        double total = 0.0;
        for (Asset asset : holdings) {
            total += asset.getMarketValue(); // Polymorphic behavior of getMarketValue() or price * quantity
        }
        return total;
    }

    /**
     * CHAPTER 4: Polymorphic reports
     * - Demonstrates Dynamic Method Dispatch by calling toString() on the base references,
     *   which dynamically executes the child overridden toString() at runtime.
     * - Demonstrates Type Introspection by using instanceof to safely check and downcast.
     */
    public void printPolymorphicReport() {
        System.out.println("\n======================================================================");
        System.out.println("                 EPMAS POLYMORPHIC HOLDINGS REPORT                     ");
        System.out.println("======================================================================");
        
        for (Asset asset : holdings) {
            // CHAPTER 4: Dynamic Method Dispatch: JVM resolves toString() dynamically at runtime
            System.out.println(asset.toString());
            
            // CHAPTER 4: Type Introspection: safe inspection of inheritance levels using instanceof
            if (asset instanceof CryptoAsset) {
                // Downcast safely to access subclass behaviors
                CryptoAsset crypto = (CryptoAsset) asset;
                System.out.println("   --> [Type Introspection Detected Crypto] Network Node Routing: " 
                                   + crypto.getBlockchainNetwork().toUpperCase() + " Network Node.");
            } else {
                System.out.println("   --> [Type Introspection Detected Base] Traditional Equity/Fiat Asset.");
            }
        }
        System.out.println("----------------------------------------------------------------------");
        System.out.println(String.format("Total Aggregated Portfolio Assets Valuation: $%,.2f", calculateTotalPortfolioValue()));
        System.out.println("======================================================================\n");
    }
}
