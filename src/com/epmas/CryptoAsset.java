package com.epmas;

/**
 * CHAPTER 3: Inheritance (Hierarchical Domain Specialization)
 * - Establishes an explicit IS-A relationship by extending the base Asset parent class.
 * - Introduces specialized domain-specific fields unique to the derived class (blockchainNetwork).
 * - Forces the child constructor to pass core data fields directly up the tree using 
 *   an explicit super(...) resource-routing call.
 * 
 * CHAPTER 4: Polymorphism (Dynamic Binding & Interfaces)
 * - Overrides the standard toString() method.
 * - Allows JVM dynamic method dispatch to automatically call this child version at runtime.
 */
public class CryptoAsset extends Asset {

    private static final long serialVersionUID = 1L; // Maintain serialization compatibility

    // CHAPTER 3: Specialized attribute unique to the derived child class
    private String blockchainNetwork;

    /**
     * CHAPTER 3: Child Constructor
     * Uses super(...) to route initialization values up the tree to the parent Asset class constructor.
     */
    public CryptoAsset(String symbol, String name, double price, double quantity, String blockchainNetwork) {
        super(symbol, name, price, quantity); // Route core data fields up the inheritance tree
        this.blockchainNetwork = blockchainNetwork;
    }

    // Accessors and mutators for child-specific features
    public String getBlockchainNetwork() {
        return blockchainNetwork;
    }

    public void setBlockchainNetwork(String blockchainNetwork) {
        this.blockchainNetwork = blockchainNetwork;
    }

    /**
     * CHAPTER 4: Overriding the standard .toString() method.
     * Demonstrates dynamic binding: when iterating over a collection of Asset objects,
     * this specific method is resolved at runtime if the concrete instance is a CryptoAsset.
     */
    @Override
    public String toString() {
        return String.format("CryptoAsset Model [Symbol: %s, Name: %s, Price: $%.2f, Quantity: %.4f, Network: %s, Market Value: $%.2f]",
                getSymbol(), getName(), getPrice(), getQuantity(), blockchainNetwork, getMarketValue());
    }
}
