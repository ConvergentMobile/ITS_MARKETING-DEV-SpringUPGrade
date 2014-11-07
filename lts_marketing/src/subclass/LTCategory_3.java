package subclass;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import keyword.KeywordApplication;
import keyword.KeywordDAOManager;

import org.apache.log4j.Logger;

import user.Campaign;
import user.Offer;
import user.SearchAttribute;
import user.User;
import util.ImageFile;
import util.PropertyUtil;
import category_1.Event;
import category_3.Category_3;

public class LTCategory_3 extends Category_3 implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(LTCategory_3.class);

	private String sql = "from LTCategory_3 as p where p.userId = ?";
	private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

	private String hotspotFile;
	
	//additional fields - 4/1/2014
	protected String address2;
	protected String altPhone;
	protected Boolean hispanicMktgId;
	protected String creditCardName;
	
	//for ui includephone option
	protected Boolean includePhoneIni;
	protected Boolean includePhoneRpt;

	public LTCategory_3() {
		super();
	}

	public LTCategory_3(Long userId, Integer categoryId) {
		super(userId, categoryId);
	}

	public String getHotspotFile() {
		return hotspotFile;
	}

	public void setHotspotFile(String hotspotFile) {
		this.hotspotFile = hotspotFile;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAltPhone() {
		return altPhone;
	}

	public void setAltPhone(String altPhone) {
		this.altPhone = altPhone;
	}

	public Boolean getHispanicMktgId() {
		return hispanicMktgId;
	}

	public void setHispanicMktgId(Boolean hispanicMktgId) {
		this.hispanicMktgId = hispanicMktgId;
	}

	public String getCreditCardName() {
		return creditCardName;
	}

	public void setCreditCardName(String creditCardName) {
		this.creditCardName = creditCardName;
	}

	public Boolean getIncludePhoneIni() {
		return includePhoneIni;
	}

	public void setIncludePhoneIni(Boolean includePhoneIni) {
		this.includePhoneIni = includePhoneIni;
	}

	public Boolean getIncludePhoneRpt() {
		return includePhoneRpt;
	}

	public void setIncludePhoneRpt(Boolean includePhoneRpt) {
		this.includePhoneRpt = includePhoneRpt;
	}

	public LTCategory_3 get(HttpServletRequest request, String mode, Integer type) throws Exception  {
		logger.debug("LTCategory_3:get");
		LTCategory_3 catg = null;
				
		if (mode.equals("get")) {
			super.getCategory(); //get the category fields
				
			List<LTCategory_3> rList = dao.getDetails(userId, sql);
			logger.debug("rList size: " + rList.size());
			User user = (User)request.getSession().getAttribute("User");
			KeywordApplication kwAppl = new KeywordDAOManager().getKeyword(user.getKeyword(), 
					PropertyUtil.load().getProperty("shortcode"));			
			if (rList == null || rList.isEmpty()) {
				catg = new LTCategory_3(this.userId, this.categoryId);
				//populate with values from KeywordApplication
				if (kwAppl != null) { //should never be null!
					catg.setBusinessName(kwAppl.getBusinessName());
					catg.setEmail(kwAppl.getEmail());
					catg.setAdminMobilePhone(kwAppl.getMobilePhone());
				}
			} else		
				catg = rList.get(0);
			
			//set this if null
			if (catg.getAdminMobilePhone() == null || catg.getAdminMobilePhone().equals(""))
				catg.setAdminMobilePhone(kwAppl.getMobilePhone());
			
			//set the keyword
			catg.setKeyword(user.getKeyword());
			
			//set the hotspot file
			catg.setHotspotFile(PropertyUtil.load().getProperty("hotspotPath") + "/" + user.getHotspotFile());
			
			if (catg.getSearchAttrs().size() <= 0)
				catg.searchAttrs.add(new SearchAttribute());
		} else {
			catg = (LTCategory_3)request.getSession().getAttribute("category");		
		}			
		
		for (int i = catg.getPhotos().size(); i < catg.MAX_PHOTOS; i++) {
			ImageFile photo = new ImageFile();
			catg.photos.add(photo);
		}
		
		//set the status to E for expired offers
		List<Offer> offers = this.setOfferStatus(catg.getOfferList());
		catg.setOfferList(offers);
		
		for (int i = catg.getOfferList().size(); i < catg.MAX_OFFERS; i++) {
			Offer offer = new Offer();
			offer.setOfferId(UUID.randomUUID().toString());			
			catg.offerList.add(offer);
		}

		for (int i = catg.getEvents().size(); i < catg.MAX_EVENT_ITEMS; i++) {
			Event event = new Event();
			catg.events.add(event);
		}
		
		//set the default Repeat Message
		String busName = "";
		if (catg.getBusinessName() != null)
			busName = catg.getBusinessName().trim();
		String repeatMsg = "Welcome back to the Liberty Tax Service SMS program. We will be sending some more great offers soon.";
		if (catg.getStdRepeatNotificationMsg() == null || catg.getStdRepeatNotificationMsg().length() == 0)
			catg.setStdRepeatNotificationMsg(repeatMsg);
		
		if (catg.getAutoResponse() == null || catg.getAutoResponse().length() <= 0) {
			catg.setAutoResponse(repeatMsg);
		}
		
		return catg;
	}
}
