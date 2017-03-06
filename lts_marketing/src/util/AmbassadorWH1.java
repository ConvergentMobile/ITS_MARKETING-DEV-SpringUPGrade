package util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.test.context.ContextConfiguration;

import service_impl.LTSMessageServiceImpl;
import subclass.LTSMSSend;
import user.Campaign;

@ContextConfiguration(locations={"file:WEB-INF/classes/5star_mkt-servlet.xml"})
public class AmbassadorWH1 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(AmbassadorWH1.class);
	protected static final Integer siteId = Integer.valueOf(PropertyUtil.load().getProperty("siteId"));
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("Processing GET request in AmbassadorWH1...");
		this.doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("Processing POST request in AmbassadorWH1...");

		String data;
		StringBuilder sb = new StringBuilder();
		
		try {
			while ((data = request.getReader().readLine()) != null) {
				sb.append(data);
			}
			
			PrintWriter pw = response.getWriter();
			pw.println("OK");
			pw.close();
			
			data = sb.toString();
			logger.debug("data: " + data);	
			
			JSONParser parser = new JSONParser();

			/*
			//for testing			
			JSONObject jobj = new JSONObject();
			jobj.put("email_id", "kpillai@yahoo.com");
			jobj.put("email_id", "kpillai@yahoo.com");
			jobj.put("first_name", "Krishna");
			jobj.put("last_name", "Pillai");
			jobj.put("phone_number", "4157226737");
			jobj.put("opted_in", "1");
			jobj.put("payout_timestamp", "2016-10-20 10:30 AM PDT");

			logger.debug("data: " + jobj.toJSONString());	
			
			JSONObject obj = (JSONObject) parser.parse(jobj.toJSONString());
			*/
			
			//for testing
			//data = "{\"event_type\": 2, \"created_at\": \"2016-12-01T18:09:00.742721\", \"webhook_version\": \"0.1\", \"event_id\": 9, \"event_data\": {\"contact\": {\"id\": 26546011, \"company\": \"Testing\", \"job_title\": null, \"zip\": \"48067\", \"first_name\": \"Eric\", \"phone\": \"650-430-1680\", \"street\": \"123 Test Ave\", \"created_at\": \"2016-12-01T18:04:15.975558\", \"city\": \"Royal Oak\", \"state\": \"MI\", \"country\": \"USA\", \"status\": \"enrolled\", \"email\": \"eric+libtaxwebhook@getambassador.com\", \"last_name\": \"LibTaxWebhook\"}, \"payout\": {\"id\": 369463, \"transaction_id\": null, \"gift_card_sku\": null, \"created_at\": \"2016-12-01T18:09:00.561561\", \"voucher_code\": null, \"amount\": 1.0, \"unit\": \"money\", \"payment_method\": \"manual\"}}, \"token\": \"0811LG0JOLBEUKLPM0OO974CYQLJWP\"}";

			JSONObject jdata = (JSONObject) parser.parse(data);
            JSONObject mobj = (JSONObject)jdata.get("event_data");
            JSONObject obj = (JSONObject)mobj.get("contact");

			Long custId = (Long)obj.get("id");
			String emailId = (String)obj.get("email_id");
			String firstName = (String)obj.get("first_name");
			String lastName = (String)obj.get("lastName");
			String phone = (String)obj.get("phone");
			String optedIn = (String)obj.get("status");
			
            JSONObject pobj = (JSONObject)mobj.get("payout");
			String ts = (String)pobj.get("created_at");
			Double amount = (Double)pobj.get("amount");
			
			logger.debug("phone: " + phone);
						
			if (optedIn.equals("enrolled")) {
				//new LTSMSSend(1, phone, messageText, UUID.randomUUID().toString(), "US411", null, null).sendIt();
				sb.setLength(0);
				sb.append("Hi ").append(firstName).append("<LF>");
				sb.append("You just got paid $")
					.append(amount)
					.append(" for referring a friend to Liberty Tax! Check your account, share, and earn more $$$!");
				new LTSMessageServiceImpl().sendMessage2(phone, sb.toString(), custId.toString());
			}
			System.out.println("Done");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}