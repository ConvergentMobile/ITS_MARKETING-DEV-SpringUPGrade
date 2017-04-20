package util;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mdp_common.DBStuff;

import org.apache.log4j.Logger;

//make all reserved keywords older than the RESERVE_PERIOD available
public class ReservedKeywords {
	static final int RESERVE_PERIOD = 14;

	private DBStuff dbs;

	Logger logger = Logger.getLogger(ReservedKeywords.class);
	
	public ReservedKeywords() throws Exception {
		this.dbs = new DBStuff();
	}
	
	public static int getRESERVE_PERIOD() {
		return RESERVE_PERIOD;
	}

	public DBStuff getDbs() {
		return dbs;
	}

	public Logger getLogger() {
		return logger;
	}

	public void updateStatus() throws Exception {
		String sql = "update keyword_application"
					+ " set status = null"
					+ " where shortcode = '5STAR'"
					+ " and status = 'R'"
					+ " and datediff(now(), last_updated) <= ?";
		
		try {
			int rows  = dbs.update(sql, new Object[] {RESERVE_PERIOD});
			logger.debug(rows + " keywords updated");
		} finally {
			dbs.close();
		}		
	}

}
