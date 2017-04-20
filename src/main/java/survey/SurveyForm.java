package survey;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class SurveyForm extends ActionForm {
	private static Logger logger = Logger.getLogger(SurveyForm.class);
	
	private static final long serialVersionUID = 1L;
	private Survey survey;
	
	public Survey getSurvey() {
		return survey;
	}
	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		logger.debug("SurveyForm: reset");
		if (this.survey == null)
			this.survey = new Survey();
	}
}
