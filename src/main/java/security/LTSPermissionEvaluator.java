package security;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class LTSPermissionEvaluator implements PermissionEvaluator {
	protected static final Logger logger = Logger.getLogger(LTSPermissionEvaluator.class);

	@Override
	public boolean hasPermission(Authentication auth, Object arg1, Object permission) {
		// TODO Auto-generated method stub
		logger.debug("hasPermission: permission = " + permission.toString());

		List<GrantedAuthority> galist = (List<GrantedAuthority>) auth.getAuthorities();
		for (GrantedAuthority ga : galist) {
			logger.debug("ga: " + ga.getAuthority());
			if (permission.equals(ga.getAuthority())) 
				return true;
		}
			
		logger.error("You do not have permission for this action");
		
		return false;
	}

	@Override
	public boolean hasPermission(Authentication arg0, Serializable arg1,
			String arg2, Object arg3) {
		logger.debug("hasPermission: permission");
		// TODO Auto-generated method stub
		return false;
	}

}
