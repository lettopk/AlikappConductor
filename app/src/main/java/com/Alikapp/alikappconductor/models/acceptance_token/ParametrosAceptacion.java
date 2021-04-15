package com.Alikapp.alikappconductor.models.acceptance_token;

public class ParametrosAceptacion {

    private String acceptance_token;
    private String permalink;
    private String type;

    public String getAcceptance_token() {
        return acceptance_token;
    }

    public void setAcceptance_token(String acceptance_token) {
        this.acceptance_token = acceptance_token;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
