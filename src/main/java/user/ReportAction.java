package user;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.util.LabelValueBean;

import admin_user.AdminDAOManager;
import admin_user.AdminForm;
import admin_user.UserProfileVO;


import reports_graphs.GenerateReportData;
import reports_graphs.*;
import reports.*;
import util.ExportData;
import util.PropertyUtil;

import java.sql.Connection;

public class ReportAction extends DispatchAction {
	private static Logger logger = Logger.getLogger(ReportAction.class);
	protected ActionErrors errors = new ActionErrors();
	protected GenerateReportData grd = new GenerateReportData();

	public ActionForward setup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		errors.clear();

		HttpSession hps = request.getSession();
		
		//clear the report related attributes
		hps.removeAttribute("reportId");
		hps.removeAttribute("fromDate");
		hps.removeAttribute("toDate");
		hps.removeAttribute("reportDataRows");
		hps.removeAttribute("reports");
		hps.removeAttribute("sites");
		hps.removeAttribute("profiles");
		hps.removeAttribute("xlsFile");

		User user = (User)hps.getAttribute("User");
		
		if (user == null) {
			errors.add("error1", new ActionMessage("error.notloggedin"));
			//saveErrors(request, errors);
			return mapping.findForward("login");
		}
		
		UserDAOManager dao = new UserDAOManager();
		
		List<LabelValueBean>sites = new ArrayList<LabelValueBean>();
		List<Report> reports = new ArrayList<Report>();
		User adminUser = (User)hps.getAttribute("AdminUser");

		//if this is not an admin user, add the default site
		if (adminUser == null) {
			logger.debug("Not an admin user");
			sites.add(new LabelValueBean(user.getKeyword(), user.getUserId().toString()));
		}
		
		reports = dao.getReports(user.getUserId(), Integer.parseInt(PropertyUtil.load().getProperty("siteId")));
		logger.debug("reports size: " + reports.size());
		
		hps.setAttribute("sites", sites);
		hps.setAttribute("reports", reports);
		
		return mapping.findForward("report_success");
	}
	
	//used by admin sites such as CompareNetworks
	public ActionForward step1(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		errors.clear();
		ReportForm mForm = (ReportForm) form;

		HttpSession hps = request.getSession();

		User user = (User)hps.getAttribute("User");
		
		if (user == null) {
			errors.add("error1", new ActionMessage("error.notloggedin"));
			//saveErrors(request, errors);
			return mapping.findForward("login");
		}
				
		user = (User)hps.getAttribute("AdminUser");
		
		Integer reportId = Integer.parseInt(request.getParameter("reportId"));
		hps.setAttribute("reportId", reportId);
		hps.setAttribute("fromDate", mForm.getFromDate());
		hps.setAttribute("toDate", mForm.getToDate());
		
		//if it is not an admin user, just run the report
		if (user == null)
			return mapping.findForward("runit");
		
		//if it is not a multi keyword report, just run it
		/*
		Report report = new Report().findReport((List<Report>)hps.getAttribute("reports"), reportId);
		if (report.getIsMulti() != null && ! report.getIsMulti()) { //if not multi, add the default keyword
			List<LabelValueBean> sites = (List<LabelValueBean>)hps.getAttribute("sites");
			sites.add(new LabelValueBean(user.getKeyword(), user.getUserId().toString()));
			return mapping.findForward("runit");
		}
		*/

		return mapping.findForward("step_1");
	}
	
	public ActionForward getByLetter(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		HttpSession hps = request.getSession();

		try {
			User user = (User)hps.getAttribute("AdminUser");
			
			ReportForm mForm = (ReportForm)form;

			List<UserProfileVO> sites = null;
			String letter = request.getParameter("letter");
			if (letter != null)
				sites = new AdminDAOManager().getSites(user.getSiteId(), letter);
			
			hps.setAttribute("profiles", sites);

			mForm.setProfiles(sites);

			return mapping.findForward("step_1");
		} catch (Exception e) {
			logger.error("get: " + e);
			e.printStackTrace();
			ActionErrors errors = new ActionErrors();
			errors.add("error1", new ActionMessage("error.user.create", e.toString()));
			//saveErrors(request, errors);
			return mapping.findForward("fail");
		} finally {
		}
	}

	//used for multi keyword Reports for SAs
	public ActionForward selectKeyword(ActionMapping mapping, ActionForm form,
										HttpServletRequest request, HttpServletResponse response)
												throws Exception {
		errors.clear();
		
		HttpSession hps = request.getSession();
		List<LabelValueBean>sites = (List<LabelValueBean>)hps.getAttribute("sites");
		
		if (sites == null)
			sites = new ArrayList<LabelValueBean>();
		
		try {
			User user = (User)hps.getAttribute("AdminUser");
			
			String keyword = request.getParameter("keyword");
			String userId = request.getParameter("userId");
			sites.add(new LabelValueBean(keyword, userId));

			hps.setAttribute("sites", sites);

			for (LabelValueBean site : sites)
				logger.debug("site keyword: " + site.getLabel());
			
			Integer reportId = (Integer) hps.getAttribute("reportId");
			Report report = new Report().findReport((List<Report>)hps.getAttribute("reports"), reportId);

			//we need to go to step_1 even for single keyword reports to get the date range for admin mode for CN - KP - 8/14/2013
			//if (report.getIsMulti() != null && ! report.getIsMulti()) { //if not multi, just run it
				//return mapping.findForward("runit");
			//	return this.runit(mapping, form, request, response);
			//}			

			return mapping.findForward("step_1");
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("error.user.create", e.toString()));
			//saveErrors(request, errors);
			return mapping.findForward("report_step1");
		} finally {
		}
	}
	
	public ActionForward runit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		User user = null;
		ReportForm mForm = (ReportForm) form;
		HttpSession hps = request.getSession();
		ServletContext context = request.getSession().getServletContext();
		Map param_value = new HashMap();
		Connection conn = null;
		
		errors.clear();

		try {
			user = (User)hps.getAttribute("User");
	  	  	
			if (user == null) {
				errors.add("error1", new ActionMessage("error.null.user"));
				//saveErrors(request, errors);
				logger.error("ReportAction:runit-Null User");
				return mapping.findForward("report_fail");
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			
			List<LabelValueBean>sites = (List<LabelValueBean>)hps.getAttribute("sites");

			Integer reportId = null;
			String ids = "";
			//check if it is an admin user
			User adminUser = (User)hps.getAttribute("AdminUser");
			if (adminUser != null) {
				if (sites.size() <= 0) {
					errors.add("error1", new ActionMessage("error.siteSelection"));
					//saveErrors(request, errors);
					return mapping.findForward("report_fail");	
				}
				
				int i = 0;
				for (LabelValueBean site: sites) {
					logger.debug("user_id: " + site.getValue());
					if (i > 0)
						ids += ",";
					ids += site.getValue();	
					i++;
				}
			} else { //regular user - only one site so just get it
				ids = sites.get(0).getValue();
				//for regular user, reportId is passed as a parameter
				reportId = Integer.valueOf(request.getParameter("reportId"));
				hps.setAttribute("reportId", reportId);
			}
			
			param_value.put("userIds", ids);	
			
			mForm.setFromDateAsString(mForm.getFromDateAsString());
			mForm.setToDateAsString(mForm.getToDateAsString());
			
			String mode = request.getParameter("mode");
			Date fromDt = null;
			Date toDt = null;
			
			if (mode != null && mode.equals("admin")) {
				fromDt = (Date)hps.getAttribute("fromDate");
				toDt = (Date)hps.getAttribute("toDate");
			} else {
				fromDt = mForm.getFromDate();
				toDt = mForm.getToDate();
				//reportId = Integer.valueOf(request.getParameter("reportId"));
				//hps.setAttribute("reportId", reportId);
			}
			
			reportId = (Integer)hps.getAttribute("reportId");

			logger.debug("reportId, from: " + reportId + " --- " + mForm.getFromDateAsString());

			if (fromDt != null && toDt != null) {			
		 		//check that to_date >= from_date
		 		if (toDt.compareTo(fromDt) < 0) {
					errors.add("error1", new ActionMessage("error.date"));
					//saveErrors(request, errors);
					logger.error("ReportAction:execute-fromDate cannot be after toDate");
					return mapping.findForward("report_fail");
		 		}
		 		
		 		sdf = new SimpleDateFormat("yyyy-MM-dd");
		 		logger.debug("fromDate: " + sdf.format(fromDt));
		 		logger.debug("userIds: " + ids);
			
				param_value.put("fromDate", sdf.format(fromDt));
				param_value.put("toDate", sdf.format(toDt));
				
		 		request.setAttribute("fromDate", sdf.format(fromDt));
		 		request.setAttribute("toDate", sdf.format(toDt));
			}
			
			List<Report> reports = (List<Report>)hps.getAttribute("reports");
			Report report = new Report().findReport(reports, reportId);
			
	 		grd = new GenerateReportData(param_value, report.getReportType());
	 		//request.setAttribute("ReportDataProducer", grd);

	 		grd.produceDataset(param_value); //need to call this first to populate the reportdata list
	 		List<reports_graphs.ReportData> rdl = grd.getReportData();
	 		for (reports_graphs.ReportData rd: rdl)
	 			logger.debug("c1,c2,c3: " + rd.getColumn1() + ", " + rd.getColumn2() + ", " + rd.getColumn3());
	 		
	 		if (grd.getReportColumnHeaders() != null)
	 			for (String colH: grd.getReportColumnHeaders())
	 				logger.debug("Header: " + colH);
	 		
	 		//request.setAttribute("reportDataRows", grd.getReportData());
	 		hps.setAttribute("reportDataRows", grd.getReportData());
	 		hps.setAttribute("ReportDataProducer", grd);
	 		hps.setAttribute("reportColHeaders", grd.getReportColumnHeaders());
	 		
	 		//clear sites list
	 		//hps.setAttribute("sites", null);
			return mapping.findForward("show_" + report.getReportType());
		} catch (Exception e) {
			logger.error("ReportAction:execute-" + e);
			e.printStackTrace();
		} finally {
		}
		
		return null;
	}
	
	public ActionForward exportData(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		errors.clear();

		HttpSession hps = request.getSession();
		User user = (User)hps.getAttribute("User");
		
		if (user == null) {
			errors.add("error1", new ActionMessage("error.notloggedin"));
			//saveErrors(request, errors);
			return mapping.findForward("login");
		}
		
		List<reports.ReportData> rd = (List<reports.ReportData>)hps.getAttribute("reportDataRows");
		List<String> colHeaders = (List<String>)hps.getAttribute("reportColHeaders");

		logger.debug("exportData: " + rd.size());
		String xlsFile = new ExportData().exportToXLS(user.getUserId(),rd,colHeaders);
		
		logger.debug("xlsFile: " + xlsFile);
		
		hps.setAttribute("xlsFile", PropertyUtil.load().getProperty("report_display_path") + xlsFile.substring(xlsFile.lastIndexOf("/")+1, xlsFile.length()));

		return mapping.findForward(request.getParameter("fromReport"));
	}
	
	/*
	public ActionForward runit1(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		User user = null;
		ReportForm mForm = (ReportForm) form;
		HttpSession hps = request.getSession();
		ServletContext context = request.getSession().getServletContext();
		Map param_value = new HashMap();
		Connection conn = null;
		
		errors.clear();

		try {
			user = (User)hps.getAttribute("User");
	  	  	
			if (user == null) {
				errors.add("error1", new ActionMessage("error.null.user"));
				//saveErrors(request, errors);
				logger.error("ReportAction:runit-Null User");
				return mapping.findForward("report_fail");
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			
			String ids = "";
			//check if it is an admin user
			User adminUser = (User)hps.getAttribute("AdminUser");
			if (adminUser != null) {
				if (mForm.getUserIds() == null || mForm.getUserIds().length <= 0) {
					errors.add("error1", new ActionMessage("error.siteSelection"));
					//saveErrors(request, errors);
					logger.error("ReportAction:runit-must select at least one site");
					return mapping.findForward("report_fail");	
				}
				
				for (int i = 0; i < mForm.getUserIds().length; i++) {
					logger.debug("user_id: " + mForm.getUserIds()[i]);
					if (i > 0)
						ids += ",";
					ids += mForm.getUserIds()[i];
				}
			} else { //regular user - only one site so just get it
				List<LabelValueBean>sites = (List<LabelValueBean>)hps.getAttribute("sites");
				ids = sites.get(0).getValue();
			}
			
			Date fromDt = mForm.getFromDate();
			Date toDt = mForm.getToDate();
			
	 		//check that to_date >= from_date
	 		if (toDt.compareTo(fromDt) < 0) {
				errors.add("error1", new ActionMessage("error.date"));
				//saveErrors(request, errors);
				logger.error("ReportAction:execute-fromDate cannot be after toDate");
				return mapping.findForward("report_fail");
	 		}
			
			param_value.put("user_ids", ids);	
	 		param_value.put("from_date", mForm.getFromDate());
	 		param_value.put("to_date", mForm.getToDate());	 	

			Properties props = new Properties();
			props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));	 		  
			String rpt_path = props.getProperty("report_path");
			String rpt_out_path = props.getProperty("report_out_path");
			String report  = props.getProperty("activity_report");

	 		String report_path = context.getRealPath("/") + rpt_path + report;
	  		conn = HibernateUtil.currentSession().connection();
	  		logger.debug("report_path = " + report_path);
	 		JasperPrint jasperPrint = JasperFillManager.fillReport(report_path, param_value, conn);
	 		//JasperPrint jasperPrint = JasperFillManager.fillReport("C:\\apache-tomcat-5.5.27\\webapps\\us411\\reports\\activity_report.jasper", param_value, conn);

	 		sdf = new SimpleDateFormat("yyyyMMdd-hhmmss");
	 		String s = "outfile_"  + user.getUserId() + "_" + sdf.format(new java.util.Date()) + ".html";
	 		String outfile = context.getRealPath("/") + rpt_out_path + s;
	 		
	 		logger.debug("outfile = " + outfile);

	 		JasperExportManager.exportReportToHtmlFile(jasperPrint, outfile);
	 		request.setAttribute("reportOutfile", rpt_out_path + s);

			//PrintWriter out = response.getWriter();
			//String S_RptFile = request.getContextPath() + "/" + rpt_out_path + s;
			//logger.debug("S_Rpt_File: " + S_RptFile);
			//out.println("<body > <form name= 'po'> ");
			//out.println("<script>");
			//out.println("window.open ('../../.."+S_RptFile+"')");
			//out.println("window.history.back(-1); ");
			//out.println("</script> </form> </body>");
			//out.close();
		} catch (Exception e) {
			logger.error("ReportAction:execute-" + e);
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}

		logger.info("ReportAction - Id.: " + user.getUserId());

		return mapping.findForward("report_success");
	}
	*/
}