package util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import user.User;
import user.UserDAOManager;

public class FormPageAction extends DispatchAction {
	Logger logger = Logger.getLogger(FormPageAction.class);
	UserDAOManager dao = new UserDAOManager();
	
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		FormPageForm mForm = (FormPageForm)form;
		User user = (User)request.getSession().getAttribute("User");
		if (user == null) {
			logger.error("Null user");
			return mapping.findForward("fail");
		}
		
		logger.debug("user id: " + user.getUserId());
		
		mForm.setProfileId(user.getUserId());
		dao.saveFormPage(mForm);
						
		return mapping.findForward("success");
	}
	
	//called to get the form data that a user needs to input
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		FormPageDataForm mForm = (FormPageDataForm)form;

		Long profileId = Long.valueOf(request.getParameter("profileId"));
		logger.debug("profileId: " + profileId);

		Map<String, Object> fpdFields = dao.getFormPageData(profileId);
		mForm.setFormPageId(Long.valueOf((String)fpdFields.get("formPageId")));
		mForm.setField1Label((String)fpdFields.get("field1"));
		mForm.setField2Label((String)fpdFields.get("field2"));
		mForm.setField3Label((String)fpdFields.get("field3"));
		mForm.setField4Label((String)fpdFields.get("field4"));
		mForm.setField5Label((String)fpdFields.get("field5"));	
		
		mForm.setField1Value((String)fpdFields.get("field1Val"));
		mForm.setField2Value((String)fpdFields.get("field2Val"));
		mForm.setField3Value((String)fpdFields.get("field3Val"));
		mForm.setField4Value((String)fpdFields.get("field4Val"));
		mForm.setField5Value((String)fpdFields.get("field5Val"));		
								
		return mapping.findForward("success");
	}
	
	//called to save the data that a user has input
	public ActionForward capture(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		FormPageDataForm mForm = (FormPageDataForm)form;
		
		logger.debug("formpage id: " + mForm.getFormPageId());
		
		dao.saveFormPage(mForm);
						
		return mapping.findForward("afterCapture");
	}
}
