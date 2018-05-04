package edu.uw.blioce.MSD_TFIDF;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App {
	
	/** The first of two data files. This is the largest with most of the data. */
	private static final String TRAIN_DATA_FILE = "src/mxm_dataset_train.txt";
	
	/** The second of two data files. The 5,000 words are the same as in previous file. */
//	private static final String TEST_DATA_FILE = "src/mxm_dataset_test.txt";
	
	private static Scanner scan;

	private static List<String> words;
	
	private static List<String> trackIDs;

	public static void main( String[] args ) throws IOException {
		scan = new Scanner(new File(TRAIN_DATA_FILE));
		
		words = new ArrayList<String>();
		try {
			populateWordList();
		} catch(FileNotFoundException e) {
			System.out.println("Could not find file: " + TRAIN_DATA_FILE + e);
			System.exit(1);
		}

		// Populate the trackIDs from the train dataset
		trackIDs = new ArrayList<String>();
		populateTrackIDs();
		
		/* TODO - INCORPORATE OTHER FILE ONCE WE CAN SUCCESSFULLY PROCESS THIS FILE. */
		
//		// Reset the scanner to the test data file and add those trackIds
//		try {
//			scan = new Scanner(new File(TEST_DATA_FILE));
//		} catch (FileNotFoundException e) {
//			System.out.println("Could not find file: " + TEST_DATA_FILE + e);
//			System.exit(1);
//		}
//		populateTrackIDs();
		scan.close();

	}

	/** 
	 * This method parses the list of words in the data file and stores them
	 * in an indexed ArrayList object.
	 * @return Returns the Scanner in the current state (to prevent multiple scanning).
	 * @throws FileNotFoundException Error is thrown if file not found.
	 */
	private static void populateWordList() throws FileNotFoundException {
		scan = new Scanner(new File(TRAIN_DATA_FILE));

		String line = "";
		while(scan.hasNextLine()) {
			line = scan.nextLine();
			
			// The line with comma-separated words begins with % character (only 1 line).
			if(line.charAt(0) == '%') {
				line = line.substring(1);
				String[] listWords = line.split(",");
				for(String s: listWords) words.add(s);
				break;
			} 
		}
	}
	
	/**
	 * This method scans the data file and stores the Million Songs Dataset
	 * trackId (the first ID in the line, not the second) and stores it in an
	 * ArrayList.
	 */
	private static void populateTrackIDs() {
		String line = "";
		while(scan.hasNextLine()) {
			line = scan.nextLine();
			if(line.charAt(0) != '#' && line.charAt(0) != '%') {
				trackIDs.add(line.substring(0, line.indexOf(",")));
			}
		}
	}
	
	/** 
	 * This method returns the data that is on a given line of text including
	 * the MSD trackId and each word and the frequency it appears in the song.
	 * @param theLine The line of text data to parse.
	 * @return Returns a String of the data on the line. 
	 */
	private static String parseLine(String theLine) {
		StringBuilder sb = new StringBuilder();
		if(theLine.charAt(0) == '#' || theLine.charAt(0) == '%') return null;
		
		String[] tokens = theLine.split(",");
		String MSD_id = tokens[0];
		
		sb.append("{" + MSD_id + ": ");
		for(int i = 2; i < tokens.length; i++) {
			String wordToken = tokens[i];
			String[] data = wordToken.split(":");
			Integer indexOfWord = Integer.valueOf(data[0]);
			
			// Appends a string of the word (in string form, not number) and the count
			sb.append("[" + words.get(indexOfWord - 1) + ": " + data[1] + "], ");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.deleteCharAt(sb.length() - 1);
		sb.append("}\n");
		return sb.toString();
	}
	
	/**
	 * This method returns the data the is associated with this trackID including
	 * each word and the frequency it appears in the song.
	 * @param trackID A String of the MSD trackID.
	 * @return Returns a String of the data if trackID is found, else returns null.
	 */
	private static String parseLineByID(String trackID) {
		Scanner scan;
		try {
			scan = new Scanner(new File(TRAIN_DATA_FILE));
			String line = "";
			while(scan.hasNextLine()) {
				if(line.startsWith(trackID)) {
					scan.close();
					return parseLine(line);
				}
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file: " + TRAIN_DATA_FILE);
			System.out.println(e);
			System.exit(1);
		}
		return null;
	}
}

class WordInfo {
	Integer count = 0;
	Integer appearsIn = 0;
}
