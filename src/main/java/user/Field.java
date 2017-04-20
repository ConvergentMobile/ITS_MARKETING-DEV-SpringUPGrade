package user;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


//this exists so that Hibernate does not complain about Profile.hbm.xml
//this should have the same number of field_i elements as the field_i columns in the category table
public class Field implements Serializable {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(Field.class);

	private Integer fieldId;
	private String fieldName;
	private String fieldType;
	private Integer	sequence;
	private String javaField;
	private Object value;

	public Field() {
		
	}

	public Field(Integer fieldId, String fieldName, String fieldType, Integer sequence, String javaField) {
		super();
		this.fieldId = fieldId;
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.sequence = sequence;
		this.javaField = javaField;
	}

	public Integer getFieldId() {
		return fieldId;
	}

	public void setFieldId(Integer fieldId) {
		this.fieldId = fieldId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fiedlType) {
		this.fieldType = fiedlType;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getJavaField() {
		return javaField;
	}

	public void setJavaField(String javaField) {
		this.javaField = javaField;
	}

}
