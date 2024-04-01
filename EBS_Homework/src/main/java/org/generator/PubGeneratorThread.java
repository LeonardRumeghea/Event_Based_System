package org.generator;

import lombok.RequiredArgsConstructor;
import org.generator.entities.Publication;

import java.util.ArrayList;

@RequiredArgsConstructor
public class PubGeneratorThread extends Thread {
    final DBGenerator generator;
    final int numberOfPublications;
    ArrayList<Publication> pubList;

    @Override
    public void run(){
        pubList = generator.generatePublications(numberOfPublications);
    }
}
