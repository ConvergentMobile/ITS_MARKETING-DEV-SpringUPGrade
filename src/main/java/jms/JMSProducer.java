package jms;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import user.Campaign;
import user.TargetUserList;
import user.TargetUserListDao;

@Component
public class JMSProducer {
	private static final Logger logger = Logger.getLogger(JMSProducer.class);
	
	@Autowired
	private JmsTemplate template;
	private Queue destination;
			
    public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	public void setDestination(Queue destination) {
		this.destination = destination;
	}
	
	public Queue getDestination() {
		return destination;
	}

	public String createMsg(String msg, String keyword) {
		// take this out per Sprint audit - 6/16/2016
		/*
		try {
			if (this.destination.getQueueName().equals("NN.QUEUE")) //hack for N&N to not add the std text
				return msg;
		} catch (JMSException e) {
			logger.error("Error in createMessage: " + e);
		}
		*/
		
		//return msg + "\nTxt STOP " + keyword + " to End,HELP 4 Help.Msg&Data rates may apply";
		return msg + "\nStop to End, HELP 4 Help. Msg & Data rates may apply";		
		//return msg + "\nStop? Txt\n" + keyword + " STOP\nHelp? Txt\nHELP. Msg & Data rates may apply";

	}

	public void sendMessage(final Campaign campaign, Object[] pNums) throws JMSException {       
		//final String msg = campaign.getMessageText() + "\nTxt STOP " + campaign.getKeyword() + " to End,HELP 4 Help.Msg&Data rates may apply";
		final String msg = this.createMsg(campaign.getMessageText(), campaign.getKeyword());
		logger.debug("JMSProducer: queing msg: " + msg);
		logger.debug("pNums size: " + pNums.length);
		final String shortcode = campaign.getShortcode();
		for (final Object pNum : pNums) {
	        MessageCreator creator = new MessageCreator() {
	            public Message createMessage(Session session)
	            {
	                MapMessage message = null;
	                try {
	                	logger.debug("campaignId, pNum: " + campaign.getCampaignId() + ", " + pNum);
	                    message = session.createMapMessage();
	                    message.setString("message", msg);
	                    message.setString("phoneNumber", pNum.toString()); //this should always be a String
	                    message.setString("campaignId", campaign.getCampaignId());
	                    message.setLong("userId", campaign.getUserId());
	                    message.setString("keyword", campaign.getKeyword());
	                    message.setString("shortcode", shortcode);
	                } catch (JMSException e) {
	                    e.printStackTrace();
	                }
	                return message;
	            }
	        };
	        	        
	        logger.debug("destination: " + destination.getQueueName());
	        template.send(destination, creator);			
		}
	}

	public void sendMessage(final Campaign campaign, Map<String, Long> pList) throws JMSException {       
		//final String msg = campaign.getMessageText() + "\nTxt STOP " + campaign.getKeyword() + " to End,HELP 4 Help.Msg&Data rates may apply";
		final String msg = this.createMsg(campaign.getMessageText(), campaign.getKeyword());
		logger.debug("JMSProducer: queing msg: " + msg);

		for (final Map.Entry<String, Long> mentry : pList.entrySet()) {
	        logger.debug("uid, num : " + mentry.getValue() + ", " + mentry.getKey());

	        MessageCreator creator = new MessageCreator() {
	            public Message createMessage(Session session){
	                MapMessage message = null;
	                try {
	                    message = session.createMapMessage();
	                    message.setString("message", msg);
	                    message.setString("phoneNumber", mentry.getKey()); //this should always be a String
	                    message.setString("campaignId", campaign.getCampaignId());
	                    message.setLong("userId", mentry.getValue());
	                    message.setString("keyword", campaign.getKeyword());
	                } catch (JMSException e) {
	                    e.printStackTrace();
	                }
	                return message;
	            }
	        };
	        	        
	        logger.debug("destination: " + destination.getQueueName());
	        template.send(destination, creator);			
		}
	}
	
	public void sendMessage(String msg) {
		System.out.println("Sending msg: " + msg);
		logger.debug("Sending msg: " + msg);
		template.convertAndSend(destination, msg);
	}

	public Queue getQueue(String shortcode) throws Exception {
		Queue q = null;
		Context ctx = new InitialContext();  
		logger.debug("shortcode: " + shortcode);
		if (shortcode.equals("5STAR"))
			q = (Queue) ctx.lookup("java:comp/env/jms/nnQueue");
		else if (shortcode.equals("US411"))
			q = (Queue) ctx.lookup("java:comp/env/jms/us411Queue");
		
		return q;
	}
}
