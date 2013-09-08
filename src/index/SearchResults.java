package index;

import json.JSONArray;
import json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class SearchResults {
	public JSONObject jObject = null;

	public SearchResults() {
		jObject = new JSONObject();
	}

	public JSONObject getSearchJSONObject() {
		return jObject;
	}

	/**
	 * @param args
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	@SuppressWarnings("null")
	public static void main(String[] args) throws ParseException, CorruptIndexException, IOException {
		JSONArray jSearchObjectArray = new JSONArray();
		JSONObject finaljSearchObject = new JSONObject();
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
		String queryString = "crista lopes";
		StringTokenizer st = new StringTokenizer(queryString);
		String token = null;
		int j = 1;
		BooleanQuery query = new BooleanQuery();
		while(st.hasMoreTokens()) {
			token = st.nextToken();
			TermQuery titleTerm = new TermQuery(new Term("title",token)); 
			titleTerm.setBoost(80.0f);
			TermQuery contentsTerm = new TermQuery(new Term("parseddata",token)); 
			contentsTerm.setBoost(190.0f);
			TermQuery anchorTerm = new TermQuery(new Term("anchortxt",token));
			anchorTerm.setBoost(25.0f);

			TermQuery boldTerm = new TermQuery(new Term("bold",token)); 
			boldTerm.setBoost(55.0f);
			TermQuery strongTerm = new TermQuery(new Term("strong",token)); 
			strongTerm.setBoost(60.0f);
			TermQuery emTerm = new TermQuery(new Term("em",token)); 
			emTerm.setBoost(65.0f);
			query.add(titleTerm, Occur.SHOULD);
			query.add(contentsTerm, Occur.SHOULD);
			query.add(anchorTerm, Occur.SHOULD);

			query.add(boldTerm, Occur.SHOULD);
			query.add(strongTerm, Occur.SHOULD);
			query.add(emTerm, Occur.SHOULD);
			++j;
		}
		//Query query = new QueryParser(Version.LUCENE_40, "contents", analyzer).parse(queryString);
		int hitsPerPage = 100;
		File indexDirectory = new File("C:\\IR_Archieve\\index");
		IndexReader reader = DirectoryReader.open(FSDirectory.open(indexDirectory));
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		System.out.println("Found " + hits.length + " hits.");
		for(int i=0;i<hits.length;i++) {
			JSONObject jSearchObject = new JSONObject();
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			try {
				System.out.println((i + 1) + ". " + "\t" + d.get("filename") + "\t" + d.get("title") + "\t :: " + d.get("parseddata").substring(100, 150));
			} catch(Exception e) {}
			jSearchObject.put("URL",d.get("filename"));
			jSearchObject.put("Title",d.get("title"));
			jSearchObject.put("Anchor",d.get("anchor"));
			jSearchObject.put("Contents",d.get("contents"));
			jSearchObjectArray.put(i,jSearchObject);
		}

		System.out.println(jSearchObjectArray.length());
		finaljSearchObject.put("Results",jSearchObjectArray);
		System.out.println(finaljSearchObject.get("Results"));
	}
}
