package ranker;

import model.DataTokenized;
import model.DataTokenizedInstances;
import parser.Parser;
import progs.Documents;
import progs.Queries;
import vsm.DocumentRank;
import vsm.VSM;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
    private String DocumentTFOption;
    private String DocumentIDFOption;
    private String DocumentnormalizationOption;
    private String DocumentstemmingOption;
    private String QueryTFOption;
    private String QueryIDFOption;
    private String QueryNormalizationOption;
    private String QueryStemmingOption;
    private int counter;
    private List<DocumentRank> [] results;

    public DocumentRanker()
    {
        this.counter = 0;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public void hijackedRead(int i, int j, int k, int l)
    {
        isExperiment=true;

        this.documentPath = "test_collections/cisi/cisi.all";
        this.queryPath = "test_collections/cisi/query.text";
        this.relevanceJudgmentPath = "test_collections/cisi/qrels.text";
        this.stopwordsPath = "custom.stopword";

        //Document TF
        this.documentTFOption = this.queryTFOption = i;
        if (i==0) this.DocumentTFOption= this.QueryTFOption = "NoTF";
        else if (i==1) this.DocumentTFOption= this.QueryTFOption = "RawTF";
        else if (i==2) this.DocumentTFOption= this.QueryTFOption = "BinaryTF";
        else if (i==3) this.DocumentTFOption= this.QueryTFOption = "AugmentedTF";
        else this.DocumentTFOption= this.QueryTFOption = "LogarithmicTF";

        //Document IDF
        this.documentUseIDF = this.queryUseIDF = (j==1);
        if (j==1) this.DocumentIDFOption= this.QueryIDFOption = "UsingIDF";
        else this.DocumentIDFOption= this.QueryIDFOption = "NoIDF";

        //Document normalization
        this.documentUseNormalization = this.queryUseNormalization = (k==1);
        if (k==1) this.DocumentnormalizationOption = this.QueryNormalizationOption = "UsingNormalization";
        else this.DocumentnormalizationOption = this.QueryNormalizationOption = "NoNormalization";

        //Document Stemming
        this.documentUseStemming = this.queryUseStemming = (l==1);
        if (l==1) this.DocumentstemmingOption = this.QueryStemmingOption = "UsingStemming";
        else this.DocumentstemmingOption = this.QueryStemmingOption = "NoStemming";
    }
    /**
     * Build the document ranker based on the options from the GUI
     */
    public void buildHijacked(int a, int b, int c, int d, int counter)
    {
        this.counter = counter;
        // baca option dari gui
        //readOption();

        //sementara diganti
        this.hijackedRead(a, b, c, d);
        System.out.println("Hijacked Read...");

        // buat parser berdasarkan collection set, test set, dan relevance judgement path
        parser = new Parser(documentPath, queryPath, relevanceJudgmentPath);
        System.out.println("Parser created...");

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
            System.out.println("VSM created...");
            queries = new Queries(stopwordsPath, queryUseStemming);
            queries.processQueriesFromFile(queryPath);
            System.out.println("Queries Processed...");
            vsm.save("tfidf");
            System.out.println("VSM saved...");
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
        /* Untuk output CSV */
        try {
            PrintWriter printer;
            printer = new PrintWriter("outputLaporan" + (counter+1) + ".csv", "UTF-8");
            printer.println(",document,query");
            printer.println("TF,"+this.DocumentTFOption+","+this.QueryTFOption);
            printer.println("IDF,"+this.DocumentIDFOption+","+this.QueryIDFOption);
            printer.println("Normalization,"+this.DocumentnormalizationOption+","+this.QueryNormalizationOption);
            printer.println("Stemming,"+this.DocumentstemmingOption+","+this.QueryStemmingOption);
            printer.println("Query");
            printer.println("Query,Precision,Recall,Non-Interpolated Average Precision");

            List<DocumentRank> result;
            results = new List[queries.getQueries().size()];

            int retrievedSize = 0;
            int relevanceSize = 0;
            double precision = 0;
            double recall = 0;
            double nonInterpolatedAveragePrecision = 0;
            toStringOutput = "";
            StringBuffer tempOutput = new StringBuffer();

            /* Jumlah thread 10, setiap thread menangani untuk kelipatan 10 dari index awal.
             * Thread 0 -> query 0, query 10, query 20, ...
             * Thread 1 -> query 1, query 11, query 21, ...
             * Dibagi seperti ini agar beban tiap thread sama (Query semakin ke belakang, semakin panjang)
            */
            QueryTaskWorker[] queryTaskWorkers = new QueryTaskWorker[10];
            for(int i=0; i<10; i++)
            {
                queryTaskWorkers[i] = new QueryTaskWorker(i);
                queryTaskWorkers[i].start();
            }
            System.out.println("All query worker started...");
            for(QueryTaskWorker queryTaskWorker : queryTaskWorkers)
            {
                queryTaskWorker.join();
            }
            System.out.println("All query worker finished...");

            // Evaluasi setiap document yang diretrieve dengan relevance judgment pada setiap query
            for(int q=0; q<queries.getQueries().size(); q++)
            {
                System.out.println("Evaluating Query " + (q+1));
                retrievedSize = 0;
                relevanceSize = 0;
                nonInterpolatedAveragePrecision = 0;
                precision = 0;
                recall = 0;

                result = results[q];
                System.out.println("Get result...");

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
                System.out.println("Get non Interpolated average precision...");

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
                    System.out.println("Get Experiment, precision, and recall...");
                }

                //toStringOutput += retrievedSize + "\n";
                tempOutput = tempOutput.append(retrievedSize).append("\n");
                if (isExperiment) {
                    tempOutput = tempOutput.append(precision).append('\n');
                    tempOutput = tempOutput.append(recall).append('\n');
                    tempOutput = tempOutput.append(nonInterpolatedAveragePrecision).append('\n');
                }

                for(int i=0; i<retrievedSize; i++)
                {
                    tempOutput = tempOutput.append(result.get(i).getDocNum()+1).append('\n');
                    tempOutput = tempOutput.append(parser.getDocumentsTitle().get(result.get(i).getDocNum())).append('\n');
                }
                printer.println((q+1)+","+precision+","+recall+","+nonInterpolatedAveragePrecision);
                System.out.println("Get All String printed...\n");
            }

            toStringOutput = tempOutput.substring(0, tempOutput.length() - 1);
            printer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
        DocumentTFOption = currentLine;
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
        DocumentIDFOption = currentLine;
        if (currentLine.equalsIgnoreCase("noidf"))
            this.documentUseIDF = false;
        else if (currentLine.equalsIgnoreCase("usingidf"))
            this.documentUseIDF = true;

        //Document normalization
        currentLine = scanner.nextLine();
        DocumentnormalizationOption = currentLine;
        if (currentLine.equalsIgnoreCase("nonormalization"))
            this.documentUseNormalization = false;
        else if (currentLine.equalsIgnoreCase("usingnormalization"))
            this.documentUseNormalization = true;

        //Document Stemming
        currentLine = scanner.nextLine();
        DocumentstemmingOption = currentLine;
        if (currentLine.equalsIgnoreCase("nostemming"))
            this.documentUseStemming = false;
        else if (currentLine.equalsIgnoreCase("usingstemming"))
            this.documentUseStemming = true;

        //Query TF
        currentLine = scanner.nextLine();
        QueryTFOption = currentLine;
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
        QueryIDFOption = currentLine;
        if (currentLine.equalsIgnoreCase("noidf"))
            this.queryUseIDF = false;
        else if (currentLine.equalsIgnoreCase("usingidf"))
            this.queryUseIDF = true;

        //Query normalization
        currentLine = scanner.nextLine();
        QueryNormalizationOption = currentLine;
        if (currentLine.equalsIgnoreCase("nonormalization"))
            this.queryUseNormalization = false;
        else if (currentLine.equalsIgnoreCase("usingnormalization"))
            this.queryUseNormalization = true;

        //Query Stemming
        currentLine = scanner.nextLine();
        QueryStemmingOption = currentLine;
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

    private class QueryTaskWorker implements Runnable
    {
        private int startIndex;
        private Thread thread;

        public QueryTaskWorker(int startIndex) {
            this.startIndex = startIndex;
        }

        @Override
        public void run() {
            for(int i = startIndex; i < queries.getQueries().size(); i=i+10)
            {
                System.out.println("Query worker processing query " + i);
                results[i] = vsm.queryTask(queries.getQuery(i), queryTFOption, queryUseIDF, queryUseNormalization);
            }
        }

        public void start()
        {
            if(thread == null)
            {
                thread = new Thread(this);
            }
            thread.start();
        }

        public void join()
        {
            if(thread != null)
            {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}