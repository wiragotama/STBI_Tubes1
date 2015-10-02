package progs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.aliasi.tokenizer.EnglishStopTokenizerFactory;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.LowerCaseTokenizerFactory;
import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;
import com.aliasi.tokenizer.StopTokenizerFactory;
import com.aliasi.tokenizer.Tokenization;
import com.aliasi.tokenizer.TokenizerFactory;
import com.google.common.collect.ImmutableSet;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import parser.Parser;
import vsm.VSM;

public class Documents {
	private List<String[]> documents;
	
	/**
	 * Constructor
	 * @param documentPath
	 * @param stopWordsPath
	 */
	public Documents(String documentPath, String stopWordsPath){
		documents = new ArrayList<>();
		
		List<String> stopWords = loadStopWords(stopWordsPath);
		
		Parser parser = new Parser();
		parser.parseDocuments(documentPath);
		tokenizeDocuments(parser.getDocuments(), stopWords);
		stem();
	}
	
	/**
	 * Tokenize all documents
	 * @param documents
	 * @param stopwords
	 */
	private void tokenizeDocuments(List<String> documents, List<String> stopwords){
		for(String document : documents){
			this.documents.add(tokenize(document, stopwords));
		}
	}
	
	/**
	 * Tokenize String, including lowercasing token and stopwords removal
	 * @param input
	 * @param stopWords
	 * @return Array of tokenized String
	 */
	private String[] tokenize(String input, List<String> stopWords){
		TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
        TokenizerFactory lowercasetokenizer = new LowerCaseTokenizerFactory(TOKENIZER_FACTORY);
        
        Set<String> stopwords = ImmutableSet.copyOf(stopWords);
        StopTokenizerFactory fstop = new StopTokenizerFactory(lowercasetokenizer, stopwords);
        TokenizerFactory tokenizer = new EnglishStopTokenizerFactory(fstop);
        
    	Tokenization tk = new Tokenization(input, tokenizer);
    	String result[] = tk.tokens();
    	
    	return result;
	}
	
	/**
	 * Load stop-words list from external file
	 * @param filePath
	 * @return List of stopwords
	 */
	private List<String> loadStopWords(String filePath){
		File stopWords = new File(filePath);
		String line = null;
		List<String> stopwords = new ArrayList<>();
		BufferedReader reader = null;
				
		try {
			reader = new BufferedReader(new FileReader(stopWords));
		} catch (FileNotFoundException e) {
			System.out.println(filePath + " is not found");
		}
		
		try {
			while((line = reader.readLine()) != null){
				String split[] = line.split(" ");
				stopwords.add(split[0]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(VSM.class.getName()).log(Level.SEVERE, null, ex);
        }
		 
		return stopwords;
	}
	
	/**
	 * Stem String using Porter Stemmer
	 */
	private void stem(){
		TokenizerFactory f1 = IndoEuropeanTokenizerFactory.INSTANCE;
		TokenizerFactory porter = new PorterStemmerTokenizerFactory(f1);
		
		for(int i = 0; i < documents.size(); i++){
			for(int j = 0; j < documents.get(i).length; j++){
				Tokenization stem = new Tokenization(documents.get(i)[j], porter);
				documents.get(i)[j] = stem.token(0);
			}
		}
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
	
	public static void main(String[] args){
		Documents docs = new Documents("test_collections/cisi/cisi.all", "custom.stopword");
		System.out.println(docs);
	}
}
