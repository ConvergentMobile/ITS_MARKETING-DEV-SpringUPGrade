package liberty;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Entity")
public class Entity {
	protected String entityId;
	protected String entityName;
	protected String contactFirstName;
	protected String contactLastName;
	protected String eMail;
	protected Integer entityStatus;
	protected String mbEntityId;
	
	@XmlElement(name = "EntityID")
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	
	@XmlElement(name = "EntityName")
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	
	@XmlElement(name = "ContactFirstName")
	public String getContactFirstName() {
		return contactFirstName;
	}
	public void setContactFirstName(String contactFirstName) {
		this.contactFirstName = contactFirstName;
	}
	
	@XmlElement(name = "ContactLastName")
	public String getContactLastName() {
		return contactLastName;
	}
	public void setContactLastName(String contactLastName) {
		this.contactLastName = contactLastName;
	}
	
	@XmlElement(name = "Email")
	public String geteMail() {
		return eMail;
	}
	public void seteMail(String eMail) {
		this.eMail = eMail;
	}
	
	@XmlElement(name = "EntityStatus")
	public Integer getEntityStatus() {
		return entityStatus;
	}
	public void setEntityStatus(Integer entityStatus) {
		this.entityStatus = entityStatus;
	}
	
	@XmlElement(name = "MasterBrokerEntity")
	public String getMbEntityId() {
		return mbEntityId;
	}
	public void setMbEntityId(String mbEntityId) {
		this.mbEntityId = mbEntityId;
	}

	//utility functions
	public Entity findByEntityId(List<Entity> tlist, String id) {
		for (Entity t : tlist)
			if (t.getEntityId().equals(id))
				return t;
		
		return null;
	}
}
