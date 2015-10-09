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

    DocumentRanker(String documentPath, String queryPath, String relevanceJudgmentPath, String stopwordsPath, double threshold)
    {
        this.documentPath = documentPath;
        this.queryPath = queryPath;
        this.relevanceJudgmentPath = relevanceJudgmentPath;
        this.stopwordsPath = stopwordsPath;
        this.documentTFOption = 1;
        this.queryTFOption = 1;
        this.documentUseIDF = true;
        this.queryUseIDF = true;
        this.documentUseNormalization = false;
        this.queryUseNormalization = false;
        this.documentUseStemming = true;
        this.queryUseStemming = true;
        this.threshold = threshold;
        parser = new Parser(documentPath, queryPath, relevanceJudgmentPath);
        documents = new Documents(documentPath, stopwordsPath, documentUseStemming);
    }

    public void build()
    {
        //TODO: Bikin load options dulu di sini (baca dari filenya melvin)

        DataTokenizedInstances collection = new DataTokenizedInstances();
        for (int i=0; i<documents.size(); i++)
        {
            DataTokenized temp = new DataTokenized(Arrays.asList(documents.getDocument(i)));
            collection.add(temp);
        }
        vsm = new VSM();
        vsm.makeTFIDFWeightMatrix(documentTFOption, documentUseIDF, documentUseNormalization, collection);

        queries = new Queries(stopwordsPath, queryUseStemming);
        queries.processQueriesFromFile(queryPath);

        List<DocumentRank> result;
        int retrievedSize = 0;
        int relevanceSize = 0;
        double precision = 0;
        double recall = 0;
        double nonInterpolatedAveragePrecision = 0;
        toStringOutput = "";

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
                    if(parser.getRelevanceJudgements().get(q).contains(result.get(d).getDocNum()))
                    {
                        relevanceSize ++;
                        nonInterpolatedAveragePrecision = nonInterpolatedAveragePrecision + ((double)relevanceSize / (double)retrievedSize);
                    }
                }
            }

            precision = (double)relevanceSize / (double)retrievedSize;
            recall = (double)relevanceSize / (double)parser.getRelevanceJudgements().get(q).size();
            nonInterpolatedAveragePrecision = nonInterpolatedAveragePrecision / (double)parser.getRelevanceJudgements().get(q).size();

            toStringOutput += retrievedSize + "\n";
            toStringOutput += precision + "\n";
            toStringOutput += recall + "\n";
            toStringOutput += nonInterpolatedAveragePrecision + "\n";

            for(int i=0; i<retrievedSize; i++)
            {
                toStringOutput += i + "\n";
                toStringOutput += parser.getDocumentsTitle().get(result.get(i).getDocNum()) + "\n";
            }
        }
        toStringOutput += -1 + "\n";
    }

    public void evaluateQuery()
    {

    }

    @Override
    public String toString() {
        return toStringOutput;
    }

    public static void main(String [] args)
    {
        DocumentRanker documentRanker = new DocumentRanker("test_collections/adi/adi.all", "test_collections/adi/query.text", "test_collections/adi/qrels.text", "custom.stopword", 0.1);
        documentRanker.build();
        System.out.println(documentRanker.toString());
    }
}
