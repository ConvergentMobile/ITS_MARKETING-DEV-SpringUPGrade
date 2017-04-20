package user;

import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.util.LabelValueBean;

import util.PropertyUtil;

public class LogOutAction extends DispatchAction {
	protected Logger logger = Logger.getLogger(UserAction.class);
	
	public void cleanup(HttpServletRequest request) throws Exception {
		HttpSession hs=request.getSession(true);
		
		Enumeration e=hs.getAttributeNames();
		
		while(e.hasMoreElements()){
			String tempString=(String)e.nextElement();
			if(hs.getAttribute(tempString)!=null)
				hs.removeAttribute(tempString);
		}

		if(hs!=null){
			hs.invalidate() ;
			hs = null;
		}	
	}
	
	public ActionForward logout(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		logger.debug("********* LogOutAction starts ***************");
		HttpSession hs=request.getSession(true);
		
		//this is to ensure that the login page uses http and not https
		String logoutURL = PropertyUtil.load().getProperty("logoutURL", PropertyUtil.load().getProperty("baseURL"));
		User user = (User) hs.getAttribute("AdminUser");
		if (user != null && user.getUserType() != null && user.getUserType().equals("A")) {
			logoutURL = PropertyUtil.load().getProperty("adminBaseURL");
		}
		
		this.cleanup(request);		

		logger.debug("********* LogOutAction ends ***************");
		
		//return mapping.findForward("success");
		
		response.sendRedirect(logoutURL);
		return null;
	}
	
	public ActionForward logoutKeywordReserve(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		logger.debug("********* LogOutAction starts ***************");
		HttpSession hs=request.getSession(true);
		
		this.cleanup(request);
		
		logger.debug("********* LogOutAction ends ***************");
		
		return mapping.findForward("success_kw_reserve");		
	}
	
	public ActionForward logoutKeywordPurchase(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		logger.debug("********* LogOutAction starts ***************");
		HttpSession hs=request.getSession(true);
		
		this.cleanup(request);
		
		logger.debug("********* LogOutAction ends ***************");
		
		return mapping.findForward("success_kw_purchase");		
	}
	
	public ActionForward logoutKeywordDecals(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		logger.debug("********* LogOutAction starts ***************");
		HttpSession hs=request.getSession(true);
		
		this.cleanup(request);
		
		logger.debug("********* LogOutAction ends ***************");
		
		return mapping.findForward("success_kw_decals");		
	}
}
