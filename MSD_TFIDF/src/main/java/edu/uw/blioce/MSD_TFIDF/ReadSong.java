package edu.uw.blioce.MSD_TFIDF;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import opennlp.tools.stemmer.PorterStemmer;

public class ReadSong {

	/** The scanner object. */
	private static Scanner myScanner;

	/** The list of words in the MSD dataset. */
	private static List<String> myWords;

	/** The mapping of words to the number of tracks in which they appear. */
	private static Map<Integer, Integer> lyrics;
	
	/** The input raw song lyrics. */
	private static final String IN_FILE = "src/Test_raw_violent_song.txt";
	
	/** The file that will be written to in a bag of words format. */
	private static final String OUT_FILE = "violent_song_bag_of_words.txt";

	/**
	 * This method populates a set of words from the MSD data file.
	 * 
	 * @throws FileNotFoundException
	 *             Exception if file could not be found.
	 */
	private static void read5000words() throws FileNotFoundException {
		myScanner = new Scanner(new File("src/mxm_dataset_train.txt"));
		myWords = new LinkedList<String>();

		String line = "";
		while (myScanner.hasNextLine()) {
			line = myScanner.nextLine();

			// The line with comma-separated words begins with % character (only 1 line).
			if (line.charAt(0) == '%') {
				line = line.substring(1);
				String[] listWords = line.split(",");
				for (String s : listWords)
					myWords.add(s);
				break;
			}
		}
		myScanner.close();
	}

	/**
	 * This method reads the raw song lyrics from txt file
	 * 
	 * @throws FileNotFoundException
	 *             Exception if file could not be found.
	 */
	private static void readRawSong() throws FileNotFoundException {
		myScanner = new Scanner(new File(IN_FILE));
		lyrics = new HashMap<Integer, Integer>();
		PorterStemmer stemmer = new PorterStemmer();
		String word = "";
		while (myScanner.hasNext()) {
			word = myScanner.next().toLowerCase().replaceAll("[^\\w\\s]", "");
			if (myWords.contains(word)) {
				int index = myWords.indexOf(word);
				if (lyrics.containsKey(index))
					lyrics.put(index, lyrics.get(index) + 1);
				else
					lyrics.put(index, 1);
			} else {
				word = stemmer.stem(word);
				if (myWords.contains(word)) {
					int index = myWords.indexOf(word);
					if (lyrics.containsKey(index))
						lyrics.put(index, lyrics.get(index) + 1);
					else
						lyrics.put(index, 1);
				}

			}

		}
		myScanner.close();
	}

	/**
	 * This method writes the input song to another file
	 * in bag-of-words format
	 * @throws IOException 
	 */
	private static void writeOutFile() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(OUT_FILE));
		//bw.write(new File(IN_FILE).getName()+": ");
		for(int s: lyrics.keySet()) {
			bw.write(myWords.get(s) + ":" + lyrics.get(s) + ",");
		}
		bw.close();
	}

	public static void main(String[] args){

		try {
			read5000words();
		} catch (FileNotFoundException e) {
			System.out.println("Error while attempting to read file.\n" + e.getMessage());
		}
		try {
			readRawSong();
		} catch (FileNotFoundException e) {
			System.out.println("Error while attempting to read raw song data file.\n" + e.getMessage());
		}
		System.out.println(lyrics);
		try {
			writeOutFile();
		} catch (IOException e) {
			System.out.println("Error while attempting to write data file.\n" + e.getMessage());
		}
	}

}
