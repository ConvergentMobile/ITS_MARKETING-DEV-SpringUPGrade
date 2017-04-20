package category_3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import keyword.KeywordApplication;
import keyword.KeywordDAOManager;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.LabelValueBean;

import category_1.ItemEnum;

import category_1.Event;

import survey.Survey;
import user.Campaign;
import user.CategoryBase;
import user.CategoryForm;
import user.Offer;
import user.SearchAttribute;
import user.TargetUserList;
import user.User;
import user.UserDAOManager;
import util.ContentTransform;
import util.Geocoder;
import util.ImageFile;
import util.JSONHandler;
import util.PropertyUtil;

public class Category_3 extends CategoryBase implements Serializable {
	protected static final long serialVersionUID = 1L;
	protected static Logger logger = Logger.getLogger(Category_3.class);
	
	protected String description;
	protected String offers;	
	protected String areaServed;	
	protected List<SearchAttribute> searchAttrs = new ArrayList<SearchAttribute>();
	protected Double lat;
	protected Double lon;
	protected String geocode; //used by livedeal
	protected List<ImageFile> photos = new ArrayList<ImageFile>();
	protected String yelpReview;
	protected String facebookLink;
	protected String twitterLink;
	
	protected String eventIntroText; //intro text for the Events page	
	protected List<Event> events = new ArrayList<Event>();
	
	protected UserDAOManager dao = new UserDAOManager();
	protected String sql = "from Category_3 as p where p.userId = ?";

	protected int MAX_PHOTOS = 4;
	protected int MAX_EVENT_ITEMS = 5;

	public Category_3() {
		super();
		this.categoryId = 3;
		this.categoryName = "category_3";
	}

	public Category_3(Long userId, Integer categoryId) {
		super(userId, categoryId);
		this.categoryName = "category_3";
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOffers() {
		return offers;
	}

	public void setOffers(String offers) {
		this.offers = offers;
	}

	public String getAreaServed() {
		return areaServed;
	}

	public void setAreaServed(String areaServed) {
		this.areaServed = areaServed;
	}

	public List<SearchAttribute> getSearchAttrs() {
		return searchAttrs;
	}

	public void setSearchAttrs(List<SearchAttribute> searchAttrs) {
		this.searchAttrs = searchAttrs;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public String getGeocode() {
		return geocode;
	}

	public void setGeocode(String geocode) {
		this.geocode = geocode;
	}

	public List<ImageFile> getPhotos() {
		return photos;
	}

	public void setPhotos(List<ImageFile> photos) {
		this.photos = photos;
	}

	public String getYelpReview() {
		return yelpReview;
	}

	public void setYelpReview(String yelpReview) {
		this.yelpReview = yelpReview;
	}

	public String getFacebookLink() {
		return facebookLink;
	}

	public void setFacebookLink(String facebookLink) {
		this.facebookLink = facebookLink;
	}

	public String getTwitterLink() {
		return twitterLink;
	}

	public void setTwitterLink(String twitterLink) {
		this.twitterLink = twitterLink;
	}

	public String getEventIntroText() {
		return eventIntroText;
	}

	public void setEventIntroText(String eventIntroText) {
		this.eventIntroText = eventIntroText;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public UserDAOManager getDao() {
		return dao;
	}

	public void setDao(UserDAOManager dao) {
		this.dao = dao;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Category_3 get(HttpServletRequest request, String mode, Integer type) throws Exception  {
		logger.debug("Category_3:get");
		Category_3 catg = null;
		
		if (mode.equals("get")) {
			super.getCategory(); //get the category fields
				
			List<Category_3> rList = dao.getDetails(userId, sql);
			User user = (User)request.getSession().getAttribute("User");
			if (rList.isEmpty()) {
				catg = new Category_3(this.userId, this.categoryId);
				//populate with values from KeywordApplication
				KeywordApplication kwAppl = new KeywordDAOManager().getKeyword(user.getKeyword(), 
													PropertyUtil.load().getProperty("shortcode"));
				if (kwAppl != null) { //should never be null!
					catg.setBusinessName(kwAppl.getBusinessName());
					catg.setEmail(kwAppl.getEmail());
					catg.setAdminMobilePhone(kwAppl.getMobilePhone());
				}				
			} else		
				catg = rList.get(0);
			
			//set the status to E for expired offers
			List<Offer> offers = this.setOfferStatus(catg.getOfferList());
			catg.setOfferList(offers);
			
			if (catg.getSearchAttrs().size() <= 0)
				catg.searchAttrs.add(new SearchAttribute());
		} else {
			catg = (Category_3)request.getSession().getAttribute("category");
			
			switch (ItemEnum.valueOf(type)) {	
			case EVENT: //Event
				List<Event> evlist = deleteEmptyEvents(catg.getEvents());
				catg.setEvents(evlist);					
				logger.debug("ev & max sizes: " + catg.getEvents().size() + ", " + catg.MAX_EVENT_ITEMS);
				if (catg.getEvents().size() >= catg.MAX_EVENT_ITEMS)
					catg.MAX_EVENT_ITEMS = catg.getEvents().size() + 1;
				break;
			case OFFER: 
				List<Offer> offerList = deleteEmptyOffers(catg.getOfferList());
				//there should be at most one active offer
				for (Offer offer : offerList)
					if (offer.getStatus().equals("A"))
						offer.setStatus("EP"); //set status to expired pending for the current offer
				
				//add a new active one
				Offer offer = new Offer();
				offer.setOfferId(UUID.randomUUID().toString());	
				offer.setStatus("A"); //New
				offerList.add(offer);
				catg.setOfferList(offerList);						
				break;				
			default:
				logger.error("Not sure what to do here ...");
				break;
			}			
		}			
		
		for (int i = catg.getPhotos().size(); i < catg.MAX_PHOTOS; i++) {
			ImageFile photo = new ImageFile();
			catg.photos.add(photo);
		}
				
		for (int i = catg.getOfferList().size(); i < catg.MAX_OFFERS; i++) {
		//for (int i = catg.getActiveOfferListSize(); i < catg.MAX_OFFERS; i++) {		
			Offer offer = new Offer();
			offer.setOfferId(UUID.randomUUID().toString());	
			offer.setStatus("A");
			catg.offerList.add(offer);
		}
		
		for (Offer offer : catg.getOfferList()) {
			logger.debug("get: offer name: " + offer.getName() + " --- " + offer.getStatus());
		}
		
		for (int i = catg.getEvents().size(); i < catg.MAX_EVENT_ITEMS; i++) {
			Event event = new Event();
			event.setId(UUID.randomUUID().toString());	
			catg.events.add(event);
		}
		
		logger.debug("In get: timezone = " + catg.getTimezone());
		
		//set the default Repeat Message
		String busName = "";
		if (catg.getBusinessName() != null)
			busName = catg.getBusinessName().trim();
		String repeatMsg = "Welcome back to the " + busName + " SMS program. We will be sending some more great offers soon.\n"
							+ "For current offers, please click on the link below.  Thanks!!!";	
		catg.setStdRepeatNotificationMsg(repeatMsg);
		
		if (catg.getAutoResponse() == null || catg.getAutoResponse().length() <= 0) {
			catg.setAutoResponse(repeatMsg);
		}
		
		return catg;
	}
	
	//used by subclass NNCategory_3
	public String preview(HttpServletRequest request, Campaign campaign) throws Exception {	
		throw new Exception("Base class preview with Campaign - not implemented");
	}
	
	public String preview(HttpServletRequest request, String currPage) throws Exception {	
		//save it first
		save(request);
		
		//Properties props = new Properties();
		//props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));
		Properties props = PropertyUtil.load();
		
		//write the static images
		String dpath = props.getProperty("displayPath");
		List<ImageFile> staticImages = new ArrayList<ImageFile>();
		ImageFile ifile = new ImageFile(null, "cplogo", props.getProperty("cpLogoName"), props.getProperty("imgFilePath") + File.separator, "us411 logo");
		ifile.setDisplayPath(dpath + "/" + props.getProperty("cpLogoName"));
		staticImages.add(ifile);
			
		ContentTransform ct = new ContentTransform();
		
		StringBuffer sb1 = this.toXMLSection1(request.getSession());
		
		//StringBuffer sb2 = ct.toXMLImages(this.getPhotos(), staticImages, 300, 320);
		
		//resize it for the browser as well
		String userAgent = request.getHeader("user-agent");
		
		//force user-agent to browser
		userAgent = "browser";
		
		List<ImageFile> rImages = ct.resizeImages(userId, userAgent, this.getPhotos(), null);
		List<ImageFile> sImages = ct.resizeImages(userId, userAgent, staticImages, "S");
		
		float d_width = sImages.get(0).getDeviceWidth();
		float d_height = sImages.get(0).getDeviceHeight();

		StringBuffer sb2 = ct.toXMLImages(rImages, sImages, d_width, d_height);

		//add the map url
		try {
			String mapUrl = new Geocoder().getStaticMapURL(this.getAddressForDisplay());
			sb2.append("<map_url><![CDATA[").append(mapUrl).append("]]></map_url>");		
		} catch (Exception e) {
			logger.error("Error in getting mapUrl for address: " + this.getAddressForDisplay());
		}
		
		//get the xslt files
		try {
			if (this.getXslTransformFiles() == null) {
				this.setXslTransformFiles(dao.getXslFiles(userId).split(","));
			}
		} catch (Exception e) {
			logger.error("Error getting xslt files");
		}
		
		//create a new file so as not to change the xmlFile
		String xmlFile = props.getProperty("xmlFilePath") + "/" + this.getUserId() + "_final.xml";
		BufferedWriter bw = new BufferedWriter(new FileWriter(xmlFile));
		bw.write(sb1.toString());
		bw.write(sb2.toString());
		
		//write this out to complete the file
		bw.write("</profile>");
		bw.close();
		
		String xslFilePath = props.getProperty("xslFilePath") + "/category_3";
		return ct.transform(xmlFile, xslFilePath, this.getXslTransformFiles());
	}
	
	public void save(HttpServletRequest request) throws Exception {
		save(request, null);
	}
	
	public void save(HttpServletRequest request, String currPage) throws Exception {
		List<ImageFile> photoList = new ArrayList<ImageFile>();
		this.setPhotos(new ImageFile().saveImageFiles(userId, this.getPhotos()));

		logger.debug("Saving events");
		List<Event> evList = new ArrayList<Event>();
		for (Event event : this.getEvents()) {
			if (event != null && event.getName() != null && event.getName().length() > 0) {
				logger.debug("event name: " + event.getName());
				evList.add(event);
			}
		}
		this.setEvents(evList);
		
		//When AddNew Offer is clicked, a new offer is added to the list with a status A and the current offer
		//status is changed to EP. If a new offer was created (a non null offer name), change the EP to E and save it.
		//Else, change the EP back to A - 10/23/12
		
		Boolean addOffer = false;
		Boolean rollbackOffer = false;
		for (Offer offer : this.getOfferList()) {
			if (offer.getStatus() != null && offer.getStatus().equals("A")) {
				if (offer.getName() != null && offer.getName().length() > 0)
					addOffer = true;
				else { //user cancelled adding new offer so roll it back
					rollbackOffer = true;
				}
			}
			
			logger.debug("offer name: " + offer.getName());
		}
		
		for (Offer offer : this.getOfferList()) {
			if (offer.getStatus() != null && offer.getStatus().equals("EP")) {
				if (addOffer)
					offer.setStatus("E");
				if (rollbackOffer)
					offer.setStatus("A");
			}
		}
		
		this.setOfferList(this.deleteEmptyOffers(this.getOfferList()));
		
		//generate the xmlfile
		this.toXMLSection1(request.getSession());

		dao.saveDetails(this);
	}
	
	/*
	public void toXML(HttpSession session) throws Exception {
		List<LabelValueBean> cuisines = (List<LabelValueBean>)session.getAttribute("cuisines");
		List<LabelValueBean> fonts = (List<LabelValueBean>)session.getAttribute("fonts");
		User user = (User)session.getAttribute("User");
		
		StringBuffer sb = new StringBuffer();
		sb.append("<profile id=\"").append(this.getUserId()).append("\">");
		
		//add the map url
		try {
			//sb.append("<map_url><![CDATA[").append(new Geocoder().getStaticMapURL(this.getAddress())).append("]]></map_url>");
		} catch (Exception e) {
			logger.error("Error in getting mapUrl");
			throw new Exception(e);
		}
		
		boolean gotPic = false;
		for (ImageFile ifile : this.getPhotos()) {
			//if (ifile.getFile() != null && ifile.getFile().getFileSize() > 0) {
			if (ifile.getFileName() != null) {
				logger.debug("ifile displayPath, location: " + ifile.getDisplayPath() + ", " + ifile.getLocation());
				if (ifile.getLocation().equals("logo")) {
					//sb.append("<logo>").append("file:///").append(ifile.getFilePath()).append("</logo>");
					sb.append("<logo>").append(ifile.getDisplayPath()).append("</logo>");
				} else {
					if (! gotPic) {
						sb.append("<pictures>");
						gotPic = true;
					} 
					sb.append("<picture>").append(ifile.getDisplayPath()).append("</picture>");
				}
			}
		}
		 if (gotPic)
			 sb.append("</pictures>");
		
		sb.append("<general>");
		
		Hours hours = this.getBusHours();
		sb.append("<hours>");
		sb.append("<mon>").append(hours.getMonOpen()).append(" - ").append(hours.getMonClose()).append("</mon>");
		sb.append("<tue>").append(hours.getTueOpen()).append(" - ").append(hours.getTueClose()).append("</tue>");
		sb.append("<wed>").append(hours.getWedOpen()).append(" - ").append(hours.getWedClose()).append("</wed>");
		sb.append("<thu>").append(hours.getThuOpen()).append(" - ").append(hours.getThuClose()).append("</thu>");
		sb.append("<fri>").append(hours.getFriOpen()).append(" - ").append(hours.getFriClose()).append("</fri>");
		sb.append("<sat>").append(hours.getSatOpen()).append(" - ").append(hours.getSatClose()).append("</sat>");
		sb.append("<sun>").append(hours.getSunOpen()).append(" - ").append(hours.getSunClose()).append("</sun>");
		sb.append("</hours>");
		
		sb.append("<businessName><![CDATA[").append(this.getBusinessName()).append("]]></businessName>");
		sb.append("<hours>").append(this.getBusHours()).append("</hours>");
		
		sb.append("<contact_info>");
		sb.append("<phone>").append(this.getPhone()).append("</phone>");
		sb.append("<location>").append(this.getAddress()).append(" ").append(this.getZip()).append("</location>");
		sb.append("<email>").append(user.getEmail()).append("</email>");
		
		sb.append("</contact_info>");
		sb.append("<areaServed>").append(this.getAreaServed()).append("</areaServed>");
		sb.append("<description><![CDATA[").append(this.getDescription()).append("]]></description>");
		sb.append("<offers><![CDATA[").append(this.getOffers()).append("]]></offers>");
		sb.append("</general>");
		
		sb.append("</profile>");
		
		//save it to a file
		Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));

		String xmlFile = props.getProperty("xmlFilePath") + "/" + this.getUserId() + ".xml";
		BufferedWriter bw = new BufferedWriter(new FileWriter(xmlFile));
		bw.write(sb.toString());
		bw.close();
		this.setXmlFile(xmlFile);
	}
	*/
	
	public StringBuffer toXMLSection1(HttpSession session) throws Exception {
		List<LabelValueBean> cuisines = (List<LabelValueBean>)session.getAttribute("cuisines");
		List<LabelValueBean> fonts = (List<LabelValueBean>)session.getAttribute("fonts");
		User user = (User)session.getAttribute("User");
		
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='ISO-8859-1'?>");

		sb.append("<profile id=\"").append(this.getUserId()).append("\"")
		.append(" keyword=\"").append(user.getKeyword())
		.append("\">");		

		/* moved to preview to handle map size based on device
		//add the map url
		try {
			sb.append("<map_url><![CDATA[").append(new Geocoder().getStaticMapURL(this.getAddressForDisplay())).append("]]></map_url>");
		} catch (Exception e) {
			logger.error("Error in getting mapUrl");
			throw new Exception(e);
		}
		*/
		
		sb.append(super.toXMLSection1(session)); //right now this has only the repeating-logo yes/no
		
		sb.append("<general>");
		
		sb.append("<businessName><![CDATA[").append(this.getBusinessName()).append("]]></businessName>");
		sb.append("<hours>").append(this.getBusHours()).append("</hours>");
		
		sb.append("<contact_info>");
		sb.append("<phone>").append(this.getPhone()).append("</phone>");
		
		//sb.append("<location>").append(this.getAddressForDisplay()).append("</location>");
		
		sb.append("<location>").append(this.getAddress()).append("</location>");
		sb.append("<location_city>").append(this.getCity()).append("</location_city>");
		sb.append("<location_state>").append(this.getState()).append("</location_state>");
		sb.append("<location_zip>").append(this.getZip()).append("</location_zip>");
		
		sb.append("<email>").append(this.getEmail()).append("</email>");
		sb.append("<website>").append(this.getWebsite()).append("</website>");
		sb.append("<facebook_link>").append(this.getFacebookLink()).append("</facebook_link>");
		sb.append("<twitter_link>").append(this.getTwitterLink()).append("</twitter_link>");		
		sb.append("</contact_info>");
		sb.append("<areaServed>").append(this.getAreaServed()).append("</areaServed>");
		sb.append("<description><![CDATA[").append(this.getDescription()).append("]]></description>");
		//sb.append("<offers><![CDATA[").append(this.getOffers()).append("]]></offers>");
		
		sb.append("</general>");

		//write the offers
		sb.append(this.createOffersSection(this.getOfferList()));

		//write the events
		sb.append(this.createEventsSection(this.getEventIntroText(), this.getEvents()));
		
		//yelp reviews
		try {
			if (this.getYelpReview().equals("Y")) {
				sb.append(new JSONHandler().getFromYelp(this.getPhone()));
			}
		} catch (Exception e) {
			logger.error("Error in getting yelp reviews: " + e);
		}
		
		//save it to a file
		//Properties props = new Properties();
		//props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));
		Properties props = PropertyUtil.load();
		
		String xmlFile = props.getProperty("xmlFilePath") + "/" + this.getUserId() + ".xml";
		BufferedWriter bw = new BufferedWriter(new FileWriter(xmlFile));
		bw.write(sb.toString());
		bw.close();
		this.setXmlFile(xmlFile);		
		
		return sb;
	}

	public ActionErrors validate(CategoryForm mForm, HttpServletRequest request) throws Exception {		
		ActionErrors errors = new ActionErrors();
		
		logger.debug("In Category_3 validate: busName = " + mForm.getCategory().getBusinessName());
		
		if (mForm.getCategory().getEmail() == null || mForm.getCategory().getEmail().length() <= 0) {
			errors.add("error1", new ActionMessage("error.null.email"));
		}
		
		return errors;
	}
}
