package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtility {
	final TimeZone timeZone = TimeZone.getDefault();
	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

	public DateUtility() {		
	}
	
	//convert from user's tz
	public String toServerTime(String d1, String userTz) throws Exception {
		sdf.setTimeZone(TimeZone.getTimeZone(userTz));
		Date dt = sdf.parse(d1);
		sdf.setTimeZone(timeZone);
		
		return sdf.format(dt);
	}
	
	//convert to user's tz
	public String toUserTime(String d1, String userTz) throws Exception {
		sdf.setTimeZone(TimeZone.getTimeZone(userTz));
		Date dt = sdf.parse(d1);

		return sdf.format(dt);
	}
}
