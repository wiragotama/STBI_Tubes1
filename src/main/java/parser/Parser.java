package parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by timothy.pratama on 02-Oct-15.
 */
public class Parser {
    private List<String> documents;
    private List<String> queries;
    private List<String> documentsTitle;
    private List<List<Integer>> relevanceJudgements;

    public Parser (String documentsFilepath, String queriesFilepath, String relevanceJudgmentsFilepath)
    {
        parseDocuments(documentsFilepath);
        parseQueries(queriesFilepath);
        parseRelevanceJudgements(relevanceJudgmentsFilepath);
    }

    public Parser()
    {
        documents = new ArrayList<>();
        queries = new ArrayList<>();
        relevanceJudgements = new ArrayList<>();
    }

    public void parseDocuments(String filepath)
    {
        documentsTitle = new ArrayList<>();
        documents = new ArrayList<>();
        String currentString = "";
        String currentDocument = "";
        String currentDocumentTitle = "";
        String title = "";

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            String currentLine = br.readLine();

            while(currentLine != null)
            {
                title = currentLine;
                currentLine = currentLine.toLowerCase();
                if(currentLine.startsWith(".i") ||
                   currentLine.startsWith(".t") ||
                   currentLine.startsWith(".a") ||
                   currentLine.startsWith(".w") ||
                   currentLine.startsWith(".x"))
                {
                   currentString = currentLine;
                }

                if(currentString.startsWith(".i"))
                {
                    if(!currentDocument.equalsIgnoreCase(""))
                    {
                        documents.add(currentDocument.substring(0, currentDocument.length()-1));
                        documentsTitle.add(currentDocumentTitle.substring(0, currentDocumentTitle.length()-1));
                        currentDocument = "";
                        currentDocumentTitle = "";
                    }
                }
                if(currentString.startsWith(".t") && !currentLine.startsWith(".t"))
                {
                    currentDocument += currentLine + " ";
                    currentDocumentTitle += title + " ";
                }
                if(currentString.startsWith(".a") && !currentLine.startsWith(".a"))
                {
                    currentDocument += currentLine + " ";
                }
                if(currentString.startsWith(".w") && !currentLine.startsWith(".w"))
                {
                    for(String word : currentLine.split(" "))
                    {
                        if(!word.equalsIgnoreCase(""))
                        {
                            currentDocument += word + " ";
                        }
                    }
                }

                currentLine = br.readLine();
            }

            if(!currentDocument.equalsIgnoreCase(""))
            {
                documents.add(currentDocument);
                documentsTitle.add(currentDocumentTitle);
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void parseQueries(String filepath)
    {
        queries = new ArrayList<>();
        String currentString = "";
        String currentQuery = "";

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            String currentLine = br.readLine();

            while(currentLine != null)
            {
                currentLine = currentLine.toLowerCase();
                if(currentLine.startsWith(".i") ||
                   currentLine.startsWith(".w") ||
                   currentLine.startsWith(".a") ||
                   currentLine.startsWith(".t"))
                {
                    currentString = currentLine;
                }

                if(currentString.startsWith(".i"))
                {
                    if(!currentQuery.equalsIgnoreCase(""))
                    {
                        queries.add(currentQuery.substring(0, currentQuery.length()-1));
                        currentQuery = "";
                    }
                }
                if(currentString.startsWith(".w") && !currentLine.startsWith(".w"))
                {
                    for(String word : currentLine.split(" "))
                    {
                        if(!word.equalsIgnoreCase(""))
                        {
                            currentQuery += word + " ";
                        }
                    }
                }
                if(currentString.startsWith(".t") && !currentLine.startsWith(".t"))
                {
                    for(String word : currentLine.split(" "))
                    {
                        if(!word.equalsIgnoreCase(""))
                        {
                            currentQuery += word + " ";
                        }
                    }
                }
                if(currentString.startsWith(".a") && !currentLine.startsWith(".a"))
                {
                    for(String word : currentLine.split(" "))
                    {
                        if(!word.equalsIgnoreCase(""))
                        {
                            currentQuery += word + " ";
                        }
                    }
                }

                currentLine = br.readLine();
            }

            if(!currentQuery.equalsIgnoreCase(""))
            {
                queries.add(currentQuery);
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void parseRelevanceJudgements(String filepath)
    {
        relevanceJudgements = new ArrayList<>();

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            String currentLine = br.readLine();
            int maxIndex = queries.size();

            br = new BufferedReader(new FileReader(filepath));
            currentLine = br.readLine();

            for(int i=0; i<maxIndex; i++)
            {
                relevanceJudgements.add(new ArrayList<Integer>());
            }

            while(currentLine != null)
            {
                String words[] = currentLine.split(" ");
                boolean indexFound = false;
                boolean relevantDocumentFound = false;
                int index = 0;
                int relevantDocument = 0;
                for(String word : words)
                {
                    if(!word.equalsIgnoreCase("") && Integer.valueOf(word) > 0 && !indexFound && !relevantDocumentFound)
                    {
                        index = Integer.valueOf(word) - 1;
                        indexFound = true;
                    }
                    else if (!word.equalsIgnoreCase("") && Integer.valueOf(word) > 0 && !relevantDocumentFound && indexFound)
                    {
                        relevantDocument = Integer.valueOf(word) - 1;
                        break;
                    }
                }

                relevanceJudgements.get(index).add(relevantDocument);
                currentLine = br.readLine();
            }

            for(int i=0; i<relevanceJudgements.size(); i++)
            {
                if(relevanceJudgements.get(i).size() == 0)
                {
                    relevanceJudgements.get(i).add(-1);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

    public List<String> getQueries() {
        return queries;
    }

    public void setQueries(List<String> queries) {
        this.queries = queries;
    }

    public List<List<Integer>> getRelevanceJudgements() {
        return relevanceJudgements;
    }

    public void setRelevanceJudgements(List<List<Integer>> relevanceJudgements) {
        this.relevanceJudgements = relevanceJudgements;
    }

    public List<String> getDocumentsTitle() {
        return documentsTitle;
    }

    public void setDocumentsTitle(List<String> documentsTitle) {
        this.documentsTitle = documentsTitle;
    }

    public void printDocuments()
    {
        for(int i=0; i<documents.size(); i++)
        {
            System.out.printf("Document[%d]: %s\n", i, documents.get(i));
        }
    }

    public void printDocumentsTitle()
    {
        for(int i=0; i<documentsTitle.size(); i++)
        {
            System.out.printf("Documents[%d]: %s\n", i, documentsTitle.get(i));
        }
    }

    public void printQueries()
    {
        for(int i=0; i<queries.size(); i++)
        {
            System.out.printf("Query[%d]: %s\n", i, queries.get(i));
        }
    }

    public void printRelevanceJudgements()
    {
        for(int i=0; i<relevanceJudgements.size(); i++)
        {
            System.out.printf("[%d]\n",i);
            for(int j=0; j<relevanceJudgements.get(i).size(); j++)
            {
                System.out.printf("--> %d\n",relevanceJudgements.get(i).get(j));
            }
        }
    }

    public static void main(String [] args)
    {
        Parser parser = new Parser();
        System.out.println("===== Test Collections CISI =====");
        parser.parseDocuments("test_collections/cisi/cisi.all");
        parser.parseQueries("test_collections/cisi/query.text");
        parser.parseRelevanceJudgements("test_collections/cisi/qrels.text");
        parser.printDocumentsTitle();

        System.out.println("\n===== Test Collections ADI =====");
        parser.parseDocuments("test_collections/adi/adi.all");
        parser.parseQueries("test_collections/adi/query.text");
        parser.parseRelevanceJudgements("test_collections/adi/qrels.text");
//        parser.printQueries();
//        parser.printRelevanceJudgements();
        parser.printDocumentsTitle();
    }
}
