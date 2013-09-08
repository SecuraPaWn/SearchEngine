package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A collection of utility methods for text processing.
 */
public class Utilities {
	/**
	 * Reads the input text file and splits it into alphanumeric tokens.
	 * Returns an ArrayList of these tokens, ordered according to their
	 * occurrence in the original text file.
	 * 
	 * Non-alphanumeric characters delineate tokens, and are discarded.
	 *
	 * Words are also normalized to lower case. 
	 * 
	 * Example:
	 * 
	 * Given this input string
	 * "An input string, this is! (or is it?)"
	 * 
	 * The output list of strings should be
	 * ["an", "input", "string", "this", "is", "or", "is", "it"]
	 * 
	 * @param input The file to read in and tokenize.
	 * @return The list of tokens (words) from the input file, ordered by occurrence.
	 * @throws IOException 
	 */
	public static ArrayList<String> tokenizeFile(File input) throws IOException {
		BufferedReader inputBR = null;
		ArrayList<String> tokanizedWords = new ArrayList<String>();
		ArrayList<String> tempArrList = new ArrayList<String>();
		
		try {
			String inputString = "";
			
			// Storing all stop words in a HashSet
			HashSet<String> stopWords = new HashSet<String>();
			
			// Reading the Stop Words
			String line = null;
			File file = new File("src/ir/assignments/UtilFiles/StopWords");
			inputBR =  new BufferedReader(new FileReader(file));
			while (( line = inputBR.readLine()) != null) {
				stopWords.add(line.trim());
			}
			int i=0;
			// Reading and processing the file passes as argument to this method
			inputBR =  new BufferedReader(new FileReader(input));
			while (( inputString = inputBR.readLine()) != null) {
				if(inputString.trim().length() != 0 || !inputString.equals(" +")) {
					// inputString += line + " ";
					
					// Removing extra white spaces between characters with a single white space
					inputString = inputString.replaceAll(" +", " ");
					
					// Reference: http://stackoverflow.com/questions/7552253/how-to-remove-special-characters-from-an-string
					inputString = inputString.replaceAll("[^\\w\\s]","");
					inputString = inputString.replaceAll("[^\\p{L}\\p{N}]"," ");
					inputString = inputString.trim();
					inputString = inputString.toLowerCase();
					
					// Removing extra white spaces between characters
					inputString = inputString.replaceAll(" +", " ");
					
					tempArrList.clear();
					tempArrList.addAll(Arrays.asList(inputString.split(" ")));
					// Removing stop words
					tempArrList = removeStopWords(tempArrList, stopWords);
					
					// Splitting the line based on spaces
					tokanizedWords.addAll(tempArrList);
					
					i++;
					if(i%500==0) {
						Calendar cal = Calendar.getInstance();
						cal.getTime();
				    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				    	System.out.println( i + " :    " + sdf.format(cal.getTime()) + " ----------------    " + tokanizedWords.size() );
						
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			inputBR.close();
		}
		if(tokanizedWords.size() == 1 && tokanizedWords.get(0).equals("")) {
			return new ArrayList<String>();
		} else {
			return tokanizedWords;
		}
	}
	
	/***
	 * This method will remove all the stop words from the input array list.
	 * @param wordList List of input words to be filtered for stop words
	 * @param stopWords List of Stop Words
	 * @return Updated List of input words after removing from it the stop words
	 */
	public static ArrayList<String> removeStopWords(List<String> wordList, HashSet<String> stopWords) {
		ArrayList<String> modifiedWordList = new ArrayList<String>(wordList);
		for(String word: wordList) {
			if(stopWords.contains(word)) {
				modifiedWordList.remove(word);
			}
		}
		return modifiedWordList;
	}
	
	/**
	 * Takes a list of {@link Frequency}s and prints it to standard out. It also
	 * prints out the total number of items, and the total number of unique items.
	 * 
	 * Example one:
	 * 
	 * Given the input list of word frequencies
	 * ["sentence:2", "the:1", "this:1", "repeats:1",  "word:1"]
	 * 
	 * The following should be printed to standard out
	 * 
	 * Total item count: 6
	 * Unique item count: 5
	 * 
	 * sentence	2
	 * the		1
	 * this		1
	 * repeats	1
	 * word		1
	 * 
	 * 
	 * Example two:
	 * 
	 * Given the input list of 2-gram frequencies
	 * ["you think:2", "how you:1", "know how:1", "think you:1", "you know:1"]
	 * 
	 * The following should be printed to standard out
	 * 
	 * Total 2-gram count: 6
	 * Unique 2-gram count: 5
	 * 
	 * you think	2
	 * how you		1
	 * know how		1
	 * think you	1
	 * you know		1
	 * 
	 * @param frequencies A list of frequencies.
	 */
	public static void printFrequencies(List<Frequency> frequencies) {
		int totTwoGrmCnt = 0;
		Boolean isTwoGram = false;
		for (Frequency frequency : frequencies) {
			totTwoGrmCnt += frequency.getFrequency();
			if(frequency.getText().split(" ").length > 1) isTwoGram = true;
		}
		if(isTwoGram) {
			System.out.println("Total 2-gram count: " + totTwoGrmCnt);
			System.out.println("Unique 2-gram count: " + frequencies.size());
		} else {
			System.out.println("Total item count: " + totTwoGrmCnt);
			System.out.println("Unique item count: " + frequencies.size());
		}
		System.out.println();
		for (Frequency frequency : frequencies) {
			System.out.println(frequency.toString());
		}
		System.out.println();
		
		System.out.println("===============");
		
		System.out.println();
	}
	
	public static List<Frequency> computeFourGramFrequencies(ArrayList<String> words) {
		List<Frequency> fourGramList = new ArrayList<Frequency>();
		Map<String, Integer> fourGramMap = new TreeMap<String, Integer>();
		
		// Adding a pair of words and its corresponding frequency in a TreeMap.
		for (int i=0;i<words.size()-3;i++) {
			if(fourGramMap.get(words.get(i) + " " + words.get(i+1) + " " + words.get(i+2) + " " + words.get(i+3)) == null) {
				fourGramMap.put(words.get(i) + " " + words.get(i+1) + " " + words.get(i+2) + " " + words.get(i+3), 1);
			} else {
				fourGramMap.put(words.get(i) + " " + words.get(i+1) + " " + words.get(i+2) + " " + words.get(i+3),fourGramMap.get(words.get(i) + " " + words.get(i+1) + " " + words.get(i+2) + " " + words.get(i+3)) + 1);
			}
		}
		
		for (Map.Entry<String, Integer> entry : fourGramMap.entrySet()) {
			fourGramList.add(new Frequency(entry.getKey(), entry.getValue()));
		}

		return fourGramList;
	}
}