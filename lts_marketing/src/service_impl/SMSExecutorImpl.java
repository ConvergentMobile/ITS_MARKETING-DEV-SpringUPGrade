package service_impl;

import org.apache.log4j.Logger;

import subclass.LTSMSSend;

public class SMSExecutorImpl implements Runnable {
	private static Logger logger = Logger.getLogger(SMSExecutorImpl.class);
	
	private String mobilePhone;
	private String campaignId;
	private String messageText;
	private String keyword;
	private String custField2;
	
	public SMSExecutorImpl(String mobilePhone, String messageText, String campaignId, String keyword, String custField2) throws Exception {		
		this.mobilePhone = mobilePhone;
		this.campaignId = campaignId;
		this.messageText = messageText;
		this.keyword = keyword;
		this.custField2 = custField2;
	}
	
	@Override
	public void run() {
		try {
			logger.debug("SMSThread: running for phone: " + mobilePhone);
			new LTSMSSend(1, mobilePhone, messageText, campaignId, keyword, custField2, null).sendIt();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
