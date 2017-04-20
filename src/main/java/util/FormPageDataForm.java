package util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

//This is used for the user to input data
public class FormPageDataForm extends ActionForm {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(FormPageForm.class);

	private Long formPageDataId;
	private Long formPageId;
	private String mobilePhone;
	private String field1Label;
	private String field2Label;
	private String field3Label;
	private String field4Label;
	private String field5Label;	
	private String field1Value;
	private String field2Value;
	private String field3Value;
	private String field4Value;
	private String field5Value;
	
	public Long getFormPageDataId() {
		return formPageDataId;
	}

	public void setFormPageDataId(Long formPageDataId) {
		this.formPageDataId = formPageDataId;
	}

	public Long getFormPageId() {
		return formPageId;
	}
	
	public void setFormPageId(Long formPageId) {
		this.formPageId = formPageId;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
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

	public String getField1Value() {
		return field1Value;
	}

	public void setField1Value(String field1Value) {
		this.field1Value = field1Value;
	}

	public String getField2Value() {
		return field2Value;
	}

	public void setField2Value(String field2Value) {
		this.field2Value = field2Value;
	}

	public String getField3Value() {
		return field3Value;
	}

	public void setField3Value(String field3Value) {
		this.field3Value = field3Value;
	}

	public String getField4Value() {
		return field4Value;
	}

	public void setField4Value(String field4Value) {
		this.field4Value = field4Value;
	}

	public String getField5Value() {
		return field5Value;
	}

	public void setField5Value(String field5Value) {
		this.field5Value = field5Value;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {
	}
}
