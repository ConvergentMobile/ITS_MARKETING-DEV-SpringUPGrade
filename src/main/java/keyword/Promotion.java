package keyword;

import java.math.BigDecimal;
import java.util.Date;

public class Promotion {
	protected Integer id;
	protected Date startDate;
	protected Date endDate;
	protected String promoCode;
	protected BigDecimal monthlyDiscountPercent;
	protected BigDecimal monthlyDiscountAmount;
	protected BigDecimal setupDiscountPercent;
	protected BigDecimal setupDiscountAmount;	
	protected String description;
	protected Integer freeMonths;
	
	public Promotion() {
		
	}
	
	public Promotion(Integer id, Date startDate, Date endDate,
			String promoCode, BigDecimal monthlyDiscountPercent,
			BigDecimal monthlyDiscountAmount, BigDecimal setupDiscountPercent,
			BigDecimal setupDiscountAmount, String description, Integer freeMonths) {
		super();
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
		this.promoCode = promoCode;
		this.monthlyDiscountPercent = monthlyDiscountPercent;
		this.monthlyDiscountAmount = monthlyDiscountAmount;
		this.setupDiscountPercent = setupDiscountPercent;
		this.setupDiscountAmount = setupDiscountAmount;
		this.description = description;
		this.freeMonths = freeMonths;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	public BigDecimal getMonthlyDiscountPercent() {
		return monthlyDiscountPercent;
	}

	public void setMonthlyDiscountPercent(BigDecimal monthlyDiscountPercent) {
		this.monthlyDiscountPercent = monthlyDiscountPercent;
	}

	public BigDecimal getMonthlyDiscountAmount() {
		return monthlyDiscountAmount;
	}

	public void setMonthlyDiscountAmount(BigDecimal monthlyDiscountAmount) {
		this.monthlyDiscountAmount = monthlyDiscountAmount;
	}

	public BigDecimal getSetupDiscountPercent() {
		return setupDiscountPercent;
	}

	public void setSetupDiscountPercent(BigDecimal setupDiscountPercent) {
		this.setupDiscountPercent = setupDiscountPercent;
	}

	public BigDecimal getSetupDiscountAmount() {
		return setupDiscountAmount;
	}

	public void setSetupDiscountAmount(BigDecimal setupDiscountAmount) {
		this.setupDiscountAmount = setupDiscountAmount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getFreeMonths() {
		return freeMonths;
	}

	public void setFreeMonths(Integer freeMonths) {
		this.freeMonths = freeMonths;
	}	
	
}
