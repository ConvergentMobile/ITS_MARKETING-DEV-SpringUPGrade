package util;

import imageutil.Converter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;

//Class for uploaded files
public class ImageFile implements Serializable {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(ImageFile.class);
	
	private String id;
	private FormFile file;
	private String location; //which tab
	private String fileName;
	private String filePath; //where on disk
	private String displayPath; //used in the jsp for viewing
	private String description;
	private String title; //title of the image - this will appear in the mwp
	
	//These are the device w & h got from ImageHandler. Currently used for determining map size
	private float deviceWidth = 0f;
	private float deviceHeight = 0f;
	
	//private static final String userAgent = "BlackBerry8300/4.2.2 Profile/MIDP-2.0 Configuration/CLDC-1.1 VendorID/136";
	private static final String userAgent = "BlackBerry9000";
	private static final String uaForLogo = "Nokia6015i/1.0 (M101V0400.nep) UP.Browser/4.1.26l1.c.2.100";
	private static final float logoRatio = 0.76f; //scale the logo to 0.76 of the device size
	
	public ImageFile() {
		
	}
	
	public ImageFile(FormFile file, String location, String fileName, String filePath, String description) {
		this(file, location, fileName, filePath, description, null);
	}

	public ImageFile(FormFile file, String location, String fileName, String filePath, String description, String title) {
		super();
		this.file = file;
		this.location = location;
		this.fileName = fileName;
		this.filePath = filePath;
		this.description = description;
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getDisplayPath() {
		return displayPath;
	}

	public void setDisplayPath(String displayPath) {
		this.displayPath = displayPath;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public float getDeviceWidth() {
		return deviceWidth;
	}

	public void setDeviceWidth(float deviceWidth) {
		this.deviceWidth = deviceWidth;
	}

	public float getDeviceHeight() {
		return deviceHeight;
	}

	public void setDeviceHeight(float deviceHeight) {
		this.deviceHeight = deviceHeight;
	}

	public String resize(String ua) throws Exception {
		Converter c = new Converter();
		String imgFile = null;
		//String fName = filePath.substring(1+filePath.lastIndexOf(File.separator));
		String fName = this.getFileName();
		String fPath = filePath.substring(0, filePath.lastIndexOf(File.separator)+1);
		
		logger.debug("resize: fName, fPath = " + fName + ", " + fPath);

		/*
		if (this.getLocation().equals("logo")) {
			String format = fileName.substring(fileName.lastIndexOf(".")+1);
			imgFile = c.convert(logoRatio, format, fileName, filePath.substring(0, 1+filePath.lastIndexOf(File.separator)), null);
		} else
			imgFile = c.convert(ua, fileName, filePath.substring(0, 1+filePath.lastIndexOf(File.separator)), null);
		*/
		
		if (this.getLocation().equals("logo")) {
			imgFile = c.logoConvert(ua, fName, fPath, null);
		} else
			imgFile = c.convert(ua, fName, fPath, null);
		
		//imgFile = c.convert(ua, fileName, filePath.substring(0, 1+filePath.lastIndexOf("/")), null);
		
		this.setDeviceWidth(c.getDeviceWidth());
		this.setDeviceHeight(c.getDeviceHeight());
		
		logger.debug("imgFile: " + imgFile);
		return imgFile;		
	}
	
	public String resize() throws Exception {
		return resize(userAgent);
	}
	
	public void saveFile(String userId) throws Exception {
		//need to move this to one place
		//Properties props = new Properties();
		//props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));
		Properties props = PropertyUtil.load();
		String fpath = props.getProperty("imgFilePath") + "/" + userId + "/";
		//String fpath = request.getSession().getServletContext().getRealPath("/");
		String dpath = props.getProperty("displayPath") + "/" + userId + "/";

		   FormFile fFile = this.getFile();
		   
		   logger.debug("saveFile: fileName, filePath, dpath = " + this.getFileName() + ", " + this.getFilePath() + ", " + this.getDisplayPath());
		   //logger.debug("saveFile: fileName, filePath, dpath = " + ffName + ", " + ffPath + ", " + this.getDisplayPath());
		   
		   if (fFile != null && fFile.getFileSize() != 0) {
			   //check the file type
			   String fType = fFile.getFileName().substring(fFile.getFileName().lastIndexOf(".")+1);
			   if (! fType.equals("jpg") && ! fType.equals("jpeg") && ! fType.equals("gif") && ! fType.equals(".png")) {
				   logger.error("Invalid file type. Supported types are jpeg, gif, png");
				   throw new Exception("Invalid file type. Supported types are jpeg, gif, png");
			   }
			   if (! (new File(fpath)).exists())
				   new File(fpath).mkdirs();
			   File savedFile = new File(fpath + fFile.getFileName());
		       if (! savedFile.exists()) {
		           FileOutputStream fileOutStream = new FileOutputStream(savedFile);
		           fileOutStream.write(fFile.getFileData());
		           fileOutStream.flush();
		           fileOutStream.close();
		       } 
		       this.fileName = fFile.getFileName();
			   this.filePath = savedFile.getAbsolutePath();
			   logger.debug("filepath before resize: " + this.filePath);
			   //don't resize it at this time - do it when requested
			   //this.filePath = this.resize();
			   //this.fileName = this.filePath.substring(this.filePath.lastIndexOf("/") + 1);
			   this.displayPath = dpath + this.fileName;
			   logger.debug("saved file name, path: " + this.fileName + ", " + this.filePath);			   
		   } else
			   logger.error("No file to save");
	}
	
	//utility to save the uploaded image files
	public List<ImageFile> saveImageFiles(Long userId, List<ImageFile> imgFiles) throws Exception {
		List<ImageFile> photoList = new ArrayList<ImageFile>();
		
	   if (imgFiles != null && imgFiles.size() > 0) {
		   for (ImageFile imgFile : imgFiles) {
			   imgFile.saveFile(userId.toString());
		   }
	   }
		   
		logger.debug("save:before photos size = " + imgFiles.size());
		for (int i = 0; i < imgFiles.size(); i++) {
			ImageFile imgFile = imgFiles.get(i);
			if (imgFile.getFileName() != null && ! imgFile.getLocation().equals("delete")) {
				photoList.add(imgFile);
			}
		}
		return photoList;
	}
}
