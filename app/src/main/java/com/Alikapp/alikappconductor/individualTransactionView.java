package com.Alikapp.alikappconductor;

public class individualTransactionView {


    private int imageMetodo;
    private String fechaTransaction;
    private String idTransaction;

    public individualTransactionView(int imageMetodo, String fechaTransaction, String idTransaction) {
        this.imageMetodo = imageMetodo;
        this.fechaTransaction = fechaTransaction;
        this.idTransaction = idTransaction;
    }

    public int getImageMetodo() {
        return imageMetodo;
    }

    public void setImageMetodo(int imageMetodo) {
        this.imageMetodo = imageMetodo;
    }

    public String getFechaTransaction() {
        return fechaTransaction;
    }

    public void setFechaTransaction(String fechaTransaction) {
        this.fechaTransaction = fechaTransaction;
    }

    public String getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(String idTransaction) {
        this.idTransaction = idTransaction;
    }
}

