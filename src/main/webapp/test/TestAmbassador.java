import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

public class TestAmbassador {
	private static final Map creds = new HashMap();
	
	public static void main(String [] args) throws Exception {
		creds.put("Liberty", new String[] {"libertytax", "c3a229100fbafb53a54741eb182a3ff3"});
		creds.put("eSmartTax", new String[] {"eSmartTax", "7673ea1fba1de6c99568e0b779655fd2"});
		creds.put("Liberty Canada", new String[] {"libertycanada", "a2dd533eb7eb4338c7a7daa263a4c61f"});
		creds.put("SiempreTax", new String[] {"siempretax", "8531a48daf0b8a3535e949ed85676c9a"});	
	
		/*
		new TestAmbassador().getIt(1);
		new TestAmbassador().getIt(2);
		new TestAmbassador().getIt(3);
		new TestAmbassador().getIt(4);
		*/
		
		new TestAmbassador().postIt();
	}
	
	public void getIt(int brand) throws Exception {
		   CloseableHttpClient  httpclient = HttpClientBuilder.create().build();
	       String url = "https://getambassador.com/api/v2/";
	       String credFields[] = null;
	       String brandName = null;
	       String apiName = null;
	       String apiKey = null;
	       String email = null;
	       
    		String email1 = "kpillai+webhook19@convergentmobile.com";
     		String email2 = "kpillai+siempre3@convergentmobile.com";
     		String email3 = "kpillai+esmarttax2@convergentmobile.com";	 
     		String email4 = "kpillai+webhookcanada@convergentmobile.com";
    		
	       switch(brand) {
	       	case 1: //Liberty
	       		brandName = "Liberty";
	       		email = email1;
	       		break;
	       	case 2: //Siempre
	       		brandName = "SiempreTax";
	       		email = email2;
	       		break;  	       		
	       	case 3: //eSmartTax
	       		brandName = "eSmartTax";
	       		email = email3;
	       		break;
	       	case 4: //Liberty Canada
	       		brandName = "Liberty Canada";
	       		email = email4;
	       		break; 	       		
	       	default:	       		
	       }
	       
	       System.out.println("brandName: " + brandName);
	       
			credFields = (String[]) creds.get(brandName);
			apiName = credFields[0];
			apiKey = credFields[1];
     		
	        try {
	        	url += apiName + "/" + apiKey + "/json/ambassador/get";
	        	
	            HttpPost httpPost = new HttpPost(url);
	            httpPost.setHeader("Content-type", "application/json");

	            JSONObject jObj = new JSONObject();
	            jObj.put("email", email); 
	            httpPost.setEntity(new StringEntity(jObj.toString(), "UTF8"));  

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
	            }
	           	           
	            EntityUtils.consume(resEntity);	            
	        } 
	        catch (Exception e) {
	            System.out.println(e);
	        }
	        finally {
	            // When HttpClient instance is no longer needed,
	            // shut down the connection manager to ensure
	            // immediate deallocation of all system resources
	            httpclient.close();
	        }		
	}
	
	public void postIt() throws Exception {
    	String url = "http://us411.co/lts_marketing/ext/amb/AmbassadorWH1?brand=4";
    	url = "http://23.23.203.174/lts_marketing/ext/amb/AmbassadorWH1?brand=1&registration=yes";    	
    	url = "http://23.23.203.174/lts_marketing/ext/amb/AmbassadorWH1?brand=1";    	
    	
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-type", "application/json");

        String data = "{\"webhook_version\": \"0.1\", \"token\": \"CA3YGN6W2099EN5VMZK6YFPIC9H8T6\", \"event_data\": {\"contact\": {\"street\": \"123 Fake St\", \"last_name\": \"Pillai\", \"email\": \"kpillai+webhook192@convergentmobile.com\", \"created_at\": \"2017-01-05T15:54:10.079256\", \"city\": \"Royal Oak\", \"country\": null, \"state\": \"MI\", \"phone\": \"415.722.6737\", \"job_title\": null, \"id\": 27565862, \"first_name\": \"Krishna\", \"company\": null, \"status\": \"enrolled\", \"zip\": \"48067\"}, \"payout\": {\"amount\": 1.0, \"transaction_id\": null, \"gift_card_sku\": null, \"created_at\": \"2017-01-24T21:53:04.485319\", \"id\": 388101, \"voucher_code\": null, \"payment_method\": \"manual\", \"unit\": \"money\"}}, \"created_at\": \"2017-01-24T21:53:04.485319\", \"event_type\": \"payout-created\", \"event_id\": 78}";
         
        httpPost.setEntity(new StringEntity(data, "UTF8"));  

        System.out.println("executing request " + httpPost.getRequestLine());
		CloseableHttpClient  httpclient = HttpClientBuilder.create().build();
        HttpResponse response = httpclient.execute(httpPost);
        HttpEntity resEntity = response.getEntity();

        System.out.println("----------------------------------------");
        System.out.println(response.getStatusLine());
	}
}
