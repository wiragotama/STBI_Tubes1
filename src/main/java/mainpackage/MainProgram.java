/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainpackage;

import ranker.DocumentRanker;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author wiragotama
 */
public class MainProgram {
   
    public static void main(String[] args)
    {
        int counter = 0;
        for (int i=0; i<5; i++)
            for (int j=0; j<2; j++)
                for (int k=0; k<2; k++)
                    for (int l=0; l<2; l++) {
                        if (i==0 && j==0) {
                            //do nothing
                        }
                        else {
                            if(counter >= 0) {
                                System.out.println(counter);
                                DocumentRanker documentRanker = new DocumentRanker();
                                documentRanker.setThreshold(0.01);
                                documentRanker.buildHijacked(i, j, k, l, counter);
                                //System.out.print(documentRanker.toString());
                            }
                            counter++;
                        }
                    }
    }
}
