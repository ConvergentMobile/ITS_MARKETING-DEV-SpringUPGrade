package survey;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Survey implements Serializable {
	private static final long serialVersionUID = 1L;
		
	protected SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	
	protected String surveyId;
	protected String surveyKeyword;
	protected String introText;
	protected Date startDate;
	protected Date endDate;
	// survey options choice  i.e. 1. Blue, 2. Red
	// survey options response is specific msg to be sent in case of of a response
	protected String option1;
	protected String response1;
	protected String option2;
	protected String response2;
	protected String option3;
	protected String response3;
	protected String option4;
	protected String response4;
	protected String option5;
	protected String response5;
	
	public Survey() {		
	}

	public String getSurveyKeyword() {
		return surveyKeyword;
	}

	public void setSurveyKeyword(String surveyKeyword) {
		this.surveyKeyword = surveyKeyword;
	}

	public String getIntroText() {
		return introText;
	}

	public void setIntroText(String introText) {
		this.introText = introText;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}

	public String getOption1() {
		return option1;
	}

	public void setOption1(String option1) {
		this.option1 = option1;
	}

	public String getResponse1() {
		return response1;
	}

	public void setResponse1(String response1) {
		this.response1 = response1;
	}

	public String getOption2() {
		return option2;
	}

	public void setOption2(String option2) {
		this.option2 = option2;
	}

	public String getResponse2() {
		return response2;
	}

	public void setResponse2(String response2) {
		this.response2 = response2;
	}

	public String getOption3() {
		return option3;
	}

	public void setOption3(String option3) {
		this.option3 = option3;
	}

	public String getResponse3() {
		return response3;
	}

	public void setResponse3(String response3) {
		this.response3 = response3;
	}

	public String getOption4() {
		return option4;
	}

	public void setOption4(String option4) {
		this.option4 = option4;
	}

	public String getResponse4() {
		return response4;
	}

	public void setResponse4(String response4) {
		this.response4 = response4;
	}

	public String getOption5() {
		return option5;
	}

	public void setOption5(String option5) {
		this.option5 = option5;
	}

	public String getResponse5() {
		return response5;
	}

	public void setResponse5(String response5) {
		this.response5 = response5;
	}
	
	public String getStartDateAsString() {
		return (startDate != null) ? sdf.format(startDate) : "(Click Calendar)";
	}
	
	public void setStartDateAsString(String startDate) throws Exception {
		try {
			if (startDate == null || startDate.length() <= 0 || startDate.equals("(Click Calendar)"))
				this.startDate = null;
			else
				this.startDate = sdf.parse(startDate);
		} catch (ParseException e) {
			throw new Exception("Offer: invalid date format" + e);
		}
	}
	
	public String getEndDateAsString() {
		return (endDate != null) ? sdf.format(endDate) : "(Click Calendar)";
	}
	
	public void setEndDateAsString(String endDate) throws Exception {
		try {
			if (endDate == null || endDate.length() <= 0 || endDate.equals("(Click Calendar)"))
				this.endDate = null;
			else
				this.endDate = sdf.parse(endDate);
		} catch (ParseException e) {
			throw new Exception("Offer: invalid date format" + e);
		}
	}
}
