package VSM;

import Model.*;
import Model.DataTokenizedInstances;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author wiragotama
 * TF IDF Based VSM
 */
public class VSM {
    
    private ArrayList<String> terms;
    private ArrayList< ArrayList<Double> > weightMatrix; //[N_terms][N_documents]
    /**
     *          doc1 doc2 doc3 ...
     * term1    
     * term2
     * term3
     * ...
     */
    private int collectionSize;
    private boolean normalization;   
    private boolean useIDF;
    private int TFOption;
    private ArrayList<Double> termsIDF;
    
    /**
     * Default Constructor
     */
    public VSM()
    {
        this.weightMatrix = new ArrayList();
        this.terms = new ArrayList();
        this.termsIDF = new ArrayList();
    }
    
    /**
     * 
     * @return collectionSize
     */
    public int getCollectionSize()
    {
        return this.collectionSize;
    }
    
    /**
     * 
     * @return terms
     */
    public ArrayList<String> getTerms()
    {
        return this.terms;
    }
    
    /**
     * 
     * @return termsIDF
     */
    public ArrayList<Double> getTermsIDF()
    {
        return this.termsIDF;
    }
    
    /**
     * 
     * @return weightMatrix
     */
    public ArrayList< ArrayList<Double> > getWeightMatrix()
    {
        return this.weightMatrix;
    }
    
    /*
    * Making union of all term vectors from papersCollection abstract
    * Producing ArrayList<String> terms
    */
    public void listOfAllTermsInDocuments(DataTokenizedInstances collection)
    {
        this.terms.clear();
        this.terms = new ArrayList();
        this.collectionSize = collection.getCollectionSize();
        for (int i=0; i<this.collectionSize; i++)
        {
            final int M = collection.getInstance(i).getText().size();
            for (int j=0; j<M; j++)
            {
                if (!this.terms.contains(collection.getInstance(i).getText().get(j)))
                {
                    this.terms.add(collection.getInstance(i).getText().get(j));
                }
            }
        }
    }
    
    /**
     * TF weight of term to the doc
     * @param option 0:no TF, 1:Raw TF, 2:Binary TF, 3:Augmented TF, 4:Logarithmic TF
     * @param term
     * @param doc
     * @return TF weight
     */
    private double TFWeight(int option, String term, ArrayList<String> doc)
    {
        if (option==0)
            return 0;
        else
        {
            double TF =  (double) Collections.frequency(doc, term); //nor normalization
            if (option==2) 
            {
                if (TF>0) TF=1;
                //else, means TF is 0
            }
            else if (option==4)
            {
                if (TF!=0)
                    TF = 1+ Math.log(TF);
            }
            return TF;
        }
    }
    
    /**
     * @param term
     * @param collection Documents collection
     * @return IDF weight of term in the collection
     */
    public double IDFWeight(String term, DataTokenizedInstances collection)
    {
        int N = this.collectionSize;
        int dft=1; //prevent infinity
        for (int i=0; i<this.collectionSize; i++)
        {
            if (collection.getInstance(i).getText().contains(term)) {
                dft++;
            }
        }
        
        return Math.log((double)N / (double)dft);
    }
    
    /**
    * Generate Terms IDF vector
    */
    private void generateTermsIDF(DataTokenizedInstances collection)
    {
        this.termsIDF.clear();
        int N = terms.size();
        for (int i=0; i<N; i++)
        {
            double idfTerm = IDFWeight(this.terms.get(i), collection);
            this.termsIDF.add(idfTerm);
        }
    }
    
    /**
     * Make TF-IDF Matrix
     * @param TFOption 0:no TF, 1:Raw TF, 2:Binary TF, 3:Augmented TF, 4:Logarithmic TF
     * @param useIDF true if use IDF
     * @param normalization true if use normalization
     * @param collection documents collection
     */
    public void makeTFIDFWeightMatrix(int TFOption, boolean useIDF, boolean normalization, DataTokenizedInstances collection)
    {
        listOfAllTermsInDocuments(collection);
        if (useIDF)
            generateTermsIDF(collection);
        this.normalization = normalization;
        this.useIDF = useIDF;
        this.TFOption = TFOption;
        this.weightMatrix = new ArrayList();
        
        double maxTF = (double) Integer.MIN_VALUE; //for Augmented TF
        
        //making TF-IDF matrix
        int N = this.terms.size();
        for (int i=0; i<N; i++)
        {
            ArrayList<Double> weightVector = new ArrayList();
            for (int j=0; j<this.collectionSize; j++)
            {
                double TF = TFWeight(TFOption, this.terms.get(i), collection.getInstance(j).getText());
                if (TFOption!=0) { //use TF
                    if (useIDF)
                        weightVector.add(termsIDF.get(i) * TF);
                    else //don't use IDF
                        weightVector.add(TF);
                }
                else if (useIDF) //IDF only
                    weightVector.add(termsIDF.get(i));
                else
                    weightVector.add(0.0);
                
                if (TF>maxTF) //for augmented TF
                    maxTF = TF;
            }
           
            this.weightMatrix.add(weightVector);
        }
        
        if (TFOption==3) //augmented TF Case, devide by biggest TF in documents
        {
            for (int i=0; i<N; i++)
                for (int j=0; j<this.collectionSize; j++)
                    this.weightMatrix.get(i).set(j, this.weightMatrix.get(i).get(j)/maxTF);
        }
        
        if (normalization) //normalization is counted to the terms vector
        {
            //foreach doc, count |doc weight|, for each element of doc devide by |doc weight|
            for (int j=0; j<this.collectionSize; j++) {
                double cosineLength = 0.0;
                for (int i=0; i<N; i++)
                {
                    cosineLength += Math.pow(this.weightMatrix.get(i).get(j), 2.0);
                }
                
                cosineLength = Math.sqrt(cosineLength);
                for (int i=0; i<N; i++)
                {
                    this.weightMatrix.get(i).set(j, this.weightMatrix.get(i).get(j)/cosineLength);
                }
            }
        }
    }
    
    /**
     * Output weight matrix to screen
     */
    public void printWeightMatrix()
    {
        int N = this.weightMatrix.size();
        for (int i=0; i<N; i++)
        {
            System.out.println(weightMatrix.get(i).toString()); 
        }
    }
    
    /**
     * Assumption : query option is same as VSM option
     * @param queryTokenized
     * @return ArrayList<Double> queryWeight
     */
    public ArrayList<Double> queryWeighting(DataTokenized queryTokenized)
    {
        ArrayList<Double> queryWeight = new ArrayList();
        int N = queryTokenized.size();
        double maxTF = 0;
        for (int i=0; i<N; i++)
        {
            double TF = TFWeight(this.TFOption, queryTokenized.getText().get(i), queryTokenized.getText());
            if (this.TFOption!=0) { //use TF
                if (this.useIDF)
                    queryWeight.add(this.termsIDF.get(i) * TF);
                else //don't use IDF
                    queryWeight.add(TF);
            }
            else if (this.useIDF) //IDF only
                queryWeight.add(this.termsIDF.get(i));
            else
                queryWeight.add(0.0);
            
            //disini ragu2, maxTF disini harusnya keseluruhan dokumen (yang dipake bikin VSM) 
            //atau di query aja, secara logika sih harusnya yang di query aja
            if (TF>maxTF) //for augmented TF case
                maxTF = TF;
        }
        
        if (this.TFOption==3) //augmented TF Case, devide by biggest TF in documents
        {
            for (int i=0; i<N; i++)
                queryWeight.set(i, queryWeight.get(i)/maxTF);
        }
        
        if (this.normalization) //normalization is counted to the terms vector
        {
            //count |doc weight|, for each element of doc devide by |doc weight|
            double cosineLength = 0.0;
            for (int i=0; i<N; i++)
            {
                cosineLength += Math.pow(queryWeight.get(i), 2.0);
            }

            cosineLength = Math.sqrt(cosineLength);
            for (int i=0; i<N; i++)
            {
                queryWeight.set(i, queryWeight.get(i)/cosineLength);
            }
        }
        return queryWeight;
    }
}
