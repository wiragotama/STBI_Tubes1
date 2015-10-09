package progs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import parser.Parser;

public class Queries {
	private List<String[]> queries;
	private List<String> stopWords;
	private boolean stem;
	
	public Queries(String stopWordsPath, boolean stem){
		queries = new ArrayList<>();
		stopWords = Preprocessor.loadStopWords(stopWordsPath);
		this.stem = stem;		
	}
	
	/**
	 * Preprocess queries originated from external file for experiment option
	 * @param queryPath path of query file
	 */
	public void processQueriesFromFile(String queryPath){
		Parser parser = new Parser();
		parser.parseQueries(queryPath);
		queries = Preprocessor.tokenizeDocuments(parser.getQueries(), stopWords);
		if(this.stem)
			Preprocessor.stem(queries);
	}
	
	/**
	 * Preprocess queries inputted by users for interactive option
	 * @param query string of query
	 */
	public void processQueryFromString(String query){
		List<String> queries = new ArrayList<>();
		
		queries.add(query);
		this.queries = Preprocessor.tokenizeDocuments(queries, stopWords);
		if(this.stem)
			Preprocessor.stem(this.queries);
	}
	
	/**
	 * Returns a string representation of the object
	 */
	public String toString(){
		String output = "";
		
		for(int i = 0; i < queries.size(); i++){
			String[] document = queries.get(i);
			output = output + "query " + i + ": " + Arrays.asList(document).toString() + "\n";
		}
		
		return output;
	}
	
	/**
	 * Get all tokenized queries
	 * @return documents
	 */
	public List<String[]> getQueries(){
		return this.queries;
	}
	
	/**
	 * Get query at a specified index
	 * @param index
	 * @return document
	 */
	public String[] getQuery(int index){
		return this.queries.get(index);
	}
	
	/**
	 * Clear memory
	 */
	public void clear(){
		this.queries.clear();
	}
	
	public static void main(String[] args){
		Queries queries = new Queries("custom.stopword", true);
		queries.processQueriesFromFile("test_collections/cisi/query.text");
		queries.processQueryFromString("What problems and concerns are there in making up descriptive titles? What difficulties are involved in automatically retrieving articles from approximate titles?  What is the usual relevance of the content of articles to their titles?");
		System.out.println(queries);
	}
}
