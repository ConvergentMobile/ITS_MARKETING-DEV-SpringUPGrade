package survey;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.actions.DispatchAction;

import user.User;
import user.UserDAOManager;

public class SurveyAction extends DispatchAction {
	private static Logger logger = Logger.getLogger(SurveyAction.class);

	public ActionForward pre(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		SurveyForm mForm = (SurveyForm) form;
		
		logger.debug("In pre");
		return mapping.findForward("success");
	}
	
	public ActionForward createSurvey(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionErrors errors = new ActionErrors();

		SurveyForm mForm = (SurveyForm) form;
		
		User user = (User)request.getSession().getAttribute("User");

		Date now = Calendar.getInstance().getTime();
		for (Survey s : user.getSurveyList()) {
			if (s.getEndDate().after(now)) {
				errors.add("error1", new ActionMessage("error.one.survey"));
				//saveErrors(request, errors);
				return mapping.findForward("fail");					
			}
		}
		user.getSurveyList().add(mForm.getSurvey());
		new UserDAOManager().saveUser(user);
		
		request.getSession().setAttribute("User", user);
		request.getSession().setAttribute("Survey", mForm.getSurvey());
		
		return mapping.findForward("survey_success");
	}
	
}
