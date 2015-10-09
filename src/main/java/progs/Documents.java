package progs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import parser.Parser;

public class Documents {
	private List<String[]> documents;
	
	/**
	 * Constructor
	 * @param documentPath
	 * @param stopWordsPath
	 */
	public Documents(String documentPath, String stopWordsPath, boolean stem){
		documents = new ArrayList<>();
		
		List<String> stopWords = Preprocessor.loadStopWords(stopWordsPath);
		
		Parser parser = new Parser();
		parser.parseDocuments(documentPath);
		documents = Preprocessor.tokenizeDocuments(parser.getDocuments(), stopWords);
		if(stem)
			Preprocessor.stem(documents);
	}
		
	/**
	 * Returns a string representation of the object
	 */
	public String toString(){
		String output = "";
		
		for(int i = 0; i < documents.size(); i++){
			String[] document = documents.get(i);
			output = output + "document " + i + ": " + Arrays.asList(document).toString() + "\n";
		}
		
		return output;
	}
	
	/**
	 * Get all tokenized documents
	 * @return documents
	 */
	public List<String[]> getDocuments(){
		return this.documents;
	}
	
	/**
	 * Get document at a specified index
	 * @param index
	 * @return document
	 */
	public String[] getDocument(int index){
		return this.documents.get(index);
	}

	/**
	 *
	 * @return documents size
	 */
	public int size()
	{
		return this.documents.size();
	}

	/**
	 * Clear memory
	 */
	public void clear(){
		this.documents.clear();
	}
	
	/**
	 * Get terms from all combined documents
	 * @return List of terms
	 */
	public List<String> getTerms(){
		List<String> terms = new ArrayList<>();
		
		for(String[] document : documents){
			for(int i = 0; i < document.length; i++){
				terms.add(document[i]);
			}
		}
		
		return terms;
	}
	
	public static void main(String[] args){
		Documents docs = new Documents("test_collections/cisi/cisi.all", "custom.stopword", true);
		System.out.println(docs);
		System.out.println(docs.getTerms());
	}
}
