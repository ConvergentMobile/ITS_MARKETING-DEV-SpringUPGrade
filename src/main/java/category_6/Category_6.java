package category_6;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jms.JMSProducer;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import sms.US411Handler;
import user.Campaign;
import user.CategoryBase;
import user.User;
import user.UserDAOManager;
import util.ContentTransform;
import util.ImageFile;
import category_1.Event;
import category_1.ItemEnum;
import category_1.MenuSection;

public class Category_6 extends CategoryBase implements Serializable {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(Category_6.class);
	
	private static final String ENCODING = "ISO-8859-1";
	
	private static final String[] xsltFiles_0 = new String[] {"0.xsl"}; //this is for the wrapper
	
	private int MAX_SECTION = 2;
	private int MAX_EVENT_ITEMS = 5;
	private int MAX_PHOTOS = 10;
	
	private String description;
	private String eventIntroText; //intro text for the Events page
	private List<MenuSection> sections = new ArrayList<MenuSection>();
	private List<Event> events = new ArrayList<Event>();
	protected List<ImageFile> photos = new ArrayList<ImageFile>();
	
	protected UserDAOManager dao = new UserDAOManager();
	private String sql = "from Category_6 as p where p.userId = ?";
	protected Properties props = new Properties();
	
	protected Integer subCategoryId = null;

	public Category_6() {
		super();
		this.categoryId = 6;
		this.categoryName = "category_6";
		try {
			props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Category_6: " + e);
		}
	}

	public Category_6(Long userId, Integer categoryId) {
		super(userId, categoryId);
		this.categoryName = "category_6";
		try {
			props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Category_6: " + e);
		}
	}

	public String getCategoryName() {
		return categoryName;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEventIntroText() {
		return eventIntroText;
	}

	public void setEventIntroText(String eventIntroText) {
		this.eventIntroText = eventIntroText;
	}

	public List<MenuSection> getSections() {
		return sections;
	}

	public void setSections(List<MenuSection> sections) {
		this.sections = sections;
	}
	
	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public List<ImageFile> getPhotos() {
		return photos;
	}

	public void setPhotos(List<ImageFile> photos) {
		this.photos = photos;
	}

	public Category_6 get(HttpServletRequest request, String mode, Integer type) throws Exception  {
		Category_6 catg = null;
		if (mode.equals("get")) {
			super.getCategory(); //get the category fields
				
			List<Category_6> rList = dao.getDetails(userId, sql);
			if (rList.isEmpty()) {
				catg = new Category_6(this.userId, this.categoryId);
			} else		
				catg= rList.get(0);
			
			//handle the collection
			if (catg.getSections() == null)
				catg.sections = new ArrayList<MenuSection>();
		} else { //adding an extra section
			catg = (Category_6)request.getSession().getAttribute("category");
			
			switch (ItemEnum.valueOf(type)) {	
				case EVENT: //Event
					List<Event> evlist = deleteEmptyEvents(catg.getEvents());
					catg.setEvents(evlist);					
					logger.debug("ev & max sizes: " + catg.getEvents().size() + ", " + catg.MAX_EVENT_ITEMS);
					if (catg.getEvents().size() >= catg.MAX_EVENT_ITEMS)
						catg.MAX_EVENT_ITEMS = catg.getEvents().size() + 1;
					break;
				default:
					List<MenuSection> sList;
					sList = deleteEmptySections(catg.getSections());		
					catg.setSections(sList);
					if (catg.getSections().size() >= catg.MAX_SECTION)
						catg.MAX_SECTION = catg.MAX_SECTION + 1;					
			}
		}
		
		for (int i = catg.getEvents().size(); i < catg.MAX_EVENT_ITEMS; i++) {
			Event event = new Event();
			catg.events.add(event);
		}
		
		for (int i = catg.getPhotos().size(); i < MAX_PHOTOS; i++) {
			ImageFile photo = new ImageFile();
			catg.photos.add(photo);
		}
		
		logger.debug("category_6: before - sections = " + catg.getSections().size());
		for (int i = catg.getSections().size(); i < catg.MAX_SECTION; i++) {
			MenuSection ms = new MenuSection();
			ms.setType(ItemEnum.GENERAL_SECTION.getItem());
			catg.sections.add(ms);
		}
		
		logger.debug("category_6: after - sections = " + catg.getSections().size());

		return catg;
	}

	public void save(HttpServletRequest request) throws Exception {
		List<MenuSection> msList = new ArrayList<MenuSection>();
		List<Event> evList = new ArrayList<Event>();
			
		//delete the empties
		msList = deleteEmptySections(this.getSections());
	
		this.setSections(msList);
		logger.debug("save:after size = " + this.getSections().size());
		
		for (int i = 0; i < this.getEvents().size(); i++) {
			Event event = this.getEvents().get(i);
			if (event.getName() != null && event.getName().length() > 0)
				evList.add(event);
		}
		this.setEvents(evList);
		
		this.setPhotos(new ImageFile().saveImageFiles(userId, this.getPhotos()));
		
		this.toXMLSection1(request.getSession()); //save only the section1 info
		
		dao.saveDetails(this);
		//generate the preview
		this.preview(request);
	}
	
	public String preview(HttpServletRequest request) throws Exception {
		//write the static images
		String dpath = props.getProperty("displayPath");
		List<ImageFile> staticImages = new ArrayList<ImageFile>();
		ImageFile ifile = new ImageFile(null, "cplogo", props.getProperty("cpLogoName"), props.getProperty("imgFilePath") + File.separator, "us411 logo");
		ifile.setDisplayPath(dpath + "/" + props.getProperty("cpLogoName"));
		staticImages.add(ifile);
		
		ContentTransform ct = new ContentTransform();
		StringBuffer sb1 = this.toXMLSection1(request.getSession());
		
		StringBuffer sb2 = ct.toXMLImages(this.getPhotos(), staticImages, 300, 320);
		
		//create a new file so as not to change the xmlFile
		String xmlFile = props.getProperty("xmlFilePath") + "/" + this.getUserId() + "_final.xml";
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFile), ENCODING));
		bw.write(sb1.toString());
		bw.write(sb2.toString());
		
		//write this out to complete the file
		bw.write("</profile>");
		bw.close();
		
		String xslFilePath = props.getProperty("xslFilePath") + "/category_6";
		
		//return ct.transform(xmlFile, xslFilePath, xsltFiles);
		//get the xslt files
		try {
			if (this.getXslTransformFiles() == null) {
				this.setXslTransformFiles(dao.getXslFiles(userId).split(","));
			}
		} catch (Exception e) {
			logger.error("Error getting xslt files");
		}
		return ct.transform(xmlFile, xslFilePath, this.getXslTransformFiles());
	}
	
	public StringBuffer toXMLSection1(HttpSession session) throws Exception {	
		Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));
		
		User user = (User)session.getAttribute("User");
		
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='ISO-8859-1'?>");
		sb.append("<profile id=\"").append(this.getUserId()).append("\"")
		.append(" keyword=\"").append(user.getKeyword())
		.append("\">");		
		
		sb.append("<contact_info>");
		sb.append("<location>").append(this.getAddressForDisplay()).append("</location>");
		sb.append("<phone>").append(this.getPhone()).append("</phone>");
		sb.append("<website>").append(this.getWebsite()).append("</website>");
		sb.append("<email>").append(this.getEmail()).append("</email>");
		sb.append("</contact_info>");
		
		//sb.append("<general>");
		sb.append("<description><![CDATA[").append(this.getDescription()).append("]]></description>");
		//sb.append("</general>");
		
		sb.append("<offers><![CDATA[").append(this.getOffers()).append("]]></offers>");
		
		//Events
		sb.append("<events>");
		
		if (this.getEventIntroText() != null && this.getEventIntroText().length() > 0) {
			sb.append("<event_intro><![CDATA[").append(this.getEventIntroText()).append("]]></event_intro>");
		}
		
		for (Event ev : this.getEvents()) {
			if (ev.getName() != null && ev.getName().length() > 0) {
				sb.append("<event>");
				sb.append("<name><![CDATA[").append(ev.getName()).append("]]></name>");
				sb.append("<date>").append(ev.getDate()).append("</date>");
				if (! ev.getHourStart().equals(ev.getHourEnd())) //write out the hours only if it was specified
					sb.append("<hours>").append(ev.getHourStart()).append(" to ")
									.append(ev.getHourEnd()).append("</hours>");
				sb.append("<cost>").append(ev.getCost()).append("</cost>");
				sb.append("<description><![CDATA[").append(ev.getDescription()).append("]]></description>");
				sb.append("</event>");
			}
		}
		sb.append("</events>");
		
		//write out the sections
		sb.append(this.createSections());
		
		//save it to a file
		String xmlFile = props.getProperty("xmlFilePath") + "/" + this.getUserId() + ".xml";
		//BufferedWriter bw = new BufferedWriter(new FileWriter(xmlFile));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFile), ENCODING));
		bw.write(sb.toString());
		bw.close();
		this.setXmlFile(xmlFile);		
		
		return sb;
	}
	
	private StringBuffer createSections() throws Exception {
		StringBuffer sb = new StringBuffer();
		
		if (sections == null || sections.size() <= 0)
			return sb;
		
		sb.append("<").append("sections").append(">");
		for (MenuSection ms : sections) {
			sb.append("<section>");
			sb.append("<title><![CDATA[").append(ms.getName()).append("]]></title>");
			
			if (ms.getIntroText() != null && !ms.getIntroText().equals(" ") && ms.getIntroText().length() > 0)
				sb.append("<intro_text><![CDATA[").append(ms.getIntroText()).append("]]></intro_text>");

			sb.append("<section_content><![CDATA[").append(ms.getContents()).append("]]></section_content>");
			
			sb.append("</section>");
		}
		sb.append("</").append("sections").append(">");
		
		return sb;
	}
	
	public void sendMessage(Campaign campaign, String keyword, HttpServletRequest request) throws Exception {
		String innerUrl = preview(request);
		String xmlFile = props.getProperty("xmlFilePath") + "/" + this.getUserId() + "_final.xml";
		String xslFilePath = props.getProperty("xslFilePath") + "/category_6";
		
		String message = campaign.getMessageText();
		
		//add the wrapper so that the mwp is accessible only from a mobile device
		//do this only here so that preview is still available from the desktop
		ContentTransform ct = new ContentTransform();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("lurl", ct.getTinyUrl(innerUrl));	
		String outfile = ct.transform(xmlFile, xslFilePath, xsltFiles_0, ".jsp", params); //xmlFile is used only to get the userId and match /profile
		outfile = ct.getTinyUrl(outfile);
		message += System.getProperty("line.separator") + outfile;
		logger.debug("sendMessage: message = " + message);
	    US411Handler smsh = new US411Handler(null, "US");
	    List<String>pNums = dao.getTargetList(userId);
	   
	    //smsh.sendBulkSMS(message, "US411", "0", pNums.toArray(), this.getUserId().toString(), null, keyword);
	    
	    campaign.setMessageText(message);
	    ApplicationContext  ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
	    JMSProducer producer = (JMSProducer)ctx.getBean("producer");
	    producer.sendMessage(campaign, pNums.toArray());
	}
}
