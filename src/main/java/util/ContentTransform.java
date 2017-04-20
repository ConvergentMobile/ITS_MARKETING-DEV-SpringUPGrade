package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import mdp_common.DBStuff;
import mdp_common.XMLTranslationUtility;
import mdp_common.XMLUtil;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;
import org.apache.struts.util.LabelValueBean;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import user.CategoryBase;
import user.User;
import user.UserDAOManager;

import category_1.Category_1;

import common.HibernateUtil;

public class ContentTransform implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ContentTransform.class);
	
	private static final String XML_FILE_PATH = "C:/apache-tomcat-5.5.27/webapps/outfiles/xml/us411/";
	private static final String XSLT_PATH = "c:/workspace/us411/xslt/category_1";
	private static String OUT_DIR = "C:/apache-tomcat-5.5.27/webapps/outfiles/xhtml/us411/";
	private Properties props;
	private String userAgent;
	private String mobilePhone;
	
	public ContentTransform() {
		this(null);
	}
	
	//used when a user texts in a keyword
	public ContentTransform(String mobilePhone) {
		this.mobilePhone = mobilePhone;
		try {
			props = PropertyUtil.load();
		} catch (Exception e) {
			logger.error("Error reading properties: " + e);
		}
	}
	
	//added displayPath arg when called from mwp
	//type indicates static images or not if null
	public List<ImageFile> resizeImages(Long userId, String userAgent, List<ImageFile> images, String type, String displayPath) throws Exception {
		List<ImageFile> resizedImages = new ArrayList<ImageFile>();
		
		String dpath = displayPath;
		if (dpath == null) {
			dpath = props.getProperty("displayPath") + "/";
		}
		
		if (type == null)
			dpath += userId + "/";
		
		logger.debug("num pic files: " + images.size());
		for (ImageFile ifile : images) {
			 if (ifile.getFileName() == null)
				 continue;
			logger.debug("Processing file: " + ifile.getFileName());
			String rfp = ifile.resize(userAgent);
			logger.debug("resized file: " + rfp);
			String fileName = rfp.substring(rfp.lastIndexOf(File.separator) + 1);
			ifile.setDisplayPath(dpath + fileName);			
			resizedImages.add(ifile);
		}
		
		return resizedImages;
	}
	
	public List<ImageFile> resizeImages(Long userId, String userAgent, List<ImageFile> images, String type) throws Exception {
			return this.resizeImages(userId, userAgent, images, type, null);
	}
	
	//this is called when a user texts in a keyword and the ua is not a smartphone
	public String toXML_bak(Long userId, HttpServletRequest request) {
		String sql = "select u.category_id, u.email_id, p.xml_file from user u, profile p where u.user_id = p.user_id and u.user_id = ?";
		DBStuff dbs = null;	
		int categoryId = 0;
		String email = null;
		String mwpPath = null;
		String xmlFile = null;
		this.userAgent = request.getHeader("user-agent");
		
		try {
			dbs = new DBStuff("US");
			ResultSet rs = dbs.getFromDB(sql, new Object[] {userId});
			while (rs.next()) {
				categoryId = rs.getInt("category_id");
				email = rs.getString("email_id");
				xmlFile = rs.getString("xml_file");
			}
			
			logger.debug("categoryId: " + categoryId);
			
			User user = new User();
			user.setEmail(email);
			request.getSession().setAttribute("User", user);
			
			List<ImageFile> staticImages = new ArrayList<ImageFile>();
			staticImages.add(new ImageFile(null, "cplogo", "us411_logo.gif", props.getProperty("imgFilePath") + File.separator, "us411 logo"));

			switch(categoryId) {
				case 1:
					Category_1 catg	= new Category_1();
					catg.setUserId(userId);
					catg = catg.get(request, "get", null);	
					List<ImageFile> rImages = this.resizeImages(userId, userAgent, catg.getPhotos(), null);
					catg.setPhotos(rImages);
					
					List<ImageFile> sImages = this.resizeImages(userId, userAgent, staticImages, "S");
					
					//get the list of cuisines
					List<LabelValueBean> cuisines = new UserDAOManager().getCuisine();
					request.getSession().setAttribute("cuisines", cuisines);

					//fonts
					List<LabelValueBean> fonts = new UserDAOManager().getFonts();
					request.getSession().setAttribute("fonts", fonts);
					
					//get the device width & height form the us411 logo
					catg.toXML(request.getSession(), sImages, staticImages.get(0).getDeviceWidth(), staticImages.get(0).getDeviceHeight());
					logger.debug("xmlFile: " + catg.getXmlFile());
					String xslFilePath = props.getProperty("xslFilePath") + "/category_1";
					String[] xsltFiles = new String[] {"a.xsl", "b_l.xsl", "b_d.xsl", "e.xsl"};
					mwpPath = this.transform(catg.getXmlFile(), xslFilePath, xsltFiles);
					break;
				default:
					logger.error("populateForm: Unrecognized categoryId: " + categoryId);
					break;
			}
		} catch (Exception e) {
			logger.error("toXML error - " + e);
			e.printStackTrace();
		} finally {
			dbs.close();
		}
		return mwpPath;
	}
	
	//this is called when a user texts in a keyword
	public String toXML(Long userId, HttpServletRequest request) {
		String sql = "select u.category_id, u.sub_category_id, u.email_id, p.xml_file, i.tab_location, i.filename,"
			+ " i.filepath, i.description, i.title, x.filenames, i.displaypath," 
			+ " p.business_address, p.business_city, p.business_state, p.zip"
			+ " from xsl_files x, user u, profile p"
			+ " left join image_files i on p.profile_id = i.profile_id"
			+ " where u.user_id = p.user_id and u.user_id = ?"
			+ " and u.category_id  = x.category_id"
			+ " and ifnull(u.sub_category_id,0)  = ifnull(x.sub_category_id,0)";
		
		DBStuff dbs = null;	
		int categoryId = 0;
		String email = null;
		String mwpPath = null;
		String xmlFile = null;
		List<ImageFile> images = new ArrayList<ImageFile>();
		this.userAgent = request.getHeader("user-agent");
		String address = null;
		String[] xsltFiles = null;
		
		try {
			dbs = new DBStuff("US");
			ResultSet rs = dbs.getFromDB(sql, new Object[] {userId});
			int i = 0;
			String dpath = null;
			while (rs.next()) {
				categoryId = rs.getInt("category_id");
				email = rs.getString("email_id");
				xmlFile = rs.getString("xml_file");
				xsltFiles = rs.getString("filenames").split(",");
				images.add(new ImageFile(null, rs.getString("tab_location"), rs.getString("filename"), 
						rs.getString("filepath"), rs.getString("description"), rs.getString("title")));
				if (i == 0) {
					i++;
					address = getAddressForDisplay(rs.getString("business_address"), rs.getString("business_city"),
							rs.getString("business_state"), rs.getString("zip"));
					String stmp = rs.getString("displaypath");
					if (stmp != null && stmp.length() > 0)
						dpath = stmp.substring(0, stmp.indexOf("/" + userId + "/") + 1);
				}
			}
			
			logger.debug("categoryId: " + categoryId);
			
			List<ImageFile> staticImages = new ArrayList<ImageFile>();
			staticImages.add(new ImageFile(null, "cplogo", props.getProperty("cpLogoName"), props.getProperty("imgFilePath") + File.separator, "us411 logo"));
			/*
			staticImages.add(new ImageFile(null, "info_tab", "m_tab_info_o.gif", props.getProperty("imgFilePath") + File.separator, "info tab"));
			staticImages.add(new ImageFile(null, "specials_tab", "m_tab_specials.gif", props.getProperty("imgFilePath") + File.separator, "specials tab"));
			staticImages.add(new ImageFile(null, "events_tab", "m_tab_events.gif", props.getProperty("imgFilePath") + File.separator, "events tab"));
			*/
			
			List<ImageFile> rImages = this.resizeImages(userId, userAgent, images, null, dpath);
			List<ImageFile> sImages = this.resizeImages(userId, userAgent, staticImages, "S");
			float d_width = sImages.get(0).getDeviceWidth();
			float d_height = sImages.get(0).getDeviceHeight();
			StringBuffer sb = toXMLImages(rImages, sImages, d_width, d_height);
			
			//copy the xmlFile to another one so as not to trample it
			if (xmlFile ==  null) {
				logger.error("Null xmlFile");
				return null;
			}
			
			InputStream in = new FileInputStream(xmlFile); 
			String xmlFileFinal = xmlFile.substring(0, xmlFile.lastIndexOf(".")) + "_final.xml";
			OutputStream out = new FileOutputStream(xmlFileFinal); 
			byte[] buf = new byte[1024]; 
			int len; 
			while ((len = in.read(buf)) > 0) { 
				out.write(buf, 0, len); 
			} 
			in.close(); 
			out.close(); 
			
			//add the map url based on the device
			try {
				String mapUrl = new Geocoder().getStaticMapURL(address, d_width, d_height);
				sb.append("<map_url><![CDATA[").append(mapUrl).append("]]></map_url>");
			} catch (Exception e) {
				logger.error("Error in getting mapUrl");
				e.printStackTrace();
				//throw new Exception(e); //continue anyway
			}
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(xmlFileFinal, true));
			bw.write(sb.toString());
			
			bw.write("</profile>");
			bw.close();
			
			String xslFilePath = props.getProperty("xslFilePath") + "/category_" + categoryId;
			logger.debug("xmlFileFinal: " + xmlFileFinal);
			mwpPath = this.transform(xmlFileFinal, xslFilePath, xsltFiles);
			
			/*
			switch(categoryId) {
				case 1:				
					//get the device width & height form the us411 logo
					logger.debug("xmlFileFinal: " + xmlFileFinal);
					xslFilePath = props.getProperty("xslFilePath") + "/category_1";
					//xsltFiles = new String[] {"a.xsl", "b_l.xsl", "b_d.xsl", "e.xsl"};
					mwpPath = this.transform(xmlFileFinal, xslFilePath, xsltFiles);
					break;
				case 3:				
					//get the device width & height form the us411 logo
					logger.debug("xmlFileFinal: " + xmlFileFinal);
					xslFilePath = props.getProperty("xslFilePath") + "/category_3";
					//xsltFiles = new String[] {"a.xsl"};
					mwpPath = this.transform(xmlFileFinal, xslFilePath, xsltFiles);
					break;		
				case 4:				
					//get the device width & height form the us411 logo
					logger.debug("xmlFileFinal: " + xmlFileFinal);
					xslFilePath = props.getProperty("xslFilePath") + "/category_4";
					//xsltFiles = new String[] {"a.xsl"};
					mwpPath = this.transform(xmlFileFinal, xslFilePath, xsltFiles);
					break;		
				case 5:				
					//get the device width & height form the us411 logo
					logger.debug("xmlFileFinal: " + xmlFileFinal);
					xslFilePath = props.getProperty("xslFilePath") + "/category_5";
					//xsltFiles = new String[] {"a.xsl", "o.xsl", "e.xsl"};
					mwpPath = this.transform(xmlFileFinal, xslFilePath, xsltFiles);
					break;	
				case 6:				
					//get the device width & height form the us411 logo
					logger.debug("xmlFileFinal: " + xmlFileFinal);
					xslFilePath = props.getProperty("xslFilePath") + "/category_6";
					//xsltFiles = new String[] {"a.xsl", "e.xsl"};
					mwpPath = this.transform(xmlFileFinal, xslFilePath, xsltFiles);
					break;					
				default:
					logger.error("toXML: Unrecognized categoryId: " + categoryId);
					break;
			}
			*/
		} catch (Exception e) {
			logger.error("toXML error - " + e);
			e.printStackTrace();
		} finally {
			dbs.close();
		}
		return mwpPath;
	}
	
	//write out all the image part 
	public StringBuffer toXMLImages(List<ImageFile> images, List<ImageFile> staticImages, float width, float height) throws Exception {
		Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));
		
		StringBuffer sb = new StringBuffer();
		StringBuffer sblogo = new StringBuffer(); //use a separate sb for logo as it should not be inside <pictures>
		
		sb.append("<device_width>").append(width).append("</device_width>");
		sb.append("<device_height>").append(height).append("</device_height>");
		
		if (images != null) {
			logger.debug("toXML:images size = " + images.size());

			boolean gotPic = false;
			for (ImageFile ifile : images) {
				if (ifile.getFileName() != null) {
					logger.debug("ifile displayPath, location: " + ifile.getDisplayPath() + ", " + ifile.getLocation());
					if (ifile.getLocation().equals("logo")) {
						sblogo.append("<logo>").append(ifile.getDisplayPath()).append("</logo>");
					} else {
						if (! gotPic) {
							sb.append("<pictures>");
							gotPic = true;
						} 
						sb.append("<picture>").append("<pic_file>").append(ifile.getDisplayPath()).append("</pic_file>");
						if (ifile.getTitle() != null)
							sb.append("<pic_title>").append(ifile.getTitle()).append("</pic_title>");
						
						sb.append("</picture>");
					}
				}
			}
			 if (gotPic)
				 sb.append("</pictures>");
			}
			if (sblogo.length() > 0)
				sb.append(sblogo);
		 
		//write the static images
		if (staticImages != null) {
			sb.append("<static_images>");
			for (ImageFile ifile : staticImages) {
				sb.append("<").append(ifile.getLocation()).append(">").append(ifile.getDisplayPath())
					.append("</").append(ifile.getLocation()).append(">");
			}
			sb.append("</static_images>");
		}

		return sb;
	}
	
	public String transform(String xmlfile, String xsltPath, String[] xsltFiles) {
		return this.transform(xmlfile, xsltPath, xsltFiles, ".html", null);
	}

	public String transform(String xmlfile, String xsltPath, String[] xsltFiles, Map<String, Object> params) {
		return this.transform(xmlfile, xsltPath, xsltFiles, ".html", params);
	}

	public String transform(String xmlfile, String xsltPath, String[] xsltFiles, String fileExt) {
		return this.transform(xmlfile, xsltPath, xsltFiles, fileExt, null);
	}
	
	public String transform(String xmlfile, String xsltPath, String[] xsltFiles, String fileExt, Map<String, Object> params) {
		logger.debug("xmlfile: " + xmlfile);
		
		if (xsltFiles == null) {
			logger.error("No xslt files");
			return null;
		}
		
		XMLTranslationUtility xmltUtil = new XMLTranslationUtility();
		
		XMLUtil xmlUtil = new XMLUtil(xmlfile);
		NodeList nl = xmlUtil.getDoc().getElementsByTagName("profile");
		Element el = (Element) nl.item(0);
		String adId = el.getAttribute("id");
		String keyword = el.getAttribute("keyword");
		adId = keyword;
		
		String outDir = props.getProperty("xhtmlFilePath") + "/" + adId;
		if (! new File(outDir).exists() && ! new File(outDir).mkdir()) {
			logger.error("Could not create dir: " + outDir);
		}
		
		String displayPath = props.getProperty("previewDisplayPath");
		
		//File xmlFile = new File(XML_FILE_PATH + xmlfile);
		File xmlFile = new File(xmlfile);
		String output_html_file = null;
		String outFile = null;
		
		//create the output files in a separate dir for each user to avoid any trashing of data
		String tpath = adId;
		if (mobilePhone != null)
			tpath = adId + "/" + mobilePhone;
		
		try {
			for (int i = 0; i < xsltFiles.length; i++) {
				String xsltFileName = xsltPath + File.separator + xsltFiles[i];
				logger.debug("xsl file: " + xsltFileName);
				//String output_html_file = i + ".xhtml";
				output_html_file = xsltFiles[i].substring(0, xsltFiles[i].indexOf(".")) + fileExt;

				xmltUtil.translate(xmlFile, outDir, xsltFileName, output_html_file, params);
				if (i == 0) {
					if (xsltFiles[0].equals("index.xsl"))
						outFile = displayPath + "/" + tpath; //uses index.html
					else
						outFile = displayPath + "/" + tpath + "/" + output_html_file;
				}
			}
		} catch (Exception e) {
			logger.error("transform: " + e);
			e.printStackTrace();
		}	
		
		logger.debug("outFile: " + outFile);

		return outFile;
	}
	
	public String getTinyUrl(String link) {
		final String url = "http://tinyurl.com/api-create.php";
		HttpClient client = new HttpClient();

		String tinyUrl = null;

		PostMethod method = new PostMethod(url);
		method.addParameter("url", link);

		try {
			int returnCode = client.executeMethod(method);

			if (returnCode == 200) {
				tinyUrl = method.getResponseBodyAsString();
				logger.debug("tinyUrl: " + tinyUrl);
			} else {
				logger.error("Error: " + method.getStatusLine());
				logger.error("Could not get tiny url");
			}
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			method.releaseConnection();
		}
		return tinyUrl;
	}
	
	//this is a duplicate of the one in CategoryBase
	public String getAddressForDisplay(String address, String city, String state, String zip) {
		StringBuffer sb = new StringBuffer();
		String sep = "";
		if (address != null) {
			sep = ", ";			
			sb.append(address).append(sep);
		}
		if (city != null) {
				sep = ", ";			
				sb.append(city).append(sep);
		} else
			sep = "";
		if (state != null)
			sb.append(state).append(" ");
		
		sb.append(zip);
		
		return sb.toString();
	}
}
