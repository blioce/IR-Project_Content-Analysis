/**
 * 
 */
package edu.uw.blioce.MSD_TFIDF;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * TODO - Integrate stopword removal into this program.
 * 
 * This program reads in MSD data files and will do processing to write a
 * file that will contain each word and the number of tracks that this word
 * appears in. This is useful for computing the IDF (Inverted Document 
 * Frequency) of certain terms in the data. 
 * 
 * @author Brandon Lioce
 * @version May 8th, 2018
 * @class TCSS 554 - Spring 2018
 * @assignment Content Analysis
 */
public class AllTrackInfo {
	
	/** The training data set. This file has the largest amount of data. */
	private static final String MSD_DATA_FILE_1 = "src/mxm_dataset_train.txt";
	
	/** The testing data set. List of words are the same as in training set. */
	private static final String MSD_DATA_FILE_2 = "src/mxm_dataset_test.txt";
	
	/** The file that will be written to with the word and frequency data. */
	private static final String OUT_FILE = "Updated_Word_Data.txt";
	
	/** A scanner object. */
	private static Scanner myScanner;
	
	/** The list of unique words. */
	private static List<String> myWords;
	
	/** The mapping of words to the number of tracks in which they appear. */
	private static Map<String, Integer> wordsToTrackFrequency;

	/**
	 * The main entry of the program; begins the word frequency calculation and 
	 * data write to a file. 
	 * @param args No arguments are used to run.
	 */
	public static void main(String[] args) {
		myWords = new ArrayList<String>();
		wordsToTrackFrequency = new HashMap<String, Integer>();

		try {
			scanFile(1);
			//scanFile(2); // Uncomment if you want to consider values in the second data file.
		} catch(FileNotFoundException e) {
			System.out.println("Error while attempting to read file.\n" + e.getMessage());
		}
		
		try {
			writeDataFile();
		} catch(IOException e) {
			System.out.println("Error while attempting to write data file.\n" + e.getMessage());
		}
	}
	
	/**
	 * This method will scan through the Million Songs Dataset and parse each 
	 * line which corresponds to an individual track. Unique words are printed
	 * on a single line which begins with the % symbol. Lines that start with 
	 * a # symbol are comment lines and are disregarded. Lines that do not begin
	 * with a special character are lines that have the MSD song id, MXM song id, 
	 * and then tokens in the form of wordIndex:count for each word that appears
	 * in the song and is in the top 5,000 words. 
	 * 
	 * @author Brandon Lioce
	 * 
	 * @param doc Whether this is document 1 or 2 (train or test).
	 * @throws FileNotFoundException Throws an exception if the scanner cannot find the document.
	 */
	private static void scanFile(int doc) throws FileNotFoundException {
		if(doc == 1) myScanner = new Scanner(new File(MSD_DATA_FILE_1));
		else myScanner = new Scanner(new File(MSD_DATA_FILE_2));
		String line = "";
		while(myScanner.hasNextLine()) {
			line = myScanner.nextLine();

			// The line with comma-separated words begins with % character (only 1 line).
			// If it is document 2, we will not scan the words as they are the same for
			// document 1.
			if(line.charAt(0) == '%' && doc == 1) {
				line = line.substring(1);
				String[] listWords = line.split(",");
				for(String s: listWords) myWords.add(s);
			} else if(line.charAt(0) != '%' && line.charAt(0) != '#') {
				// Else if it is a data line...
				String[] tokens = line.split(",");
				for(int i = 2; i < tokens.length; i++) {
					String token = tokens[i];
					String index = token.substring(0, token.indexOf(":"));
					String word = myWords.get(Integer.valueOf(index) - 1);
					if(wordsToTrackFrequency.containsKey(word)) {
						wordsToTrackFrequency.put(word, wordsToTrackFrequency.get(word) + 1);
					} else wordsToTrackFrequency.put(word, 1);
				}
			}
		}
		myScanner.close();
	}
	
	/**
	 * This method writes the data (each word and the number of tracks in which it appears)
	 * to a file in the form of the word followed by an integer. This data is separated by
	 * lines, one for each word and integer.
	 * 
	 * @author Brandon Lioce
	 * 
	 * @throws IOException Throws an exception if there was an error while writing to the file.
	 */
	private static void writeDataFile() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(OUT_FILE));
		for(String s: wordsToTrackFrequency.keySet()) {
			bw.write(s + " " + wordsToTrackFrequency.get(s) + "\n");
		}
		bw.close();
	}
}
