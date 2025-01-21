package uk.ac.ed.inf.PizzaDronz.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

public class OrderUnitTests {
    
    private CreditCardInformation creditCardInformation;

    @BeforeEach
    void setUp() {
        creditCardInformation = new CreditCardInformation();
    }


    @Test
    void testCreditCardNumber_InvalidNumber_TooLong() {
        creditCardInformation.setCreditCardNumber("12345678987654321");        
        assertFalse(creditCardInformation.isValidCreditCardNumber());
    }

    @Test
    void testCreditCardNumber_InvalidNumber_TooShort() {
        creditCardInformation.setCreditCardNumber("123456789876543");        
        assertFalse(creditCardInformation.isValidCreditCardNumber());
    }

    @Test
    void testCreditCardNumber_InvalidNumber_NotNumeric() {
        creditCardInformation.setCreditCardNumber("123456789876543A");        
        assertFalse(creditCardInformation.isValidCreditCardNumber());
    }

    @Test
    void testCreditCardNumber_ValidNumber() {
        creditCardInformation.setCreditCardNumber("1234567898765432");        
        assertTrue(creditCardInformation.isValidCreditCardNumber());
    }

    @Test
    void testCVV_InvalidCVV_TooShort() {
        creditCardInformation.setCvv("13");        
        assertFalse(creditCardInformation.isValidCvv());
    }

    @Test
    void testCVV_InvalidCVV_NotNumeric() {
        creditCardInformation.setCvv("123A");        
        assertFalse(creditCardInformation.isValidCvv());
    }   

    @Test
    void testCVV_ValidCVV() {
        creditCardInformation.setCvv("123");        
        assertTrue(creditCardInformation.isValidCvv());
    }

    @Test
    void testExpiryDate_InvalidExpiryDate_InThePast() {
        creditCardInformation.setCreditCardExpiry("01/24");        
        assertFalse(creditCardInformation.isValidExpiryDate());
    }

    @Test
    void testExpiryDate_ValidExpireyDate() {
        creditCardInformation.setCreditCardExpiry("01/26");        
        assertTrue(creditCardInformation.isValidExpiryDate());
    }
}