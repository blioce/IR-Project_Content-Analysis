package edu.uw.blioce.MSD_TFIDF;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import opennlp.tools.stemmer.PorterStemmer;

/**
 * This class scans the words in the MSD file and compares it to a given 
 * list of words (that we created) that are believed to be violent or drug
 * related. It will then stem the words and scan the bag of words to see 
 * if they are present. If they are present, they are written to a 
 * confirmed words file for use later on in the project. 
 * 
 * @author Brandon Lioce
 * @version May 11th, 2018
 * @class TCSS 554 - Spring 2018
 * @assignment Content Analysis
 */
public class ConfirmWords {
	
	/** The scanner object. */
	private static Scanner myScanner;
	
	/** The list of words in the MSD dataset. */
	private static Set<String> myWords;

	/**
	 * @param args No args are used.
	 */
	public static void main(String[] args) {
		try{
			populateWords();			
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file.\n" + e.toString());
		}
		
		try {
			confirmWords();
		} catch(IOException e) {
			System.out.println("Could not write to file.\n" + e.toString());
		}
	}
	
	/**
	 * This method populates a set of words from the MSD data file.
	 * 
	 * @author Brandon Lioce
	 * 
	 * @throws FileNotFoundException Exception if file could not be found.
	 */
	private static void populateWords() throws FileNotFoundException {
		myScanner = new Scanner(new File("src/mxm_dataset_train.txt"));
		myWords = new HashSet<String>();

		String line = "";
		while(myScanner.hasNextLine()) {
			line = myScanner.nextLine();

			// The line with comma-separated words begins with % character (only 1 line).
			if(line.charAt(0) == '%') {
				line = line.substring(1);
				String[] listWords = line.split(",");
				for(String s: listWords) myWords.add(s);
				break;
			} 
		}
		myScanner.close();
	}
	
	/**
	 * This method compares the list of possibly suggestive words with the words
	 * that are in the word set and writes them to a file if they are found in
	 * in the set.
	 * 
	 * @author Brandon Lioce
	 * 
	 * @throws IOException Exception if a word could not be written to the file. 
	 */
	private static void confirmWords() throws IOException {
		PorterStemmer stemmer = new PorterStemmer();
		BufferedWriter bw = new BufferedWriter(new FileWriter("confirmed_words.txt"));
		myScanner = new Scanner(new File("src/words_list.txt"));
		
		while(myScanner.hasNext()) {
			String word = myScanner.next().toLowerCase();
			if(myWords.contains(word)) bw.write(word + "\n");
			else {
				word = stemmer.stem(word);
				if(myWords.contains(word)) bw.write(word + "\n");
			}
		}
		bw.close();
	}
}
