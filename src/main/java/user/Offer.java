package user;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

public class Offer implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(Offer.class);

	private Long profileId;
	private String offerId;
	private String name;
	private String description;
	private Date expiration;
	private String code;
	private String status;
	
	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

	public Offer() {		
	}
	
	public Long getProfileId() {
		return profileId;
	}
	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}
	public String getOfferId() {
		return offerId;
	}
	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public Date getExpiration() {
		return expiration;
	}
	
	public String getExpirationAsString() {
		return (expiration != null) ? sdf.format(expiration) : "(Click Calendar)";
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setExpirationAsString(String expiration) throws Exception {
		try {
			if (expiration == null || expiration.length() <= 0 || expiration.equals("(Click Calendar)"))
				this.expiration = null;
			else
				this.expiration = sdf.parse(expiration);
		} catch (ParseException e) {
			logger.error("Offer: invalid date format");
			throw new Exception("Offer: invalid date format" + e);
		}
	}
}
