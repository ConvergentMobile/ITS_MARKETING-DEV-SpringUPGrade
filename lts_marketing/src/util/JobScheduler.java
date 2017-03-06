package util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.jms.Queue;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.TriggerUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import user.Campaign;
import user.TargetUserList;
import util.DateUtility;
import util.PropertyUtil;

public class JobScheduler {
	protected static final Logger logger = Logger.getLogger(JobScheduler.class);
	protected static final String APPLICATION_CONTEXT_FILE = "classpath:application_context.xml";

	protected Scheduler scheduler;
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void schedule(String schedDate, String schedTime, Campaign campaign, String userTZ) throws Exception {
		this.schedule(schedDate, schedTime, campaign, userTZ, SendMsgJobLTS.class);
	}
	
	public void schedule(String schedDate, String schedTime, Campaign campaign, String userTZ, Class clazz) throws Exception {
		//convert the date to server's tz
		String stmp = new DateUtility().toServerTime(schedDate + " " + schedTime, userTZ);
		logger.debug("sSchedDate: " + stmp);
		
		String[] sf = stmp.split(" ");
		String sSchedDate = sf[0];
		String sSchedTime = sf[1] + " " + sf[2];
		
		//parse the date into mon, day, year
		String[] mdy = sSchedDate.split("/");
		
		//parse the hours into mins and hour
		String[] f = sSchedTime.split(" ");
		String[] hhmi = f[0].split(":");
		int hr = hhmi[0].equals("12") ? 0 : Integer.parseInt(hhmi[0]);
		if (f[1].equals("pm") || f[1].equals("PM")) {
				hr += 12;
		}
		String hour = "" + hr;
		
		StringBuffer cronExpression = new StringBuffer();
		cronExpression.append("0 ").append(hhmi[1]). append(" ").append(hour)
						.append(" ").append(mdy[1])
						.append(" ").append(mdy[0])
						.append(" ? ").append(mdy[2]);
		
		logger.debug("cronExpression = " + cronExpression.toString());
		
		JobDetail job = new JobDetail("SendMsgJob-" + Calendar.getInstance().getTimeInMillis(), Scheduler.DEFAULT_GROUP, clazz);
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("campaign", campaign);
				
		CronTrigger trigger = new CronTrigger();
		trigger.setName(campaign.getKeyword() + " Scheduled Send " + Calendar.getInstance().getTime());
		trigger.setCronExpression(cronExpression.toString());
		//trigger.setCronExpression("0 26 10 * * ?");
		
		trigger.setJobDataMap(jobDataMap);
		
		/*
		SchedulerFactoryBean sb = new SchedulerFactoryBean();
		sb.afterPropertiesSet();
		scheduler = sb.getScheduler();
		*/
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_FILE);
		scheduler = (Scheduler)ctx.getBean("scheduler");
		
		Queue q = (Queue) ctx.getBean("destination");
		logger.debug("In JobScheduler - destination: " + q.getQueueName());
		
		Date ft = scheduler.scheduleJob(job, trigger);
		logger.debug(job.getFullName() + " has been scheduled to run at: " + ft
				+ " and repeat based on expression: "
				+ trigger.getCronExpression());	
	}

	public void schedule(String schedDate, String schedTime, Campaign campaign, String userTZ, List<TargetUserList>tuList) throws Exception {
		//convert the date to server's tz
		String stmp = new DateUtility().toServerTime(schedDate + " " + schedTime, userTZ);
		logger.debug("sSchedDate: " + stmp);
		
		String[] sf = stmp.split(" ");
		String sSchedDate = sf[0];
		String sSchedTime = sf[1] + " " + sf[2];
		
		//parse the date into mon, day, year
		String[] mdy = sSchedDate.split("/");
		
		//parse the hours into mins and hour
		String[] f = sSchedTime.split(" ");
		String[] hhmi = f[0].split(":");
		int hr = hhmi[0].equals("12") ? 0 : Integer.parseInt(hhmi[0]);
		if (f[1].equals("pm") || f[1].equals("PM")) {
				hr += 12;
		}
		String hour = "" + hr;
		
		StringBuffer cronExpression = new StringBuffer();
		cronExpression.append("0 ").append(hhmi[1]). append(" ").append(hour)
						.append(" ").append(mdy[1])
						.append(" ").append(mdy[0])
						.append(" ? ").append(mdy[2]);
		
		logger.debug("cronExpression = " + cronExpression.toString());
		
		JobDetail job = new JobDetail("SendMsgJob-" + Calendar.getInstance().getTimeInMillis(), Scheduler.DEFAULT_GROUP, SendMsgJobLTS.class);
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("campaign", campaign);
		jobDataMap.put("tuList", tuList);
				
		CronTrigger trigger = new CronTrigger();
		trigger.setName(campaign.getKeyword() + " Scheduled Send " + Calendar.getInstance().getTime());
		trigger.setCronExpression(cronExpression.toString());
		//trigger.setCronExpression("0 26 10 * * ?");
		
		trigger.setJobDataMap(jobDataMap);
		
		/*
		SchedulerFactoryBean sb = new SchedulerFactoryBean();
		sb.afterPropertiesSet();
		scheduler = sb.getScheduler();
		*/
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_FILE);
		scheduler = (Scheduler)ctx.getBean("scheduler");
		
		Queue q = (Queue) ctx.getBean("destination");
		logger.debug("In JobScheduler - destination: " + q.getQueueName());
		
		Date ft = scheduler.scheduleJob(job, trigger);
		logger.debug(job.getFullName() + " has been scheduled to run at: " + ft
				+ " and repeat based on expression: "
				+ trigger.getCronExpression());	
	}

	public void schedule(String schedDate, String schedTime, Campaign campaign, String userTZ, Integer repeat, String unit, Integer numberOccurrences) throws Exception {
		Calendar cal = Calendar.getInstance();
		Date nowDt = cal.getTime();
		SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy h:mm a");
		
		if (schedTime == null || schedTime.equals(" ")) {
			schedTime = new SimpleDateFormat("h:mm a").format(nowDt);
		}
		
		String stmp = new DateUtility().toServerTime(schedDate + " " + schedTime, userTZ);
		logger.debug("sSchedDate: " + stmp);

		cal.setTime(sdf1.parse(stmp));
		
		if (numberOccurrences == null)
			numberOccurrences = 1;
		
		List<Date> fireDates = new ArrayList<Date>(numberOccurrences);
		
		if (cal.getTime().compareTo(nowDt) > 0) {
			fireDates.add(cal.getTime());
			numberOccurrences--;
		}
				
		if (repeat != null) {
			if (unit.equals("d")) {
				for (int i = 0; i < numberOccurrences; i++) {
					cal.add(Calendar.DATE, repeat);
					fireDates.add(cal.getTime());
				}
			}
			
			if (unit.equals("m")) {
				for (int i = 0; i < numberOccurrences; i++) {
					cal.add(Calendar.MONTH, repeat);
					fireDates.add(cal.getTime());
				}
			}
		}
		
		//check if we have any fire dates
		if (fireDates.size() <= 0) {
			logger.error("No fire dates");
			throw new Exception("No valid dates to schedule.");
		}

		ApplicationContext ctx = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_FILE);
		scheduler = (Scheduler)ctx.getBean("scheduler");

		Queue q = (Queue) ctx.getBean("destination");
		logger.debug("In JobScheduler - destination: " + q.getQueueName());
		
		//JobDetail job = new JobDetail("SendMsgJob", Scheduler.DEFAULT_GROUP, SendMsgJob.class);

		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("campaign", campaign);
		jobDataMap.put("baseURL", PropertyUtil.load().getProperty("baseURL"));
		jobDataMap.put("destQ", q);
				
		for (Date d : fireDates) {
			JobDetail job = new JobDetail("SendMsgJob-" + Calendar.getInstance().getTimeInMillis(), Scheduler.DEFAULT_GROUP, SendMsgJobLTS.class);

			cal.setTime(d);
			String cexp = "0 " + cal.get(Calendar.MINUTE) + " " + cal.get(Calendar.HOUR_OF_DAY) + " " + cal.get(Calendar.DAY_OF_MONTH) + " " + (1+cal.get(Calendar.MONTH))  + " ?" + " " + cal.get(Calendar.YEAR);
			
			CronTrigger trigger = new CronTrigger();
			trigger.setName(campaign.getKeyword() + " Scheduled Send-" + Calendar.getInstance().getTimeInMillis());
			trigger.setCronExpression(cexp);
			trigger.setJobName(job.getName());
			trigger.setGroup(campaign.getUserId().toString());
			trigger.setJobDataMap(jobDataMap);
			
			Date ft = scheduler.scheduleJob(job, trigger);
			
			logger.debug(job.getFullName() + " has been scheduled to run at: " + ft
					+ " and repeat based on expression: "
					+ trigger.getCronExpression());	
		}
	}
	
	/*
	public void schedule(String schedDate, String schedTime, Campaign campaign, String userTZ, Integer repeat, String unit, Integer numberOccurrences) throws Exception {
		//convert the date to server's tz
		String stmp = new DateUtility().toServerTime(schedDate + " " + schedTime, userTZ);
		logger.debug("sSchedDate: " + stmp);
		
		String[] sf = stmp.split(" ");
		String sSchedDate = sf[0];
		String sSchedTime = sf[1] + " " + sf[2];
		
		//parse the date into mon, day, year
		String[] mdy = schedDate.split("/");
		
		//parse the hours into mins and hour
		String ttime = stmp.split(" ")[1] + " " + stmp.split(" ")[2]; //use the converted tz time
		String[] f = ttime.split(" ");
		String[] hhmi = f[0].split(":");
		int hr = hhmi[0].equals("12") ? 0 : Integer.parseInt(hhmi[0]);
		if (f[1].equals("PM")) {
				hr += 12;
		}
		String hour = "" + hr;
				
		JobDetail job = new JobDetail("SendMsgJob-" + Calendar.getInstance().getTimeInMillis(), Scheduler.DEFAULT_GROUP, SendMsgJob.class);
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("campaign", campaign);
		
		StringBuffer cronExpression = new StringBuffer();
		cronExpression.append("0 ").append(hhmi[1]). append(" ").append(hour).append(" ");
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy h:mm a");
		cal.setTime(sdf1.parse(stmp));
				
		if (repeat != null) {
			if (unit.equals("d")) {
				cronExpression.append("*").append("/").append(repeat).append(" *").append(" ?");
				//figure out the end date
				cal.add(Calendar.DATE, repeat * (numberOccurrences - 1));
			}
			if (unit.equals("m")) {
				cronExpression.append(mdy[1]).append(" ").append(mdy[0]).append("/").append(repeat).append(" ?");
				cal.add(Calendar.MONTH, repeat * (numberOccurrences - 1));
			}
		} else {
			cronExpression.append(mdy[1])
			.append(" ").append(mdy[0])
			.append(" ?");
		}
		
		CronTrigger trigger = new CronTrigger();
		trigger.setName(campaign.getKeyword() + " Scheduled Send " + Calendar.getInstance().getTime());
		trigger.setCronExpression(cronExpression.toString());
		trigger.setEndTime(cal.getTime());

		//trigger.setCronExpression("0 26 10 * * ?");
				
		//SchedulerFactoryBean sb = new SchedulerFactoryBean();
		//sb.afterPropertiesSet();
		//scheduler = sb.getScheduler();
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_FILE);
		scheduler = (Scheduler)ctx.getBean("scheduler");
		
		Date ft = scheduler.scheduleJob(job, trigger);

		logger.debug(job.getFullName() + " has been scheduled to run at: " + ft
				+ " and repeat based on expression: "
				+ trigger.getCronExpression());	
		
		Calendar cal1 = Calendar.getInstance();
		outputFireTimeList(trigger, cal1, cal);
	}
	*/
	
	 private void outputFireTimeList(CronTrigger trigger, java.util.Calendar from, java.util.Calendar to) {
		  List fireTimeList = TriggerUtils.computeFireTimesBetween(trigger, null, from.getTime(), to.getTime());
		  for ( int i = 0; i < fireTimeList.size(); i++ ) {
			  logger.debug(fireTimeList.get(i));
		  }
	 }
	 
	 public void unScheduleJob(String triggerName, String triggerGroup) throws Exception {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_FILE);
			scheduler = (Scheduler)ctx.getBean("scheduler");
			scheduler.unscheduleJob(triggerName, triggerGroup);
	 }
	 
	 public void reScheduleJob(String triggerName, String triggerGroup, String newMsg) throws Exception {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_FILE);
			scheduler = (Scheduler)ctx.getBean("scheduler");
			
			CronTrigger newTrigger = new CronTrigger();
			
			CronTrigger trigger = (CronTrigger)scheduler.getTrigger(triggerName, triggerGroup);
			JobDataMap jdMap = trigger.getJobDataMap();
			Campaign camp = (Campaign)jdMap.get("campaign");
			camp.setMessageText(newMsg);
			camp.setRawMessageText(newMsg);
			
			jdMap.put("campaign", camp);
			
			newTrigger.setName(camp.getKeyword() + " Scheduled Send-" + Calendar.getInstance().getTimeInMillis());
			newTrigger.setJobName(trigger.getJobName());
			newTrigger.setCronExpression(trigger.getCronExpression());
			newTrigger.setGroup(triggerGroup);
			newTrigger.setJobDataMap(jdMap);
			
			Date ft = scheduler.rescheduleJob(triggerName, triggerGroup, newTrigger);
			
			logger.debug(trigger.getJobName() + " has been rescheduled to run at: " + ft
					+ " and repeat based on expression: "
					+ newTrigger.getCronExpression());	
		
	 }
}
