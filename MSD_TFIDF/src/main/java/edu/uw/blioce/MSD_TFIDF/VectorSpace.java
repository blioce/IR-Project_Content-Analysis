/**
 * 
 */
package edu.uw.blioce.MSD_TFIDF;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * This program will take in files that contain TF*IDF values for tracks
 * and will populate it into a 2D array or vector space. The first half 
 * of the entries will be drug/violence related songs and the second half
 * will be songs without such topics. We can then use these arrays to 
 * compute cosine similarity and find K-Nearest Neighbors.
 * 
 * @author Brandon Lioce
 * @version May 19th, 2018
 * @class Information Retrieval - Spring
 * @assignment Content Analysis
 *
 */
public class VectorSpace {
	
	/** Drug or violence? */
	private static final String D_or_V = "violent";
	
	/** The file that contains TF*IDF values for tracks with drug/violent words. */
	private static final String THE_BAD = "TFIDF_" + D_or_V + "_bad_words.txt";
	
	/** The file that contains TF*IDF values for tracks without drug/violent words. */
	private static final String THE_GOOD = "TFIDF_" + D_or_V + "_good_words.txt";
	
	/** The list of good and drug/violent words, for indexing purposes. */
	private static final String THE_WORDS = "src/" + D_or_V + "_words_balanced.txt";
	
	/** The list of good and drug/violent words. */
	private static List<String> myWords;
	
	/** The vector that will have TF*IDF values for each track. */
	private static double[][] myVector;

	/**
	 * This method initiates the vector space.
	 * 
	 * @param args No arguments are used. 
	 */
	public static void main(String[] args) {
		try {
			fillVector();
		} catch (FileNotFoundException e) {
			System.out.println(e);
		}
		
		knn(myVector[1000], 5);
	}
	
	/**
	 * This method fills the vector space with values from a file. 
	 * 
	 * @throws FileNotFoundException Throws exception if not found. 
	 */
	private static void fillVector() throws FileNotFoundException {
		myWords = new ArrayList<String>();
		Scanner myScanner = new Scanner(new File(THE_WORDS));
		while(myScanner.hasNext()) myWords.add(myScanner.next());
		
		myVector = new double[2000][myWords.size()];
		
		myScanner = new Scanner(new File(THE_BAD));
		for(int i = 0; i < 1000 && myScanner.hasNextLine(); i++) {
			String line = myScanner.next();
			String[] tokens = line.split(",");
			for(int j = 1; j < tokens.length; j++) {
				String[] token = tokens[j].split(":");
				String word = token[0];
				Double val = Double.valueOf(token[1]);
				myVector[i][myWords.indexOf(word)] = val;
			}
		}
		
		myScanner = new Scanner(new File(THE_GOOD));
		for(int i = 0; i < 1000 && myScanner.hasNextLine(); i++) {
			String line = myScanner.next();
			String[] tokens = line.split(",");
			for(int j = 1; j < tokens.length; j++) {
				String[] token = tokens[j].split(":");
				String word = token[0];
				Double val = Double.valueOf(token[1]);
				myVector[i + 1000][myWords.indexOf(word)] = val;
			}
		}
		
		myScanner.close();
		
	}
	
	private static void knn(double[] theArray, int k) {
		List<IndexToValue> theK = new ArrayList<IndexToValue>();
		for(int i = 0; i < k; i++) theK.add(new IndexToValue(-1, 1000));

		for(int i = 0; i < myVector.length; i++) {
			IndexToValue max = Collections.max(theK);
			Double ret = compute(theArray, myVector[i]);
			if(ret < max.value) {
				theK.remove(max);
				theK.add(new IndexToValue(i, ret));
			}
		}
		
		double count = 0.0;
		System.out.println("The " + k + "-Nearest Neighbors of the given vector:\n");
		for(IndexToValue i: theK) {
			if(i.index < 1000) count++;
			System.out.println("Index in the vector space: " + i.index + "\t\tTF*IDF value: " + i.value);
		}
		if(Math.round(count / k) == 1) {
			System.out.println("\nThis song is inappropriate!");
		} else {
			System.out.println("\nThis song is just fine. :)");
		}
		
	}
	
	/**
	 * 
	 * This method computes the cosine similarity between to tracks 
	 * in the form of arrays of the vector object. 
	 * 
	 * @author Ziyu Gao
	 * 
	 * @param arr_a The first array of track TF*IDF info. 
	 * @param arr_b The second array of track TF*IDF info.
	 * @return The double value of the cosine similarity between the two tracks.
	 */
	private static double compute(double[] arr_a, double[] arr_b) {
		double angle = 0;
		double angle_1 = 0; // the Radian of angle
		double angle_2 = 0; // the degree measure of angle
		double a_len = 0;
		double b_len = 0;
		// Calculate a*b
		for (int i = 0; i < arr_a.length; i++) {
			angle += arr_a[i] * arr_b[i];
			a_len += arr_a[i] * arr_a[i];
			b_len += arr_b[i] * arr_b[i];
		}
		//Calculate||a|*|b||
		a_len = sqrt(a_len);
		b_len = sqrt(b_len);
		
		angle = angle / (a_len * b_len);
		//If one of array distance is 0, we set the outcome equals to 0
		if (a_len * b_len ==0) {
			angle = 0;
		}

		angle_1 = Math.acos(angle);                  // the Radian of angle
		angle_2 = Math.acos(angle) * 180 / Math.PI;  // the degree measure of angle
		return angle_2; //You can choose one as return from angle_1 or angle_2
	}

	/**
	 * @author Ziyu Gao
	 * 
	 * @param doub Value to square root.
	 * @return Returns square root value. 
	 */
	private static double sqrt(double doub) {
		if (doub <= 0) {
			return 0;
		}
		
		return Math.sqrt(doub);
	}
}

class IndexToValue implements Comparable<IndexToValue>{
	int index;
	double value;
	
	public IndexToValue(int ind, double val) {
		index = ind;
		value = val;
	}
	
	public int compareTo(IndexToValue theOther) {
		if(this.value < theOther.value) return -1;
		else if(this.value > theOther.value) return 1;
		else return 0;
	}
}
