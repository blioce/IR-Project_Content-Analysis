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

//	private static List<String> trackIDs;

	private static Map<String, Integer> wordInfo;

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
		//trackIDs = new ArrayList<String>();
		//populateTrackIDs();

		// This will take in all the info about the occurrence of each word. 
		populateWordInfo();

		// Writes the data to a file in the same format that the data is read from the data file.
		// trackId,wordIndex:idf,wordIndex:idf,.....
		computeTFIDF();

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

//	/**
//	 * This method scans the data file and stores the Million Songs Dataset
//	 * trackId (the first ID in the line, not the second) and stores it in an
//	 * ArrayList.
//	 */
//	private static void populateTrackIDs() {
//		String line = "";
//		while(scan.hasNextLine()) {
//			line = scan.nextLine();
//			if(line.charAt(0) != '#' && line.charAt(0) != '%') {
//				trackIDs.add(line.substring(0, line.indexOf(",")));
//			}
//		}
//	}

	private static void populateWordInfo() throws FileNotFoundException {
		Scanner scan = new Scanner(new File("Word Data.txt"));
		wordInfo = new HashMap<String, Integer>();
		String line = "";
		while(scan.hasNextLine()) {
			line = scan.nextLine();
			String[] tokens = line.split("\\s+");
			wordInfo.put(tokens[0], Integer.valueOf(tokens[2]));
		}
		scan.close();
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
			double tf = Double.valueOf(data[1]);
			double idf = Math.log10(210519.0 / wordInfo.get(words.get(indexOfWord)));

			// Appends a string of the word (in string form, not number) and the count
			sb.append("[" + words.get(indexOfWord - 1) + ": " + (tf*idf) + "], ");


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

	/** 
	 * This method computes the TF*IDF values for each line in the data file
	 * (for each track). This uses the wordInfo data (from Word Data.txt file)
	 * to computer the IDF.
	 * @throws IOException Returns an error if could not write file.
	 */
	private static void computeTFIDF() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter("TF*IDF.txt"));
		String line = "";
		while(scan.hasNextLine()) {
			line = scan.nextLine();

			if(line.charAt(0) != '#' && line.charAt(0) != '%') {
				String[] tokens = line.split(",");
				String MSD_id = tokens[0];

				String res = MSD_id + ",";
				for(int i = 2; i < tokens.length; i++) {
					String wordToken = tokens[i];
					String[] data = wordToken.split(":");
					Integer indexOfWord = Integer.valueOf(data[0]);
					double tf = Double.valueOf(data[1]);
					double idf = Math.log10(210519.0 / wordInfo.get(words.get(indexOfWord - 1)));

					res += indexOfWord + ":" + tf*idf +",";
				}
				res += "\n";
				bw.write(res);
			}
		}
		bw.close();
	}
}

class WordInfo {
	Integer count = 0;
	Integer appearsIn = 0;
}
