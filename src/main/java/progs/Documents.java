package progs;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import parser.Parser;

public class Documents {
	private List<String[]> documents;
	
	public Documents(String filePath){
		documents = new ArrayList<>();
		
		Parser parser = new Parser();
		parser.parseDocuments(filePath);
		tokenizeDocuments(parser.getDocuments());
	}
	
	private void tokenizeDocuments(List<String> documents){
		for(String document : documents){
			this.documents.add(tokenize(document));
		}
	}
	
	private String[] tokenize(String input){
		String[] token = null;
		
    	Reader reader = new StringReader(input);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		
		for(List<HasWord> sentence : dp){
			token = new String[sentence.size()];
			for(int i = 0; i < sentence.size(); i++){
				token[i] = sentence.get(i).word();
			}
		}
		
		return token;
	}
	
	public String toString(){
		String output = "";
		
		for(int i = 0; i < documents.size(); i++){
			String[] document = documents.get(i);
			output = output + "document " + i + ": " + Arrays.asList(document).toString() + "\n";
		}
		
		return output;
	}
	
	public List<String[]> getDocuments(){
		return this.documents;
	}
	
	public String[] getDocument(int index){
		return this.documents.get(index);
	}
	
	public static void main(String[] args){
		Documents docs = new Documents("test_collections/cisi/cisi.all");
		System.out.println(docs);
	}
}