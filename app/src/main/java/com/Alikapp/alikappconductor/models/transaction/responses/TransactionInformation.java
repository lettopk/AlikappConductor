package com.Alikapp.alikappconductor.models.transaction.responses;

import com.paypal.android.sdk.payments.PaymentMethodActivity;

public class TransactionInformation {

    private String id;
    private PaymentMethod payment_method;
    private String status;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
