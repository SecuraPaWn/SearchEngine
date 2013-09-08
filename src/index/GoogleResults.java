package index;

import json.JSONArray;
import json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class GoogleResults {


	public static void main(String args[]) throws IOException {
		String[] queyString = new String[]{"mondego", "machine%20learning", "software%20engineering",
				"security", "student%20affairs", "Crista%20Lopes", "REST", "computer%20games", "information%20retrieval"};
		String noOfResults = "8";
		for(String query : queyString) {
			URL url = new URL(
					"https://ajax.googleapis.com/ajax/services/search/web?v=1.0&"
							+ "q="+ query +"%20site:ics.uci.edu&userip=USERS-IP-ADDRESS&rsz=" + noOfResults);
			URLConnection connection = url.openConnection();
			connection.addRequestProperty("Referer", "http://www.ics.uci.edu/");

			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}

			JSONObject json = new JSONObject(builder.toString());
			// System.out.println(json);

			JSONArray jsonArray = json.getJSONObject("responseData").getJSONArray("results");

			System.out.println("==========================================");

			System.out.println();
			System.out.println("-------------------------------------");
			System.out.println("Query String: " + query.replace("%20", " "));
			System.out.println("-------------------------------------");
			System.out.println();

			for(int i=0; i<jsonArray.length(); i++) {
				JSONObject o = (JSONObject) jsonArray.get(i);
				System.out.println("Title: " + o.get("titleNoFormatting"));
				System.out.println("URL: " + o.get("url"));
				// System.out.println("Content: " + o.get("content"));
				System.out.println();
				System.out.println();
			}
			System.out.println("==========================================");
		}
	}

}
