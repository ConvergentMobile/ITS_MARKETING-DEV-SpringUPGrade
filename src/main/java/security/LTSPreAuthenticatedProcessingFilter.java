package security;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import dao.LTUserDAOManager;

import service_impl.LTSMarketingServiceImpl;
import user.RoleAction;
import util.InputDecoder;

public class LTSPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {
	LTSMarketingServiceImpl ltsService = new LTSMarketingServiceImpl();

	protected static final Logger logger = Logger.getLogger(LTSPreAuthenticatedProcessingFilter.class);

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		logger.debug("In getPreAuthenticatedPrincipal");
		
		UserDetails myUser = null;
		
		try {
			String roleP = request.getParameter("role");
			String fp = request.getParameter("fingerprint");
			String idP = request.getParameter("id");
			
			if (roleP == null && fp == null) {
				request.setAttribute("errorMsg", "You do not have a valid session. <br/> Please log in through the Liberty Tax portal.");
				logger.error("You do not have a valid session. <br/> Please log in through the Liberty Tax portal.");
				return null;
			}
			
			InputDecoder inpDec = new InputDecoder();
			
			String role = URLDecoder.decode(inpDec.getPlainText(roleP), "UTF-8");
			String eId = inpDec.getPlainText(idP);
			
			logger.debug("role: " + role);
			logger.debug("eId: " + eId);
			logger.debug("fp: " + fp);
			
			if (! inpDec.fpMatch(role, eId, fp)) {
				logger.error("fp mismatch");
				request.setAttribute("errorMsg", "fp mismatch");
				return null;
			}
			
			//check if it is a valid role
			List<RoleAction> roleActions = new LTUserDAOManager().getRoleActions(role);
			if (roleActions == null || roleActions.isEmpty()) {
				request.setAttribute("errorMsg", "Invalid role. Please make sure that you have a valid role to access US411.");
				logger.error("Invalid role");				
				return null;
			}
			
			Collection<? extends GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(); 

			/*
			User user = ltsService.loginSSO(roleP, idP, fp, new LTUserDAOManager(), request);	
			myUser = new org.springframework.security.core.userdetails.User(user.getUserId().toString(), "password", true, true, true, true, 
						authorities);	
			*/

			myUser = new org.springframework.security.core.userdetails.User(eId, "password", true, true, true, true, 
					authorities);		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return myUser;
	}

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		logger.debug("In getPreAuthenticatedCredentials");
		
		String fp = request.getParameter("fingerprint");
		if (fp == null) {
			logger.error("No credentials found");
			return null;
		}

		return "password";
	}
	
}
