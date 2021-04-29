package com.Alikapp.alikappconductor.models.transaction.responses;

import com.paypal.android.sdk.payments.PaymentMethodActivity;

public class TransactionInformation {

    private String id;
    private int amount_in_cents;
    private PaymentMethod payment_method;
    private String status;
    private String status_message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAmount_in_cents() {
        return amount_in_cents;
    }

    public void setAmount_in_cents(int amount_in_cents) {
        this.amount_in_cents = amount_in_cents;
    }

    public PaymentMethod getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(PaymentMethod payment_method) {
        this.payment_method = payment_method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }
}
