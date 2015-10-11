/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainpackage;

import ranker.DocumentRanker;
/**
 *
 * @author wiragotama
 */
public class MainProgram {
   
    public static void main(String[] args)
    {
        DocumentRanker documentRanker = new DocumentRanker();
        documentRanker.setThreshold(0.01);
        documentRanker.build();
        System.out.print(documentRanker.toString());
    }
}
