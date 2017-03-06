import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

import dao.LTUserDAOManager;
import data.ValueObject;

public class TestIt {
	private static final String GOOGLE_URL_SHORT_API = "https://www.googleapis.com/urlshortener/v1/url";
	private static final String GOOGLE_API_KEY = "AIzaSyDeU9Su5Dp5Jcwtvb668MFaxn55af8dMl4";
	
	public static void main(String[] args) throws Exception {
		//new TestIt().test1();
		
		String longUrl = "http://localhost/lts_marketing/ext/infoForm?m=e&id=5010";
		String surl = new TestIt().shortenUrl(longUrl);
		System.out.println("surl: " + surl);
	}
	
	public String shortenUrl(String longUrl) throws Exception {	
        String json = "{\"longUrl\": \""+longUrl+"\"}";   
        String apiURL = GOOGLE_URL_SHORT_API+"?key="+GOOGLE_API_KEY;
         
        HttpPost postRequest = new HttpPost(apiURL);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(json, "UTF-8"));
 
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(postRequest);
        String responseText = EntityUtils.toString(response.getEntity());   
        System.out.println("responseText: " + responseText);
         
        Gson gson = new Gson();
        @SuppressWarnings("unchecked")
        HashMap<String, String> res = gson.fromJson(responseText, HashMap.class);
 
        return res.get("id");  
	}
	
	public void test1() throws Exception {
		Map<String, Long> pList = new HashMap<String, Long>();
		String[] listIds = {"186a41f3-e630-49dd-a07a-59191af9e718", "ac0a1db3-7f63-48f5-b404-2af5216a8533"};
		
		ValueObject vo1 = new ValueObject();
		vo1.setField1("12702269137");

		ValueObject vo2 = new ValueObject();
		vo2.setField1("12702269137");
		
		if (! pList.containsValue(vo1.getField1())) 
				pList.put((String)vo1.getField1(), null);

		if (! pList.containsValue(vo2.getField1())) 
			pList.put((String)vo2.getField1(), null);
		
		
        for (final String listId : listIds) {
            List<ValueObject> listData = new LTUserDAOManager().getListData(listId);
            for (ValueObject vo : listData) {
                    if (! pList.containsValue(vo.getField1())) //add the number only if does not exist
                            pList.put((String)vo.getField1(), (Long)vo.getField2());

            }
    }

        
		for (Map.Entry<String, Long> entry : pList.entrySet())
			System.out.println("num: " + entry.getKey());
	}
}
