package liberty;

//user defined custom fields
public class CustomFields {
	private Long id;
	private Long userId;
	private String officeId;
	private String entityId;
	private String entityName;
	private String officeStatus;
	private String userType;
	private String territoryId;
	private String dmaCode;
	private String regionId;
	private String entityStatus;
	private String mbEntityId;
	private String territoryStatus;
	private String location;

	public CustomFields() {
		
	}
	
	public CustomFields(Long userId, String officeId, String entityId, String entityName, String officeStatus) {
		this(userId, officeId, entityId, entityName, officeStatus, null, null);
	}

	public CustomFields(Long userId, String officeId, String entityId, String entityName, String officeStatus,
							String userType, String territoryId) {
		super();
		this.userId = userId;
		this.officeId = officeId; 
		this.entityId = entityId;
		this.entityName = entityName;
		this.officeStatus = officeStatus;
		this.userType = userType;
		this.territoryId = territoryId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getOfficeStatus() {
		return officeStatus;
	}

	public String getDmaCode() {
		return dmaCode;
	}

	public void setDmaCode(String dmaCode) {
		this.dmaCode = dmaCode;
	}

	public void setOfficeStatus(String officeStatus) {
		this.officeStatus = officeStatus;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getTerritoryId() {
		return territoryId;
	}

	public void setTerritoryId(String territoryId) {
		this.territoryId = territoryId;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getEntityStatus() {
		return entityStatus;
	}

	public void setEntityStatus(String entityStatus) {
		this.entityStatus = entityStatus;
	}

	public String getMbEntityId() {
		return mbEntityId;
	}

	public void setMbEntityId(String mbEntityId) {
		this.mbEntityId = mbEntityId;
	}

	public String getTerritoryStatus() {
		return territoryStatus;
	}

	public void setTerritoryStatus(String territoryStatus) {
		this.territoryStatus = territoryStatus;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
}
