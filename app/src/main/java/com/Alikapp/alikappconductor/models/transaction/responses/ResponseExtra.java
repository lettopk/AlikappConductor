package com.Alikapp.alikappconductor.models.transaction.responses;

public class ResponseExtra {

    private String async_payment_url;
    private String business_agreement_code;
    private String payment_intention_identifier;

    public String getAsync_payment_url() {
        return async_payment_url;
    }

    public void setAsync_payment_url(String async_payment_url) {
        this.async_payment_url = async_payment_url;
    }

    public String getBusiness_agreement_code() {
        return business_agreement_code;
    }

    public void setBusiness_agreement_code(String business_agreement_code) {
        this.business_agreement_code = business_agreement_code;
    }

    public String getPayment_intention_identifier() {
        return payment_intention_identifier;
    }

    public void setPayment_intention_identifier(String payment_intention_identifier) {
        this.payment_intention_identifier = payment_intention_identifier;
    }
}



