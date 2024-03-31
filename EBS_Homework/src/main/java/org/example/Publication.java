package org.example;


import org.json.simple.JSONObject;

public class Publication {
    private String company = null;
    private String value = null;
    private String drop = null;
    private String variation = null;
    private String date = null;

    public Publication(String company, String value, String drop, String variation, String date) {
        this.company = company;
        this.value = value;
        this.drop = drop;
        this.variation = variation;
        this.date = date;
    }

    public JSONObject pubToJson(){
        JSONObject pubDetails = new JSONObject();
        pubDetails.put("company", this.company);
        pubDetails.put("value", this.value);
        pubDetails.put("drop", this.drop);
        pubDetails.put("variation", this.variation);
        pubDetails.put("date", this.date);
        return pubDetails;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDrop() {
        return drop;
    }

    public void setDrop(String drop) {
        this.drop = drop;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
