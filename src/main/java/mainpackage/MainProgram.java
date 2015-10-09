/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainpackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.*;
import vsm.*;
import parser.*;
import progs.*;
/**
 *
 * @author wiragotama
 */
public class MainProgram {
   
    public static void main(String[] args)
    {
        /*ArrayList<String> terms = new ArrayList();
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
        tfidf.makeTFIDFWeightMatrix(3, false, false, collection);
        System.out.println(tfidf.getTerms());
        tfidf.printWeightMatrix();*/

        Documents docs = new Documents("test_collections/adi/adi.all", "custom.stopword", true);
        System.out.println(docs);
        DataTokenizedInstances collection = new DataTokenizedInstances();
        for (int i=0; i<docs.size(); i++)
        {
            DataTokenized temp = new DataTokenized(Arrays.asList(docs.getDocument(i)));
            collection.add(temp);
        }
        VSM tfidf = new VSM();
        tfidf.makeTFIDFWeightMatrix(1,false,false,collection);
        //tfidf.printWeightMatrix();

        Queries queries = new Queries("test_collections/adi/query.text", "custom.stopword", true);

        List<DocumentRank> res = tfidf.queryTask(queries.getQuery(0),1,true,false);
        for (int i=0; i<res.size(); i++)
            System.out.println(res.get(i).getDocNum()+" "+res.get(i).getSC());
        
        //contoh save
        //tfidf.save("tfidf");
        
        //contoh load
        //VSM tfidf = new VSM();
        /*tfidf.load("tfidf");
        System.out.println(tfidf.getTerms());
        tfidf.printWeightMatrix();*/
    }
}
