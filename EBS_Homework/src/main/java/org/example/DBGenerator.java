package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.example.Main.*;

public class DBGenerator {
    public DBGenerator(){}
    public ArrayList<Subscription> generateSubsStructure(){
        int nrCompany = (int)Math.ceil((double) companyPercentage / 100 * numberOfSubscriptions);
        int nrValue = (int)Math.ceil((double)valuePercentage / 100 * numberOfSubscriptions);
        int nrDrop = (int)Math.ceil((double)dropPercentage / 100 * numberOfSubscriptions);
        int nrVariation = (int)Math.ceil((double)variationPercentage / 100 * numberOfSubscriptions);
        int nrDate = (int)Math.ceil((double)datePercentage / 100 * numberOfSubscriptions);

        int totalFields = nrCompany + nrValue + nrDrop + nrVariation + nrDate;
        ArrayList<Subscription> listOfSubscriptions = new ArrayList<>();
        for(int i=0; i<numberOfSubscriptions; i++){
            listOfSubscriptions.add(new Subscription());
        }
        while(totalFields != 0){
            for(int i=0; i<numberOfSubscriptions;i++){
                if (totalFields == 0) {
                    break;
                }
                if (nrCompany != 0){
                    listOfSubscriptions.get(i).setCompany(new ArrayList<>());
                    nrCompany -= 1;
                    totalFields -= 1;
                }
                else if(nrValue != 0){
                    listOfSubscriptions.get(i).setValue(new ArrayList<>());
                    nrValue -= 1;
                    totalFields -= 1;
                }
                else if(nrDrop != 0) {
                    listOfSubscriptions.get(i).setDrop(new ArrayList<>());
                    nrDrop -= 1;
                    totalFields -= 1;
                }
                else if(nrVariation != 0) {
                    listOfSubscriptions.get(i).setVariation(new ArrayList<>());
                    nrVariation -= 1;
                    totalFields -= 1;
                }
                else if(nrDate != 0){
                    listOfSubscriptions.get(i).setDate(new ArrayList<>());
                    nrDate -= 1;
                    totalFields -= 1;
                }
            }
        }
        return listOfSubscriptions;
    }
}
