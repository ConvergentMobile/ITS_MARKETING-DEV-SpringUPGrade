package category_6;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import category_1.Event;
import category_1.ItemEnum;
import category_1.MenuSection;

import sms.US411Handler;
import user.Campaign;
import user.User;
import user.UserDAOManager;
import util.ContentTransform;
import util.ImageFile;

public class Category_6_1 extends Category_6 implements Serializable {
	private static final long serialVersionUID = 1L;
	private int MAX_PHOTOS = 3;
	private String sql = "from Category_6_1 as p where p.userId = ?";

	Logger logger = Logger.getLogger(Category_6_1.class);

	private String homeAddress;
	private String reward;
	private String petName;
	private String locLastSeen;
	private String zipsLastSeen;
	
	public Category_6_1() {
		super();
		this.subCategoryId = 1;
	}
	
	public Category_6_1(Long userId, Integer categoryId) {
		super(userId, categoryId);
	}
	
	public String getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	public String getPetName() {
		return petName;
	}

	public void setPetName(String petName) {
		this.petName = petName;
	}

	public String getLocLastSeen() {
		return locLastSeen;
	}

	public void setLocLastSeen(String locLastSeen) {
		this.locLastSeen = locLastSeen;
	}

	public String getZipsLastSeen() {
		return zipsLastSeen;
	}

	public void setZipsLastSeen(String zipsLastSeen) {
		this.zipsLastSeen = zipsLastSeen;
	}

	public Category_6_1 get(HttpServletRequest request, String mode, Integer type) throws Exception  {
		Category_6_1 catg = null;
		if (mode.equals("get")) {
			super.getCategory(); //get the category fields
				
			List<Category_6_1> rList = dao.getDetails(userId, sql);
			if (rList.isEmpty()) {
				catg = new Category_6_1(userId, categoryId);
			} else		
				catg = rList.get(0);
		}
		
		for (int i = catg.getPhotos().size(); i < MAX_PHOTOS; i++) {
			ImageFile photo = new ImageFile();
			catg.photos.add(photo);
		}
		
		return catg;
	}

	//writes out the xml 
	public StringBuffer toXMLSection1(HttpSession session) throws Exception {		
		StringBuffer sb = super.toXMLSection1(session); //get the main category xml first
		
		sb.append("<pet>");
		sb.append("<pet_name>").append(this.getPetName()).append("</pet_name>");
		sb.append("<loc_last_seen>").append(this.getLocLastSeen()).append("</loc_last_seen>");
		sb.append("<zips_last_seen>").append(this.getZipsLastSeen()).append("</zips_last_seen>");
		sb.append("<reward>").append(this.getReward()).append("</reward>");
		sb.append("</pet>");
		return sb;
	}
	
	//override the sendMessage in Category_6
	public void sendMessage(Campaign campaign, String keyword, HttpServletRequest request) throws Exception {
		logger.debug("Category_6_1 sendMessage");
		String innerUrl = preview(request);
		String xmlFile = props.getProperty("xmlFilePath") + "/" + this.getUserId() + "_final.xml";
		String xslFilePath = props.getProperty("xslFilePath") + "/category_6";
		
		String message = campaign.getMessageText();
		
		message += System.getProperty("line.separator") + new ContentTransform().getTinyUrl(innerUrl);
		logger.debug("sendMessage: message = " + message);
	    US411Handler smsh = new US411Handler(null, "US");
	    List<String>pNums = dao.getTargetList(userId, this.getZip()); //restrict the list to matching zip
	    smsh.sendBulkSMS(message, "US411", "0", pNums.toArray(), this.getUserId().toString(), null, keyword);
	}
}
