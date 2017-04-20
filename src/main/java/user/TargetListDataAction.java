package user;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.actions.DispatchAction;

import reports_graphs.ReportData;
import util.ExportData;
import util.PropertyUtil;

public class TargetListDataAction extends DispatchAction {
	protected static final Logger logger = Logger.getLogger(TargetListDataAction.class);

	public ActionForward viewListData(ActionMapping mapping, ActionForm form,
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
		
		TargetListDataForm mForm =  (TargetListDataForm)form;
		
		String listId = request.getParameter("listId");	
		logger.debug("listId: " + listId);
		
		try {
			/*
			List<TargetListData> targetListData = new TargetUserListDao().getListData(listId);
			
			for (TargetListData tld : targetListData)
				logger.debug("phone: " + tld.getMobilePhone());
			
			mForm.setTargetListData(targetListData);
			hps.setAttribute("targetListData", targetListData);
			
			if (targetListData.size() == 0) {
				errors.add("error1", new ActionMessage("error.empty.list"));
				//saveErrors(request, errors);
			}
			*/
			
			if (listId.equals("Numbers")) { //called when user has selected specific numbers instead of a list
				mForm.setTargetListNumbers(((List<String>)hps.getAttribute("allSelectedNumbers")).toArray());
				
				return mapping.findForward("success");
			}
			
			Object[] targetListNumbers = new TargetUserListDao().getListData(listId, user.getUserId()).toArray();
			if (targetListNumbers.length == 0) {
				errors.add("error1", new ActionMessage("error.empty.list"));
				//saveErrors(request, errors);
			}
			
			mForm.setTargetListNumbers(targetListNumbers);
			hps.setAttribute("targetListNumbers", targetListNumbers);
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("error.list.get", e));
		}
		
		return mapping.findForward("success");
	}
	
	public ActionForward deleteNumber(ActionMapping mapping, ActionForm form,
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
		
		TargetListDataForm mForm =  (TargetListDataForm)form;

		TargetUserListDao dao = new TargetUserListDao();
		String mobilePhone = request.getParameter("mobilePhone");
		String listId = request.getParameter("listId");
		
		/*
		List<TargetListData> targetListData = (List<TargetListData>)hps.getAttribute("targetListData");
		String listId = targetListData.get(0).getListId();
		try {
			int ret = dao.deleteNumber(listId, mobilePhone);
			if (ret > 0)
				errors.add("error1", new ActionMessage("ok.number.delete"));
			else 
				errors.add("error1", new ActionMessage("error.number.delete"));
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("error.number.delete", e));
		}

		//saveErrors(request, errors);
		
		targetListData = dao.getListData(listId); //refresh
		mForm.setTargetListData(targetListData);
		*/
		
		logger.debug("listId, num: " + listId + ", " + mobilePhone);
		
		if (listId.equals("Numbers")) { //called when user has selected specific numbers instead of a list
			List<String> allSelectedNumbers = (List<String>)hps.getAttribute("allSelectedNumbers");
			allSelectedNumbers.remove(mobilePhone);
			mForm.setTargetListNumbers(allSelectedNumbers.toArray());
			hps.setAttribute("allSelectedNumbers", allSelectedNumbers);
			
			return mapping.findForward("success");
		}
		
		try {
			int ret = 0;
			//cannot delete from All now - 3/1/2012 - alert pops up 
			if (listId.equals("All")) { //delete from all lists
				ret = dao.deleteNumber(user.getUserId(), mobilePhone);
			} else {
				ret = dao.deleteNumber(listId, mobilePhone);
			}
			if (ret > 0)
				errors.add("error1", new ActionMessage("ok.number.delete"));
			else 
				errors.add("error1", new ActionMessage("error.number.delete", mobilePhone));
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("error.number.delete", e));
		}

		//saveErrors(request, errors);

		Object[] targetListNumbers = dao.getListData(listId, user.getUserId()).toArray(); //refresh
		mForm.setTargetListNumbers(targetListNumbers);
		
		return mapping.findForward("success");
	}
	
	public ActionForward exportListData(ActionMapping mapping, ActionForm form,
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
				
		String listId = request.getParameter("listId");
		List<TargetListData> tldList = new TargetUserListDao().getTargetListData(listId);
		
		List<String> colHeaders = new ArrayList<String>();
		colHeaders.add("Mobile Phone");
		colHeaders.add("Timestamp");
		
		List<reports.ReportData> rd = new ArrayList<reports.ReportData>();
		for (TargetListData tld : tldList)
			rd.add(new reports.ReportData(tld.getMobilePhone(), tld.getLastUpdatedAsStr()));
		
		String xlsFile = new ExportData().exportToXLS(user.getUserId(), rd, colHeaders);
		logger.debug("xlsFile: " + xlsFile);

		hps.setAttribute("xlsFile", PropertyUtil.load().getProperty("report_display_path") + xlsFile.substring(xlsFile.lastIndexOf("/")+1, xlsFile.length()));

		return mapping.findForward("view_tld_export");
	}
}
