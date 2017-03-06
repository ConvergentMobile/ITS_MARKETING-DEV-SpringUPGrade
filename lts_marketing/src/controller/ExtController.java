package controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import data.InfoForm;
import data.LTUserForm;
import service.LTSMarketingService;
import service_impl.LTSMarketingServiceImpl;

@Controller
public class ExtController {
	protected static final Logger logger = Logger.getLogger(ExtController.class);

	protected LTSMarketingServiceImpl mktgService = new LTSMarketingServiceImpl();
	
	@RequestMapping(value = "/ext/infoForm", method = RequestMethod.GET)
	public ModelAndView getInfoForm(@ModelAttribute("ltUser") LTUserForm ltUser,
								HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("/WEB-INF/view/ext/infoform.jsp", "ltUser", ltUser);
		
		ltUser.setSortColumn(request.getParameter("id").toString());
		ltUser.setSortOrder(request.getParameter("m").toString());

		return mv;		
	}
	
	@RequestMapping(value = "/ext/infoForm", method = RequestMethod.POST)
	public @ResponseBody String saveInfoForm(@ModelAttribute("ltUser") LTUserForm ltUser,
								HttpServletRequest request) throws Exception {
		logger.debug("id:" + ltUser.getSortColumn());
		InfoForm iform = new InfoForm();
		iform.setFirstName(ltUser.getSearchKeywordString());
		iform.setLastName(ltUser.getSearchDMAString());
		iform.setMobilePhone(ltUser.getSearchStateString());
		iform.setEmail(ltUser.getSearchCityString());
		
		if (ltUser.getSortOrder().equals("e"))
			iform.setEntityId(ltUser.getSortColumn());
		else if (ltUser.getSortOrder().equals("o"))
			iform.setOfficeId(ltUser.getSortColumn());
		
		try {
			mktgService.saveDetails(iform);
			return "Success";
		} catch (Exception e) {
			return "Error saving the information: " + e.getMessage();		
		}
	}

}
