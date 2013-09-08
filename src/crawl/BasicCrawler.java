package crawl;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class BasicCrawler extends WebCrawler {

	//This variable holds temporarily the longest documents's length
	public static int longDocLength = 0;

	//This object will hold the parameters for the longest document
	public Map<String, Integer> subDomainMap = new TreeMap<String, Integer>();

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4"
			+ "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	private final static String PATH_LOCATION = "src/ir/assignments/output/";

	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		return (!FILTERS.matcher(href).matches() && href.contains(".ics.uci.edu")) &&
				!href.startsWith("https") && !href.contains("calendar.ics.uci.edu") && 
				!href.contains("http://archive.ics.uci.edu") && !href.contains("ftp.ics.uci.edu") &&
				(!href.contains("http://djp3-pc2.ics.uci.edu/LUCICodeRepository") || !href.startsWith("http://djp3-pc2.ics.uci.edu/LUCICodeRepository")) && 
				!href.contains("rss.ics.uci.edu") && !href.contains("feed=rss") && !href.contains(".css?") && 
				!href.contains("http://testlab.ics.uci.edu/") && !href.contains("http://phoenix.ics.uci.edu") &&
				!href.contains("networkdata.ics.uci.edu") && !href.contains("ics.uci.edu/~lopes/datasets") &&
				!href.contains("http://mlearn.ics.uci.edu/MLRepository.html") && !href.contains("sourcerer.ics.uci.edu/") &&
				!href.contains("http://www.ics.uci.edu/~eppstein/pix/") && !href.contains("http://www.ics.uci.edu/~xhx/project");
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {

		int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL();
		String domain = page.getWebURL().getDomain();
		String path = page.getWebURL().getPath();
		String subDomain = page.getWebURL().getSubDomain();
		String parentUrl = page.getWebURL().getParentUrl();

		if (page.getParseData() instanceof HtmlParseData) {

			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			List<WebURL> links = htmlParseData.getOutgoingUrls();

			// There is a unique file for each document containing the length.
			String fileName = docid + "_logger";
			try {
				if(text.length() != 0) {
					String parsedData = htmlParseData.toString();	

					if(parsedData.trim().length() != 0 || !parsedData.equals(" +")) {
						System.out.println("=============");
						System.out.println("=============");
						System.out.println("Docid: " + docid);
						System.out.println("URL: " + url);
						System.out.println("Domain: '" + domain + "'");
						System.out.println("Sub-domain: '" + subDomain + "'");
						System.out.println("Path: '" + path + "'");
						System.out.println("Parent page: " + parentUrl);
						System.out.println("Text length: " + text.length());
						System.out.println("Html length: " + html.length());
						System.out.println("Number of outgoing links: " + links.size());

						// Getting the text within <title> ... </title>
						ArrayList<String> extracts = getTagContents(Jsoup.parse(html));
						
						// title, h1, h2, h3, b, strong, em, a
						
						String title = extracts.get(0);
						String h1 = extracts.get(1);
						String h2 = extracts.get(2);
						String h3 = extracts.get(3);
						String bold = extracts.get(4);
						String strong = extracts.get(5);
						String em = extracts.get(6);
						String anchorText = extracts.get(7);

						// Extracting anchor tag texts

						// Formatting the crawled webpage's (data) contents
						parsedData = parseData(parsedData);

						writeSeperateFile(docid, url, path, parentUrl, title, h1, h2, h3, bold, strong, em, anchorText, parsedData, fileName);
						
						writeSingleFile(docid, url, path, parentUrl, title, h1, h2, h3, bold, strong, em, anchorText, parsedData);
						
						FileWriter fstream_answers = new FileWriter("urlnames", true);
						BufferedWriter out_answers = new BufferedWriter(fstream_answers);
						out_answers.write(url); out_answers.newLine();
						out_answers.close();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String parseData(String data) {
		// This converts words of form HelloWorld to Hello World
		// Reference: http://stackoverflow.com/questions/4886091/insert-space-after-capital-letter
		data = data.replaceAll("(\\p{Ll})(\\p{Lu})(\\p{Ll})","$1 $2$3");

		// Writing all the data in a single large file
		// Each line will correspond to one web page.
		data = data.replaceAll("(\\r|\\n)", " ");

		// Removing extra white spaces between characters with a single white space
		data = data.replaceAll(" +", " ");

		// Reference: http://stackoverflow.com/questions/7552253/how-to-remove-special-characters-from-an-string
		data = data.replaceAll("[^\\w\\s]","");
		data = data.replaceAll("[^\\p{L}\\p{N}]"," ");
		data = data.trim();
		data = data.toLowerCase();

		// Removing extra white spaces between characters
		data = data.replaceAll(" +", " ");

		// Removing data of the form &nbsp; - anything that begins with & and ends with ;
		data = data.replaceAll("\\&.*?\\;", "");

		return data;
	}

	/*public static String parseAnchorTagTxt(String data) {
		String[] anchorTags = (data.split("</a>"));
		String anchorText = "";
		for(String anchorTag: anchorTags) {
			String tempStr = "";
			if( anchorTag.lastIndexOf('>') != -1 ) {
				try {
					tempStr = anchorTag.substring(anchorTag.lastIndexOf('>') + 1);
					tempStr = tempStr.replaceAll("&raquo;", "");
					tempStr = tempStr.replaceAll(" +", " ");
					tempStr = tempStr.replaceAll("[^\\w\\s]","");
					tempStr = tempStr.replaceAll("[^\\p{L}\\p{N}]"," ");
					tempStr = tempStr.trim();
					tempStr = tempStr.toLowerCase();
					tempStr = tempStr.replaceAll(" +", " ");
					if(tempStr.trim().length() != 0) {
						anchorText += tempStr;
						anchorText += " ";
					}
				} catch(IndexOutOfBoundsException e) {
					anchorText += "";
				}
			}
		}
		return anchorText.replaceAll("\\<.*?\\>", "").replaceAll("(\\r|\\n)", " ").replaceAll("\\&.*?\\;", "");
	}

	public static String getTagContents(String data, String htmlData) {
		String combinedData = "";
		String tag = "<" + data + ">";
		String closeTag = "</" + data + ">";
		int index = htmlData.indexOf(tag);
		try {
			while (index >= 0) {
				int closeIndex = htmlData.indexOf(closeTag);
				while(closeIndex < index) {
					closeIndex = htmlData.indexOf(closeTag, closeIndex + 1);
				}
				combinedData += (htmlData.substring(index+tag.length(), closeIndex) + " ");
				index = htmlData.indexOf(tag, index + 1);
			}
		} catch(IndexOutOfBoundsException e) {
			combinedData += "";
		}
		return combinedData.replaceAll("\\<.*?\\>", "").replaceAll("(\\r|\\n)", " ").replaceAll("\\&.*?\\;", "");
	}*/
	
	public static ArrayList<String> getTagContents(Document doc) {
		// title, h1, h2, h3, b, strong, em, a
		ArrayList<String> extracts = new ArrayList<String>();
		
		// Extracting the title
		Element title = doc.select("title").first();
		extracts.add((title==null)?"":title.text());
		
		// Extracting h1
		String h1String = "";
		Elements h1List = doc.select("h1");
		for (Element h1 : h1List) {
			h1String += h1.text() + " ";
		}
		extracts.add((h1String==null)?"":h1String);
		
		// Extracting h2
		String h2String = "";
		Elements h2List = doc.select("h2");
		for (Element h2 : h2List) {
			h2String += h2.text() + " ";
		}
		extracts.add((h2String==null)?"":h2String);
		
		// Extracting h1
		String h3String = "";
		Elements h3List = doc.select("h3");
		for (Element h3 : h3List) {
			h1String += h3.text() + " ";
		}
		extracts.add((h3String==null)?"":h3String);
		
		// Extracting b
		String bString = "";
		Elements bList = doc.select("b");
		for (Element b : bList) {
			bString += b.text() + " ";
		}
		extracts.add((bString==null)?"":bString);
		
		// Extracting strong
		String strongString = "";
		Elements strongList = doc.select("strong");
		for (Element strong : strongList) {
			strongString += strong.text() + " ";
		}
		extracts.add((strongString==null)?"":strongString);
		
		// Extracting em
		String emString = "";
		Elements emList = doc.select("em");
		for (Element em : emList) {
			emString += em.text() + " ";
		}
		extracts.add((emString==null)?"":emString);
		
		// Extracting a
		String aString = "";
		Elements aList = doc.select("a");
		for (Element a : aList) {
			emString += a.text() + " ";
		}
		extracts.add((aString==null)?"":aString);
		
		return extracts;
	}
	
	public static void writeSeperateFile(Integer docId, String url, String path, String parentUrl, String title, String h1, String h2, String h3, 
			String bold, String strong, String em, String anchorText, String parsedData, String fileName) throws IOException {
		FileWriter fstream = new FileWriter(PATH_LOCATION+fileName);
		BufferedWriter out = new BufferedWriter(fstream);
		
		out.write(docId.toString()); out.newLine();
		out.write(url); out.newLine();
		out.write(path); out.newLine();
		if(parentUrl != null) {
			out.write(parentUrl); out.newLine();
		}
		out.write(title); out.newLine();
		out.write(h1); out.newLine();
		out.write(h2); out.newLine();
		out.write(h3); out.newLine();
		out.write(bold); out.newLine();
		out.write(strong); out.newLine();
		out.write(em); out.newLine();
		out.write(anchorText); out.newLine();
		out.write(parsedData);
		
		out.close();
	}
	
	public static void writeSingleFile(Integer docId, String url, String path, String parentUrl, String title, String h1, String h2, String h3, String bold, 
			String strong, String em, String anchorText, String parsedData) throws IOException {
		FileWriter fstream = new FileWriter("combinedwebpages", true);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(docId.toString() + "^" + url + "^" + path + "^" + parentUrl + "^" + title + "^" + h1 + "^" + h2 + "^" + h3 + "^" + bold
				 + "^" + strong + "^" + em + "^" + anchorText + "^" + parsedData);
		out.newLine();
		out.close();
	}
} 