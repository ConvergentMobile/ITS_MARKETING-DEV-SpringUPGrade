package util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.gson.Gson;

import data.ValueObject;
import security.LTSUserDetails;
import user.User;

public class Utility {
	protected static final Logger logger = Logger.getLogger(Utility.class);
	private static final String GOOGLE_URL_SHORT_API = "https://www.googleapis.com/urlshortener/v1/url";
	private static final String GOOGLE_API_KEY = "AIzaSyDeU9Su5Dp5Jcwtvb668MFaxn55af8dMl4";
	
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
	
	public String formatPhone(String pnum) throws Exception {
		if (pnum == null)
			return null;
		
		StringBuilder sb = new StringBuilder();
		sb.append("1");

		if (pnum.length() > 10) {
			pnum = pnum.substring(1);
		}
		sb.append("(").append(pnum.substring(0, 3)).append(")");
		sb.append(pnum.substring(3, 6)).append("-").append(pnum.substring(6));
		
		return sb.toString();
	}
	
    public void createFile(List<ValueObject> rows) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet sheet = workbook.createSheet("Usage Report");
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setFontName("Calibri");
                font.setBoldweight(Font.BOLDWEIGHT_BOLD);
                style.setFont(font);
        		
        		Calendar cal = Calendar.getInstance();
        		cal.add(Calendar.DAY_OF_MONTH, -cal.get(Calendar.DAY_OF_WEEK)+1);
                
        		int rowCount = 1;
                int col = 0;

                HSSFRow header1 = sheet.createRow(rowCount++);
                header1.createCell(col).setCellValue("Date: " + cal.getTime());
                rowCount++;
              
                HSSFRow header = sheet.createRow(rowCount++);
                header.createCell(col).setCellValue("Entity");
                header.getCell(col).setCellStyle(style);

                col++;
                header.createCell(col).setCellValue("Office");
                header.getCell(col).setCellStyle(style);

                col++;
                header.createCell(col).setCellValue("Keyword");
                header.getCell(col).setCellStyle(style);

                col++;
                header.createCell(col).setCellValue("Keyword Status");
                header.getCell(col).setCellStyle(style);

                col++;
                header.createCell(col).setCellValue("Marketing Msgs Sent");
                header.getCell(col).setCellStyle(style);

                col++;
                header.createCell(col).setCellValue("Optins");
                header.getCell(col).setCellStyle(style);

                col++;
                header.createCell(col).setCellValue("Email");
                header.getCell(col).setCellStyle(style);

                col++;
                header.createCell(col).setCellValue("Phone");
                header.getCell(col).setCellStyle(style);
               
                int i = 0;
                for (ValueObject row : rows) {
                        HSSFRow aRow = sheet.createRow(rowCount++);
                        aRow.createCell(0).setCellValue(row.getField1().toString());
                        aRow.createCell(1).setCellValue(row.getField2().toString());
                        aRow.createCell(2).setCellValue(row.getField3().toString());
                        aRow.createCell(3).setCellValue(row.getField6().toString());
                        aRow.createCell(4).setCellValue(row.getField4().toString());
                        aRow.createCell(5).setCellValue(row.getField5().toString());
                        aRow.createCell(6).setCellValue(row.getField7().toString());
                        aRow.createCell(7).setCellValue(row.getField8().toString());                        
                }

            String filePath = PropertyUtil.load().getProperty("report_out_path");
            FileOutputStream out = new FileOutputStream(new File(filePath + "dashboard_report.xls"));
            workbook.write(out);
            out.close();
        }

	public String shortenUrl(String longUrl) throws Exception {	
        String json = "{\"longUrl\": \""+longUrl+"\"}";   
        String apiURL = GOOGLE_URL_SHORT_API+"?key="+GOOGLE_API_KEY;
         
        try {
			HttpPost postRequest = new HttpPost(apiURL);
			postRequest.setHeader("Content-Type", "application/json");
			postRequest.setEntity(new StringEntity(json, "UTF-8"));
 
			CloseableHttpClient httpClient = HttpClients.createDefault();
			CloseableHttpResponse response = httpClient.execute(postRequest);
			String responseText = EntityUtils.toString(response.getEntity());   
			logger.debug("responseText: " + responseText);
			 
			Gson gson = new Gson();
			@SuppressWarnings("unchecked")
			HashMap<String, String> res = gson.fromJson(responseText, HashMap.class);
 
			return res.get("id");
		} catch (Exception e) {
			return longUrl;
		}  
	}
	
	//checks if we are in the 8am - 8pm window
	public Boolean inWindow(Integer delay, String userTZ) throws Exception {		
		Calendar cal = Calendar.getInstance();

		int hour = cal.get(Calendar.HOUR_OF_DAY);

		cal.setTimeZone(TimeZone.getTimeZone(userTZ));
		
		if (delay != null)
			cal.add(Calendar.HOUR, delay);
		
		Date userDate = cal.getTime();
		
		cal.set(Calendar.HOUR_OF_DAY, 8);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		
		if (hour >= 0 && hour < 12)
                        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)-1);

		Date windowStart = cal.getTime();

		cal.set(Calendar.HOUR_OF_DAY, 20);

		//if (hour >= 0 && hour < 12)
                        //cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)-1);

		Date windowEnd = cal.getTime();

		logger.debug("userDate: " + userDate);
		logger.debug("windowStart: " + windowStart);
		logger.debug("windowEnd: " + windowEnd);

		if (userDate.compareTo(windowStart) >= 0 && userDate.compareTo(windowEnd) <= 0)
			return true;
		
		return false;		
	}
}
