package util;

import java.util.Properties;
import org.apache.log4j.Logger;

public class PropertyUtil {
	private static Logger logger = Logger.getLogger(PropertyUtil.class);

	private static Properties props;
	
	public static Properties load() {		
		try {
			if (props == null) {
				props = new Properties();
				props.load(PropertyUtil.class.getResourceAsStream("/MessageResources.properties"));
			}
			return props;
		} catch (Exception e) {
			logger.error("Error reading properties: " + e);
		}
		
		return null;
	}
}
