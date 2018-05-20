package edu.uw.blioce.MSD_TFIDF;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * This program computes the TF*IDF values for a given track using the Million Songs
 * Dataset. This dataset is split into two files, one for training and one for testing.
 * Each file is implemented as a bag of words and the words are the same for each file. 
 * This program writes the TF*IDF value for each word (excluding stopwords) in a track.
 * 
 * @author Brandon Lioce
 * @version May 8th, 2018
 * @class TCSS 554 - Spring 2018
 * @assignment Content Analysis
 */
public class App {

	/** The training data set. This file has the largest amount of data. */
	private static final String MSD_DATA_FILE_1 = "src/mxm_dataset_train.txt";
	
	/** The testing data set. List of words are the same as in training set. */
//	private static final String MSD_DATA_FILE_2 = "src/mxm_dataset_test.txt";
	
	/** The list of drugs words and non-drug words for balancing. */
	private static final String DRUG_WORDS = "src/drug_words_balanced.txt";
	
	/** The list of violent words and non-violent words for balancing. */
	private static final String VIOLENT_WORDS = "src/violent_words_balanced.txt";
	
	/** The file that contains the words and the number of tracks they appear in. */
	private static final String WORD_DATA = "Word_Data.txt";
	
	/** The file with a list of stopwords that have little meaning or significance. */
	private static final String STOPWORDS = "src/stopwords.txt";
	
	/** The output file where the TF*IDF data should be written. */
	private static final String TFIDF_OUT = "TFIDF_drug_good_words.txt";

	/** A scanner object. */
	private static Scanner myScanner;
	
	/** The target words to isolate. */
	private static Set<String> myTargetWords;

	/** The list of unique words. */
	private static List<String> myWords;

	/** The word and track occurrence mapping. */
	private static Map<String, Integer> myWordInfo;
	
	/** A set of stopwords to disregard. */
	private static Set<String> myStopwords;
	
	/** A list of only the bad words. */
	private static Set<String> badwords;

	/**
	 * The main entry to the program where the process of populating the known
	 * words, removal of stopwords, and computation of TF*IDF will take place.
	 * 
	 * @param args No arguments are used to run this program.
	 */
	public static void main(String[] args) {
		try {
			myScanner = new Scanner(new File("src/just_drugs.txt"));
			badwords = new HashSet<String>();
			while(myScanner.hasNext()) {
				badwords.add(myScanner.next());
			}
			
			populateTargetWords(DRUG_WORDS);
			populateWordList();
			populateStopwords();
			populateWordInfo();
		} catch(FileNotFoundException e) {
			System.out.println("Could not find file.\n" + e.getMessage());
			System.exit(1);
		}
		

		

		try {
			/* WARNING -- CALLING THIS METHOD WILL APPEND TO THE FILE. TO START FRESH,
			 * YOU MUST DELETE THE TFIDF_OUT.TXT FILE FIRST!
			 */
			computeTFIDF(MSD_DATA_FILE_1);
			//computeTFIDF(MSD_DATA_FILE_2);
		} catch (IOException e) {
			System.out.println("Error while attempting to write to file.\n" + e.getMessage());
			System.exit(1);
		}
		/* TODO - INCORPORATE OTHER FILE*/
	}
	
	/**
	 * This method populates words from a file that are the words we are trying
	 * to isolate from the overall list of 5,000 words. This will dramatically focus
	 * the task on identifying drug or violent words in a song and greatly reduce 
	 * the output of the TF*IDF files. 
	 * 
	 * @param theFile The file of drug or violent words.
	 * @throws FileNotFoundException An exception is thrown if the file is not found. 
	 */
	private static void populateTargetWords(final String theFile) throws FileNotFoundException {
		myScanner = new Scanner(new File(theFile));
		myTargetWords = new HashSet<String>();
		while(myScanner.hasNext()) myTargetWords.add(myScanner.next());
	}
	

	/** 
	 * This method parses the list of words in the data file and stores them
	 * in an indexed ArrayList object.
	 * @throws FileNotFoundException Error is thrown if file not found.
	 */
	private static void populateWordList() throws FileNotFoundException {
		myScanner = new Scanner(new File(MSD_DATA_FILE_1));
		myWords = new ArrayList<String>();

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
	 * This method reads the stopwords from the file. These will be used to 
	 * reduce the amount of unnecessary data before processing the TF*IDF.
	 * 
	 * @throws FileNotFoundException Throws an exception if the file is not found. 
	 */
	private static void populateStopwords() throws FileNotFoundException {
		myScanner = new Scanner(new File(STOPWORDS));
		myStopwords = new HashSet<String>();
		while(myScanner.hasNext()) myStopwords.add(myScanner.next());
		myScanner.close();
	}

	/** 
	 * This method reads the word info (word and the number of tracks it appears in)
	 * from the file and loads it into a HashMap.
	 * 
	 * @throws FileNotFoundException Throws an exception if the file is not found.
	 */
	private static void populateWordInfo() throws FileNotFoundException {
		myScanner = new Scanner(new File(WORD_DATA));
		myWordInfo = new HashMap<String, Integer>();
		String line = "";
		while(myScanner.hasNextLine()) {
			line = myScanner.nextLine();
			String[] tokens = line.split("\\s+");
			
			// ONLY LOOK AT THE TARGETED WORDS
			if(myTargetWords.contains(tokens[0])) {
				myWordInfo.put(tokens[0], Integer.valueOf(tokens[1]));
			}
			
		}
		myScanner.close();
	}
	
	/** 
	 * This method computes the TF*IDF values for each line in the data file
	 * (for each track). This uses the wordInfo data (from Word Data.txt file)
	 * to computer the IDF.
	 * @param fileName The file to scan and compute TF*IDF.
	 * @throws IOException Returns an error if could not write file.
	 */
	private static void computeTFIDF(final String fileName) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(TFIDF_OUT, false));
		myScanner = new Scanner(new File(fileName));

		String line = "";
		while(myScanner.hasNextLine()) {
			line = myScanner.nextLine();
			bw.write(parseLine(line));
		}
		bw.close();
		myScanner.close();
	}

	/** 
	 * This method returns the data that is on a given line of text including
	 * the MSD trackId and each word and the frequency it appears in the song.
	 * @param theLine The line of text data to parse.
	 * @return Returns a String of the data on the line. 
	 */
	private static String parseLine(final String theLine) {
		boolean isBad = false;
		
		String res = "";
		if(theLine.charAt(0) != '#' && theLine.charAt(0) != '%') {
			String[] tokens = theLine.split(",");
			String MSD_id = tokens[0];
			
			res = MSD_id + ",";
			for(int i = 2; i < tokens.length; i++) {
				String wordToken = tokens[i];
				String[] data = wordToken.split(":");
				Integer indexOfWord = Integer.valueOf(data[0]);
				String word = myWords.get(indexOfWord - 1);
				
				// ONLY LOOK AT TARGETED WORDS
				if(myTargetWords.contains(word) && !badwords.contains(word)) {
					//if(badwords.contains(word)) isBad = true;
					
					double tf = Double.valueOf(data[1]);
					double idf = Math.log10(210519.0 / myWordInfo.get(word));

					res += word + ":" + tf*idf +",";
				} 
			}
			res += "\n";
		}
		//if(isBad) return res;
		//else return "";
		return res;
	}	
}