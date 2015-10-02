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
    private List<String> relevanceJudgements;

    public Parser() {
        documents = new ArrayList<>();
        queries = new ArrayList<>();
        relevanceJudgements = new ArrayList<>();
    }

    public void parseDocuments(String filepath)
    {
        String currentString = "";
        String currentDocument = "";

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            String currentLine = br.readLine();
            int i = 0;

            while(currentLine != null)
            {
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
                        i++;
                        documents.add(currentDocument.substring(0, currentDocument.length()-1));
                        currentDocument = "";
                    }
                }
                if(currentString.startsWith(".t") && !currentLine.startsWith(".t"))
                {
                    currentDocument += currentLine + " ";
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
                if(currentString.startsWith(".x") && !currentLine.startsWith(".x"))
                {
                }

                currentLine = br.readLine();
            }

            if(currentDocument.equalsIgnoreCase("") == false)
            {
                documents.add(currentDocument);
            }

        } catch (IOException e)
        {
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

    public List<String> getRelevanceJudgements() {
        return relevanceJudgements;
    }

    public void setRelevanceJudgements(List<String> relevanceJudgements) {
        this.relevanceJudgements = relevanceJudgements;
    }
    
    public void printDocuments()
    {
        for(int i=0; i<documents.size(); i++)
        {
            System.out.printf("Document[%d]: %s\n", i, documents.get(i));
        }
    }

    public static void main(String [] args)
    {
        Parser parser = new Parser();
        parser.parseDocuments("test_collections/cisi/cisi.all");
        parser.printDocuments();
    }
}
