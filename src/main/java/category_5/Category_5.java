package category_5;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import user.CategoryBase;
import user.User;
import user.UserDAOManager;
import util.ContentTransform;
import util.ImageFile;
import category_1.Event;
import category_1.ItemEnum;
import category_1.MenuSection;

public class Category_5 extends CategoryBase implements Serializable {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(Category_5.class);
	
	private static final String ENCODING = "ISO-8859-1";
	
	private static final String[] xsltFiles = new String[] {"a.xsl", "o.xsl", "e.xsl"};
	
	private int MAX_SECTION = 2;
	private int MAX_EVENT_ITEMS = 5;
	private int MAX_PHOTOS = 3;
	
	private String description;
	private String eventIntroText; //intro text for the Events page
	private List<MenuSection> sections = new ArrayList<MenuSection>();
	private List<Event> events = new ArrayList<Event>();
	private List<ImageFile> photos = new ArrayList<ImageFile>();
	
	private UserDAOManager dao = new UserDAOManager();
	private String sql = "from Category_5 as p where p.userId = ?";
	
	public Category_5() {
		super();
		this.categoryId = 5;
	}

	public Category_5(Long userId, Integer categoryId) {
		super(userId, categoryId);
		this.categoryName = "category_5";
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

	public Category_5 get(HttpServletRequest request, String mode, Integer type) throws Exception  {
		Category_5 catg = null;
		if (mode.equals("get")) {
			super.getCategory(); //get the category fields
				
			List<Category_5> rList = dao.getDetails(userId, sql);
			if (rList.isEmpty()) {
				catg = new Category_5(this.userId, this.categoryId);
			} else		
				catg= rList.get(0);
			
			//handle the collection
			if (catg.getSections() == null)
				catg.sections = new ArrayList<MenuSection>();
		} else { //adding an extra section
			catg = (Category_5)request.getSession().getAttribute("category");
			
			switch (ItemEnum.valueOf(type)) {	
				case EVENT: //Event
					if (catg.getEvents().size() >= catg.MAX_EVENT_ITEMS)
						catg.MAX_EVENT_ITEMS++;
					break;
				default:
					List<MenuSection> sList;
					sList = deleteEmptySections(catg.getSections());		
					catg.setSections(sList);
					if (catg.getSections().size() >= catg.MAX_SECTION)
						catg.MAX_SECTION++;					
			}
		}
		
		for (int i = catg.getEvents().size(); i < catg.MAX_EVENT_ITEMS; i++) {
			Event event = new Event();
			catg.events.add(event);
		}
		
		for (int i = catg.getPhotos().size(); i < catg.MAX_PHOTOS; i++) {
			ImageFile photo = new ImageFile();
			catg.photos.add(photo);
		}
		
		logger.debug("category_5: before - sections = " + catg.getSections().size());
		for (int i = catg.getSections().size(); i < catg.MAX_SECTION; i++) {
			MenuSection ms = new MenuSection();
			ms.setType(ItemEnum.GENERAL_SECTION.getItem());
			catg.sections.add(ms);
		}
		
		logger.debug("category_5: after - sections = " + catg.getSections().size());

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
	}
	
	public String preview(HttpServletRequest request, String currPage) throws Exception {
		Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));
		
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
		
		//get the xslt files
		try {
			if (this.getXslTransformFiles() == null) {
				this.setXslTransformFiles(dao.getXslFiles(userId).split(","));
			}
		} catch (Exception e) {
			logger.error("Error getting xslt files");
		}
		
		String xslFilePath = props.getProperty("xslFilePath") + "/category_5";
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
		
		sb.append("<general>");
		sb.append("<description><![CDATA[").append(this.getDescription()).append("]]></description>");
		sb.append("</general>");
		
		sb.append("<offers><![CDATA[").append(this.getOffers()).append("]]></offers>");
		
		//Events
		sb.append("<events>");
		
		if (this.getEventIntroText() != null && this.getEventIntroText().length() > 0) {
			sb.append("<event_intro>").append(this.getEventIntroText()).append("</event_intro>");
		}
		
		for (Event ev : this.getEvents()) {
			if (ev.getName() != null && ev.getName().length() > 0) {
				sb.append("<event>");
				sb.append("<name>").append(ev.getName()).append("</name>");
				sb.append("<date>").append(ev.getDate()).append("</date>");
				if (! ev.getHourStart().equals(ev.getHourEnd())) //write out the hours only if it was specified
					sb.append("<hours>").append(ev.getHourStart()).append(" to ")
									.append(ev.getHourEnd()).append("</hours>");
				sb.append("<cost>").append(ev.getCost()).append("</cost>");
				sb.append("<description>").append(ev.getDescription()).append("</description>");
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
}
