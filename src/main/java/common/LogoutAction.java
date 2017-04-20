package common;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.apache.log4j.*;

/**
 * This class to invalidate all the session variables once the user logs out
 * 
 * @author ValueLabs
 */
	public class LogoutAction extends Action{
		
		/**
		 * This method is used to invalidate all the session variables once the user logs out
		 * @param mapping
		 * @param form
		 * @param request
		 * @param response
		 * @return ActionForward
		 * @throws Exception
		 */
		Logger 	logger = Logger.getLogger(LogoutAction.class);
		
		public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
			
			logger.debug("********* LogOutAction starts ***************");
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
			logger.debug("********* LogOutAction ends ***************");
			
			//request.setAttribute("expired","");
			return mapping.findForward("logoutSuccess");
		}
	}

