package org.generator;

import java.util.ArrayList;

public class MultithreadedPubGeneration extends Thread{
    DBGenerator generator;
    int numberOfPublications;
    ArrayList<Publication> pubList;

    public MultithreadedPubGeneration(DBGenerator generator, int numberOfPublications) {
        this.generator = generator;
        this.numberOfPublications = numberOfPublications;
    }
    @Override
    public void run(){
        pubList = generator.generatePublications(numberOfPublications);
    }
}
