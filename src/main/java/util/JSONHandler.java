package util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class JSONHandler {
	private static final String yelpURL = "http://api.yelp.com/phone_search";
	private static final String ywsid = "tdnAMtFptSsUaeZhUcWdHw";
	Logger logger = Logger.getLogger(JSONHandler.class);

	public JSONHandler() {
		
	}
	
	public void parse(String jsonStr) {
		JSONObject obj = (JSONObject)JSONValue.parse(jsonStr);
		JSONArray bus = (JSONArray)obj.get("businesses");
		JSONObject bus1 = (JSONObject)bus.get(0);
		logger.debug("bus: " + bus1.get("review_count") + ", " + bus1.get("avg_rating"));
	}
	
	public String toXML(String jsonStr) throws Exception {
		JSONObject obj = (JSONObject)JSONValue.parse(jsonStr);
		JSONArray bus = (JSONArray)obj.get("businesses");
		JSONObject bus1 = (JSONObject)bus.get(0);

		StringBuffer sb = new StringBuffer();
		sb.append("<yelp_review>");
		sb.append("<avg_rating>").append(bus1.get("avg_rating")).append("</avg_rating>");
		sb.append("<review_count>").append(bus1.get("review_count")).append("</review_count>");
		sb.append("<rating_img_url>").append(bus1.get("rating_img_url_small")).append("</rating_img_url>");
		sb.append("<mobile_url>").append(bus1.get("mobile_url")).append("</mobile_url>");
		sb.append("</yelp_review>");
		
		return sb.toString();
	}
	
	public String getFromYelp(String phone) throws Exception {
		PostMethod postMethod = new PostMethod(yelpURL);
		postMethod.setRequestHeader("Keep-Alive", "Keep-Alive");
		postMethod.addParameter("phone", phone);
		postMethod.addParameter("ywsid", ywsid);
		
		String result = null;
		
		int statusCode = new HttpClient().executeMethod(postMethod);
		if (statusCode == 200) {
			result = postMethod.getResponseBodyAsString();
		} else {
			logger.error("Error: " + postMethod.getStatusLine());
		}

		postMethod.releaseConnection();
		
		return toXML(result);		
	}
	
	
}
