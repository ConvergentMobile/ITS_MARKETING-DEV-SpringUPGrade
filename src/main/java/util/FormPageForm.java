package util;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

//This is used when you a have a form with a submit button
//Supports 5 fields for now
public class FormPageForm extends ActionForm {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(FormPageForm.class);

	private Long formPageId;
	private Long profileId;
	private String field1Label;
	private String field2Label;
	private String field3Label;
	private String field4Label;
	private String field5Label;
	private String action;
	
	public Long getFormPageId() {
		return formPageId;
	}

	public void setFormPageId(Long formPageId) {
		this.formPageId = formPageId;
	}

	public Long getProfileId() {
		return profileId;
	}

	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}

	public String getField1Label() {
		return field1Label;
	}

	public void setField1Label(String field1Label) {
		this.field1Label = field1Label;
	}

	public String getField2Label() {
		return field2Label;
	}

	public void setField2Label(String field2Label) {
		this.field2Label = field2Label;
	}

	public String getField3Label() {
		return field3Label;
	}

	public void setField3Label(String field3Label) {
		this.field3Label = field3Label;
	}

	public String getField4Label() {
		return field4Label;
	}

	public void setField4Label(String field4Label) {
		this.field4Label = field4Label;
	}

	public String getField5Label() {
		return field5Label;
	}

	public void setField5Label(String field5Label) {
		this.field5Label = field5Label;
	}

	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
	}
}
