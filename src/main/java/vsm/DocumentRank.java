package vsm;

import java.util.Comparator;

/**
 * Created by wiragotama on 10/9/15.
 */
public class DocumentRank implements Comparable<DocumentRank> {
    private int docNum;
    private double SC;

    public DocumentRank(int docNum, double SC)
    {
        this.docNum = docNum;
        this.SC = SC;
    }

    public int getDocNum()
    {
        return docNum;
    }

    public double getSC()
    {
        return SC;
    }

    public int compareTo(DocumentRank other)
    {
        return Double.compare(this.SC, other.SC);
    }

    public boolean equals(Object other)
    {
        if (!(other instanceof DocumentRank))
            return false;
        DocumentRank n = (DocumentRank) other;
        return Double.compare(n.SC, this.SC)==0;
    }
}
