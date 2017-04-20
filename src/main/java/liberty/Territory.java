package liberty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Territory")
public class Territory {
	protected String territoryId;
	protected String entityId;
	protected String mbEntityId;
	protected Integer dmaCode;
	protected Integer territoryStatus;
	
	@XmlElement(name = "TerritoryID")
	public String getTerritoryId() {
		return territoryId;
	}
	public void setTerritoryId(String territoryId) {
		this.territoryId = territoryId;
	}
	
	@XmlElement(name = "EntityID")
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	
	@XmlElement(name = "MasterBrokerEntityID")
	public String getMbEntityId() {
		return mbEntityId;
	}
	public void setMbEntityId(String mbEntityId) {
		this.mbEntityId = mbEntityId;
	}
	
	@XmlElement(name = "DMA_Code")
	public Integer getDmaCode() {
		return dmaCode;
	}
	public void setDmaCode(Integer dmaCode) {
		this.dmaCode = dmaCode;
	}
	
	@XmlElement(name = "TerritoryStatus")
	public Integer getTerritoryStatus() {
		return territoryStatus;
	}
	public void setTerritoryStatus(Integer territoryStatus) {
		this.territoryStatus = territoryStatus;
	}
	
	//utility functions
	public Territory findByTerritoryId(List<Territory> tlist, String id) {
		for (Territory t : tlist)
			if (t.getTerritoryId().equals(id.toUpperCase()))
				return t;
		
		return null;
	}
	
	public List<Territory> findByEntityId(List<Territory> tlist, String id) {
		List<Territory> res = new ArrayList<Territory>();

		for (Territory t : tlist)
			if (t.getEntityId().equals(id))
				res.add(t);
		
		return res;
	}
	
	public List<Territory> findByMBEntityId(List<Territory> tlist, String id) {
		List<Territory> res = new ArrayList<Territory>();
		
		for (Territory t : tlist)
			if (t.getMbEntityId().equals(id))
				res.add(t);
		
		return res;
	}
	
	//find all the unique entityIds for a given MBEntityId
	public List<String> getEntityIds(List<Territory> tlist, String mbEntityId) {
		Set<String> res = new HashSet<String>();
		
		for (Territory t : tlist) {
			if (t.getMbEntityId().equals(mbEntityId)) {
				res.add(t.getEntityId());
			}			
		}
		
		return new ArrayList<String>(res);
	}
}
