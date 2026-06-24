package com.epmas;

import java.io.Serializable;

/**
 * CHAPTER 1: Introduction to Java & OOP (Object Modeling & Architecture)
 * - Models a real-world entity cleanly as a self-contained object.
 * - Enforces strict modularity by keeping data attributes isolated from processing drivers.
 * 
 * CHAPTER 2: Inside Objects & Classes (Encapsulation, State, and Scope)
 * - Restricts access using private access modifiers on fields.
 * - Exposes fields safely using standard accessors (getters) and mutators (setters).
 * - Implements overloaded constructors using this() for safe default state initialization.
 * - Maintains a running global tally of all class instances created in memory using a static 
 *   member field variable (totalObjectsCreated) alongside a static monitoring method.
 * 
 * CHAPTER 6: Files & Streams (Data Archiving & Persistence)
 * - Implements the Serializable marker interface to support freezing/hydrating memory objects.
 */
public class Asset implements Serializable {
    
    // CHAPTER 6: Serializable Version ID to maintain class evolution compatibility
    private static final long serialVersionUID = 1L;

    // CHAPTER 2: Static member variable tracking the global lifecycle tally of all instances generated in memory
    private static int totalObjectsCreated = 0;

    // CHAPTER 2: Encapsulated private fields
    private String symbol;
    private String name;
    private double price;
    private double quantity;

    /**
     * CHAPTER 2: Overloaded default constructor.
     * Demonstrates using this() to initialize default states safely.
     */
    public Asset() {
        this("CASH", "US Dollar Cash Reserves", 1.0, 0.0);
    }

    /**
     * CHAPTER 2: Parameterized constructor.
     * Initializes state and manages the static class instance tracking.
     */
    public Asset(String symbol, String name, double price, double quantity) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        
        // CHAPTER 2: Increment the global static class counter on initialization
        totalObjectsCreated++;
    }

    // CHAPTER 2: Static monitoring method to retrieve class-level lifecycle count
    public static int getTotalObjectsCreated() {
        return totalObjectsCreated;
    }

    // CHAPTER 2: Encapsulation - Accessors and Mutators (Getters & Setters)
    
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    /**
     * Set the current price of the asset.
     * Throws IllegalArgumentException on invalid negative values.
     */
    public void setPrice(double price) {
        if (price < 0.0) {
            throw new IllegalArgumentException("Asset price cannot be negative.");
        }
        this.price = price;
    }

    public double getQuantity() {
        return quantity;
    }

    /**
     * Set the current held quantity of the asset.
     * Throws IllegalArgumentException on invalid negative values.
     */
    public void setQuantity(double quantity) {
        if (quantity < 0.0) {
            throw new IllegalArgumentException("Asset quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    // CHAPTER 1: Behavioral logic belonging directly to the Object model
    public double getMarketValue() {
        return this.price * this.quantity;
    }

    /**
     * CHAPTER 4: Polymorphism (Dynamic Binding & Interfaces)
     * - Overriding the standard toString() method.
     * - Allows JVM to resolve the runtime instance's method representation at execution time.
     */
    @Override
    public String toString() {
        return String.format("Asset Model [Symbol: %s, Name: %s, Price: $%.2f, Quantity: %.4f, Market Value: $%.2f]",
                symbol, name, price, quantity, getMarketValue());
    }
}
