package uk.ac.ed.inf.PizzaDronz.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CreditCardInformation {
    private String creditCardNumber;
    private String creditCardExpiry;  // Format: "MM/YY"
    private String cvv;

    // Default constructor for JSON deserialization
    public CreditCardInformation() {
    }

    // Constructor
    public CreditCardInformation(String creditCardNumber, String creditCardExpiry, String cvv) {
        this.creditCardNumber = creditCardNumber;
        this.creditCardExpiry = creditCardExpiry;
        this.cvv = cvv;
    }

    // Getters and Setters
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getCreditCardExpiry() {
        return creditCardExpiry;
    }

    public void setCreditCardExpiry(String creditCardExpiry) {
        this.creditCardExpiry = creditCardExpiry;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    // Validation methods
    @JsonIgnore
    public boolean isValid() {
        return isValidCreditCardNumber() && 
               isValidExpiryDate() && 
               isValidCvv();
    }

    @JsonIgnore
    private boolean isValidCreditCardNumber() {
        return creditCardNumber != null && 
               creditCardNumber.matches("\\d{16}");  // Exactly 16 digits
    }

    @JsonIgnore
    private boolean isValidExpiryDate() {
        if (creditCardExpiry == null || !creditCardExpiry.matches("\\d{2}/\\d{2}")) {
            return false;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth expiryDate = YearMonth.parse(creditCardExpiry, formatter);
            YearMonth currentDate = YearMonth.now();
            
            return expiryDate.isAfter(currentDate);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @JsonIgnore
    private boolean isValidCvv() {
        return cvv != null && 
               cvv.matches("\\d{3}");  // Exactly 3 digits
    }
} 