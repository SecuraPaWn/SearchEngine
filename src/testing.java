

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import json.JSONArray;
import json.JSONObject;

import org.apache.lucene.analysis.standard.StandardAnalyzer;	
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * Servlet implementation class for Servlet: testing
 *
 */
 public class testing extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public testing() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Hello");
		JSONArray jSearchObjectArray = new JSONArray();
		JSONObject finalJsonSearchObject = new JSONObject();
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_41);
		String queryString = request.getParameter("querystr").toLowerCase();
		System.out.println(queryString);
		StringTokenizer st = new StringTokenizer(queryString);
		String token = null;
		int j = 1;
		BooleanQuery query = new BooleanQuery();
		while(st.hasMoreTokens()) {
			token = st.nextToken();
			TermQuery titleTerm = new TermQuery(new Term("title",token)); 
			titleTerm.setBoost(120.0f);
			TermQuery contentsTerm = new TermQuery(new Term("parseddata",token)); 
			contentsTerm.setBoost(190.0f);
			TermQuery anchorTerm = new TermQuery(new Term("anchortxt",token)); 
			anchorTerm.setBoost(125.0f);

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
			jSearchObject.put("URL",d.get("filename"));
			jSearchObject.put("Title",d.get("title"));
			try{
				jSearchObject.put("Contents",d.get("parseddata").substring(0, 60) + "...");
			} catch(Exception e) {}
			jSearchObjectArray.put(i,jSearchObject);
		}

		// System.out.println(jSearchObjectArray.length());
		finalJsonSearchObject.put("Results",jSearchObjectArray);
		// System.out.println(finalJsonSearchObject);
		PrintWriter pw = response.getWriter();
		pw.print(finalJsonSearchObject);
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}   	  	    
}