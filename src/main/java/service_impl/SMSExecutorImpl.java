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
	
	private String stdStuff = "\nStop to End,HELP 4 Help. Msg & Data rates may apply";	

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
			new LTSMSSend(1, mobilePhone, messageText + stdStuff, campaignId, keyword, custField2, null).sendIt2();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
