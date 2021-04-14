package com.Alikapp.alikappconductor.models.creditCardToken;

public class CreditCardTokenizar {

    private String number;
    private String cvc;
    private String exp_month;
    private String exp_year;
    private String card_holder;

    public CreditCardTokenizar(String number, String cvc, String exp_month, String exp_year, String card_holder) {
        this.number = number;
        this.cvc = cvc;
        this.exp_month = exp_month;
        this.exp_year = exp_year;
        this.card_holder = card_holder;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public String getExp_month() {
        return exp_month;
    }

    public void setExp_month(String exp_month) {
        this.exp_month = exp_month;
    }

    public String getExp_year() {
        return exp_year;
    }

    public void setExp_year(String exp_year) {
        this.exp_year = exp_year;
    }

    public String getCard_holder() {
        return card_holder;
    }

    public void setCard_holder(String card_holder) {
        this.card_holder = card_holder;
    }
}
