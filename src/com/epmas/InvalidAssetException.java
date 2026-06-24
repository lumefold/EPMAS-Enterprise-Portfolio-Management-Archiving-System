package com.epmas;

/**
 * CHAPTER 5: Exception Handling (The Crash-Prevention Shield)
 * Technical Constraints: Custom Assertions
 * 
 * This checked exception class extends the foundational Exception tree.
 * It is thrown when input validation checks for a financial asset fail
 * (e.g., negative prices, negative quantities, or invalid asset names).
 */
public class InvalidAssetException extends Exception {
    
    // Default constructor for general exceptions
    public InvalidAssetException() {
        super("Invalid asset data provided.");
    }

    // Constructor that accepts a detailed error message
    public InvalidAssetException(String message) {
        super(message);
    }

    // Constructor that accepts both a detailed message and a cause
    public InvalidAssetException(String message, Throwable cause) {
        super(message, cause);
    }
}
