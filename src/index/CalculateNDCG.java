package index;

import java.util.Arrays;
import java.util.List;

/**
 * References :
 * Class Notes on Search Engine
 * http://dev.grouplens.org/trac/lenskit/browser/lenskit-eval/src/main/java/org/grouplens/lenskit/eval/predict/NDCGEvaluator.java?rev=644
 */

public class CalculateNDCG {
	public static void main(String[] args) {
		int noOfEntries = 5;

		// Before Optimization

		// Crista Lopes
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 100, 99, 98, 33, 5 });*/

		// mondego
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 4, 3, 6 });*/

		// software engineering
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 23, 12, 19, 21, 8 });*/

		// security
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 2, 54, 58, 21, 30 });*/

		// Machine learning
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 56, 32, 62 });*/

		// information retrieval
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3, 3, 4, 5 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 1, 2, 4, 10, 5 });*/

		// student affairs
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 100, 99, 98, 97, 96 });*/

		// graduate courses
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 92, 96, 100, 90, 50 });*/

		// computer games
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 100, 99, 98, 97, 96 });*/

		// rest
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] {99, 100, 73, 20, 22 });*/


		// ---------------------------------------------------
		// After Optimization

		// Crista Lopes
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 5, 4, 3, 1, 8 });*/

		// mondego
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 2, 1, 3, 6, 8 });*/

		// software engineering
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 23, 12, 19, 21, 8 });*/

		// security
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 2, 54, 58, 21, 30 });*/

		// Machine learning
		List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 56, 32, 62 });

		// information retrieval
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3, 3, 4, 5 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 1, 2, 4, 10, 5 });*/

		// student affairs
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 3, 2, 37, 10, 8 });*/

		// graduate courses
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 3, 7, 8, 10, 12 });*/

		// computer games
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 3, 1, 77, 16, 37 });*/

		// rest
		/*List<Integer> googleOrder = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 });
		List<Integer> luceneOrder = Arrays.asList(new Integer[] { 6, 100, 48, 50, 2 });*/


		double ndcgValue = evaluateNDCG(luceneOrder, googleOrder, noOfEntries);
		System.out.println(1/ndcgValue);
	}

	public static double evaluateNDCG(List<Integer> urls, List<Integer> googleOrder, int noOfEntries) {
		double luceneUrlDCG = computeDCG(urls, googleOrder, noOfEntries);
		double idealDCG = computeDCG(googleOrder, googleOrder, noOfEntries);
		double normalized = luceneUrlDCG / idealDCG;
		return normalized;
	}

	private static double computeDCG(List<Integer> urls, List<Integer> googleOrder, int noOfEntries) {
		double gain = 0;
		double logTwo = Math.log(2);
		int rank = 0;
		for (int i = 0; i < noOfEntries; i++) {
			Integer item = googleOrder.get(i);
			Integer val = urls.get(item-1);
			rank++;
			if(rank < 2) {
				gain += val;
			} else {
				gain += val * logTwo / Math.log(rank);
			}
		}
		return gain;
	}
}