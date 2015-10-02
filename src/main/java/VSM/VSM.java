package VSM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import Model.DataTokenized;
import Model.DataTokenizedInstances;

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
        Collections.sort(this.terms);
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
     * Print Options
     */
    public void printOptions()
    {
        System.out.println("--------------OPTIONS---------------");
        System.out.println("normalization = "+this.normalization);   
        System.out.println("use IDF       = "+this.useIDF); 
        System.out.println("TF Option     = "+this.TFOption); 
        System.out.println("------------------------------------");
    }
    
    /**
     * Output weight matrix to screen
     */
    public void printWeightMatrix()
    {
        int N = this.weightMatrix.size();
        int M = this.weightMatrix.get(0).size();
        DecimalFormat df = new DecimalFormat("0.00");
        
        for (int i=0; i<N; i++)
        {
            System.out.print("[ ");
            for (int j=0; j<M; j++)
                System.out.print(df.format(this.weightMatrix.get(i).get(j))+" ");
            System.out.println("]");
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
    
    /**
     * save inverted file, terms-IDF and configuration
     * @param filePath without extension
     */
    public void save(String filePath)
    {
        saveConfig(filePath+".config");
        saveIDF(filePath+".idf");
        saveToInvertedFile(filePath+".invertedFile");
    }
    
    /**
     * load inverted file, terms-IDF and configuration
     * @param filePath without extension
     */
    public void load(String filePath)
    {
        loadConfig(filePath+".config");
        loadIDF(filePath+".idf");
        loadFromInvertedFile(filePath+".invertedFile");
    }
    
    /**
     * Save to invertedFile
     * format (separated by space)
     * term docNo Weight
     * @param filePath 
     */
    private void saveToInvertedFile(String filePath)
    {
        System.out.println("Saving Inverted File");
        try {
            PrintWriter writer = new PrintWriter(filePath, "UTF-8");
            int N = this.terms.size();
            for (int i=0; i<N; i++)
            {
                for (int j=0; j<this.collectionSize; j++)
                {
                    if (Double.compare(this.weightMatrix.get(i).get(j),0.0)!=0)
                    {
                        writer.println(this.terms.get(i)+" "+Integer.toString(j)+" "+this.weightMatrix.get(i).get(j));
                    }
                }
            }
            writer.close();
        }
        catch (FileNotFoundException | UnsupportedEncodingException ex)
        {
            System.out.println("save inverted file failed");
        }
    }
    
    /**
     * load Data from inverted file
     * @param filePath 
     */
    private void loadFromInvertedFile(String filePath)
    {
        System.out.println("Loading Inverted File");
        BufferedReader reader = null;
        String line = null;
        this.weightMatrix = new ArrayList();
        int colSize = this.collectionSize;
        int N = this.terms.size();
        
        File invertedFile = new File(filePath);
        
        for (int i=0; i<N; i++) {
            ArrayList<Double> temp = new ArrayList();
            for (int j=0; j<colSize; j++)
                temp.add(0.0);
            this.weightMatrix.add(temp);
        }
        
        try
        {
            reader = new BufferedReader(new FileReader(invertedFile));
        }
        catch (FileNotFoundException ex)
        {
            System.out.println(filePath+" is not found");
        }

        try {
			while ((line = reader.readLine()) != null)
			{
				String split[] = line.split(" ");
			    int idxTerm = this.terms.indexOf(split[0]);
			    int idxDoc = Integer.valueOf(split[1]);
			    double val = Double.valueOf(split[2]);
			    this.weightMatrix.get(idxTerm).set(idxDoc, val);
			}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
        
        
        try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(VSM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private int index(String term){
    	int index = 0;
    	boolean found = false;
    	
    	while(index < terms.size() && !found){
    		if (terms.get(index).equalsIgnoreCase(term)){
    			found = true;
    		}
    		
    		index++;
    	}
    	
    	for(String item : terms){
    		System.out.println("term " + terms.indexOf(item) + ": " + item);
    	}
    	    	
    	return index;
    }
    
    /**
     * Saving Terms IDF
     * @param filePath 
     */
    private void saveIDF(String filePath)
    {
        System.out.println("Saving Terms IDF");
        try {
            PrintWriter writer = new PrintWriter(filePath, "UTF-8");
            int N = this.terms.size();
            for (int i=0; i<N; i++)
            {
                if (this.useIDF)
                    writer.println(this.terms.get(i)+" "+this.termsIDF.get(i).toString());
                else
                    writer.println(this.terms.get(i));
            }
            writer.close();
        }
        catch (FileNotFoundException | UnsupportedEncodingException ex)
        {
            System.out.println("save terms-IDF failed");
        }
    }
    
    /**
     * Load Terms IDF
     * @param filePath 
     */
    private void loadIDF(String filePath)
    {
        System.out.println("Loading Terms IDF File");
        BufferedReader reader = null;
        String line = null;
        this.terms = new ArrayList();
        this.termsIDF = new ArrayList();
        
        File idfFile = new File(filePath);
        
        try
        {
            reader = new BufferedReader(new FileReader(idfFile));
        }
        catch (FileNotFoundException ex)
        {
            System.out.println(filePath+" is not found");
        }

        try {
			while ((line = reader.readLine()) != null)
			{
				String split[] = line.split(" ");
			    this.terms.add(split[0]);
			    if (this.useIDF)
			        this.termsIDF.add(Double.valueOf(split[1]));
			}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
        try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(VSM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Saving configuration options in making VSM
     * @param filePath 
     */
    private void saveConfig(String filePath)
    {
        System.out.println("Saving Configuration");
        try {
            PrintWriter writer = new PrintWriter(filePath, "UTF-8");
            writer.println(collectionSize+" "+normalization+" "+useIDF+" "+TFOption);
            writer.close();
        }
        catch (FileNotFoundException | UnsupportedEncodingException ex)
        {
            System.out.println("save config failed");
        }
    }
    
    /**
     * load configuration options from file
     * @param filePath 
     */
    private void loadConfig(String filePath)
    {
        System.out.println("Loading Config File");
        this.terms = new ArrayList();
        this.termsIDF = new ArrayList();
        
        File configFile = new File(filePath);
        BufferedReader reader = null;
        String line = null;
        
        try {
			reader = new BufferedReader(new FileReader(configFile));			
		} catch (FileNotFoundException e) {
			System.out.println(filePath + " is not found");
		}
        
        try {
			while((line = reader.readLine()) != null){
				String split[] = line.split(" ");
				
				this.collectionSize = Integer.valueOf(split[0]);
				this.normalization = Boolean.valueOf(split[1]);
				this.useIDF = Boolean.valueOf(split[2]);
				this.TFOption = Integer.valueOf(split[3]);
			}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(VSM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * getLine from inputStream separated by separator
     * @param inputStream
     * @param separator character
     * @return ArrayList<String>
     */
    public static ArrayList<String> getLine(FileInputStream inputStream, char separator) 
    {
        try {
            StringBuffer str = new StringBuffer();
            ArrayList<String> line = new ArrayList();

            char character = (char) inputStream.read();
            while (character != '\n' && character != 65535)
            {
                if (character == separator)
                {
                    line.add(str.toString());
                    str.delete(0, str.length());
                }
                else
                    str.append(character);
                character = (char) inputStream.read();
            } //character == '\n'
            if (!str.toString().isEmpty())
                line.add(str.toString());
            return line;
        }
        catch (IOException ex) {
            return null;
        }
    }
}
