package liberty;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Database")
public class DBEntity {
		protected List<Entity> entities;	
		protected List<Territory> territories;
		protected List<Office> offices;
		protected List<OfficeHour> officeHours;
		private String mode;
		
		@XmlAttribute(name="mode")
		public String getMode() {
			return mode;
		}

		public void setMode(String mode) {
			this.mode = mode;
		}

		@XmlElementWrapper(name="Entities")
		@XmlElement(name="Entity")
		public List<Entity> getEntities() {
			return entities;
		}

		public void setEntities(List<Entity> entities) {
			this.entities = entities;
		}

		@XmlElementWrapper(name="Territories")
		@XmlElement(name="Territory")
		public List<Territory> getTerritories() {
			return territories;
		}

		public void setTerritories(List<Territory> territories) {
			this.territories = territories;
		}

		@XmlElementWrapper(name="Offices")
		@XmlElement(name="Office")
		public List<Office> getOffices() {
			return offices;
		}

		public void setOffices(List<Office> offices) {
			this.offices = offices;
		}

		@XmlElementWrapper(name="OfficeHours")
		@XmlElement(name="OfficeHour")
		public List<OfficeHour> getOfficeHours() {
			return officeHours;
		}

		public void setOfficeHours(List<OfficeHour> officeHours) {
			this.officeHours = officeHours;
		}
		
		
}
