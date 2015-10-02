package progs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import parser.Parser;

public class Queries {
	private List<String[]> queries;
	
	public Queries(String queryPath, String stopWordsPath, boolean stem){
		queries = new ArrayList<>();
		
		List<String> stopWords = Preprocessor.loadStopWords(stopWordsPath);
		
		Parser parser = new Parser();
		parser.parseQueries(queryPath);
		queries = Preprocessor.tokenizeDocuments(parser.getQueries(), stopWords);
		if(stem)
			Preprocessor.stem(queries);
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
	
	public static void main(String[] args){
		Queries queries = new Queries("test_collections/cisi/query.text", "custom.stopword", true);
		System.out.println(queries);
	}
}
