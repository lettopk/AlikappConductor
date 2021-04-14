package com.Alikapp.alikappconductor.models.transaction;

import java.util.HashMap;

public class Transaction {

    private String acceptance_token;
    private int amount_in_cents;
    private String currency= "COP";
    private String customer_email;
    private String reference;
    private HashMap payment_method;

    public Transaction(String acceptance_token, int amount_in_cents, String customer_email, String reference, HashMap payment_method) {
        this.acceptance_token = acceptance_token;
        this.amount_in_cents = amount_in_cents;
        this.customer_email = customer_email;
        this.reference = reference;
        this.payment_method = payment_method;
    }

    public String getAcceptance_token() {
        return acceptance_token;
    }

    public void setAcceptance_token(String acceptance_token) {
        this.acceptance_token = acceptance_token;
    }

    public int getAmount_in_cents() {
        return amount_in_cents;
    }

    public void setAmount_in_cents(int amount_in_cents) {
        this.amount_in_cents = amount_in_cents;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public HashMap getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(HashMap payment_method) {
        this.payment_method = payment_method;
    }
}
