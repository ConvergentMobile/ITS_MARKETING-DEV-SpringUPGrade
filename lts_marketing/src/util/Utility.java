package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import security.LTSUserDetails;
import user.User;

public class Utility {
	protected static final Logger logger = Logger.getLogger(Utility.class);

	public User getUserFromSecurityContext() {
		LTSUserDetails ud = null;
		User user = null;
		try {
			ud = (LTSUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Long userId = Long.valueOf(ud.getUsername());
			if (userId.intValue() == 0) { //Corporate user
				user = new User();
				user.setUserId(0L);
			} else {
				//user = new LTSUserDAOManager().getUser(userId);
				user = new User();
				user.setUserId(userId);
			}
			//set these
			user.setSecRole(ud.getRole());
			user.setRoleActions(ud.getRoleActions());
		} catch (NumberFormatException e) {
			logger.error("Invalid userId: " + ud.getUsername());
		} catch (Exception e) {
			logger.error("Error getting user from Principal: " + e.getMessage());
			e.printStackTrace();
		}
		return user;
	}
	
	public String getDateInTZ(Date date, String tz) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");
		
		if (tz != null)
			sdf.setTimeZone(TimeZone.getTimeZone(tz));

		return sdf.format(date);
	}
	
	public Date getLocalDate(Date date) throws Exception {
		if (date == null)
			return null;
		Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	cal.add(Calendar.MILLISECOND, cal.get(Calendar.ZONE_OFFSET));
    	
		return cal.getTime();
	}
	
	public String normalizePhoneNumber(String phoneNumber) {
		if (phoneNumber == null)
			return null;
		
		Pattern p = Pattern.compile("\\s+|\\.|-| |\\(|\\)");

		Matcher matcher = p.matcher(phoneNumber);
		String tmp = matcher.replaceAll("");
		if (tmp.length() < 11)
			tmp = "1" + tmp;

		return tmp;
	}
}
