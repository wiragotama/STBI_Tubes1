package ranker;

import model.DataTokenized;
import model.DataTokenizedInstances;
import parser.Parser;
import progs.Documents;
import progs.Queries;
import vsm.DocumentRank;
import vsm.VSM;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by timothy.pratama on 09-Oct-15.
 */
public class DocumentRanker {
    private Parser parser;
    private VSM vsm;
    private Documents documents;
    private Queries queries;
    private String documentPath;
    private String queryPath;
    private String relevanceJudgmentPath;
    private String stopwordsPath;
    private int documentTFOption;
    private boolean documentUseIDF;
    private boolean documentUseNormalization;
    private boolean documentUseStemming;
    private int queryTFOption;
    private boolean queryUseIDF;
    private boolean queryUseNormalization;
    private boolean queryUseStemming;
    private double threshold;
    private String toStringOutput;
    private boolean isExperiment;
    private String queryInput;

    public String getToStringOutput() {
        return toStringOutput;
    }

    public void setToStringOutput(String toStringOutput) {
        this.toStringOutput = toStringOutput;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public Parser getParser() {
        return parser;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public VSM getVsm() {
        return vsm;
    }

    public void setVsm(VSM vsm) {
        this.vsm = vsm;
    }

    public Documents getDocuments() {
        return documents;
    }

    public void setDocuments(Documents documents) {
        this.documents = documents;
    }

    public Queries getQueries() {
        return queries;
    }

    public void setQueries(Queries queries) {
        this.queries = queries;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public String getQueryPath() {
        return queryPath;
    }

    public void setQueryPath(String queryPath) {
        this.queryPath = queryPath;
    }

    public String getRelevanceJudgmentPath() {
        return relevanceJudgmentPath;
    }

    public void setRelevanceJudgmentPath(String relevanceJudgmentPath) {
        this.relevanceJudgmentPath = relevanceJudgmentPath;
    }

    public int getDocumentTFOption() {
        return documentTFOption;
    }

    public void setDocumentTFOption(int documentTFOption) {
        this.documentTFOption = documentTFOption;
    }

    public boolean isDocumentUseIDF() {
        return documentUseIDF;
    }

    public void setDocumentUseIDF(boolean documentUseIDF) {
        this.documentUseIDF = documentUseIDF;
    }

    public boolean isDocumentUseNormalization() {
        return documentUseNormalization;
    }

    public void setDocumentUseNormalization(boolean documentUseNormalization) {
        this.documentUseNormalization = documentUseNormalization;
    }

    public String getStopwordsPath() {
        return stopwordsPath;
    }

    public void setStopwordsPath(String stopwordsPath) {
        this.stopwordsPath = stopwordsPath;
    }

    public boolean isDocumentUseStemming() {
        return documentUseStemming;
    }

    public void setDocumentUseStemming(boolean documentUseStemming) {
        this.documentUseStemming = documentUseStemming;
    }

    /**
     * Build the document ranker based on the options from the GUI
     */
    public void build()
    {
        // baca option dari gui
        readOption();

        // buat parser berdasarkan collection set, test set, dan relevance judgement path
        parser = new Parser(documentPath, queryPath, relevanceJudgmentPath);

        if(isExperiment)
        {
            documents = new Documents(documentPath, stopwordsPath, documentUseStemming);

            DataTokenizedInstances collection = new DataTokenizedInstances();
            for (int i = 0; i < documents.size(); i++) {
                DataTokenized temp = new DataTokenized(Arrays.asList(documents.getDocument(i)));
                collection.add(temp);
            }
            vsm = new VSM();
            vsm.makeTFIDFWeightMatrix(documentTFOption, documentUseIDF, documentUseNormalization, collection);
            queries = new Queries(stopwordsPath, queryUseStemming);
            queries.processQueriesFromFile(queryPath);
            vsm.save("tfidf");
        }
        else
        {
            //queries dari input
            vsm = new VSM();
            vsm.load("tfidf");
            queries = new Queries(stopwordsPath, queryUseStemming);
            queries.processQueryFromString(this.queryInput);
        }
        // evaluate query sesuai dengan option dari gui
        evaluateQuery();
    }

    /**
     * Evaluate query and return the document that has been ranked based on similarity to query
     */
    private void evaluateQuery()
    {
        List<DocumentRank> result;
        int retrievedSize = 0;
        int relevanceSize = 0;
        double precision = 0;
        double recall = 0;
        double nonInterpolatedAveragePrecision = 0;
        toStringOutput = "";

        // Evaluasi setiap document yang diretrieve dengan relevance judgment pada setiap query
        for(int q=0; q<queries.getQueries().size(); q++)
        {
            retrievedSize = 0;
            relevanceSize = 0;
            nonInterpolatedAveragePrecision = 0;
            precision = 0;
            recall = 0;

            result = vsm.queryTask(queries.getQuery(q), queryTFOption, queryUseIDF, queryUseNormalization);
            for(int d=0; d<result.size(); d++)
            {
                if(result.get(d).getSC() > threshold)
                {
                    retrievedSize ++;
                    if (isExperiment)
                        if(parser.getRelevanceJudgements().get(q).contains(result.get(d).getDocNum())) {
                            relevanceSize++;
                            nonInterpolatedAveragePrecision = nonInterpolatedAveragePrecision + ((double) relevanceSize / (double) retrievedSize);
                        }
                }
            }

            if (isExperiment) {
                if(retrievedSize > 0)
                {
                    precision = (double) relevanceSize / (double) retrievedSize;
                    recall = (double) relevanceSize / (double) parser.getRelevanceJudgements().get(q).size();
                    nonInterpolatedAveragePrecision = nonInterpolatedAveragePrecision / (double) parser.getRelevanceJudgements().get(q).size();
                }
                else
                {
                    precision = 0;
                    recall = 0;
                    nonInterpolatedAveragePrecision = 0;
                }
            }

            toStringOutput += retrievedSize + "\n";
            if (isExperiment) {
                toStringOutput += precision + "\n";
                toStringOutput += recall + "\n";
                toStringOutput += nonInterpolatedAveragePrecision + "\n";
            }

            for(int i=0; i<retrievedSize; i++)
            {
                toStringOutput += result.get(i).getDocNum()+1 + "\n";
                toStringOutput += parser.getDocumentsTitle().get(result.get(i).getDocNum()) + "\n";
            }
        }
//        toStringOutput += -1 + "\n";
        toStringOutput = toStringOutput.substring(0, toStringOutput.length()-1);
    }

    /**
     * Read Option for TF, IDF, Normalization, Stemming, and Experiment / Interactive from GUI
     */
    private void readOption()
    {
        isExperiment=true;
        String currentLine;
        Scanner scanner = new Scanner(System.in);
        currentLine = scanner.nextLine();
        if (currentLine.equalsIgnoreCase("experiment"))
            isExperiment = true;
        else
            isExperiment = false;

        if (isExperiment==false)
            this.queryInput = scanner.nextLine();

        this.documentPath = scanner.nextLine();
        this.queryPath = scanner.nextLine();
        this.relevanceJudgmentPath = scanner.nextLine();
        this.stopwordsPath = scanner.nextLine();

        //Document TF
        currentLine = scanner.nextLine();
        if (currentLine.equalsIgnoreCase("notf"))
            this.documentTFOption = 0;
        else if (currentLine.equalsIgnoreCase("rawtf"))
            this.documentTFOption = 1;
        else if (currentLine.equalsIgnoreCase("binarytf"))
            this.documentTFOption = 2;
        else if (currentLine.equalsIgnoreCase("augmentedtf"))
            this.documentTFOption = 3;
        else if (currentLine.equalsIgnoreCase("logarithmictf"))
            this.documentTFOption = 4;

        //Document IDF
        currentLine = scanner.nextLine();
        if (currentLine.equalsIgnoreCase("noidf"))
            this.documentUseIDF = false;
        else if (currentLine.equalsIgnoreCase("usingidf"))
            this.documentUseIDF = true;

        //Document normalization
        currentLine = scanner.nextLine();
        if (currentLine.equalsIgnoreCase("nonormalization"))
            this.documentUseNormalization = false;
        else if (currentLine.equalsIgnoreCase("usingnormalization"))
            this.documentUseNormalization = true;

        //Document Stemming
        currentLine = scanner.nextLine();
        if (currentLine.equalsIgnoreCase("nostemming"))
            this.documentUseStemming = false;
        else if (currentLine.equalsIgnoreCase("usingstemming"))
            this.documentUseStemming = true;

        //Query TF
        currentLine = scanner.nextLine();
        if (currentLine.equalsIgnoreCase("notf"))
            this.queryTFOption = 0;
        else if (currentLine.equalsIgnoreCase("rawtf"))
            this.queryTFOption = 1;
        else if (currentLine.equalsIgnoreCase("binarytf"))
            this.queryTFOption = 2;
        else if (currentLine.equalsIgnoreCase("augmentedtf"))
            this.queryTFOption = 3;
        else if (currentLine.equalsIgnoreCase("logarithmictf"))
            this.queryTFOption = 4;

        //Query IDF
        currentLine = scanner.nextLine();
        if (currentLine.equalsIgnoreCase("noidf"))
            this.queryUseIDF = false;
        else if (currentLine.equalsIgnoreCase("usingidf"))
            this.queryUseIDF = true;

        //Query normalization
        currentLine = scanner.nextLine();
        if (currentLine.equalsIgnoreCase("nonormalization"))
            this.queryUseNormalization = false;
        else if (currentLine.equalsIgnoreCase("usingnormalization"))
            this.queryUseNormalization = true;

        //Query Stemming
        currentLine = scanner.nextLine();
        if (currentLine.equalsIgnoreCase("nostemming"))
            this.queryUseStemming = false;
        else if (currentLine.equalsIgnoreCase("usingstemming"))
            this.queryUseStemming = true;
    }

    @Override
    /**
     * Print the result
     */
    public String toString() {
        return toStringOutput;
    }
}
