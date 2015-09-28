package Model;

import java.util.ArrayList;


/**
 *
 * @author wiragotama
 */
public class DataTokenizedInstances {
    
    ArrayList<DataTokenized> papersCollection;
    
    /**
     * Default Constructor
     */
    public DataTokenizedInstances()
    {
        this.papersCollection = new ArrayList();
    }
    
    /**
     * copy constructor
     * @param d DataTokenizedInstances
     */
    public DataTokenizedInstances(DataTokenizedInstances d)
    {
        this.papersCollection = new ArrayList();
        int len = d.getCollectionSize();
        for (int i=0; i<len; i++) {
            DataTokenized newData = new DataTokenized(d.getInstance(i));
            this.papersCollection.add(newData);
        }
    }
    
    /**
     * @param idx
     * @return an instance
     */
    public DataTokenized getInstance(int idx)
    {
        return this.papersCollection.get(idx);
    }
    
    /**
     * @return papersCollection size
     */
    public int getCollectionSize()
    {
        return this.papersCollection.size();
    }
    
    /**
     * @return dataTokenizedInstances
     */
    public ArrayList<DataTokenized> getInstances()
    {
        return this.papersCollection;
    }
    
    /**
     * Set instance at idx into newInstance
     * @param idx
     * @param newInstance 
     */
    public void setInstance(int idx, DataTokenized newInstance)
    {
        this.papersCollection.get(idx).setText(newInstance.getText());
    }
    
    /**
     * Add new instance to collection
     * @param instance 
     */
    public void add(DataTokenized instance)
    {
        DataTokenized newInstance = new DataTokenized(instance);
        this.papersCollection.add(newInstance);
    }
    
    /**
     * Add new instances from other DataTokenizedsInstances to this collection
     * @param dataInstances 
     */
    public void add(DataTokenizedInstances dataInstances)
    {
        int len = dataInstances.getInstances().size();
        for (int i=0; i<len; i++)
        {
            DataTokenized newInstance = new DataTokenized(dataInstances.getInstance(i));
            this.papersCollection.add(newInstance);
        }
    }
    
    /**
     * output to screen
     */
    public void print()
    {
        int len = this.papersCollection.size();
        System.out.println("Count = "+len);
        for (int i=0; i<len; i++)
        {
            this.papersCollection.get(i).print();
        }
    }
    
    /**
     * Clear memory
     */
    public void clear()
    {
        this.papersCollection.clear();
    }
    
    /**
     * Delete data
     * @param beginIndex
     * @param lastIndex 
     */
    public void deleteData(int beginIndex, int lastIndex)
    {
        int N = lastIndex - beginIndex +1;
        while (N>0) {
            this.papersCollection.remove(beginIndex);
            N--;
        }
    }
}
