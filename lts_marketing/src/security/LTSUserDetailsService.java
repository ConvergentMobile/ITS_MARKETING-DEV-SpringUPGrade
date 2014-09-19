package security;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import user.RoleAction;
import user.User;
import util.InputDecoder;

import dao.LTUserDAOManager;

public class LTSUserDetailsService implements UserDetailsService {
	protected static final Logger logger = Logger.getLogger(LTSUserDetailsService.class);

	@Autowired
	private HttpServletRequest request;
	  
	protected String role;
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public UserDetails loadUserByUsername(String eId) throws UsernameNotFoundException {
		logger.debug("In loadUserByUsername");
		try {
			role = URLDecoder.decode(new InputDecoder().getPlainText(request.getParameter("role")), "UTF-8");
			User user = null;
			
			//User user = new LTUserDAOManager().getUser(Long.valueOf(userId));
			List<RoleAction> roleActions = new LTUserDAOManager().getRoleActions(role);
			//check if this is a Corporate user
			if (roleActions.get(0).getRoleType().equals("Corporate")) {
				user = new User();
				user.setUserId(0L); //set this so that ReportAction.setup does not barf
			} else {
				user = new LTUserDAOManager().loginLT(roleActions.get(0).getRoleType(), eId);
			}
			user.setRoleActions(roleActions);
			
			Collection<GrantedAuthority> gal = new ArrayList<GrantedAuthority>();
			
			for (RoleAction ra : user.getRoleActions()) {
				GrantedAuthority ga = new SimpleGrantedAuthority(ra.getRoleAction());
				gal.add(ga);
			}			
			
			LTSUserDetails ltsUser = new LTSUserDetails(user.getUserId().toString(), "password", true, true, true, true, gal, role, roleActions);
			
			return ltsUser;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
