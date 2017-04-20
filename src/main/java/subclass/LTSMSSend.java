package subclass;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.Normalizer;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import service_impl.LTSMessageServiceImpl;
import sms.SMSDelivery;
import sms.SMSSend;
import sms.UpdateStats;
import util.PropertyUtil;

public class LTSMSSend extends SMSSend {
	private static Logger logger = Logger.getLogger(LTSMSSend.class);

    private static final String shortCode = "87411";
    protected String keyword;

    //private static final String deliveryURL = "http://convergentmobile.com/sms/deliveryreport_us.jsp";
    //private static final String deliveryURL = "http://23.23.203.174/sms/deliveryreport_us.jsp";
    private static final String deliveryURL = PropertyUtil.load().getProperty("deliveryURL", "http://us411.co/sms/deliveryreport_us.jsp");

	private SMSDelivery smsD = new SMSDelivery();

    public LTSMSSend(int account, String smsto, String msg, String campaignId, String keyword, String customerId, String txId) throws Exception {
		super(account, smsto, msg, campaignId, customerId, txId);

         this.keyword = keyword;
    }
    
    //used by Ambassador
    public void sendIt2() throws Exception {
        Vector<String> ticketIds = new Vector<String>();
        smsto = smsD.normalizePhoneNumber(smsto);
 	    try {
 			ticketIds = smsD.sendMsg(smsto, msg, keyword, "US411", true);
            upds.updateMsgStats(ticketIds, smsto, campaignId, "US411", customerId, txId, keyword);
 	    } finally {
 	    }
     }

    public Vector<String> sendIt() throws Exception {
 		String stdStuff = "\nStop to End,HELP 4 Help. Msg & Data rates may apply";	
        Vector<String> ticketIds = new Vector<String>();

        /*
		if (new LTSMessageServiceImpl().checkOptedOut(smsto, null, null)) {
			logger.error(smsto + " has opted out");
			ticketIds.add("Opted Out");
            upds.updateMsgStats(ticketIds, smsto, campaignId, "US411", customerId, txId, keyword);
            return ticketIds;
		}
		*/
		
 		String temp = Normalizer.normalize(msg, Normalizer.Form.NFD); 
 		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
 		msg = pattern.matcher(temp).replaceAll("");
 		    
         logger.debug("LT smsto, msg: " + smsto + ", " + msg);

         String encodeV = "Basic " + base64;

         URL url = new URL(sybaseUrl);
 	    URLConnection uconn = url.openConnection();
 	    conn = (HttpURLConnection) uconn;
 	    conn.setDoInput (true);
 	    conn.setDoOutput (true);
 	    conn.setUseCaches (false);
 	    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
 	    conn.setRequestProperty("Authorization", encodeV); //login:password base64 encoded

         logger.debug("Using: " + sybaseUrl);

         wr = new OutputStreamWriter(conn.getOutputStream());
 	
 	    StringBuffer sb = new StringBuffer();
 	    final String SEP = System.getProperty("line.separator");
 	
 	    sb.append("[SETUP]").append(SEP).append("SplitText=YES").append(SEP);
 	    sb.append("DCS=8859-7").append(SEP);
 	    sb.append("AckReplyAddress=").append(deliveryURL).append(SEP);
 	    sb.append("AckType=").append("Message").append(SEP);
 	    sb.append("OriginatingAddress=").append(shortCode).append(SEP);
 	    sb.append("MobileNotification=Yes").append(SEP);
 	
 	    sb.append("[MSISDN]").append(SEP).append("List=").append(smsto).append(SEP);
 	    sb.append("[MESSAGE]").append(SEP).append("Text=" + msg).append(SEP);
 	
 	    sb.append("[END]");
 	
 	    wr.write(sb.toString());
 	    wr.flush();
 	
 	    try {
 	        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
 	
             String response = null;
             boolean start = false;
             String ticketId = null;
             while ((response = rd.readLine()) != null) {
                 if (response.indexOf("<BODY>") >= 0) {
                         start = true;
                         continue;
                 }

                 if (! start || response.indexOf("#Message") >= 0)
                         continue;

                 logger.debug("response: " + response);
                 int idx = response.indexOf("ORDERID=");
                 if (idx >= 0) {//success
                         ticketId = response.substring(8, response.indexOf("<br>"));
                         logger.debug("ticketId: " + ticketId);
                         String[] tktIds = ticketId.split(",");
                         for (String tktId : tktIds)
                                 ticketIds.add(tktId);
                         upds.updateMsgStats(ticketIds, smsto, campaignId, "US411", customerId, txId, keyword);
                         break;
                 }

                 //must be an error
                 if (response.indexOf("Error") >= 0) {
                         String[] errs = response.split(" ");
                         String errCode = errs[0];
                         String errMsg = errs[1];
                         logger.error(smsto + ": Message was not sent!");
                         //ticketId = UUID.randomUUID().toString();
                         ticketId = "-1";
                         ticketIds.add(ticketId);

                         logger.error("errCode, errMsg = " + errCode + ", " + errMsg);
                         break;
                 }
             }

             return ticketIds;
 	    } finally {
 	            if (wr != null)
 	                    wr.close();
 	            if (rd != null)
 	                    rd.close();
 	            if (conn != null)
 	                    conn.disconnect();
 	    }
     }
}
                            
