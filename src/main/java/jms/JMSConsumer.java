package jms;

import java.util.Vector;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import sms.SMSDelivery;
import sms.US411Handler;
import user.UserDAOManager;
import util.PropertyUtil;
import util.US411Message;

@Component
public class JMSConsumer implements MessageListener {
	private static final Logger logger = Logger.getLogger(JMSConsumer.class);
	
	private String shortCode = PropertyUtil.load().getProperty("shortcode");

	public void onMessage(Message message) {		
			logger.debug("In JMSConsuer: onMessage");
	        MapMessage mapMessage = null;
			SMSDelivery smsD = new SMSDelivery();
			US411Handler smsh = new US411Handler(null, "US", shortCode);
			
	        if (message instanceof MapMessage) {
	            mapMessage = (MapMessage)message;
	            String msg = null;
	            String campaignId = null;
	            String keyword = null;
	            String pNum = null;
	            Long userId = null;
	            String shortcode = null;
	            try {
	        		logger.debug("JMSConsumer: shortcode = " + shortCode);
	            	msg = mapMessage.getString("message");
	                pNum = mapMessage.getString("phoneNumber");
	                //logger.debug("msg, pNum: " + msg + ", " + pNum);	                
	                pNum = smsD.normalizePhoneNumber(pNum);
	                campaignId = mapMessage.getString("campaignId");
	                keyword = mapMessage.getString("keyword");
	                userId = mapMessage.getLong("userId");
	                shortcode = mapMessage.getString("shortcode");
	                if (shortcode == null) {
	                	logger.debug("Got null shortcode. Using one from the props file");
	                	shortcode = shortCode;
	                }
	    			Vector<String>ticketIds = smsD.sendSybase(pNum, msg, shortcode, keyword, true);
	    			smsh.updateMsgStats(ticketIds, pNum, campaignId, shortcode, userId.toString(), "JMS Test", keyword);
	            } catch (Exception e) {
	            	e.printStackTrace();
	                logger.error("Error in sendMsg for " + msg + " - " + e);
	                try {
						this.handleError(shortcode, keyword, campaignId, pNum, msg, userId);
					} catch (Exception e1) {
						logger.error("Error in savingMsg: " + e1);
					}
	            } finally {	            	
	            }
	       }
	}	
	
	private void handleError(String shortcode, String keyword, String campaignId, String mobilePhone, String msg, Long userId) throws Exception {
		new UserDAOManager().saveUS411Message(new US411Message(shortcode, keyword, campaignId, msg, mobilePhone, "E", userId));
	}
}
