package user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.apache.struts.action.ActionMessages;

import category_3.Category_3;

import reports_graphs.ReportData;
import sms.SMSMain;
import survey.Survey;
import util.ExportData;
import util.PropertyUtil;
import util.US411Exception;

public class SendMessageAction extends DispatchAction {
	Logger logger = Logger.getLogger(SendMessageAction.class);

	public ActionForward dummyAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		SendMessageForm mForm = (SendMessageForm) form;
		
		logger.debug("In dummyAction: name = " + mForm.getCampaignName());
		return mapping.findForward("success");
	}
	
	public ActionForward getList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		HttpSession hps = request.getSession();		
		User user = (User)hps.getAttribute("User");
		ActionMessages errors = new ActionMessages();
		
		if (user == null) {
			logger.debug("Null user");
			errors.add("error1", new ActionMessage("error.null.user"));
			//saveErrors(request, errors);
			return mapping.findForward("fail");	
		}
		
		SendMessageForm mForm = (SendMessageForm)form;

		//List<TargetUserList> tuLists = new TargetUserListDao().get(user.getUserId());
		
		//check the listType
		String listType = request.getParameter("listType");
		
		List<TargetUserList> tuLists = user.getTargetUserLists(listType);
		if (tuLists == null || tuLists.size() <= 0) {
			logger.debug("Null user list");
			if (listType != null && listType.equals("Appointment")) {
				errors.add("error1", new ActionMessage("error.list.get", "First time users - Please make sure that you upload a list using the template provided"));
				//saveErrors(request, errors);

				return mapping.findForward("fail_ops");	
			}

			errors.add("error1", new ActionMessage("error.list.get", "No list found"));
			//saveErrors(request, errors);
			
			return mapping.findForward("fail");	
		} 
		
		logger.debug("tuLists size: " + tuLists.size());

		hps.setAttribute("targetUserLists", tuLists);
		mForm.setTargetUserLists(tuLists);
		
		//set the sendNow to true
		mForm.setSendNow(true);
		
		if (listType != null && listType.equals("Appointment"))
			return mapping.findForward("success_ops");
		
		//if called from ops, just return
		String mode = request.getParameter("mode");

		if (mode != null && mode.equals("ops")) {
			hps.removeAttribute("allSelectedNumbers");
			return mapping.findForward("show_lists");
		}
		
		//set the default list
		for (TargetUserList tuList : tuLists) {
			logger.debug("tuList: " + tuList.getListId());
			if (tuList.getListName().toUpperCase().equals(user.getKeyword().toUpperCase())) {
				tuList.setListName(user.getKeyword() + " (opt-ins)");
				mForm.setTuList(tuList.getListId());
			}
		}
		
		//get all the scheduled deliveries
		
		//check if we came from survey
		if (mode != null && mode.equals("survey")) {
			Survey survey = (Survey)hps.getAttribute("Survey");
			StringBuffer sb = new StringBuffer();
			sb.append(survey.getIntroText());
			if (survey.getOption1() != null && survey.getOption1().length() > 0)
				sb.append("\n").append(survey.getOption1());
			if (survey.getOption2() != null && survey.getOption2().length() > 0)
				sb.append("\n").append(survey.getOption2());
			if (survey.getOption3() != null && survey.getOption3().length() > 0)
				sb.append("\n").append(survey.getOption3());
			if (survey.getOption4() != null && survey.getOption4().length() > 0)
				sb.append("\n").append(survey.getOption4());
			if (survey.getOption5() != null && survey.getOption5().length() > 0)
				sb.append("\n").append(survey.getOption5());	
			
			sb.append("\n").append("Text your response as ");
			sb.append(user.getKeyword()).append(" Response");
			mForm.setMessage(sb.toString());
		}
		
		return mapping.findForward("success");
	}

	public ActionForward deleteList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		HttpSession hps = request.getSession();		
		User user = (User)hps.getAttribute("User");
		ActionErrors errors = new ActionErrors();
		
		if (user == null) {
			logger.debug("Null user");
			errors.add("error1", new ActionMessage("error.null.user"));
			//saveErrors(request, errors);
			return mapping.findForward("fail");	
		}
		
		SendMessageForm mForm = (SendMessageForm)form;
		
		String listId = request.getParameter("listId");	
		String listName = request.getParameter("listName");
		String listType = request.getParameter("listType");

		logger.debug("listId/name: " + listId + ", " + listName);		
		
		try {		
			//check if it is the default list as you cannot delete that
			if (listName.equals(user.getKeyword() + " (opt-ins)")) {
				errors.add("error1", new ActionMessage("error.default.list.delete"));
				//saveErrors(request, errors);
				this.getList(mapping, mForm, request, response);
				return mapping.findForward("fail");						
			}
			
			int numDeleted = new TargetUserListDao().deleteList(listId);
			logger.debug(numDeleted + " deleted");
			
			for (Iterator i = user.getTargetUserLists().iterator(); i.hasNext();) {
				TargetUserList tul = (TargetUserList)i.next();
				if (tul.getListId().equals(listId)) {				
					i.remove();
				}
			}
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("error.list.get", e));
		}
		
		//refresh
		this.getList(mapping, mForm, request, response);
		
		if (listType != null && listType.equals("Appointment"))
			return mapping.findForward("success_ops");

		return mapping.findForward("success");
	}
	
	public ActionForward saveList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		HttpSession hps = request.getSession();		
		User user = (User)hps.getAttribute("User");
		ActionErrors errors = new ActionErrors();
		
		if (user == null) {
			logger.debug("Null user");
			errors.add("error1", new ActionMessage("error.null.user"));
			//saveErrors(request, errors);
			return mapping.findForward("fail");	
		}
		
		logger.debug("In saveList");
		
		List<TargetUserList> tuLists = new ArrayList<TargetUserList>();
		new UserDAOManager().saveUser(user);
		
		return mapping.findForward("success");
	}

	private String saveListToDB(User user, FormFile fFile, String listName) throws Exception {
		return saveListToDB(user, fFile, listName, "Marketing");
	}
	
	private String saveListToDB(User user, FormFile fFile, String listName, String listType) throws Exception {
		   if (fFile == null || fFile.getFileSize() <= 0) {
			   throw new Exception("Null form file");
		   }
		   
		   BufferedReader br = null;
		   try {
			   TargetUserListDao tulDao = new TargetUserListDao();
			   //create the list
			   TargetUserList tuList = new TargetUserList();
			   String listId = UUID.randomUUID().toString();
			   tuList.setListId(listId);
			   tuList.setUserId(user.getUserId());
			   tuList.setListName(listName);
			   tuList.setListType(listType);
			   //tulDao.save(tuList);
		   
			   List<TargetListData> tListData = new ArrayList<TargetListData>();

			   String fName = fFile.getFileName();

			   int listCount = 0;
			   if (fName.indexOf(".xls") > 0) {
				   tListData = getListDataFromXLS(listId, fFile);
				   listCount = tListData.size();
			   } else {				   
				  br = new BufferedReader(new InputStreamReader(fFile.getInputStream()));
				  //if there are additional fields, the format is Mobile Number, Last Name, FirstName, Address1, Address2 - comma delimited
				  String line = null;
				  String pNum = null;
				  String[] fields = null;
				  while ((line = br.readLine()) != null) {
					  fields = line.split(",");
					  pNum = fields[0];
					  if (pNum == null || pNum.length() <= 0)
						  continue;
					  pNum = normalize(pNum);
					  TargetListData tld = new TargetListData(listId, pNum);
					  if (fields.length == 1)
						  tListData.add(tld);
					  else {
						  if (fields.length == 2) {
							  tld.setLastName(fields[1]);
						  }
						  
						  if (fields.length == 3) {
							  tld.setLastName(fields[1]);
							  tld.setFirstName(fields[2]);
						  }
						  
						  if (fields.length == 4) {
							  tld.setLastName(fields[1]);
							  tld.setFirstName(fields[2]);  
							  tld.setAddress1(fields[3]);
						  }
						  
						  if (fields.length == 5) {
							  tld.setLastName(fields[1]);
							  tld.setFirstName(fields[2]);  
							  tld.setAddress1(fields[3]);
							  tld.setAddress2(fields[4]);
						  }
						  
						  tListData.add(tld);
					  }
					  listCount++;
					  logger.debug("pNum: " + pNum);
				  }	        
			   }
			   //check if this exceeds the max list limit
			   Integer maxListLimit = Integer.valueOf(PropertyUtil.load().getProperty("MAX_LIST_LIMIT"));
			   if (listCount > maxListLimit) {
				   throw new US411Exception("List limit exceeded");
			   }
			   
			   tulDao.saveListData(tListData);
			   
			   //Since tul is now a part of the User, save the user
			   List<TargetUserList> tuListstmp = user.getTargetUserLists();
			   List<TargetUserList> tuLists = new ArrayList<TargetUserList>();
			   for (TargetUserList tul : tuListstmp) { //skip the All list
				   if (tul.getListName().equals("All"))
					   continue;
					if (tul.getListName().indexOf("(opt-ins)") > 0) {
						logger.error("Found opt-ins in the list name: " + tul.getListName());
						tul.setListName(user.getKeyword());
					}				   
				   tuLists.add(tul);
			   }
			   tuLists.add(tuList);
			   user.setTargetUserLists(tuLists);
			   new UserDAOManager().saveUser(user);
			   
			   //add the All list back
				TargetUserList allTul = new TargetUserList();
				allTul.setListId("All");
				allTul.setUserId(user.getUserId());
				allTul.setListName("All");
				user.getTargetUserLists().add(0, allTul);
			   
			  return listId;
		   } finally {
			  if (br != null)
				  br.close();
		  }		   
	}
	
	/*
	//read the data from a xls file
	private List<TargetListData> getListDataFromXLS(String listId, FormFile infile) {
		List<TargetListData> tListData = new ArrayList<TargetListData>();
		try {
		Sheet[] sheets = Workbook.getWorkbook(infile.getInputStream()).getSheets();
		Sheet sheet = sheets[0];
		
		for (int i = 0; i < sheet.getRows(); i++) {
			Cell[] cells = sheet.getRow(i);
			if (cells.length <= 0 || cells[0].getContents().length() <= 0 || cells[0].getContents().equals("")) //Process only the rows with some data
				continue;
			String pNum = normalize(cells[0].getContents());
			tListData.add(new TargetListData(listId, pNum));
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tListData;
	}
	*/
	
	//read the data from a xls & xlsx file using POI
	private List<TargetListData> getListDataFromXLS1(String listId, FormFile infile) {
		List<TargetListData> tListData = new ArrayList<TargetListData>();
		try {
			Sheet sheet = (Sheet) WorkbookFactory.create(infile.getInputStream()).getSheetAt(0);

			DataFormatter df = new DataFormatter();
			for (Row row : sheet) {
				for (Cell cell : row) {
					if (cell == null) //Process only the rows with some data
						continue;
					String pNum = normalize(df.formatCellValue(cell));
					tListData.add(new TargetListData(listId, pNum));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tListData;
	}

	//read the data from a xls & xlsx file using POI
	private List<TargetListData> getListDataFromXLS(String listId, FormFile infile) {
		List<TargetListData> tListData = new ArrayList<TargetListData>();
		try {
			Sheet sheet = (Sheet) WorkbookFactory.create(infile.getInputStream()).getSheetAt(0);

			DataFormatter df = new DataFormatter();
			Iterator<Row> rowIterator = sheet.rowIterator();
			
			while (rowIterator.hasNext()) {
				Row row = (Row)rowIterator.next();
				if (df.formatCellValue(row.getCell(0)).startsWith("#"))  //header row - skip it
					continue;
				
				TargetListData tld = new TargetListData();
				tld.setListId(listId);

				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					if (cell == null) //Process only the rows with some data
						continue;
					
					if (cell.getColumnIndex() == 0) {
						String pNum = normalize(df.formatCellValue(cell));
						tld.setMobilePhone(pNum);
					}
					if (cell.getColumnIndex() == 1) {
						tld.setLastName(df.formatCellValue(cell));
					}
					if (cell.getColumnIndex() == 2) {
						tld.setFirstName(df.formatCellValue(cell));
					}
					if (cell.getColumnIndex() == 3) {
						tld.setAddress1(df.formatCellValue(cell));
					}
					if (cell.getColumnIndex() == 4) {
						tld.setAddress2(df.formatCellValue(cell));
					}					
				}
				tListData.add(tld);					
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tListData;
	}
	
	public ActionForward scheduleJob(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, Campaign campaign, String tzone)
			throws Exception {

		HttpSession hps = request.getSession();		
		User user = (User)hps.getAttribute("User");
		ActionErrors errors = new ActionErrors();
		
		if (user == null) {
			logger.debug("Null user");
			errors.add("error1", new ActionMessage("error.null.user"));
			//saveErrors(request, errors);
			return mapping.findForward("fail");	
		}
		
		SendMessageForm mForm = (SendMessageForm)form;
		
		logger.debug("schedDate/Hour: " + mForm.getSchedDate() + ", [" + mForm.getSchedTime() + "]");
        logger.debug("repeat Days: " + mForm.getRepeatDayCount());
        logger.debug("repeat Mon: " + mForm.getRepeatMonthCount());
		
		try {
			Integer repeat = null;
			String unit = null;
			Integer numberOccurrences = null;

			if (mForm.getRepeatDayCount() > 0) {
				repeat = mForm.getRepeatDayCount();
				unit = "d";
				numberOccurrences = mForm.getNumberOccurrencesDays();
			}
			
			if (mForm.getRepeatMonthCount() > 0) {
				repeat = mForm.getRepeatMonthCount();
				unit = "m";
				numberOccurrences = mForm.getNumberOccurrencesMonths();
			}
			
			new JobScheduler().schedule(mForm.getSchedDate(), mForm.getSchedTime(), campaign, tzone, repeat, unit, numberOccurrences);

			errors.add("error1", new ActionMessage("schedule.ok"));
		} catch (Exception e) {
			e.printStackTrace();
			//errors.add("error1", new ActionMessage("schedule.error", e.getMessage() + "\nPlease check your timezone setting and the schedule date & time"));
			throw new Exception(e);
		}
		
		//saveErrors(request, errors);

		List<TargetUserList> tuLists = (List<TargetUserList>) hps.getAttribute("targetUserLists");
		mForm.setTargetUserLists(tuLists);
		
		return mapping.findForward("success");
	}
	
	public ActionForward scheduleJob_bak(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, Campaign campaign)
			throws Exception {

		HttpSession hps = request.getSession();		
		User user = (User)hps.getAttribute("User");
		ActionErrors errors = new ActionErrors();
		
		if (user == null) {
			logger.debug("Null user");
			errors.add("error1", new ActionMessage("error.null.user"));
			//saveErrors(request, errors);
			return mapping.findForward("fail");	
		}
		
		SendMessageForm mForm = (SendMessageForm)form;
		
		logger.debug("schedDate/Hour: " + mForm.getSchedDate() + ", " + mForm.getSchedTime());
		
		try {
			StdSchedulerFactory factory = (StdSchedulerFactory) request.getSession().getServletContext()
					.getAttribute(QuartzInitializerListener.QUARTZ_FACTORY_KEY);	
			Scheduler sched = factory.getScheduler();
			//sched.start();
			
			//parse the date into mon, day, year
			String[] mdy = mForm.getSchedDate().split("/");
			
			//parse the hours into mins and hour
			String[] f = mForm.getSchedTime().split(" ");
			String[] hhmi = f[0].split(":");
			int hr = hhmi[0].equals("12") ? 0 : Integer.parseInt(hhmi[0]);
			if (f[1].equals("pm")) {
					hr += 12;
			}
			String hour = "" + hr;
			
			StringBuffer cronExpression = new StringBuffer();
			cronExpression.append("0 ").append(hhmi[1]). append(" ").append(hour)
							.append(" ").append(mdy[1])
							.append(" ").append(mdy[0])
							.append(" ? ").append(mdy[2]);
			
			logger.debug("cronExpression = " + cronExpression.toString());
			JobDetail job = new JobDetail("SendMsgJob-" + Calendar.getInstance().getTimeInMillis(), Scheduler.DEFAULT_GROUP, SendMsgJob.class);
			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put("msgText", campaign.getMessageText());
			jobDataMap.put("campaignId", campaign.getCampaignId());
			jobDataMap.put("listId", mForm.getTuList());
			jobDataMap.put("userId", user.getUserId().toString());
			jobDataMap.put("keyword", user.getKeyword());
			logger.debug("keyword: " + user.getKeyword());

			jobDataMap.put("campaign", campaign);
			jobDataMap.put("baseURL", PropertyUtil.load().getProperty("baseURL"));
			
			//job.setJobDataMap(jobDataMap);
			
			CronTrigger trigger = new CronTrigger();
			trigger.setName(user.getKeyword() + " Scheduled Send " + Calendar.getInstance().getTime());
			//trigger.setCronExpression(cronExpression.toString());
			trigger.setCronExpression("0 47 15 * * ?");
			
			trigger.setJobDataMap(jobDataMap);
			
			Date ft = sched.scheduleJob(job, trigger);
			logger.debug(job.getFullName() + " has been scheduled to run at: " + ft
					+ " and repeat based on expression: "
					+ trigger.getCronExpression());	
			
			errors.add("error1", new ActionMessage("schedule.ok"));
			//saveErrors(request, errors);
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("schedule.error", e.toString()));
			//saveErrors(request, errors);
			e.printStackTrace();
		}
		
		List<TargetUserList> tuLists = (List<TargetUserList>) hps.getAttribute("targetUserLists");
		mForm.setTargetUserLists(tuLists);
		
		return mapping.findForward("success");
	}
	
	public ActionForward deleteJob(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		HttpSession hps = request.getSession();		
		User user = (User)hps.getAttribute("User");
		ActionErrors errors = new ActionErrors();
		
		if (user == null) {
			logger.debug("Null user");
			errors.add("error1", new ActionMessage("error.null.user"));
			//saveErrors(request, errors);
			return mapping.findForward("edit_scheduled_job");	
		}
		
		SendMessageForm mForm = (SendMessageForm)form;
		
		try {
			String triggerName = request.getParameter("trigName");
			String triggerGroup = request.getParameter("trigGroup");
			new JobScheduler().unScheduleJob(triggerName, triggerGroup);
			
			errors.add("error1", new ActionMessage("deleteJob.ok"));
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("deleteJob.error", e.getMessage()));
			e.printStackTrace();
		}
		
		//saveErrors(request, errors);

		List<ReportData> schedDeliveries = new UserDAOManager().getScheduledTriggers(user.getUserId());
		mForm.setSchedDeliveries(schedDeliveries);
		
		return mapping.findForward("edit_scheduled_job");
	}
	
	//get all the scheduled deliveries
	public ActionForward getSchedJobs(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		HttpSession hps = request.getSession();		
		User user = (User)hps.getAttribute("User");
		ActionErrors errors = new ActionErrors();
		
		if (user == null) {
			logger.debug("Null user");
			errors.add("error1", new ActionMessage("error.null.user"));
			//saveErrors(request, errors);
			return mapping.findForward("delete_job_fail");	
		}
		
		SendMessageForm mForm = (SendMessageForm)form;

		try {
			List<ReportData> schedDeliveries = new UserDAOManager().getScheduledTriggers(user.getUserId());
			mForm.setSchedDeliveries(schedDeliveries);		
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("deleteJob.error", e.getMessage()));
			e.printStackTrace();
		}
		
		//saveErrors(request, errors);

		return mapping.findForward("edit_scheduled_job");
	}
	
	public ActionForward editJob(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		HttpSession hps = request.getSession();		
		User user = (User)hps.getAttribute("User");
		ActionErrors errors = new ActionErrors();
		
		if (user == null) {
			logger.debug("Null user");
			errors.add("error1", new ActionMessage("error.null.user"));
			//saveErrors(request, errors);
			return mapping.findForward("edit_scheduled_job");	
		}
		
		SendMessageForm mForm = (SendMessageForm)form;

		try {
			String triggerName = request.getParameter("trigName");
			String triggerGroup = request.getParameter("trigGroup");
			String newMsg = request.getParameter("msgText");
			new JobScheduler().reScheduleJob(triggerName, triggerGroup, newMsg);
			
			List<ReportData> schedDeliveries = new UserDAOManager().getScheduledTriggers(user.getUserId());
			mForm.setSchedDeliveries(schedDeliveries);
			
			errors.add("error1", new ActionMessage("editJob.ok"));
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("editJob.error", e.getMessage()));
			e.printStackTrace();
		}
		
		//saveErrors(request, errors);

		return mapping.findForward("edit_scheduled_job");
	}
	
	/*
	public ActionForward getList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		HttpSession hps = request.getSession();		
		User user = (User)hps.getAttribute("User");
		ActionErrors errors = new ActionErrors();
		
		if (user == null) {
			logger.debug("Null user");
			errors.add("error1", new ActionMessage("error.null.user"));
			//saveErrors(request, errors);
			return mapping.findForward("fail");	
		}
		
		TargetUserList tuList = new TargetUserListDao().get(user.getUserId());
		if (tuList == null || tuList.getListName() == null || tuList.getListName().length() <= 0) {
			logger.debug("Null user list");
			return mapping.findForward("fail");				
		}
		
		SendMessageForm mForm = (SendMessageForm)form;
		mForm.setTuList(tuList);
		
		logger.debug("getList: tulist id = " + mForm.getTuList().getListId());
		
		Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));	 		  
		String displayPath = props.getProperty("displayTargetFilePath") + user.getUserId() + "/";
		displayPath += tuList.getListPath().substring(tuList.getListPath().lastIndexOf("/")+1);
				
		hps.setAttribute("targetUserListDPath", displayPath);
		hps.setAttribute("targetUserList", tuList);
		
		return mapping.findForward("success");
	}
	*/
	
	public ActionForward showUploadPage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward("uploadPage");
	}
	
	public ActionForward uploadFile1(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		HttpSession hps = request.getSession();		
		User user = (User)hps.getAttribute("User");
		ActionErrors errors = new ActionErrors();
		
        SendMessageForm mForm = (SendMessageForm)form;

	      //Save the file of target users
	      Properties props = new Properties();
	      props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));
	      String fpath = props.getProperty("targetFilePath") + user.getUserId() + "/";
			
	      FormFile targetFile = mForm.getTargetFile();
	      logger.debug("target file: " + targetFile);
	      if (targetFile == null || targetFile.getFileSize() <= 0) {
				logger.debug("Null targetUserFile");
				errors.add("error1", new ActionMessage("error.null.targetUserFile"));
				//saveErrors(request, errors);
				return mapping.findForward("fail");		    	  
	      }
	      
	      try {
	    	  	saveToDisk(targetFile, fpath);
	    	  //this.saveListToDB(user.getUserId(), targetFile, mForm.getListName());
	      } catch (Exception e) {
				logger.debug("Error saving targetUserFile");
				errors.add("error1", new ActionMessage("error.save.targetUserFile"));
				//saveErrors(request, errors);
				return mapping.findForward("fail");		    	  
	      }
	      
	      logger.debug("target filepath: " + fpath + targetFile.getFileName());

	      /*
	      TargetUserList tuList = (TargetUserList)hps.getAttribute("targetUserList");
	      if (tuList == null) {
		      tuList = new TargetUserList();
	      } 
	      */
	      
	      TargetUserList tuList = new TargetUserList();
	      
	      logger.debug("uploadFile: tulist id = " + tuList.getListId());
		
	      String displayPath = props.getProperty("displayTargetFilePath") + user.getUserId() + "/" + targetFile.getFileName();

	      tuList.setListName(mForm.getListName());
	      tuList.setListPath(fpath + targetFile.getFileName());
	      tuList.setUserId(user.getUserId());
	      tuList.setListDisplayPath(displayPath);
	      
	      new TargetUserListDao().save(tuList);
	      hps.setAttribute("targetUserListDPath", displayPath);	      
		  hps.setAttribute("targetUserList", tuList);

		  //refresh
		  this.getList(mapping, mForm, request, response);
	      
	      return mapping.findForward("success");		   	  
	}

	public ActionForward uploadFile(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		HttpSession hps = request.getSession();		
		User user = (User)hps.getAttribute("User");
		ActionErrors errors = new ActionErrors();
		
        SendMessageForm mForm = (SendMessageForm)form;
  	  	String listType = request.getParameter("listType") == null ? "Marketing" : request.getParameter("listType");

	      //Save the file of target users
	      Properties props = new Properties();
	      props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));
	      String fpath = props.getProperty("targetFilePath") + user.getUserId() + "/";
			
	      if (mForm.getListName() == null || mForm.getListName().length() <= 0) {
				errors.add("error1", new ActionMessage("error.null.targetUserFileName"));
				//saveErrors(request, errors);
				this.getList(mapping, mForm, request, response);
				if (listType != null && listType.equals("Appointment"))
					return mapping.findForward("fail_ops");	
				
				return mapping.findForward("fail");		    	  
	      }
	      
	      FormFile targetFile = mForm.getTargetFile();
	      logger.debug("target file: " + targetFile);
	      if (targetFile == null || targetFile.getFileSize() <= 0) {
				logger.debug("Null targetUserFile");
				errors.add("error1", new ActionMessage("error.null.targetUserFile"));
				//saveErrors(request, errors);
				this.getList(mapping, mForm, request, response);	
				if (listType != null && listType.equals("Appointment"))
					return mapping.findForward("fail_ops");	
				
				return mapping.findForward("fail");		    	  
	      }
	      
	      try {
	    	  //get the listType of the uploaded list - default is Marketing
	    	  this.saveListToDB(user, targetFile, mForm.getListName(), listType);
	      } catch (US411Exception usex) {
	    	  //send email				
	    	  	Category_3 catg = (Category_3)request.getSession().getAttribute("category");
	    	  	StringBuffer userInfo = new StringBuffer(); 
	    	  	userInfo.append("Phone: ").append(catg.getPhone());
	    	  	userInfo.append(" Email: ").append(catg.getEmail());
				new SMSMain().sendEmail(null, PropertyUtil.load().getProperty("us411AlertToEmail"),
						"support@convergentmobile.com", "List Limit Exceeded - " + user.getKeyword(), "User (id = " + user.getUserId() + ") " +
								"is trying to upload a list that exceeds the allowed limit.\n" + userInfo.toString(), "TEXT", PropertyUtil.load().getProperty("us411AlertToCCEmail"));
				logger.debug(usex.getMessage());
				errors.add("error1", new ActionMessage("error.list_limit"));
				//saveErrors(request, errors);
				this.getList(mapping, mForm, request, response);	
				if (listType != null && listType.equals("Appointment"))
					return mapping.findForward("fail_ops");	
				
				return mapping.findForward("fail");	    	  
		  } catch (Exception e) {
	    	  	e.printStackTrace();
				logger.debug("Error saving targetUserFile");
				errors.add("error1", new ActionMessage("error.save.targetUserFile"));
				//saveErrors(request, errors);
				this.getList(mapping, mForm, request, response);	
				if (listType != null && listType.equals("Appointment"))
					return mapping.findForward("fail_ops");	
				
				return mapping.findForward("fail");		    	  
	      }
	      
	      errors.add("error1", new ActionMessage("ok.save.targetUserFile"));
		  //saveErrors(request, errors);

	      logger.debug("target filepath: " + fpath + targetFile.getFileName());
	      
	      mForm.reset(mapping, request); //clear out the form
	      
		  //refresh
		  this.getList(mapping, mForm, request, response);
	      
			if (listType != null && listType.equals("Appointment"))
				return mapping.findForward("success_ops");	
			
	      return mapping.findForward("success");		   	  
	}
	
	//used by N&N
	public ActionForward previewCampaign(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		SendMessageForm mForm = (SendMessageForm)form;
			    
        Timestamp ts = new Timestamp(Calendar.getInstance().getTimeInMillis());
        
        Campaign campaign = new Campaign();
        campaign.setCampaignId(UUID.randomUUID().toString());
        campaign.setName(mForm.getCampaignName());
        campaign.setListId(mForm.getTuList());
        campaign.setStartDate(ts);
        campaign.setEndDateAsString(mForm.getCampaignEndDate());
        campaign.setMessageText(mForm.getMessage());
        
	    logger.debug("campaign endDate: " + campaign.getEndDateAsString());

		CategoryBase cbase = (CategoryBase)request.getSession().getAttribute("category");

	    String outFile = cbase.preview(request, campaign);
	    request.setAttribute("previewFile", outFile);
		mForm.setSmpreviewFile(outFile); //this will be the actual text for NN

	    this.getList(mapping, mForm, request, response);
	    
		return mapping.findForward("success");
	}
	
	public ActionForward sendMessage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		HttpSession hps = request.getSession();		
		User user = (User)hps.getAttribute("User");
		ActionErrors errors = new ActionErrors();
		
        SendMessageForm mForm = (SendMessageForm)form;

	      CategoryBase cbase = (CategoryBase)hps.getAttribute("category");
	      logger.debug("SendMessageAction - categoryId: " + cbase.getCategoryId());
	
	        //create a campaign
	        String cName = mForm.getCampaignName();
	        Long dt = Calendar.getInstance().getTimeInMillis();
	        if (cName == null) {
				cName = "Campaign-" + dt.toString();
	        }
	        
	        logger.debug("getCampaignEndDate: " + mForm.getCampaignEndDate());
	        
	        Timestamp ts = new Timestamp(dt);
	        
	        Campaign campaign = new Campaign();
	        campaign.setCampaignId(UUID.randomUUID().toString());
	        campaign.setUserId(user.getUserId());
	        campaign.setName(cName);
	        campaign.setListId(mForm.getTuList());
	        campaign.setStartDate(ts);
	        campaign.setEndDateAsString(mForm.getCampaignEndDate());
	        campaign.setKeyword(user.getKeyword());
	        campaign.setShortcode(PropertyUtil.load().getProperty("shortcode"));
	        
	        String msgText = cbase.createMessage(user.getKeyword() + "\n" + mForm.getMessage(), campaign);
	        	        
        //get the link if checked
        if (mForm.isIncludeLink()) {
		      //String linkUrl = cbase.preview(request, mForm.getCurrentPage());      
		      //msgText += "\n" + new ContentTransform().getTinyUrl(linkUrl);
        	String burl = PropertyUtil.load().getProperty("baseURL");
        	burl = burl.substring(0, burl.lastIndexOf("/"));
        	String linkUrl = burl + "/mwp/" + user.getUserId();
		    msgText += "\n" + linkUrl;
        }
        
        if (mForm.getExternalLink() != null && mForm.getExternalLink().length() > 0) {
        	msgText += "\n" + mForm.getExternalLink();
        }

        //check if we are sending to a list of selected numbers
        if (mForm.getTuList().equals("Numbers")) {
        	campaign.setTargetNumbers(((List<String>)request.getSession().getAttribute("allSelectedNumbers")).toArray());
        }
        
	    //save this campaign
        campaign.setMessageText(msgText);     
        campaign.setRawMessageText(mForm.getMessage());
        //new UserDAOManager().saveCampaign(campaign);

        logger.debug("SendMessageAction: msgText = " + msgText);
	    
        logger.debug("sendnow: " + mForm.isSendNow());
        
        //check if it is for immediate send or to be scheduled
        if (! mForm.isSendNow()) {
        	//this.scheduleJob(mapping, mForm, request, response, msgText);
        	try {
        		logger.debug("tzone: " + cbase.getTimezone());
				this.scheduleJob(mapping, mForm, request, response, campaign, cbase.getTimezone());
			} catch (Exception e) {
				errors.add("error1", new ActionMessage("schedule.error", e.getMessage() + "\nPlease check your timezone setting and the schedule date & time"));
				//saveErrors(request, errors);

				return mapping.findForward("success");
			}
        } else {       
		    try {    		
			      //cbase.sendMessage(msgText, mForm.getTuList(), user.getKeyword(), request.getSession());
		    		cbase.sendMessage(campaign, user.getKeyword(), request);
			      errors.add("error1", new ActionMessage("success.msg.send"));
		      } catch (Exception e) {
					logger.debug("Error in sending msgs " + e);
					errors.add("error1", new ActionMessage("error.msg.send", e.getMessage()));
		      }
		      
		      /*
		      String infile = tuList.getListPath();
		      logger.debug("infile: " + infile);
				
		      US411Handler smsh = new US411Handler(null, "US");
		      try {
		    	  smsh.sendBulkSMS(mForm.getMessage(), "US411", 0, infile, user.getUserId().toString(), user.getKeyword());
		    	  errors.add("error1", new ActionMessage("success.msg.send"));	    	  
		      } catch (Exception e) {
					logger.debug("Error in sending msgs " + e);
					errors.add("error1", new ActionMessage("error.msg.send"));
					e.printStackTrace();
		      }
		      */
		      
				//saveErrors(request, errors);
				
				List<TargetUserList> tuLists = (List<TargetUserList>) hps.getAttribute("targetUserLists");
				mForm.setTargetUserLists(tuLists);				
        }
        
        //save this campaign only if scheduling is successful
        new UserDAOManager().saveCampaign(campaign);

        mForm.reset(mapping, request);
		mForm.setSendNow(true);

		if (campaign.getListId().equals("Numbers"))
		    return mapping.findForward("success_to_numbers");	

	    return mapping.findForward("success");	
	}

	//send msg to selected numbers
	public ActionForward sendMessageToNumbers(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		HttpSession hps = request.getSession();		
		User user = (User)hps.getAttribute("User");
		ActionErrors errors = new ActionErrors();
		
        SendMessageForm mForm = (SendMessageForm)form;
        List<String> allSelectedNumbers = (List<String>)hps.getAttribute("allSelectedNumbers");
        if (allSelectedNumbers == null)
        	allSelectedNumbers = new ArrayList<String>();
        
		//add the last selected numbers as well - minus duplicates
        if (mForm.getSelectedNumbers() != null && mForm.getSelectedNumbers().length > 0) {
			for (String pnum : mForm.getSelectedNumbers())
				if (! allSelectedNumbers.contains(pnum))
					allSelectedNumbers.add(pnum);
        }

        if (allSelectedNumbers.size() <= 0) {
			errors.add("error1", new ActionMessage("error.empty.list"));
			//saveErrors(request, errors);
	        return mapping.findForward("show_lists");
        }
        
        for (String pnum : allSelectedNumbers)
        	logger.debug("num: " + pnum);
        
        hps.setAttribute("allSelectedNumbers", allSelectedNumbers);
        
        //return mapping.findForward("success_to_numbers");
        return this.sendMessage(mapping, mForm, request, response);
	}
	
	private void saveToDisk(FormFile fFile, String fpath) throws Exception {
		   if (fFile == null || fFile.getFileSize() <= 0) {
			   throw new Exception("Null form file");
		   }
		   
		   if (! (new File(fpath)).exists())
				   new File(fpath).mkdirs();
		   
		   File savedFile = new File(fpath + fFile.getFileName());
		   FileOutputStream fileOutStream = null;
		   try {
				fileOutStream = new FileOutputStream(savedFile);
		        fileOutStream.write(fFile.getFileData());
		        fileOutStream.flush();		        
			} catch (FileNotFoundException e) {
				throw new Exception(e);
			} finally {
		           fileOutStream.close();
		   }
	}

	public ActionForward addNumber(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		HttpSession hps = request.getSession();		
		User user = (User)hps.getAttribute("User");
		ActionErrors errors = new ActionErrors();
		
		if (user == null) {
			logger.debug("Null user");
			errors.add("error1", new ActionMessage("error.null.user"));
			//saveErrors(request, errors);
			return mapping.findForward("fail");	
		}
		
		SendMessageForm mForm = (SendMessageForm)form;		
		String phoneNumber = mForm.getPhoneNumber();
		String listId = request.getParameter("listId");
		
		List<TargetListData> tListData = new ArrayList<TargetListData>();
		try {
			if (phoneNumber != null && phoneNumber.length() > 0) {
				//addToList(phoneNumber, tuList.getListPath());
				  tListData.add(new TargetListData(listId, normalize(phoneNumber)));
				  new TargetUserListDao().saveListData(tListData);
				  errors.add("error1", new ActionMessage("error.number.added"));
			}
//		} catch (MySQLIntegrityConstraintViolationException e) { //catch dups
//			errors.add("error1", new ActionMessage("error.number.exists", phoneNumber));
		} catch (org.hibernate.exception.ConstraintViolationException e1) {
			errors.add("error1", new ActionMessage("error.number.exists", phoneNumber));
		} catch (Exception e1) {
			errors.add("error1", new ActionMessage("error.number.add"));
		}

		//saveErrors(request, errors);
		
		List<TargetUserList> tuLists = (List<TargetUserList>) hps.getAttribute("targetUserLists");
		mForm.setTargetUserLists(tuLists);
		mForm.setPhoneNumber(null); //clear it out
		
		return mapping.findForward("success");
	}

	private String normalize(String smsto) {
		//remove all non-digits
		smsto = smsto.replaceAll("\\D", "");
		//if the prefix is missing, add it
		if (smsto.length() == 10) {
			smsto = "1" + smsto;
		}
		return smsto;
	}
	
	//called when selecting specific numbers to send - for operations/appt messages
	public ActionForward getNumbers(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		HttpSession hps = request.getSession();		
		User user = (User)hps.getAttribute("User");
		ActionErrors errors = new ActionErrors();
		
		if (user == null) {
			logger.debug("Null user");
			errors.add("error1", new ActionMessage("error.null.user"));
			//saveErrors(request, errors);
			return mapping.findForward("fail");	
		}
		
		SendMessageForm mForm = (SendMessageForm)form;
		List<String> allSelectedNumbers = (List<String>)hps.getAttribute("allSelectedNumbers");
		if (allSelectedNumbers == null)
			allSelectedNumbers = new ArrayList<String>();
		
		if (mForm.getSelectedNumbers() != null) {
			allSelectedNumbers.addAll(Arrays.asList(mForm.getSelectedNumbers()));
			hps.setAttribute("allSelectedNumbers", allSelectedNumbers);
			for (String num : allSelectedNumbers) 
				logger.debug("selected number: " + num);
		}
		
		String listId = request.getParameter("listId");
		List<String> listNumbers = new TargetUserListDao().getListData(listId, user.getUserId());
		mForm.setListNumbers(listNumbers);
	    
		mForm.setTargetUserLists((List<TargetUserList>) hps.getAttribute("targetUserLists"));
		
		return mapping.findForward("show_lists");
	}
	
	/*
	public ActionForward addNumber(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		HttpSession hps = request.getSession();		
		User user = (User)hps.getAttribute("User");
		ActionErrors errors = new ActionErrors();
		
		if (user == null) {
			logger.debug("Null user");
			errors.add("error1", new ActionMessage("error.null.user"));
			//saveErrors(request, errors);
			return mapping.findForward("fail");	
		}
		
		SendMessageForm mForm = (SendMessageForm)form;
		TargetUserList tuList = (TargetUserList)hps.getAttribute("targetUserList");
		
		String phoneNumber = request.getParameter("phoneNumber");
		logger.debug("In addNumber");
		if (phoneNumber != null && phoneNumber.length() > 0)
			addToList(phoneNumber, tuList.getListPath());
		
		return mapping.findForward("success");
	}
	
	private void addToList(String phoneNumber, String listFile) throws Exception {
		logger.debug("phoneNumber = " + phoneNumber);
		if (listFile.indexOf(".xls") > 0 ) {
			addToListXls(phoneNumber, listFile);
			return;
		}
		
	    BufferedWriter bw = new BufferedWriter(new FileWriter(listFile, true));
	    bw.write("\n" + phoneNumber);
		bw.close();
	}
	
	private void addToListXls(String phoneNumber, String listFile) throws Exception {
		logger.debug("phoneNumber = " + phoneNumber);
		File inFile = new File(listFile);
		Workbook workbook = Workbook.getWorkbook(inFile);
		File tmpFile = new File(listFile + ".tmp");
		WritableWorkbook copy = Workbook.createWorkbook(tmpFile, workbook);
		WritableSheet sheet = (WritableSheet)copy.getSheet(0);
		sheet.addCell(new Label(0, sheet.getRows(), phoneNumber));
		copy.write();
		copy.close();
		inFile.delete();
		if (! tmpFile.renameTo(inFile)) {
			logger.error("File not Moved");
			throw new Exception("File not moved");
		}
		removeDups(inFile.getName());
	}
	
	private void removeDups(String infile) throws Exception {
		File tmpfile = new File("/tmp/" + infile);
		Set<String> lines = new HashSet<String>();
		String line = null;
		BufferedReader br = new BufferedReader(new FileReader(infile));
	    BufferedWriter bw = new BufferedWriter(new FileWriter(tmpfile));
		while ((line = br.readLine()) != null) {
			if (! lines.contains(line)) {
				lines.add(line);
				bw.write(line);
			}
		}
		bw.close();
		br.close();
		lines.clear();
	}
	*/
}
