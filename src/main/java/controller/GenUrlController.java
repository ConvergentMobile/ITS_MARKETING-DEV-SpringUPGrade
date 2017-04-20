package controller;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import util.InputDecoder;
import data.LTUserForm;

@Controller
public class GenUrlController {

	@RequestMapping(value = "/generateUrl", method = RequestMethod.GET)
	public ModelAndView generateUrlG(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) {
		return new ModelAndView("/WEB-INF/jsp/generate_url.jsp", "ltUser", ltUser);
	}
	
	@RequestMapping(value = "/generateUrl", method = RequestMethod.POST)
	public @ResponseBody String generateUrl(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) {
		final String SALT = "u5QY1ZjFq-P2kLYn";

		String irole = request.getParameter("role");
		String iid = request.getParameter("id");
		String password = request.getParameter("password");
		
		if (! password.equals("7434hhfkjf")) {
			return "Sorry";
		}

		try {
			String erole = Base64.encodeBase64String(URLEncoder.encode(irole, "UTF-16LE").getBytes("UTF-16LE"));
			String eid = Base64.encodeBase64String(URLEncoder.encode(iid, "UTF-16LE").getBytes("UTF-16LE"));
			String efp =  new InputDecoder().decodeIt(irole, iid, SALT);
			
			String ssourl = "http://23.23.203.174/lts_marketing/loginSSO?role=" + erole + "&id=" + eid + "&fingerprint=" + efp;
			return ssourl;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error: " + e.getMessage();
		}
	}
}
