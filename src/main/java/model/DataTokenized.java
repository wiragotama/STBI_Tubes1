/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;

/**
 *
 * @author wiragotama
 */
public class DataTokenized {
    
    private ArrayList<String> text; //text of document, could be say tokenized text
    
    /**
     * Default Constructor
     * @param text
     */
    public DataTokenized(ArrayList<String> text)
    {
        this.text = new ArrayList();
        int len = text.size();
        for (int i=0; i<len; i++)
            this.text.add(text.get(i));
    }
    
    /**
     * Copy Constructor
     * @param d Data
     */
    public DataTokenized(DataTokenized d)
    {
        this.text = new ArrayList();
        int len = d.getText().size();
        for (int i=0; i<len; i++)
            this.text.add(d.getText().get(i));
    }
    
    /**
     * 
     * @return text
     */
    public ArrayList<String> getText()
    {
        return this.text;
    }
    
    /**
     * 
     * @param text new text
     */
    public void setText(ArrayList<String> text)
    {
        this.text = new ArrayList();
        int len = text.size();
        for (int i=0; i<len; i++)
            this.text.add(text.get(i));
    }
    
    /**
    * Get instance
     * @return this instance
    */
    public DataTokenized instance()
    {
        return this;
    }
    
    /**
     * 
     * @return size
     */
    public int size()
    {
        return this.text.size();
    }
    
    /**
     * Output to screen
     */
    public void print()
    {
        System.out.println("Text    = "+this.text.toString());
    }
}
