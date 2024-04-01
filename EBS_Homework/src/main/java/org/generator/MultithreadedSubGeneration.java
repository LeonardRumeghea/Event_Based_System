package org.generator;

import java.util.ArrayList;

public class MultithreadedSubGeneration extends Thread {
    DBGenerator generator;
    int companyEqualSign;
    int numberOfSubscriptions;
    int totalFields;
    int nrCompany;
    int nrValue;
    int nrDrop;
    int nrVariation;
    int nrDate;
    ArrayList<Subscription> subList;
    int threadNumber;

    public MultithreadedSubGeneration(int threadNumber, DBGenerator generator, int companyEqualSign, int numberOfSubscriptions, int totalFields, int nrCompany, int nrValue, int nrDrop, int nrVariation, int nrDate) {
        this.threadNumber=threadNumber;
        this.generator=generator;
        this.companyEqualSign = companyEqualSign;
        this.numberOfSubscriptions = numberOfSubscriptions;
        this.totalFields = totalFields;
        this.nrCompany = nrCompany;
        this.nrValue = nrValue;
        this.nrDrop = nrDrop;
        this.nrVariation = nrVariation;
        this.nrDate = nrDate;
    }

    @Override
    public void run(){
        subList = generator.generateSubscriptions(companyEqualSign, numberOfSubscriptions, totalFields, nrCompany, nrValue, nrDrop, nrVariation, nrDate);
    }
}
