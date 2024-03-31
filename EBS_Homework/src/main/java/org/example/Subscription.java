package org.example;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Subscription {
    private List<String> company = null;
    private List<String> value = null;
    private List<String> drop = null;
    private List<String> variation = null;
    private List<String> date = null;

    public Subscription(){}

    public JSONObject subToJson(){
        JSONObject subDetails = new JSONObject();
        if (company!=null){
            subDetails.put("company", this.company);
        }
        if (value!=null){
            subDetails.put("value", this.value);
        }
        if (drop!=null){
            subDetails.put("drop", this.drop);
        }
        if (variation!=null){
            subDetails.put("variation", this.variation);
        }
        if (date!=null){
            subDetails.put("date", this.date);
        }
        return subDetails;
    }

    public List<String> getCompany() {
        return company;
    }

    public void setCompany(List<String> company) {
        this.company = new ArrayList<>(company);
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = new ArrayList<>(value);
    }

    public List<String> getDrop() {
        return drop;
    }

    public void setDrop(List<String> drop) {
        this.drop = new ArrayList<>(drop);
    }

    public List<String> getVariation() {
        return variation;
    }

    public void setVariation(List<String> variation) {
        this.variation = new ArrayList<>(variation);
    }

    public List<String> getDate() {
        return date;
    }

    public void setDate(List<String> date) {
        this.date = new ArrayList<>(date);
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "company=" + company +
                ", value=" + value +
                ", drop=" + drop +
                ", variation=" + variation +
                ", date=" + date +
                '}';
    }
}
