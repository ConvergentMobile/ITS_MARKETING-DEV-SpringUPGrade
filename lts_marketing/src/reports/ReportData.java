package reports;

import java.io.Serializable;

public class ReportData implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String column1;
	private String column2;
	private String column3;
	private String column4;
	private String column5;
	private String column6;
	private String column7;
	private String column8;
	private String column9;
	private String column10;
	private Object obj1;
	
	public ReportData() {
	}

	public ReportData(String column1, String column2) {
		this(column1, column2, null, null, null, null);
	}
	
	public ReportData(String column1, String column2, String column3) {
		this(column1, column2, column3, null, null, null);
	}
	
	public ReportData(String column1, String column2, String column3,
			String column4, String column5, String column6) {
		super();
		this.column1 = column1;
		this.column2 = column2;
		this.column3 = column3;
		this.column4 = column4;
		this.column5 = column5;
		this.column6 = column6;
	}

	public String getColumn1() {
		return column1;
	}

	public void setColumn1(String column1) {
		this.column1 = column1;
	}

	public String getColumn2() {
		return column2;
	}

	public void setColumn2(String column2) {
		this.column2 = column2;
	}

	public String getColumn3() {
		return column3;
	}

	public void setColumn3(String column3) {
		this.column3 = column3;
	}

	public String getColumn4() {
		return column4;
	}

	public void setColumn4(String column4) {
		this.column4 = column4;
	}

	public String getColumn5() {
		return column5;
	}

	public void setColumn5(String column5) {
		this.column5 = column5;
	}

	public String getColumn6() {
		return column6;
	}

	public void setColumn6(String column6) {
		this.column6 = column6;
	}

	public Object getObj1() {
		return obj1;
	}

	public String getColumn7() {
		return column7;
	}

	public void setColumn7(String column7) {
		this.column7 = column7;
	}

	public String getColumn8() {
		return column8;
	}

	public void setColumn8(String column8) {
		this.column8 = column8;
	}

	public String getColumn9() {
		return column9;
	}

	public void setColumn9(String column9) {
		this.column9 = column9;
	}

	public String getColumn10() {
		return column10;
	}

	public void setColumn10(String column10) {
		this.column10 = column10;
	}

	public void setObj1(Object obj1) {
		this.obj1 = obj1;
	}
	
	public String get(int idx) {
		idx++; //as it starts with 0
		if (idx == 1)
			return column1;
		if (idx == 2)
			return column2;
		if (idx == 3)
			return column3;
		if (idx == 4)
			return column4;
		if (idx == 5)
			return column5;
		if (idx == 6)
			return column6;
		if (idx == 7)
			return column7;
		if (idx == 8)
			return column8;
		if (idx == 9)
			return column9;
		if (idx == 10)
			return column10;
		
		return null;		
	}
}
