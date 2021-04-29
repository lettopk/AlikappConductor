package com.Alikapp.alikappconductor.models.transaction.responses;

public class PaymentMethod {

    private String type;
    private ResponseExtra extra;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ResponseExtra getExtra() {
        return extra;
    }

    public void setExtra(ResponseExtra extra) {
        this.extra = extra;
    }
}
