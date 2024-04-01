package org.generator;

import lombok.RequiredArgsConstructor;
import org.generator.entities.Subscription;

import java.util.ArrayList;

@RequiredArgsConstructor
public class SubGeneratorThread extends Thread {
    final DBGenerator generator;
    final int companyEqualSign;
    final int numberOfSubscriptions;
    final int totalFields;
    final int nrCompany;
    final int nrValue;
    final int nrDrop;
    final int nrVariation;
    final int nrDate;
    final int threadNumber;
    ArrayList<Subscription> subList;

    @Override
    public void run(){
        subList = generator.generateSubscriptions(companyEqualSign, numberOfSubscriptions, totalFields, nrCompany, nrValue, nrDrop, nrVariation, nrDate);
    }
}
