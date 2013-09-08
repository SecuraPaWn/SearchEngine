package crawl;

import java.io.IOException;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Crawler {
	/**
	 * This method is for testing purposes only. It does not need to be used
	 * to answer any of the questions in the assignment. However, it must
	 * function as specified so that your crawler can be verified programatically.
	 *
	 * This methods performs a crawl starting at the specified seed URL. Returns a
	 * collection containing all URLs visited during the crawl.
	 */
	public static void main(String[] args) throws Exception {
		//CrawlController controller = new CrawlController("/data/crawl/root");
		crawl("http://www.ics.uci.edu/");
	}

	public static void crawl(String seedURL) throws Exception {

		//CrawlController controller = new CrawlController("/data/crawl/root");

		long startTime = System.currentTimeMillis();


		String crawlStorageFolder = "C:\\IR\\";
		int numberOfCrawlers = 8;
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);

		// * Instantiate the controller for this crawl.

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

		String userAgentString = "UCI IR Crawler 84568218_15289486";
		config.setUserAgentString(userAgentString);

		config.setPolitenessDelay(300);

		// * You can set the maximum crawl depth here. The default value is -1 for
		// * unlimited depth

		config.setMaxDepthOfCrawling(-1);
		config.setConnectionTimeout(1000);


		// * You can set the maximum number of pages to crawl. The default value
		// * is -1 for unlimited number of pages

		config.setMaxPagesToFetch(-1);


		// * For each crawl, you need to add some seed urls. These are the first
		// * URLs that are fetched and then the crawler starts following links
		// * which are found in these pages


		// controller.addSeed("http://www.ics.uci.edu/~welling/");
		// controller.addSeed("http://ics.uci.edu/~lopes/");
		controller.addSeed(seedURL); 

		// * Start the crawl. This is a blocking operation, meaning that your code
		// * will reach the line after this only when crawling is finished.

		controller.start(BasicCrawler.class, numberOfCrawlers);

		long endTime = System.currentTimeMillis();

		System.out.println("Total Time: " + (endTime - startTime));


	}
}
