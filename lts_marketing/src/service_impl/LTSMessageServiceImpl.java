package service_impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Queue;

import jms.JMSProducer;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import user.Campaign;
import user.CategoryBase;
import user.JobScheduler;
import user.TargetUserList;
import user.TargetUserListDao;
import util.LTException;
import util.PropertyUtil;
import dao.LTUserDAOManager;
import data.LTUserForm;
import data.ValueObject;

public class LTSMessageServiceImpl {
	protected static final Logger logger = Logger.getLogger(LTSMessageServiceImpl.class);
	private static final String APPLICATION_CONTEXT_FILE = "classpath:application_context.xml";
	private Integer siteId = Integer.parseInt(PropertyUtil.load().getProperty("siteId"));

	//check that the entity message quota is not being exceeded. True => not exceeded
	public Boolean checkQuota(List<String> listIds, Long userId, Integer msgLength) throws Exception {
		Object[] pNums = null;
		TargetUserListDao tulDAO = new TargetUserListDao();
		List<String> tmpNums = new ArrayList<String>();
		
		for (String listId : listIds) {
			logger.debug("listId: " + listId);
			pNums = tulDAO.getListData(listId, userId).toArray();
			for (int i = 0; i < pNums.length; i++)
				tmpNums.add((String) pNums[i]);					
		}	
		pNums = tmpNums.toArray();
		
		if (userId.intValue() == 0) {//if this is a corporate user, return true
			return true;
		}
		
		//Map<String, Integer> quota = new LTUserDAOManager().getMsgQuota(userId);
		ValueObject quota = new LTUserDAOManager().getMsgQuota(userId).get(0);

		int numMsgs = pNums.length * (int)((msgLength + 159) / 160);
		//if (quota.get("Used") + numMsgs > quota.get("Allowed")) {
		if (((Integer)quota.getField3()) + numMsgs > ((Integer)quota.getField2())) {		
			return false;
		}
		
		//update the counts
		int ret = new LTUserDAOManager().updateMsgQuota(userId, (Integer)quota.getField3() + numMsgs);
		
		return true;
	}
	
	public void scheduleJob(String schedDate, String schedTime,
			Integer repeatDayCount, Integer repeatMonthCount,
			Integer numOccurrenceDays, Integer numOccurrenceMonths,
			Campaign campaign, String tzone) throws Exception {

		logger.debug("schedDate/Hour: " + schedDate + ", [" + schedTime + "]");
		logger.debug("repeat Days: " + repeatDayCount);
		logger.debug("repeat Mon: " + repeatMonthCount);

		try {
			Integer repeat = null;
			String unit = null;
			Integer numberOccurrences = null;

			if (repeatDayCount != null && repeatDayCount > 0) {
				repeat = repeatDayCount;
				unit = "d";
				numberOccurrences = numOccurrenceDays;
			}

			if (repeatMonthCount != null && repeatMonthCount > 0) {
				repeat = repeatMonthCount;
				unit = "m";
				numberOccurrences = numOccurrenceMonths;
			}

			new JobScheduler().schedule(schedDate, schedTime, campaign, tzone,
					repeat, unit, numberOccurrences);
		} catch (Exception e) {
			e.printStackTrace();
			throw new LTException(e.getMessage() +
					"\nPlease check your timezone setting and the schedule date & time");
		}

		return;
	}
	
	public void sendMessageLT(Campaign campaign, LTUserForm ltUser) throws Exception {
		CategoryBase cbase = new CategoryBase();
		
		// check if it is for immediate send or to be scheduled
		if (! ltUser.isSendNow()) {
			try {
				if (cbase.getTimezone() == null)
					cbase.setTimezone("US/Pacific");  //this is a stop gap for now - 11/19/2013
        		logger.debug("tzone: " + cbase.getTimezone());
        		
        		Integer repeatDayCount = null;
        		Integer repeatMonthCount = null;
        		Integer numberOccurrencesDays = null;
        		Integer numberOccurrencesMonths = null;
        		
        		if (ltUser.getRepeatPeriod() != null && ltUser.getRepeatPeriod().equals("Days")) {
        			repeatDayCount = ltUser.getRepeatDayCount();
        			numberOccurrencesDays = ltUser.getNumberOccurrencesDays();
        		}
        		if (ltUser.getRepeatPeriod() != null && ltUser.getRepeatPeriod().equals("Months")) {
        			repeatMonthCount = ltUser.getRepeatDayCount();
        			numberOccurrencesMonths = ltUser.getNumberOccurrencesDays();
        		}   
        		
				//this.scheduleJob(ltUser.getSchedDate(), ltUser.getSchedTime(), ltUser.getRepeatDayCount(), ltUser.getRepeatMonthCount(), 
				//					ltUser.getNumberOccurrencesDays(), ltUser.getNumberOccurrencesMonths(), campaign, cbase.getTimezone());
        		
				this.scheduleJob(ltUser.getSchedDate(), ltUser.getSchedTime(), repeatDayCount, repeatMonthCount, 
						numberOccurrencesDays, numberOccurrencesMonths, campaign, cbase.getTimezone());        		
			} catch (Exception e) {
				throw new LTException(e.getMessage()
						+ "\nPlease check your timezone setting and the schedule date & time");
			}
		} else {
			try {
				//this.sendMessage(campaign, campaign.getUserId());
				this.sendMessage(campaign);
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("Error in sending msgs " + e.getMessage());
				throw new Exception(e);
			}
		}

		// save this campaign only if scheduling is successful
		LTUserDAOManager ltdao = new LTUserDAOManager();		
		ltdao.saveCampaign(campaign);
	}
	
	private void sendMessage(Campaign campaign) throws Exception {
		logger.debug("sendMessage - keyword: " + campaign.getKeyword());
		Map<String, Long> pList = new HashMap<String, Long>();
		
		for (final String listId : campaign.getListIds()) {
			List<ValueObject> listData = new LTUserDAOManager().getListData(listId);
			for (ValueObject vo : listData) {
				if (! pList.containsValue(vo.getField1())) //add the number only if does not exist
					pList.put((String)vo.getField1(), (Long)vo.getField2());

			}
		}
		
		if (pList.isEmpty()) {
			throw new Exception("No phone numbers found");
		}
		
	    ApplicationContext ctx = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_FILE);
	    JMSProducer producer = (JMSProducer)ctx.getBean("producer");
		Queue q = (Queue) ctx.getBean("destination");
		logger.debug("In CategoryBase - destination: " + q.getQueueName());
	    producer.sendMessage(campaign, pList);
	}
	
	private void sendMessage(Campaign campaign, Long userId) throws Exception {
		logger.debug("sendMessage of categoryBase - keyword: " + campaign.getKeyword());
		Map<String, Long> pList = new HashMap<String, Long>();
		
		for (final String listId : campaign.getListIds()) {
			List<String> pNums = new TargetUserListDao().getListData(listId);
			for (String pNum : pNums)
				if (! pList.containsValue(pNum)) //add the number only if does not exist
					pList.put(pNum, userId);
		}
		
		if (pList.isEmpty()) {
			throw new Exception("No phone numbers found");
		}
		
	    ApplicationContext ctx = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_FILE);
	    JMSProducer producer = (JMSProducer)ctx.getBean("producer");
		Queue q = (Queue) ctx.getBean("destination");
		logger.debug("In CategoryBase - destination: " + q.getQueueName());
	    producer.sendMessage(campaign, pList);
	}
	
	public List<TargetUserList> getList(List<String> officeIds) throws Exception {
		return new LTUserDAOManager().getList(officeIds);
	}
	
	public List<String> getListData(String listId) throws Exception {
		return new TargetUserListDao().getListData(listId);
	}
}
