package index;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import util.Utilities;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class IRIndexer {

	public static HashSet<String> stopWords = new HashSet<String>();
	public static String WEBPAGE_PATH = "src/ir/assignments/output/";
	private static StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
	private IndexWriter writer;
	private ArrayList<File> queue = new ArrayList<File>();

	IRIndexer(String indexDir) throws IOException {
		FSDirectory dir = FSDirectory.open(new File(indexDir));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
		writer = new IndexWriter(dir, config);
	}

	public static void main(String[] args) throws IOException {

		// Reading the Stop Words
		String swLine = null;
		File swFile = new File("src/ir/assignments/UtilFiles/StopWords");
		BufferedReader swInputBR =  new BufferedReader(new FileReader(swFile));
		while (( swLine = swInputBR.readLine()) != null) {
			stopWords.add(swLine.trim());
		}
		swInputBR.close();
		System.out.println("Enter the path where the index will be created:");
		String indexLocation = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();   
		IRIndexer indexer = null;
		try {
			indexLocation = s;
			indexer = new IRIndexer(s);
		} catch (Exception ex) {
			System.out.println("Cannot create index..." + ex.getMessage());
			System.exit(-1);
		}

		try {
			System.out.println("Enter the full path of the file to add into the index");
			s = br.readLine();
			//try to add file into the index
			indexer.indexFileOrDirectory(s);
		} catch (Exception e) {
			System.out.println("Error indexing " + s + " : " + e.getMessage());
		}

		indexer.closeIndex();
	}

	/**
	 * Indexes a file or directory
	 * @param fileName the name of a text file or a folder we wish to add to the index
	 * @throws java.io.IOException when exception
	 * @throws InterruptedException 
	 */
	public void indexFileOrDirectory(String fileName) throws IOException, InterruptedException {
		int j=1;
		addFiles(new File(fileName));
		int originalNumDocs = writer.numDocs();
		for (File f : queue) {
			j=1;
			Document doc = new Document();
			Scanner scanner = new Scanner(f);
			StringBuilder builder = new StringBuilder();
			String docId = "", url = "", path = "", parentURL = "";
			String title = "", h1 = "", h2 = "", h3 = "";
			String bold = "", strong = "", em = "", anchorText = "", parsedData = "";
			ArrayList<String> tempArrList = new ArrayList<String>();
			while (scanner.hasNextLine()) {
				String currLine = scanner.nextLine();
				if(j==1) {
					docId = currLine;
					j++;
					System.out.println("docId: " + currLine);
				} else if(j==2) {
					url = currLine;
					j++;
					System.out.println("URL: " + currLine);
				} else if(j==3) {
					path = currLine;
					j++;
					System.out.println("Path: " + currLine);
				} else if(j==4) {
					parentURL = currLine;
					j++;
					System.out.println("docParent URL: " + currLine);
				} else if(j==5) {
					title = currLine;
					j++;
					System.out.println("Title: " + currLine);
				} else if(j==6) {
					h1 = currLine;
					j++;
					System.out.println("h1: " + currLine);
				} else if(j==7) {
					h2 = currLine;
					j++;
					System.out.println("h2: " + currLine);
				} else if(j==8) {
					h3 = currLine;
					j++;
					System.out.println("h3: " + currLine);
				} else if(j==9) {
					bold = currLine;
					j++;
					System.out.println("Bold: " + currLine);
				} else if(j==10) {
					strong = currLine;
					j++;
					System.out.println("String: " + currLine);
				} else if(j==11) {
					em = currLine;
					j++;
					System.out.println("Em: " + currLine);
				} else if(j==12) {
					anchorText = currLine;
					j++;
					System.out.println("AnchorText: " + currLine);
				} else if(j==13) {
					parsedData = currLine;
					j++;
					tempArrList = Utilities.removeStopWords(Arrays.asList(parsedData), stopWords);
					for (String string : tempArrList) {
						builder.append(string + " ");
					}
					System.out.println("Parsed Data: " + currLine);
				} 
			}
			
			// Adding the title to the index
			Field titleField = new TextField("title",title,Field.Store.YES); titleField.setBoost(9.1f); doc.add(titleField);
			Field h1Field = new TextField("h1",h1,Field.Store.YES); h1Field.setBoost(50f); doc.add(h1Field);
			Field h2Field = new TextField("h2",h2,Field.Store.YES); h2Field.setBoost(35f); doc.add(h2Field);
			Field h3Field = new TextField("h3",h3,Field.Store.YES); h3Field.setBoost(25f); doc.add(h3Field);
			Field boldField = new TextField("bold",bold,Field.Store.YES); boldField.setBoost(17f); doc.add(boldField);
			Field strongField = new TextField("strong",strong,Field.Store.YES); strongField.setBoost(17f); doc.add(strongField);
			Field emField = new TextField("em",em,Field.Store.YES); emField.setBoost(20f); doc.add(emField);
			Field anchorTxtField = new TextField("anchortxt",anchorText,Field.Store.YES); anchorTxtField.setBoost(25f); doc.add(anchorTxtField);
			Field parsedDataField = new TextField("parseddata",builder.toString(),Field.Store.YES); parsedDataField.setBoost(75f); doc.add(parsedDataField);

			doc.add(new StringField("filename",url,Field.Store.YES));
			writer.addDocument(doc);
			System.out.println("Added: " + f);
			scanner.close();
		}
		int newNumDocs = writer.numDocs();
		System.out.println("");
		System.out.println("************************");
		System.out.println((newNumDocs - originalNumDocs) + " documents added.");
		System.out.println("************************");
		queue.clear();
	}

	public static int nthOccurrence(String str, char c, int n) {
		int pos = str.indexOf(c, 0);
		while (n-- > 0 && pos != -1)
			pos = str.indexOf(c, pos+1);
		return pos;
	}

	private void addFiles(File file) {
		if (!file.exists()) {
			System.out.println(file + " does not exist.");
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				addFiles(f);
			}
		} else {
			queue.add(file);
		}
	}

	public void closeIndex() throws IOException {
		writer.close();
	}
}