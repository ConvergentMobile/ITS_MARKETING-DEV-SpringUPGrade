import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

import dao.LTUserDAOManager;
import data.ValueObject;
import user.Campaign;
import user.JobScheduler;
import user.TargetUserList;
import util.LatLon;
import util.PropertyUtil;
import util.Utility;

public class TestIt {
	private static final String GOOGLE_URL_SHORT_API = "https://www.googleapis.com/urlshortener/v1/url";
	private static final String GOOGLE_API_KEY = "AIzaSyDeU9Su5Dp5Jcwtvb668MFaxn55af8dMl4";
	
	public static void main(String[] args) throws Exception {
		//new TestIt().test1();
		
		/*
		String pnum = new Utility().formatPhone("14157226737");
		System.out.println("pnum: " + pnum);
		
		String longUrl = "http://localhost/lts_marketing/ext/infoForm?m=e&id=5010";
		String surl = new TestIt().shortenUrl(longUrl);
		System.out.println("surl: " + surl);
		*/
		
		new LatLon().getIt("d:\\temp\\LTS Locations for LatLong 022417.csv", "d:\\temp\\latlon_out.csv");
		//new LatLon().getIt("d:\\temp\\latlon_1.csv", "d:\\temp\\latlon_out.csv");
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
	
	public void testScheduleJob() throws Exception {
		Campaign  campaign = new Campaign();
		campaign.setCampaignId(UUID.randomUUID().toString());
		campaign.setUserId(31454L);
		campaign.setName("Test Campaign 1");
		campaign.setKeyword("LIBTAXOG");
		
		String shortcode = "US411";
		campaign.setShortcode(shortcode);
		
		List<String> listIds = new ArrayList<String>();
		listIds.add("08bc9908-98b7-4fb1-af02-695c6bf7732f");
		listIds.add("a33e4f47-6fc3-4d01-af8f-fee7672bbd63");
		
		campaign.setListIds(listIds);
		campaign.setListId("Multi");
		
		List<TargetUserList> tuList = new ArrayList<TargetUserList>();
		for (String listId : campaign.getListIds()) {
			TargetUserList tul = new TargetUserList();
			tul.setListId(listId);
			tuList.add(tul);
		}
		campaign.setMultiList(tuList);
		
		campaign.setMessageText("This is a test msg");
		campaign.setRawMessageText("This is a test msg");
		
		new JobScheduler().schedule("1/12/2017", "10:30 AM", campaign, "US/Pacific", null, null, null);
	}
}
