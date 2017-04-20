package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Geocoder {
	private final static String ENCODING = "UTF-8";
	//private final static String CM_KEY = "ABQIAAAA64A8f659IkxGDcld75YWZxSqKBQ5o0skgsl59OQt65WFk-AoCBQXSkcKTsoVFlPm7QJ_gQqmFrUJ0A";
	//private final static String CM_KEY = "ABQIAAAA64A8f659IkxGDcld75YWZxSe5JDe8brlelEWWWeLgZo9Y2NagRStlzpShyZUiKM0BCrl3AsZzm3JeA";
	//private final static String CM_KEY = "ABQIAAAA0xdaY4dOffj3tjDBwczQVRTvQMTsTfTOQF2QCzAgXuQJh5QJvhRPVH4aQeaTY-meMA7nkgQNthxB1g";
	private final static String CM_KEY = "AIzaSyDARjrXRXU-8ZXAksHLOkpBIPqaaBEkHy4";  //v3
	
	public static class Location {
		public String lon, lat;

		private Location(String lat, String lon) {
			this.lon = lon;
			this.lat = lat;
		}

		public String toString() {
			return "Lat: " + lat + ", Lon: " + lon;
		}
		
		public String getLat() {
			return lat;
		}
		
		public String getLon() {
			return lon;
		}
	}
	
	public String getStaticMapURL(String address) throws Exception {
		Location loc = getLocation(address);
		String mapUrl = "http://maps.googleapis.com/maps/api/staticmap?center=" + loc.lat + "," + loc.lon + "&zoom=15&size=320x320&mobile=true&markers=" + loc.lat + "," + loc.lon + "&sensor=false";
		//String mapUrl = "http://maps.google.com/staticmap?center=" + loc.lat + "," + loc.lon + "&zoom=15&mobile=true&markers=" + loc.lat + "," + loc.lon + "&key=" + CM_KEY;
		return mapUrl;
	}
	
	public String getStaticMapURL(String address, float width, float height) throws Exception {
		Location loc = getLocation(address);
		String mapUrl = "http://maps.googleapis.com/maps/api/staticmap?center=" + loc.lat + "," + loc.lon + "&zoom=15&mobile=true&markers=" + loc.lat + "," + loc.lon + "&sensor=false";
		int m_width = (int)(width * 0.9);
		int m_height = (int)height;
		
		//these need to be integers
		mapUrl += "&size=" + m_width + "x" + m_height;
		return mapUrl;
	}
	
	public static Location getLocation(String address) throws IOException {
		String url = "https://maps.googleapis.com/maps/api/geocode/json?address=";
		BufferedReader in = new BufferedReader(new InputStreamReader(new URL(
						url + URLEncoder.encode(address, ENCODING) + "&key=" + CM_KEY).openStream()));	
		
		String line;
		String jsonStr = "";
		while ((line = in.readLine()) != null) {
			jsonStr += line;
		}
			
        JSONParser parser = new JSONParser();

        JSONObject obj = null;
		try {
			obj = (JSONObject)parser.parse(jsonStr);
	        if (! obj.get("status").equals("OK"))
		        return null;
		   
		    JSONArray resA = (JSONArray)obj.get("results");
		    JSONObject res = (JSONObject) resA.get(0);
		    JSONObject geo = (JSONObject) res.get("geometry");
		    JSONObject loc = (JSONObject) geo.get("location");
		    //System.out.println("lat: " + loc.get("lat") + ", lng: " + loc.get("lng"));	
		    return new Location(loc.get("lat").toString(), loc.get("lng").toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	    
	}

	
	public static Location getLocationOld(String address) throws IOException {		
		BufferedReader in = new BufferedReader(new InputStreamReader(new URL(
				"http://maps.google.com/maps/geo?q="
						+ URLEncoder.encode(address, ENCODING)
						+ "&output=csv&key=" + CM_KEY).openStream()));

		String line;
		Location location = null;
		int statusCode = -1;
		while ((line = in.readLine()) != null) {
			// Format: 200,6,42.730070,-73.690570
			statusCode = Integer.parseInt(line.substring(0, 3));
			if (statusCode == 200)
				location = new Location(line.substring("200,6,".length(), line
						.indexOf(',', "200,6,".length())), line.substring(line
						.indexOf(',', "200,6,".length()) + 1, line.length()));
		}
		if (location == null) {
			switch (statusCode) {
			case 400:
				throw new IOException("Bad Request");
			case 500:
				throw new IOException("Unknown error from Google Encoder");
			case 601:
				throw new IOException("Missing query");
			case 602:
				return null;
			case 603:
				throw new IOException("Legal problem");
			case 604:
				throw new IOException("No route");
			case 610:
				throw new IOException("Bad key");
			case 620:
				throw new IOException("Too many queries");
			}
		}
		return location;
	}

	public static void main(String[] argv) throws Exception {
		System.out.println(Geocoder.getLocation("960 Tournament Dr., Hillsborough, CA 94010"));
	}
}
