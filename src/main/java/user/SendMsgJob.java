package user;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.jms.Queue;

import jms.JMSProducer;
import mdp_common.DBStuff;
import sms.SMSDelivery;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class SendMsgJob extends QuartzJobBean {
	protected static final Logger logger = Logger.getLogger(SendMsgJob.class);
	protected static final String APPLICATION_CONTEXT_FILE = "application_context.xml";
	protected ApplicationContext ctx;
	protected static final String APPLICATION_CONTEXT = "applicationContext";
	
	public SendMsgJob() {	
	}
	
	public ApplicationContext getCtx() {
		return ctx;
	}

	public void setCtx(ApplicationContext ctx) {
		this.ctx = ctx;
	}

	public void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        
        logger.debug("In SendMsgJob: executeInternal");
        
        try {
            ctx = (ApplicationContext) context.getScheduler().getContext().get(APPLICATION_CONTEXT);
                        
            JMSProducer producer = (JMSProducer)ctx.getBean("producer");
            if (producer == null) {
            	logger.error("Null producer");
            	throw new Exception("Null Producer");
            }
            
            Queue destQ = (Queue)jobDataMap.get("destQ");
            logger.debug("dest: " + destQ.getQueueName());
            producer.setDestination(destQ);
            
    		Object[] pNums = null;
    		TargetUserListDao tulDAO = new TargetUserListDao();
            Campaign campaign = (Campaign)jobDataMap.get("campaign");
            
            logger.debug("listId: " + campaign.getListId());
            
            if (campaign.getListId().equals("Numbers")) { //send to selected numbers
            	pNums = campaign.getTargetNumbers();
                for (Object pnum : pNums)
                	logger.debug("num: " + pnum.toString());
            } else {
	            List<TargetUserList>tuList = (List<TargetUserList>)jobDataMap.get("tuList");
	            if (tuList != null && ! tuList.isEmpty() && campaign.getListId().equals("Multi")) {
	        	    producer.sendMessage(campaign, pNums);            
	        	    return;
	            }
	            
	    		List<String> tmpNums = new ArrayList<String>();
	    		if (campaign.getListId().equals("Multi")) { //this is a multi list case
	    			SMSDelivery smsd = new SMSDelivery();
    				String pn = null;
	    			for (TargetUserList tul : campaign.getMultiList()) {
	    				logger.debug("Campaign listId: " + tul.getListId());
	    				pNums = tulDAO.getListData(tul.getListId(), campaign.getUserId()).toArray();
						for (int i = 0; i < pNums.length; i++)
	    					pn = smsd.normalizePhoneNumber((String) pNums[i]);
	    					if (! tmpNums.contains(pn))
	    						tmpNums.add(pn);					
	    			}	
	    			pNums = tmpNums.toArray();
	    		} else {		
	                //List<String> pNums = getNumbers(listId);
	    			pNums = tulDAO.getListData(campaign.getListId(), campaign.getUserId()).toArray();
	    		}
            }
            
    	    if (pNums == null || pNums.length == 0)
    	    	throw new Exception("No phone numbers found");

    	    producer.sendMessage(campaign, pNums);            
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public void executeInternal_old(JobExecutionContext context)
			throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        //US411Handler smsH = new US411Handler(null, "US");
        
        Campaign campaign = (Campaign)jobDataMap.get("campaign");
        String listId = campaign.getListId();
        
        String baseURL = jobDataMap.getString("baseURL");
        logger.debug("SendMsgJob base: " + baseURL);
        
        try {
    		Object[] pNums = null;
    		TargetUserListDao tulDAO = new TargetUserListDao();
    		List<String> tmpNums = new ArrayList<String>();
    		if (campaign.getListId().equals("Multi")) { //this is a multi list case
    			for (TargetUserList tul : campaign.getMultiList()) {
    				pNums = tulDAO.getListData(tul.getListId(), campaign.getUserId()).toArray();
    				for (int i = 0; i < pNums.length; i++)
    					tmpNums.add((String) pNums[i]);					
    			}	
    			pNums = tmpNums.toArray();
    		} else {		
                //List<String> pNums = getNumbers(listId);
    			pNums = tulDAO.getListData(campaign.getListId(), campaign.getUserId()).toArray();
    		}
    		    	    
    	    if (pNums == null || pNums.length == 0)
    	    	throw new Exception("No phone numbers found");
    	    
            //smsH.sendBulkSMS(jobDataMap.getString("msgText"), "US411", campaignId, pNums.toArray(), userId, null, keyword);
    	    //ApplicationContext  ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
            //ctx = new ClassPathXmlApplicationContext(baseURL + "/" + APPLICATION_CONTEXT_FILE);
    	    //ctx = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_FILE);
            ctx = (ApplicationContext) context.getScheduler().getContext().get(APPLICATION_CONTEXT);
            
            JMSProducer producer = (JMSProducer)ctx.getBean("producer");
            if (producer == null) {
            	logger.error("Null producer");
            	throw new Exception("Null Producer");
            }
            
            Queue destQ = (Queue)jobDataMap.get("destQ");
            logger.debug("dest: " + destQ.getQueueName());
            producer.setDestination(destQ);
    	    producer.sendMessage(campaign, pNums);            
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	private List<String> getNumbers(String listId) throws Exception {
		String sql = "select mobile_phone from target_list_data tld where tld.list_id = ?";
		DBStuff dbs = new DBStuff();
		List<String> pNums = new ArrayList<String>();
		ResultSet rs = dbs.getFromDB(sql, new Object[]{listId});
		while (rs.next())
			pNums.add(rs.getString("mobile_phone"));
		
		if (pNums == null)
			throw new Exception("Null phone numbers");
		return pNums;
	}
}