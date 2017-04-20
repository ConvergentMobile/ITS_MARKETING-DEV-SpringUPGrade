package util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;

import org.apache.http.util.EntityUtils;
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
	
	private static final Map creds = new HashMap();
	private static final String msg_Liberty_en = "ALERT: Check your email, you just got paid $<amt> for a referral to Liberty Tax! To keep sharing & earn more login to Refer.LibertyTax.com.";
	private static final String msg_Liberty_sp = "Alerta: Revise su email, recibio $<amt> por el referido a Liberty Tax! Siga compartiendo y gane mas ingresando a Refer.LibertyTax.com. Responde STOP para salir";
	private static final String msg_eSmartTax_en = "ALERT: Check your email, you just got paid $<amt> for a referral to eSmart Tax! To keep sharing & earn more go to Refer.eSmartTax.com.";
	private static final String msg_eSmartTax_sp = "";
	private static final String msg_LibertyCA_en = "ALERT: Check your email, you just got paid $<amt> for a referral to Liberty Tax! To keep sharing & earning go to Refer.LibertyTaxCanada.ca.";
	private static final String msg_LibertyCA_sp = "";
	private static final String msg_Siempre_en = "ALERT: Check your email, you just got paid $<amt> for a referral to SiempreTax+! To keep sharing & earn more login to Refer.SiempreTax.com.";
	private static final String msg_Siempre_sp = " Alerta: Revise su email, recibio $<amt> por el referido a SiempreTax+! Siga compartiendo y gane mas ingresando a Refer.Siempretax.com. Responde STOP para salir";
	private static final String msg_LibertyPartners_en = "ALERT: Check your email, your organization just got paid $<amt> for a referral to Liberty Tax! To keep sharing & earn more login to Refer.LibertyTax.com.";
	private static final String msg_LibertyPartners_sp = "Alerta: Revise su correo electronico, su organizacion acaba de recibir pago de $<amt> por un referido a Liberty Tax! Para continuar compartiendo y ganar mas ingrese a Refer.LibertyTax.com. Responde STOP para salir";

	private static final String msg_SAF_liberty_en = "You're officially a Friend of Liberty Tax! Share your link below with friends. You get paid & they get a $50 Off discount! <SHARELINK>";
	private static final String msg_SAF_liberty_sp = "Usted es oficialmente un Amigo de Liberty Tax! Comparta su enlace con amigos. Obtenga pago y para ellos un descuento de $50 <SHARELINK>";
	private static final String msg_SAF_eSmartTax_en = "You're officially a Friend of eSmart Tax! Share your link below with friends. You get paid & they get a $10 Off discount! <SHARELINK>";
	private static final String msg_SAF_eSmartTax_sp = "";
	private static final String msg_SAF_libertyCA_en = "You're officially a Friend of Liberty Tax! Share your link below with friends. You get paid & they get a $20 Off discount! <SHARELINK>";
	private static final String msg_SAF_libertyCA_sp = "";
	private static final String msg_SAF_Siempre_en = "You're officially a Friend of SiempreTax+! Share your link below with friends. You get paid & they get a $50 Off discount! <SHARELINK>";
	private static final String msg_SAF_Siempre_sp = "Usted es oficialmente un Amigo de SiempreTax+! Comparta su enlace con amigos. Obtenga pago y para ellos un descuento de $50 <SHARELINK>";
	
	private static void initCreds() {
		creds.put("Liberty", new String[] {"libertytax", "c3a229100fbafb53a54741eb182a3ff3"});
		creds.put("eSmartTax", new String[] {"eSmartTax", "7673ea1fba1de6c99568e0b779655fd2"});
		creds.put("Liberty Canada", new String[] {"libertycanada", "a2dd533eb7eb4338c7a7daa263a4c61f"});
		creds.put("SiempreTax", new String[] {"siempretax", "8531a48daf0b8a3535e949ed85676c9a"});
	}
	
	private JSONParser parser = new JSONParser();
	
	private String createMsg(Integer brand, String lang, String fname, Double amount) throws Exception {
		return this.createMsg(brand, lang, fname, amount, false, null);
	}
	
	private String createMsg(Integer brand, String lang, String fname, Double amount, Boolean isSAF, String url) throws Exception {
	   String msg = null;
       switch (brand) {
       	case 1: //Liberty
       		if (lang.equals("LTSPartners_SP"))
       			msg = msg_LibertyPartners_sp;
       		else if (lang.equals("LTSPartners_EN"))
       			msg = msg_LibertyPartners_en;
       		else if (lang.equals("SP")) {
       			if (isSAF)
       				msg = msg_SAF_liberty_sp;
       			else
       				msg = msg_Liberty_sp;
       		} else {
       			if (isSAF)
       				msg = msg_SAF_liberty_en;
       			else
       				msg = msg_Liberty_en;
       		}
       		break;
       	case 2: //Siempre
       		if (lang.equals("SP")) {
       			if (isSAF)
       				msg = msg_SAF_Siempre_sp;
       			else
       				msg = msg_Siempre_sp;
       		} else {
       			if (isSAF)
       				msg = msg_SAF_Siempre_en;
       			else
       				msg = msg_Siempre_en;	
       		}
       		break;         		
       	case 3: //eSmartTax
       		lang = "EN";
       		if (isSAF)
       			msg = msg_SAF_eSmartTax_en;
       		else
       			msg = msg_eSmartTax_en;
       		break;
       	case 4: //Liberty Canada
       		lang = "EN";
       		if (isSAF)
       			msg = msg_SAF_libertyCA_en;
       		else
       			msg = msg_LibertyCA_en;
       		break;      		
       	default:
       		logger.error("No msg exists for: " + brand + " - " + lang);
       		return null;
       }
                     
        if (isSAF)
        	msg = msg.replaceAll("<SHARELINK>", url);
        else
            msg = msg.replaceAll("<amt>", String.valueOf(amount.intValue()));
        
		StringBuilder sb = new StringBuilder();
		sb.append("Hi ").append(fname).append("<LF>");
		sb.append(msg);
		
		return sb.toString();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("Processing GET request in AmbassadorWH1...");
		this.doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("Processing POST request in AmbassadorWH1...");
		
		String brands = request.getParameter("brand");
		if (brands == null || brands.length() <= 0) {
			logger.error("Missing brand parameter");
			return;
		}
		Integer brand = Integer.valueOf(brands);
		logger.debug("brand: " + brand);

		Boolean isSAF = false;
		String registration = request.getParameter("registration"); //to indicate if this is for SAF
		if (registration != null && registration.equals("yes")) {
			isSAF = true;
			logger.debug("It is SAF");
		}
			
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
			//data = "{\"event_type\": 2, \"created_at\": \"2016-12-01T18:09:00.742721\", \"webhook_version\": \"0.1\", \"event_id\": 9, \"event_data\": {\"contact\": {\"id\": 26546011, \"company\": \"Testing\", \"job_title\": null, \"zip\": \"48067\", \"first_name\": \"Eric\", \"phone\": \"650-430-1680\", \"street\": \"123 Test Ave\", \"created_at\": \"2016-12-01T18:04:15.975558\", \"city\": \"Royal Oak\", \"state\": \"MI\", \"country\": \"USA\", \"status\": \"enrolled\", \"email\": \"kpillai+webhook19@convergentmobile.com\", \"last_name\": \"LibTaxWebhook\"}, \"payout\": {\"id\": 369463, \"transaction_id\": null, \"gift_card_sku\": null, \"created_at\": \"2016-12-01T18:09:00.561561\", \"voucher_code\": null, \"amount\": 1.0, \"unit\": \"money\", \"payment_method\": \"manual\"}}, \"token\": \"0811LG0JOLBEUKLPM0OO974CYQLJWP\"}";

			//String lang = this.getAPI(1, "eric+1212@getambassador.com");
						
			JSONObject jdata = (JSONObject) parser.parse(data);
            JSONObject mobj = (JSONObject)jdata.get("event_data");
            JSONObject obj = (JSONObject)mobj.get("contact");

			Long custId = (Long)obj.get("id");
			String emailId = (String)obj.get("email");
			String firstName = (String)obj.get("first_name");
			String lastName = (String)obj.get("lastName");
			String phone = (String)obj.get("phone");
			String optedIn = (String)obj.get("status");
			
			String ts = null;
			Double amount = null;
			
            JSONObject pobj = (JSONObject)mobj.get("payout");
            if (! isSAF && pobj != null) {
            	ts = (String)pobj.get("created_at");
            	amount = (Double)pobj.get("amount");
            }
			
			logger.debug("amount: " + amount);
			logger.debug("phone: " + phone);
			logger.debug("email" + emailId);
			
			String loc = "US";
						
			//if (optedIn.equals("enrolled")) {
				String[] ret = this.getAPI(brand, emailId);
			
				String lang = ret[0];
				if (lang.equals("not_optedin")) {
					logger.error("not opted in");
					return;
				}
				
				//String lang = "EN";

				if (brand == 4) {
					loc = "CA";
				}
				//new LTSMSSend(1, phone, messageText, UUID.randomUUID().toString(), "US411", null, null).sendIt();
				String msg = this.createMsg(brand, lang, firstName, amount, isSAF, ret[1]);
				new LTSMessageServiceImpl().sendMessage2(phone, msg, custId.toString(), loc);
			//}
			System.out.println("Done");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String[] getAPI(int brand, String email) throws Exception {
		String[] ret = new String[2];
		
		initCreds();

	   CloseableHttpClient  httpclient = HttpClientBuilder.create().build();
       String url = "https://getambassador.com/api/v2/";
       String credFields[] = null;
       String brandName = null;
       String apiName = null;
       String apiKey = null;
       String lang = null;
       
       switch(brand) {
       	case 1: //Liberty
       		brandName = "Liberty";
       		break;
       	case 2: //Siempre
       		brandName = "SiempreTax";
       		break;         		
       	case 3: //eSmartTax
       		brandName = "eSmartTax";
       		lang = "EN";
       		break;
       	case 4: //Liberty Canada
       		brandName = "Liberty Canada";
       		lang = "EN";
       		break;      		
       	default:
       		logger.error("No brand exists for: " + brand);
       		return null;
       }
       
       //if (lang != null)
    	   //return lang;
       
       logger.debug("brandName: " + brandName);
       		
		credFields = (String[]) creds.get(brandName);
		apiName = credFields[0];
		apiKey = credFields[1];
		//email = "eric+1212@getambassador.com";
	
        try {
        	url += apiName + "/" + apiKey + "/json/ambassador/get";
        	
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json");

            JSONObject jObj = new JSONObject();
            jObj.put("email", email); 
            httpPost.setEntity(new StringEntity(jObj.toString(), "UTF8"));

            //List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            //nameValuePairs.add(new BasicNameValuePair("email", email));           
            //httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));    
            
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
                
                JSONObject jdata1 = (JSONObject) parser.parse(responseBody);
                JSONObject resp = (JSONObject)jdata1.get("response");
                JSONObject jdata = (JSONObject)resp.get("data");
                JSONObject amb = (JSONObject)jdata.get("ambassador");
                String custom1 = (String) amb.get("custom1");
                if (custom1 == null || custom1.length() <= 0 || !custom1.equals("on")) {
                	ret[0] = "not_optedin";
                	return ret;
                }
                
                String murl = (String) amb.get("memorable_url");
                
                String groups = (String)amb.get("groups");
                
                if (groups == null)
                	lang = "EN";               
                else if (groups.equals("4"))
                	lang = "LTSPartners_EN";
                else if (groups.equals("6"))
                	lang = "LTSPartners_SP";    
                else if (groups.equals("1"))
                	lang = "EN";
                else 
                	lang = "SP";
                
                ret[0] = lang;
                ret[1] = murl;
            }
           	           
            EntityUtils.consume(resEntity);	  
        } 
        catch (Exception e) {
            logger.error(e);
        }
        finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.close();
        }
		return ret;		
	}
}