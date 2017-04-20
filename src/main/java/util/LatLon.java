package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class LatLon {
	protected static final Logger logger = Logger.getLogger(LatLon.class);

	public static final String authId = "001d7013-a8a7-436f-a9f1-ba5be9e12413";
	public static final String authToken = "gJSh1Xi9wRGXNFQTLRcS";
	
	private JSONParser parser = new JSONParser();
	private String burl = "https://us-street.api.smartystreets.com/street-address?auth-id=" + authId + "&auth-token=" + authToken;
	private String burl_intl = "https://international-street.api.smartystreets.com/verify?auth-id=" + authId + "&auth-token=" + authToken;
	
	public void getIt(String infile, String outfile) throws Exception {
		CloseableHttpClient  httpclient = HttpClientBuilder.create().build();
		BufferedReader br = null;
		BufferedWriter bw = null;
		
		try {
			bw = new BufferedWriter(new FileWriter(outfile));
			br = new BufferedReader(new FileReader(infile));
			String line = null;
			String url = null;
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(",");
				String officeId = fields[0];
				String street = URLEncoder.encode(fields[2], "UTF-8");
				String suite = fields[3] != null || fields[3].length() <= 0 ? fields[3] : null;
				String city = URLEncoder.encode(fields[4], "UTF-8");
				String state = fields[5];
				String zip = fields[6];		
				String country = fields[7];
				
				if (country.equals("CA")) {
					url = burl_intl + "&address1=" + street;
					if (suite != null && suite.length() > 0)
						url += URLEncoder.encode(" Suite " + suite, "UTF-8");
					url += "&locality=" + city;
					url += "&administrative_area=" + state;
					url += "&postal_code=" + zip;
					url += "&country=" + country;					
				} else {
					url = burl + "&street=" + street;
					if (suite != null && suite.length() > 0)
						url += URLEncoder.encode(" Suite " + suite, "UTF-8");
					url += "&city=" + city;
					url += "&state=" + state;
					url += "&zipcode=" + zip;
					url += "&country=" + country;
				}
						   		   
			    HttpPost httpPost = new HttpPost(url);			        
			    httpPost.setHeader("Content-type", "application/json");				
			    
				System.out.println("executing request " + httpPost.getRequestLine());
				HttpResponse response = httpclient.execute(httpPost);
				HttpEntity resEntity = response.getEntity();
				
				System.out.println("----------------------------------------");
				System.out.println(response.getStatusLine());
				if (resEntity != null) {
				    System.out.println("Response content length: " + resEntity.getContentLength());
				    System.out.println("Chunked?: " + resEntity.isChunked());
				    String responseBody = EntityUtils.toString(resEntity);
				    System.out.println("Data: " + responseBody);
				    if (responseBody == null || responseBody.length() <= 3) {
					    logger.debug("office: " + officeId);
					    bw.write(officeId);
					    bw.newLine();
				    	continue;
				    }
				    
				    JSONArray jarr = (JSONArray) parser.parse(responseBody);
				    JSONObject jdata1 = (JSONObject) jarr.get(0);
				    JSONObject mdata = (JSONObject)jdata1.get("metadata");
				    Double lat = (Double) mdata.get("latitude");
				    Double lon = (Double) mdata.get("longitude");
				    logger.debug("office, lat, lon: " + officeId + ", " + lat + ", " + lon);
				    bw.write(officeId + ", " + lat + ", " + lon);
				    bw.newLine();
				    
				    EntityUtils.consume(resEntity);	  
				}			    
			}
	        
	        /*
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("street", "1600 amphitheatre pkwy"));   
			nameValuePairs.add(new BasicNameValuePair("city", "mountain view"));       
			nameValuePairs.add(new BasicNameValuePair("state", "ca"));           
			nameValuePairs.add(new BasicNameValuePair("zipcode", "94043"));           
			
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
			*/			
		} finally {
		    // When HttpClient instance is no longer needed,
		// shut down the connection manager to ensure
		// immediate deallocation of all system resources
		    httpclient.close();
		    br.close();
		    bw.close();
		}
	}
}
