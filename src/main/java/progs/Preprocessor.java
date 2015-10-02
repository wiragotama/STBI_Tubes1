package progs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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

import vsm.VSM;

public final class Preprocessor {
	private Preprocessor(){}
	
	/**
	 * Tokenize all documents
	 * @param documents
	 * @param stopwords
	 */
	public static List<String[]> tokenizeDocuments(List<String> documents, List<String> stopwords){
		List<String[]> tokenizedDocuments = new ArrayList<>();
		
		for(String document : documents){
			tokenizedDocuments.add(tokenize(document, stopwords));
		}
		
		return tokenizedDocuments;
	}
	
	/**
	 * Tokenize String, including lowercasing token and stopwords removal
	 * @param input
	 * @param stopWords
	 * @return Array of tokenized String
	 */
	private static String[] tokenize(String input, List<String> stopWords){
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
	public static List<String> loadStopWords(String filePath){
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
	public static void stem(List<String[]> documents){
		TokenizerFactory f1 = IndoEuropeanTokenizerFactory.INSTANCE;
		TokenizerFactory porter = new PorterStemmerTokenizerFactory(f1);
		
		for(int i = 0; i < documents.size(); i++){
			for(int j = 0; j < documents.get(i).length; j++){
				Tokenization stem = new Tokenization(documents.get(i)[j], porter);
				documents.get(i)[j] = stem.token(0);
			}
		}
	}
}
