package category_1;

//Restaurant
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.util.LabelValueBean;

import sms.US411Handler;
import user.CategoryBase;
import user.Field;
import user.Offer;
import user.SearchAttribute;
import user.User;
import user.UserDAOManager;
import util.ContentTransform;
import util.Geocoder;
import util.ImageFile;
import util.JSONHandler;
import util.PropertyUtil;

public class Category_1 extends CategoryBase implements Serializable {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(Category_1.class);
	
	private static final String[] xsltFiles = new String[] {"a.xsl", "b_b.xsl", "b_l.xsl", "b_d.xsl", "b_de.xsl", "b_c.xsl", "e.xsl"};
	
	private String cuisine;
	private String price;
	private String[] mealsServed;
	private String mealsServedDB; //save in db as a string
	private String description;
	
	private String chef;
	private String dressCode;
	private String[] paymentOptions;
	private String paymentOptionsDB;
	private String whatOthersSay;
	private String yelpReview;
		
	private String barOption;
	private String eventIntroText; //intro text for the Events page
	
	private List<Event> events = new ArrayList<Event>();
	private List<ImageFile> photos = new ArrayList<ImageFile>();
	
	private List<MenuSection> menuSections = new ArrayList<MenuSection>();
	private List<MenuSection> breakfastMenu = new ArrayList<MenuSection>();
	private List<MenuSection> lunchMenu = new ArrayList<MenuSection>();
	private List<MenuSection> dinnerMenu = new ArrayList<MenuSection>();
	private List<MenuSection> dessertMenu = new ArrayList<MenuSection>();
	private List<MenuSection> cocktailMenu = new ArrayList<MenuSection>();
	
	private List<SearchAttribute> searchAttrs = new ArrayList<SearchAttribute>();
	
	private UserDAOManager dao = new UserDAOManager();
	private String sql = "from Category_1 as p where p.userId = ?";
	
	private int MAX_EVENT_ITEMS = 5;
	private int MAX_PHOTOS = 4;
	
	private int MAX_BFASTMENU_SECTION = 3;
	private int MAX_LUNCHMENU_SECTION = 6;
	private int MAX_DINNERMENU_SECTION = 6;
	private int MAX_DESSERTMENU_SECTION = 3;
	private int MAX_COCKTAILMENU_SECTION = 3;
	
	private int MAX_MSEARCH = 1;

	public Category_1() {
		super();
		this.categoryId = 1;
		this.categoryName = "category_1";
	}

	public Category_1(Long userId, Integer categoryId) {
		super(userId, categoryId);
		this.categoryName = "category_1";
	}

	public String getCategoryName() {
		return categoryName;
	}
	
	public String getCuisine() {
		return cuisine;
	}

	public void setCuisine(String cuisine) {
		this.cuisine = cuisine;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String[] getMealsServed() {
		return mealsServed;
	}

	public void setMealsServed(String[] mealsServed) {
		this.mealsServed = mealsServed;
		this.setMealsServedDB();
	}

	//Set the array from a string
	public void setMealsServed() {
		if (this.mealsServedDB != null && this.mealsServedDB.length() > 0) {
			this.mealsServed = mealsServedDB.split(",");
			for (String s : mealsServed)
				logger.debug("mealsServed: " + s);
		}
	}
	
	public String getMealsServedDB() {
		logger.debug("In getMealsServedDB");
		return mealsServedDB;
	}

	public void setMealsServedDB(String mealsServedDB) {
		this.mealsServedDB = mealsServedDB;
		logger.debug("In setMealsServedDB calling setMealsServed");
		this.setMealsServed();
	}

	//convert the array to a string 
	public void setMealsServedDB() {	
		this.mealsServedDB = arrayToString(this.mealsServed);
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public String getChef() {
		return chef;
	}

	public void setChef(String chef) {
		this.chef = chef;
	}

	public String getDressCode() {
		return dressCode;
	}

	public void setDressCode(String dressCode) {
		this.dressCode = dressCode;
	}

	public String[] getPaymentOptions() {
		return paymentOptions;
	}

	public void setPaymentOptions(String[] paymentOptions) {
		this.paymentOptions = paymentOptions;
		this.setPaymentOptionsDB();
	}

	//set from string
	public void setPaymentOptions() {
		if (this.paymentOptionsDB != null && this.paymentOptionsDB.length() > 0)
			this.paymentOptions = paymentOptionsDB.split(",");
	}
	
	public String getPaymentOptionsDB() {
		return paymentOptionsDB;
	}

	public void setPaymentOptionsDB(String paymentOptionsDB) {
		this.paymentOptionsDB = paymentOptionsDB;
		this.setPaymentOptions();
	}
	
	//convert to string
	public void setPaymentOptionsDB() {	
		this.paymentOptionsDB = arrayToString(this.paymentOptions);
	}

	public String getWhatOthersSay() {
		return whatOthersSay;
	}

	public void setWhatOthersSay(String whatOthersSay) {
		this.whatOthersSay = whatOthersSay;
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

	public String getEventIntroText() {
		return eventIntroText;
	}

	public void setEventIntroText(String eventIntroText) {
		this.eventIntroText = eventIntroText;
	}

	public UserDAOManager getDao() {
		return dao;
	}

	public void setDao(UserDAOManager dao) {
		this.dao = dao;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public String getBarOption() {
		return barOption;
	}

	public void setBarOption(String barOption) {
		this.barOption = barOption;
	}

	public List<MenuSection> getMenuSections() {
		return menuSections;
	}

	public void setMenuSections(List<MenuSection> menuSections) {
		this.menuSections = menuSections;
	}

	public List<MenuSection> getLunchMenu() {
		return lunchMenu;
	}

	public void setLunchMenu(List<MenuSection> lunchMenu) {
		this.lunchMenu = lunchMenu;
	}

	public List<MenuSection> getDinnerMenu() {
		return dinnerMenu;
	}

	public void setDinnerMenu(List<MenuSection> dinnerMenu) {
		this.dinnerMenu = dinnerMenu;
	}

	public List<MenuSection> getBreakfastMenu() {
		return breakfastMenu;
	}

	public void setBreakfastMenu(List<MenuSection> breakfastMenu) {
		this.breakfastMenu = breakfastMenu;
	}

	public List<MenuSection> getDessertMenu() {
		return dessertMenu;
	}

	public void setDessertMenu(List<MenuSection> dessertMenu) {
		this.dessertMenu = dessertMenu;
	}

	public List<MenuSection> getCocktailMenu() {
		return cocktailMenu;
	}

	public void setCocktailMenu(List<MenuSection> cocktailMenu) {
		this.cocktailMenu = cocktailMenu;
	}

	public List<SearchAttribute> getSearchAttrs() {
		return searchAttrs;
	}

	public void setSearchAttrs(List<SearchAttribute> searchAttrs) {
		this.searchAttrs = searchAttrs;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<Field> getCategory(Integer categoryId) throws Exception {		
		List<Field> fields = dao.getCategory(categoryId);
		if (! fields.isEmpty())
			return fields;
		
		return null;
	}
	
	public Category_1 get(HttpServletRequest request, String mode, Integer type) throws Exception  {
		logger.debug("Category_1:get");
		Category_1 catg_1 = null;
		
		if (mode.equals("get")) {
			super.getCategory(); //get the category fields
				
			List<Category_1> rList = dao.getDetails(userId, sql);
			if (rList.isEmpty()) {
				catg_1 = new Category_1(this.userId, this.categoryId);
			} else		
				catg_1 = rList.get(0);
			
			//handle the collection
			if (catg_1.getMenuSections() == null)
				catg_1.menuSections = new ArrayList<MenuSection>();
			
			//split the collection based on the itemType
			//need to do this as there is no easy way to map this in hibernate. parent has one pk (profile_id)
			//while child has a composite pk (profile_id, item_type)
			//List<MenuItem> items = new ArrayList<MenuItem>();			
			
			breakfastMenu = new ArrayList<MenuSection>();
			lunchMenu = new ArrayList<MenuSection>();
			dinnerMenu = new ArrayList<MenuSection>();
			dessertMenu = new ArrayList<MenuSection>();
			cocktailMenu = new ArrayList<MenuSection>();
			for (MenuSection ms : catg_1.getMenuSections()) {
				logger.debug("menusection type = " + ms.getType());
				switch(ItemEnum.valueOf(ms.getType())) {
					case BREAKFAST_MENU: 
						breakfastMenu.add(ms);
						break;
					case LUNCH_MENU: 
						lunchMenu.add(ms);
						break;
					case DINNER_MENU:
						dinnerMenu.add(ms);
						break;	
					case DESSERT_MENU: 
						dessertMenu.add(ms);
						break;
					case COCKTAIL_MENU: 
						cocktailMenu.add(ms);
						break;						
					default:
						logger.error("section: " + ms.getType() + " unknown");
				}
			}
			logger.debug("get: lunchMenu size: " + catg_1.MAX_LUNCHMENU_SECTION);
			logger.debug("lunchMenu().size(): " + lunchMenu.size());
			
			catg_1.setBreakfastMenu(breakfastMenu);
			catg_1.setLunchMenu(lunchMenu);
			catg_1.setDinnerMenu(dinnerMenu);
			catg_1.setDessertMenu(dessertMenu);
			catg_1.setCocktailMenu(cocktailMenu);
			
			logger.debug("catg_1.getLunchMenu().size(): " + catg_1.getLunchMenu().size());
			
			if (catg_1.getBreakfastMenu().size() < catg_1.MAX_BFASTMENU_SECTION && catg_1.getBreakfastMenu().size() > 0) 
				catg_1.MAX_BFASTMENU_SECTION = catg_1.getBreakfastMenu().size();
			if (catg_1.getLunchMenu().size() < catg_1.MAX_LUNCHMENU_SECTION && catg_1.getLunchMenu().size() > 0)
				catg_1.MAX_LUNCHMENU_SECTION = catg_1.getLunchMenu().size();
			if (catg_1.getDinnerMenu().size() < catg_1.MAX_DINNERMENU_SECTION && catg_1.getDinnerMenu().size() > 0)
				catg_1.MAX_DINNERMENU_SECTION = catg_1.getDinnerMenu().size();	
			if (catg_1.getDessertMenu().size() < catg_1.MAX_DESSERTMENU_SECTION && catg_1.getDessertMenu().size() > 0)
				catg_1.MAX_DESSERTMENU_SECTION = catg_1.getDessertMenu().size();		
			if (catg_1.getCocktailMenu().size() < catg_1.MAX_COCKTAILMENU_SECTION && catg_1.getCocktailMenu().size() > 0)
				catg_1.MAX_COCKTAILMENU_SECTION = catg_1.getCocktailMenu().size();	
			
			logger.debug("get: lunchMenu size: " + catg_1.MAX_LUNCHMENU_SECTION);
		} else { //adding an extra line/section
			catg_1 = (Category_1)request.getSession().getAttribute("category");						
			List<MenuSection> msList;
			switch (ItemEnum.valueOf(type)) {				
				case EVENT: //Event
					List<Event> evlist = deleteEmptyEvents(catg_1.getEvents());
					catg_1.setEvents(evlist);					
					if (catg_1.getEvents().size() >= catg_1.MAX_EVENT_ITEMS)
						catg_1.MAX_EVENT_ITEMS = catg_1.getEvents().size() + 1;
					break;	
				case BREAKFAST_MENU:
					msList = deleteEmptySections(catg_1.getBreakfastMenu());		
					catg_1.setBreakfastMenu(msList);
					if (catg_1.getBreakfastMenu().size() >= catg_1.MAX_BFASTMENU_SECTION)
						catg_1.MAX_BFASTMENU_SECTION = catg_1.getBreakfastMenu().size() + 1;
					break;					
				case LUNCH_MENU:
					msList = deleteEmptySections(catg_1.getLunchMenu());		
					catg_1.setLunchMenu(msList);
					logger.debug("add: lunchMenu size = " + catg_1.getLunchMenu().size());
					if (catg_1.getLunchMenu().size() >= catg_1.MAX_LUNCHMENU_SECTION)
						catg_1.MAX_LUNCHMENU_SECTION = catg_1.getLunchMenu().size() + 1;
					logger.debug("add: MAX_LUNCHMENU_SECTION = " + catg_1.MAX_LUNCHMENU_SECTION);					
					break;		
				case DINNER_MENU:
					msList = deleteEmptySections(catg_1.getDinnerMenu());		
					catg_1.setDinnerMenu(msList);
					logger.debug("add: size = " + catg_1.getDinnerMenu().size());
					if (catg_1.getDinnerMenu().size() >= catg_1.MAX_DINNERMENU_SECTION)
						catg_1.MAX_DINNERMENU_SECTION = catg_1.getDinnerMenu().size() + 1;
					break;		
				case DESSERT_MENU:
					msList = deleteEmptySections(catg_1.getDessertMenu());		
					catg_1.setDessertMenu(msList);
					if (catg_1.getDessertMenu().size() >= catg_1.MAX_DESSERTMENU_SECTION)
						catg_1.MAX_DESSERTMENU_SECTION = catg_1.getDessertMenu().size() + 1;
					break;	
				case COCKTAIL_MENU:
					msList = deleteEmptySections(catg_1.getCocktailMenu());		
					catg_1.setCocktailMenu(msList);
					if (catg_1.getCocktailMenu().size() >= catg_1.MAX_COCKTAILMENU_SECTION)
						catg_1.MAX_COCKTAILMENU_SECTION = catg_1.getCocktailMenu().size() + 1;
					break;						
				default:
			}
		}			
			
		for (int i = catg_1.getEvents().size(); i < catg_1.MAX_EVENT_ITEMS; i++) {
			Event event = new Event();
			catg_1.events.add(event);
		}
		
		for (int i = 0; i < catg_1.getPhotos().size(); i++) {
			logger.debug("get: photo[i], id = " + catg_1.getPhotos().get(i).getFileName() + ", " + catg_1.getPhotos().get(i).getId());
		}
		
		for (int i = catg_1.getPhotos().size(); i < catg_1.MAX_PHOTOS; i++) {
			ImageFile photo = new ImageFile();
			catg_1.photos.add(photo);
		}
			
		for (int i = catg_1.getBreakfastMenu().size(); i < catg_1.MAX_BFASTMENU_SECTION; i++) {
			MenuSection ms = new MenuSection();
			ms.setType(ItemEnum.BREAKFAST_MENU.getItem());
			catg_1.breakfastMenu.add(ms);
		}
		
		for (int i = catg_1.getLunchMenu().size(); i < catg_1.MAX_LUNCHMENU_SECTION; i++) {
			MenuSection ms = new MenuSection();
			ms.setType(ItemEnum.LUNCH_MENU.getItem());
			catg_1.lunchMenu.add(ms);
			logger.debug("added lunchMenu");
		}
		
		for (int i = catg_1.getDinnerMenu().size(); i < catg_1.MAX_DINNERMENU_SECTION; i++) {
			MenuSection ms = new MenuSection();
			ms.setType(ItemEnum.DINNER_MENU.getItem());
			catg_1.dinnerMenu.add(ms);
		}
		
		for (int i = catg_1.getDessertMenu().size(); i < catg_1.MAX_DESSERTMENU_SECTION; i++) {
			MenuSection ms = new MenuSection();
			ms.setType(ItemEnum.DESSERT_MENU.getItem());
			catg_1.dessertMenu.add(ms);
		}
		
		for (int i = catg_1.getCocktailMenu().size(); i < catg_1.MAX_COCKTAILMENU_SECTION; i++) {
			MenuSection ms = new MenuSection();
			ms.setType(ItemEnum.COCKTAIL_MENU.getItem());
			catg_1.cocktailMenu.add(ms);
		}
		
		for (int i = catg_1.getCocktailMenu().size(); i < catg_1.MAX_MSEARCH; i++) {
			SearchAttribute ms = new SearchAttribute();
			catg_1.searchAttrs.add(ms);
		}
	
		//set the status to E for expired offers
		List<Offer> offers = this.setOfferStatus(catg_1.getOfferList());
		catg_1.setOfferList(offers);
		
		for (int i = catg_1.getOfferList().size(); i < catg_1.MAX_OFFERS; i++) {
			Offer offer = new Offer(); 
			offer.setOfferId(UUID.randomUUID().toString());
			catg_1.offerList.add(offer);
		}
		
		return catg_1;
	}
	
	public String preview(HttpServletRequest request, String currPage) throws Exception {
		//save it first
		save(request);
		
		/*
		Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));
		*/
		
		Properties props = PropertyUtil.load();
		
		logger.debug("preview: profileId, phone = " + this.getProfileId() + ", " + this.getPhone());
		
		//write the static images
		String dpath = props.getProperty("displayPath");
		List<ImageFile> staticImages = new ArrayList<ImageFile>();
		ImageFile ifile = new ImageFile(null, "cplogo", props.getProperty("cpLogoName"), props.getProperty("imgFilePath") + File.separator, "us411 logo");
		ifile.setDisplayPath(dpath + "/" + props.getProperty("cpLogoName"));
		staticImages.add(ifile);

		/*
		//add the tab images
		ifile = new ImageFile(null, "info_tab", "m_tab_info_o.gif", props.getProperty("imgFilePath") + File.separator, "info tab");
		ifile.setDisplayPath(dpath + "/m_tab_info_o.gif");
		staticImages.add(ifile);

		ifile = new ImageFile(null, "specials_tab", "m_tab_specials.gif", props.getProperty("imgFilePath") + File.separator, "specials tab");
		ifile.setDisplayPath(dpath + "/m_tab_specials.gif");
		staticImages.add(ifile);		

		ifile = new ImageFile(null, "events_tab", "m_tab_events.gif", props.getProperty("imgFilePath") + File.separator, "events tab");
		ifile.setDisplayPath(dpath + "/m_tab_events.gif");
		staticImages.add(ifile);
		*/
		
		//this.toXML(session, staticImages);
		
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
			logger.error("Error in getting mapUrl");
			//throw new Exception(e); //continue anyway
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
		
		String xslFilePath = props.getProperty("xslFilePath") + "/category_1";
		return ct.transform(xmlFile, xslFilePath, this.getXslTransformFiles());
	}
	
	public void save(HttpServletRequest request) throws Exception {
		List<MenuSection> msList = new ArrayList<MenuSection>();
		List<Event> evList = new ArrayList<Event>();
		List<ImageFile> photoList = new ArrayList<ImageFile>();
		
		//clear it first
		this.getMenuSections().clear();
		
		//merge all the menuItems
		this.getMenuSections().addAll(this.getBreakfastMenu());
		this.getMenuSections().addAll(this.getLunchMenu());
		this.getMenuSections().addAll(this.getDinnerMenu());
		this.getMenuSections().addAll(this.getDessertMenu());
		this.getMenuSections().addAll(this.getCocktailMenu());
		
		for (MenuSection s : this.getCocktailMenu())
			logger.debug("Cocktail section: item = " + s.getName());
		
		//delete the empties
		msList = deleteEmptySections(this.getMenuSections());
	
		this.setMenuSections(msList);
		logger.debug("save:after size = " + this.getMenuSections().size());
		
		for (int i = 0; i < this.getEvents().size(); i++) {
			Event event = this.getEvents().get(i);
			if (event.getName() != null && event.getName().length() > 0)
				evList.add(event);
		}
		this.setEvents(evList);
		
		/*
		List<Offer> offlist = new ArrayList<Offer>();
		for (Offer offer : this.getOfferList()) {
			if (offer.getName() == null)
				continue;
			offlist.add(offer);
		}

		this.setOfferList(offlist);
		*/
		
		this.setOfferList(this.deleteEmptyOffers(this.getOfferList()));

		//save the uploaded image files - this is now in a utility func in ImageFile
		/*
		   List<ImageFile> imgFiles = this.getPhotos();
		   if (imgFiles != null && imgFiles.size() > 0) {
			   for (ImageFile imgFile : imgFiles) {
				   imgFile.saveFile(this.getUserId().toString());
			   }
		   }
		   
		logger.debug("save:before photos size = " + this.getPhotos().size());
		for (int i = 0; i < this.getPhotos().size(); i++) {
			ImageFile imgFile = this.getPhotos().get(i);
			if (imgFile.getFileName() != null && ! imgFile.getLocation().equals("delete")) {
				photoList.add(imgFile);
			}
		}
		this.setPhotos(photoList);
		*/
		
		this.setPhotos(new ImageFile().saveImageFiles(userId, this.getPhotos()));
		logger.debug("save:after photos size = " + this.getPhotos().size());
		
		logger.debug("userid, catgid = " + this.getUserId() + ", " + this.getCategoryId());
		logger.debug("yelpReview, phone = " + this.getYelpReview() + ", " + this.getPhone());
		
		/*
		//generate the xmlfile
		this.toXML(session);
		
		//transform & save the path
		this.setMwpPath(new ContentTransform().transform(this.getXmlFile(), xsltFiles));
		*/
		
		//this.setMwpPath(this.preview(session));
		this.toXMLSection1(request.getSession()); //save only the section1 info
		
		dao.saveDetails(this);
	}
	
	public void toXML(HttpSession session, List<ImageFile> staticImages) throws Exception {
		toXML(session, staticImages, 300, 320);
	}
	
	public void toXML(HttpSession session, List<ImageFile> staticImages, float width, float height) throws Exception {
		List<LabelValueBean> cuisines = (List<LabelValueBean>)session.getAttribute("cuisines");
		List<LabelValueBean> fonts = (List<LabelValueBean>)session.getAttribute("fonts");
		
		//Properties props = new Properties();
		//props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));
		Properties props = PropertyUtil.load();
		
		User user = (User)session.getAttribute("User");
		
		StringBuffer sb = new StringBuffer();
		sb.append("<profile id=\"").append(this.getUserId()).append("\">");
		
		sb.append("<device_width>").append(width).append("</device_width>");
		sb.append("<device_height>").append(height).append("</device_height>");
		
		//add the map url
		try {
			String mapUrl = new Geocoder().getStaticMapURL(this.getAddress(), width, height);
			sb.append("<map_url><![CDATA[").append(mapUrl).append("]]></map_url>");
		} catch (Exception e) {
			logger.error("Error in getting mapUrl");
			//throw new Exception(e); //continue anyway
		}
		
		logger.debug("toXML:photos size = " + this.getPhotos().size());
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
		
		sb.append("<cuisine>").append(this.getLabel(this.getCuisine(), cuisines)).append("</cuisine>");
		sb.append("<dress>").append(this.getDressCode()).append("</dress>");
		sb.append("<price>").append(this.getPrice()).append("</price>");
		sb.append("<payment_options>").append(this.getPaymentOptionsDB()).append("</payment_options>");
		sb.append("<meals_served>").append(this.getMealsServedDB()).append("</meals_served>");
			
		sb.append("<hours>").append(this.getBusHours()).append("</hours>");
		
		sb.append("<contact_info>");
		sb.append("<phone>").append(this.getPhone()).append("</phone>");
		sb.append("<website>").append(this.getWebsite()).append("</website>");
		sb.append("<location>").append(this.getAddress()).append("</location>");
		sb.append("<email>").append(user.getEmail()).append("</email>");
		sb.append("</contact_info>");
		
		sb.append("<description>").append(this.getDescription()).append("</description>");
		sb.append("</general>");
		
		sb.append("<current>");
		
		//Breakfast
		if (this.getBreakfastMenu() != null && this.getBreakfastMenu().get(0).getName() != null
				&& this.getBreakfastMenu().get(0).getName().length() > 0)
			sb.append(this.createSections("breakfast", this.getBreakfastMenu(), fonts));
		
		//Lunch
		if (this.getLunchMenu() != null && this.getLunchMenu().get(0).getName() != null
				&& this.getLunchMenu().get(0).getName().length() > 0 )
			sb.append(this.createSections("lunch", this.getLunchMenu(), fonts));

		//Dinner
		if (this.getDinnerMenu() != null && this.getDinnerMenu().get(0).getName() != null
				&& this.getDinnerMenu().get(0).getName().length() > 0)
			sb.append(this.createSections("dinner", this.getDinnerMenu(), fonts));
		
		//Dessert
		if (this.getDessertMenu() != null && this.getDessertMenu().get(0).getName() != null
				&& this.getDessertMenu().get(0).getName().length() > 0)
			sb.append(this.createSections("dessert", this.getDessertMenu(), fonts));
		
		//Cocktail
		if (this.getCocktailMenu() != null && this.getCocktailMenu().get(0).getName() != null
				&& this.getCocktailMenu().get(0).getName().length() > 0)
			sb.append(this.createSections("cocktail", this.getCocktailMenu(), fonts));
				
		sb.append("</current>");
		
		sb.append("<events>");
		
		if (this.getEventIntroText() != null && this.getEventIntroText().length() > 0) {
			sb.append("<event_intro><![CDATA[").append(this.getEventIntroText()).append("]]></event_intro>");
		}
		
		logger.debug("toXML: events size = " + this.getEvents().size());
		for (Event ev : this.getEvents()) {
			if (ev.getName() != null && ev.getName().length() > 0) {
				sb.append("<event>");
				sb.append("<name><![CDATA[").append(ev.getName()).append("]]></name>");
				sb.append("<date>").append(ev.getDate()).append("</date>");
				sb.append("<hours>").append(ev.getHourStart()).append(" to ")
									.append(ev.getHourEnd()).append("</hours>");
				sb.append("<cost>").append(ev.getCost()).append("</cost>");
				sb.append("<description><![CDATA[").append(ev.getDescription()).append("]]></description>");
				sb.append("</event>");
			}
		}
		sb.append("</events>");
		
		//yelp reviews
		try {
			if (this.getYelpReview().equals("Y")) {
				sb.append(new JSONHandler().getFromYelp(this.getPhone()));
			}
		} catch (Exception e) {
			logger.error("Error in getting yelp reviews: " + e);
		}
		
		//write the static images
		sb.append(this.writeStaticImages(staticImages));
		
		sb.append("</profile>");
		
		//save it to a file
		String xmlFile = props.getProperty("xmlFilePath") + "/" + this.getUserId() + ".xml";
		BufferedWriter bw = new BufferedWriter(new FileWriter(xmlFile));
		bw.write(sb.toString());
		bw.close();
		this.setXmlFile(xmlFile);
	}
	
	public StringBuffer writeStaticImages(List<ImageFile> staticImages) throws Exception {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<static_images>");
		for (ImageFile ifile : staticImages) {
			sb.append("<").append(ifile.getLocation()).append(">").append(ifile.getDisplayPath())
				.append("</").append(ifile.getLocation()).append(">");
		}
		sb.append("</static_images>");
		
		return sb;
	}
	
	private String arrayToString(String[] sa) {
		StringBuffer sb = new StringBuffer();
		for (String s : sa) 
			if (sb.length() <= 0)
					sb.append(s);
			else 
					sb.append(",").append(s);
		
		return sb.toString();
	}
	
	//delete the empties
	private List<MenuItem> deleteEmpties(List<MenuItem> theList) {
		List<MenuItem> mList = new ArrayList<MenuItem>();
		int size = theList.size();
		logger.debug("deleteEmpties:before size = " + size);
		for (int i = 0; i < size; i++) {
			MenuItem mItem = theList.get(i);
			if (mItem.getName() != null && mItem.getName().length() > 0)
				mList.add(mItem);
		}
		return mList;
	}
	
	/* In CategoryBase now
	private List<MenuSection> deleteEmptySections(List<MenuSection> theList) {
		List<MenuSection> mList = new ArrayList<MenuSection>();
		int size = theList.size();
		logger.debug("deleteEmpties:before size = " + size);
		for (int i = 0; i < size; i++) {
			MenuSection mItem = theList.get(i);
			if (mItem.getName() != null && mItem.getName().length() > 0)
				mList.add(mItem);
		}
		return mList;
	}
	*/
	
	private StringBuffer createSections(String sectionName, List<MenuSection> menuSections, List<LabelValueBean> fonts) throws Exception {
		StringBuffer sb = new StringBuffer();
		
		if (menuSections == null || menuSections.size() <= 0)
			return sb;
		
		sb.append("<").append(sectionName).append(">");
		for (MenuSection ms : menuSections) {
			sb.append("<menu_section>");
			sb.append("<title><![CDATA[").append(ms.getName()).append("]]></title>");
			//sb.append("<itemline_font>").append(this.getLabel(ms.getItemLineFont(), fonts)).append("</itemline_font>");
			//sb.append("<descline_font>").append(this.getLabel(ms.getDescLineFont(), fonts)).append("</descline_font>");
			if (ms.getIntroText() != null && !ms.getIntroText().equals(" "))
				sb.append("<intro_text><![CDATA[").append(ms.getIntroText()).append("]]></intro_text>");

			BufferedReader br = new BufferedReader(new StringReader(ms.getContents()));
			boolean line = true;
			StringBuffer line1 = new StringBuffer();
			StringBuffer line2 = new StringBuffer();
			String s;
			while ((s = br.readLine()) != null) {
				if (s.length() == 0) //skip empty lines
					continue;
				sb.append("<item>");
				if (line) { //item line
					sb.append("<itemline><![CDATA[").append(s).append("]]></itemline>");
				} else { //desc line
					sb.append("<descline><![CDATA[").append(s).append("]]></descline>");
				}
				sb.append("</item>");

				line = !line;
			}
			sb.append("</menu_section>");
		}
		sb.append("</").append(sectionName).append(">");
		
		return sb;
	}
	
	//just treat it as one big cdata section
	private StringBuffer createCocktailSection(String sectionName, List<MenuSection> menuSections, List<LabelValueBean> fonts) throws Exception {
		StringBuffer sb = new StringBuffer();
		
		if (menuSections == null || menuSections.size() <= 0)
			return sb;
		
		sb.append("<").append(sectionName).append(">");
		for (MenuSection ms : menuSections) {
			sb.append("<menu_section>");
			sb.append("<title><![CDATA[").append(ms.getName()).append("]]></title>");
			if (ms.getIntroText() != null && !ms.getIntroText().equals(" "))
				sb.append("<intro_text><![CDATA[").append(ms.getIntroText()).append("]]></intro_text>");

			sb.append("<content><![CDATA[").append(ms.getContents()).append("]]></content>");
			sb.append("</menu_section>");
		}
		sb.append("</").append(sectionName).append(">");
		
		return sb;
	}	
	
	//write out all the non-image part that does not need to be resized based on ua
	public StringBuffer toXMLSection1(HttpSession session) throws Exception {
		List<LabelValueBean> cuisines = (List<LabelValueBean>)session.getAttribute("cuisines");
		List<LabelValueBean> fonts = (List<LabelValueBean>)session.getAttribute("fonts");
		
		//Properties props = new Properties();
		//props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));
		Properties props = PropertyUtil.load();
		
		User user = (User)session.getAttribute("User");
		
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='ISO-8859-1'?>");
		
		sb.append("<profile id=\"").append(this.getUserId()).append("\"")
					.append(" keyword=\"").append(user.getKeyword())
					.append("\">");
		
		/*
		//add the map url - moved to preview to handle map size based on device - see ContentTransform.toXML (called from getP.jsp)
		try {
			String mapUrl = new Geocoder().getStaticMapURL(this.getAddressForDisplay());
			sb.append("<map_url><![CDATA[").append(mapUrl).append("]]></map_url>");
		} catch (Exception e) {
			logger.error("Error in getting mapUrl");
			//throw new Exception(e); //continue anyway
		}
		*/
		
		logger.debug("toXML:photos size = " + this.getPhotos().size());
		
		sb.append(super.toXMLSection1(session));
		
		sb.append("<general>");
		
		sb.append("<businessName><![CDATA[").append(this.getBusinessName()).append("]]></businessName>");

		if (this.getChef() != null && this.getChef().length() > 0)
			sb.append("<chef><![CDATA[").append(this.getChef()).append("]]></chef>");
		
		sb.append("<cuisine>").append(this.getLabel(this.getCuisine(), cuisines)).append("</cuisine>");
		sb.append("<dress>").append(this.getDressCode()).append("</dress>");
		sb.append("<price>").append(this.getPrice()).append("</price>");
		sb.append("<payment_options>").append(this.getPaymentOptionsDB()).append("</payment_options>");
		sb.append("<meals_served>").append(this.getMealsServedDB()).append("</meals_served>");
			
		sb.append("<hours>").append(this.getBusHours()).append("</hours>");
		
		sb.append("<contact_info>");
		sb.append("<location>").append(this.getAddress()).append("</location>");
		sb.append("<location_city>").append(this.getCity()).append("</location_city>");
		sb.append("<location_state>").append(this.getState()).append("</location_state>");
		sb.append("<location_zip>").append(this.getZip()).append("</location_zip>");
		
		sb.append("<phone>").append(this.getPhone()).append("</phone>");
		sb.append("<website>").append(this.getWebsite()).append("</website>");
		sb.append("<email>").append(this.getEmail()).append("</email>");
		sb.append("</contact_info>");
		
		sb.append("<description><![CDATA[").append(this.getDescription()).append("]]></description>");
		
		sb.append("</general>");
		
		//write the offers
		sb.append(this.createOffersSection(this.getOfferList()));
		
		sb.append("<current>");
		
		//Breakfast
		if (this.getBreakfastMenu() != null && this.getBreakfastMenu().get(0).getName() != null
				&& this.getBreakfastMenu().get(0).getName().length() > 0)
			sb.append(this.createSections("breakfast", this.getBreakfastMenu(), fonts));
		
		//Lunch
		if (this.getLunchMenu() != null && this.getLunchMenu().get(0).getName() != null
				&& this.getLunchMenu().get(0).getName().length() > 0 )
			sb.append(this.createSections("lunch", this.getLunchMenu(), fonts));

		//Dinner
		if (this.getDinnerMenu() != null && this.getDinnerMenu().get(0).getName() != null
				&& this.getDinnerMenu().get(0).getName().length() > 0)
			sb.append(this.createSections("dinner", this.getDinnerMenu(), fonts));
		
		//Dessert
		if (this.getDessertMenu() != null && this.getDessertMenu().get(0).getName() != null
				&& this.getDessertMenu().get(0).getName().length() > 0)
			sb.append(this.createSections("dessert", this.getDessertMenu(), fonts));
		
		//Cocktail
		if (this.getCocktailMenu() != null && this.getCocktailMenu().get(0).getName() != null
				&& this.getCocktailMenu().get(0).getName().length() > 0)
			sb.append(this.createCocktailSection("cocktail", this.getCocktailMenu(), fonts));
				
		sb.append("</current>");
		
		sb.append("<events>");
		
		if (this.getEventIntroText() != null && this.getEventIntroText().length() > 0) {
			sb.append("<event_intro><![CDATA[").append(this.getEventIntroText()).append("]]></event_intro>");
		}
		
		logger.debug("toXML: events size = " + this.getEvents().size());
		for (Event ev : this.getEvents()) {
			if (ev.getName() != null && ev.getName().length() > 0) {
				sb.append("<event>");
				sb.append("<name><![CDATA[").append(ev.getName()).append("]]></name>");
				sb.append("<date>").append(ev.getDate()).append("</date>");
				if (ev.getHourStart() != null && ! ev.getHourStart().equals(" ")) {
					sb.append("<hours>").append(ev.getHourStart()).append(" to ")
									.append(ev.getHourEnd()).append("</hours>");
				}
				sb.append("<cost>").append(ev.getCost()).append("</cost>");
				sb.append("<description><![CDATA[").append(ev.getDescription()).append("]]></description>");
				sb.append("</event>");
			}
		}
		sb.append("</events>");
		
		//yelp reviews
		try {
			if (this.getYelpReview().equals("Y")) {
				sb.append(new JSONHandler().getFromYelp(this.getPhone()));
			}
		} catch (Exception e) {
			logger.error("Error in getting yelp reviews: " + e);
		}
		
		//Don't write this out yet as section2 needs to be appended
		//sb.append("</profile>");
		
		//save it to a file
		String xmlFile = props.getProperty("xmlFilePath") + "/" + this.getUserId() + ".xml";
		BufferedWriter bw = new BufferedWriter(new FileWriter(xmlFile));
		bw.write(sb.toString());
		bw.close();
		this.setXmlFile(xmlFile);		
		
		return sb;
	}
	
	public void sendMessage(String message, String infile, String keyword) throws Exception {
		logger.debug("sendMessage of category_1");
	    US411Handler smsh = new US411Handler(null, "US");
	    smsh.sendBulkSMS(message, "US411", "0", infile, userId.toString(), keyword);
	}
	
	public void init(HttpSession session) throws Exception {
		//get the list of cuisines
		List<LabelValueBean> cuisines = new UserDAOManager().getCuisine();
		session.setAttribute("cuisines", cuisines);
		
		//get the list of hours
		List<LabelValueBean> hours = new UserDAOManager().getHours();
		session.setAttribute("hours", hours);
		
		//fonts
		List<LabelValueBean> fonts = new UserDAOManager().getFonts();
		session.setAttribute("fonts", fonts);
	}
	
	/*
	//write out all the image part that needs to be resized based on ua
	public StringBuffer toXMLSection2(HttpSession session, List<ImageFile> staticImages, float width, float height) throws Exception {
		Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("<device_width>").append(width).append("</device_width>");
		sb.append("<device_height>").append(height).append("</device_height>");
		
		logger.debug("toXML:photos size = " + this.getPhotos().size());
		boolean gotPic = false;
		for (ImageFile ifile : this.getPhotos()) {
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
		 
		//write the static images
		sb.append(this.writeStaticImages(staticImages));

		return sb;
	}
	*/
	
	/*
	//delete the empties
	private List<K> deleteEmpties(List<K> theList) {
		List<K> mList = new ArrayList<K>();
		int size = theList.size();
		logger.debug("deleteEmpties:before size = " + size);
		for (int i = 0; i < size; i++) {
			K mItem = theList.get(i);
			String name = null;
			if (mItem instanceof MenuSection)
				name = ((MenuSection)mItem).getName();
			else if (mItem instanceof MenuItem)
				name = ((MenuItem)mItem).getName();
			if (name != null && name.length() > 0)
				mList.add(mItem);
		}
		return mList;
	}
	*/
}
