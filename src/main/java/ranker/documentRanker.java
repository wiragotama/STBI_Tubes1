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
    private int TFOption;
    private boolean useIDF;
    private boolean useNormalization;
    private boolean useStemming;
    private double threshold;

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

    public int getTFOption() {
        return TFOption;
    }

    public void setTFOption(int TFOption) {
        this.TFOption = TFOption;
    }

    public boolean isUseIDF() {
        return useIDF;
    }

    public void setUseIDF(boolean useIDF) {
        this.useIDF = useIDF;
    }

    public boolean isUseNormalization() {
        return useNormalization;
    }

    public void setUseNormalization(boolean useNormalization) {
        this.useNormalization = useNormalization;
    }

    public String getStopwordsPath() {
        return stopwordsPath;
    }

    public void setStopwordsPath(String stopwordsPath) {
        this.stopwordsPath = stopwordsPath;
    }

    public boolean isUseStemming() {
        return useStemming;
    }

    public void setUseStemming(boolean useStemming) {
        this.useStemming = useStemming;
    }

    DocumentRanker(String documentPath, String queryPath, String relevanceJudgmentPath, String stopwordsPath, double threshold)
    {
        this.documentPath = documentPath;
        this.queryPath = queryPath;
        this.relevanceJudgmentPath = relevanceJudgmentPath;
        this.stopwordsPath = stopwordsPath;
        this.TFOption = 1;
        this.useIDF = true;
        this.useNormalization = false;
        this.useStemming = true;
        this.threshold = threshold;
        parser = new Parser(documentPath, queryPath, relevanceJudgmentPath);
        documents = new Documents(documentPath, stopwordsPath, useStemming);
    }

    public void buildVSM(String optionPath)
    {
        //TODO: Bikin load options dulu di sini (baca dari filenya melvin)

        DataTokenizedInstances collection = new DataTokenizedInstances();
        for (int i=0; i<documents.size(); i++)
        {
            DataTokenized temp = new DataTokenized(Arrays.asList(documents.getDocument(i)));
            collection.add(temp);
        }
        vsm = new VSM();
        vsm.makeTFIDFWeightMatrix(TFOption, useIDF, useNormalization, collection);

        queries = new Queries(queryPath, stopwordsPath, useStemming);

        List<DocumentRank> result;
        int retrievedSize = 0;
        int relevanceSize = 0;
        double precision = 0;
        double recall = 0;
        double nonInterpolatedAveragePrecision = 0;

        for(int q=0; q<queries.getQueries().size(); q++)
        {
            retrievedSize = 0;
            relevanceSize = 0;
            nonInterpolatedAveragePrecision = 0;
            precision = 0;
            recall = 0;
            result = vsm.queryTask(queries.getQuery(q), TFOption, useIDF, useNormalization);
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

            System.out.println(retrievedSize);
            System.out.println(precision);
            System.out.println(recall);
            System.out.println(nonInterpolatedAveragePrecision);

            for(int i=0; i<retrievedSize; i++)
            {
                System.out.println(i);
                System.out.println(parser.getDocumentsTitle().get(result.get(i).getDocNum()));
            }

            //TODO: Ouput hasil
        }
    }

    public static void main(String [] args)
    {
        DocumentRanker documentRanker = new DocumentRanker("test_collections/adi/adi.all", "test_collections/adi/query.text", "test_collections/adi/qrels.text", "custom.stopword", 0.1);
        documentRanker.buildVSM("");
    }
}
