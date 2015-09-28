/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainpackage;

import java.util.ArrayList;
import Model.*;
import VSM.*;
/**
 *
 * @author wiragotama
 */
public class MainProgram {
   
    public static void main(String[] args)
    {
        ArrayList<String> terms = new ArrayList();
        terms.add("wira");
        terms.add("wira");
        terms.add("wira");
        terms.add("wira");
        terms.add("ganteng");
        DataTokenized temp = new DataTokenized(terms);
        temp.print();
        DataTokenizedInstances collection = new DataTokenizedInstances();
        collection.add(temp);
        terms.set(0, "haha");
        temp.setText(terms);
        collection.add(temp);
        VSM tfidf = new VSM();
        tfidf.makeTFIDFWeightMatrix(4, false, false, collection);
        System.out.println(tfidf.getTerms());
        tfidf.printWeightMatrix();
        
        //contoh save
        //tfidf.save("tfidf");
        
        //contoh load
        //VSM tfidf = new VSM();
        tfidf.load("tfidf");
        System.out.println(tfidf.getTerms());
        tfidf.printWeightMatrix();
    }
}
