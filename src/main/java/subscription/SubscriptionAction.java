package subscription;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import keyword.KeywordApplication;
import keyword.KeywordDAOManager;
import keyword.KeywordForm;
import mdp_common.ANetHandler;
import net.authorize.data.Customer;
import net.authorize.data.creditcard.CreditCard;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.actions.DispatchAction;

import user.User;
import user.UserDAOManager;
import util.PropertyUtil;

public class SubscriptionAction extends DispatchAction {
	Logger logger = Logger.getLogger(SubscriptionAction.class);
	ActionErrors errors = new ActionErrors();
	KeywordDAOManager kwDAO = new KeywordDAOManager();
	UserDAOManager userDAO = new UserDAOManager();

	public ActionForward pre(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {	
			errors.clear();			
			KeywordForm mForm = (KeywordForm)form;

			String mode = request.getParameter("mode");
			logger.debug("In KeywordAction:mode = " + request.getParameter("mode"));
			
			if (mode != null && (mode.equals("purchase"))) {
				List<KeywordApplication> kwAppls = new ArrayList<KeywordApplication>();
				request.getSession().setAttribute("kwAppls", kwAppls);
				return mapping.findForward(mode);
			}
			
			return mapping.findForward("success");
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("kwappl.error", e));
			//saveErrors(request, errors);			
			e.printStackTrace();
			return mapping.findForward("fail");
		}
	}

	public ActionForward renew(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {			
			errors.clear();

			//check for resubmit
		     if (isTokenValid(request)) {  
		            resetToken(request);  
		        } else if (! isTokenValid(request)) {  
		            logger.error("You have already submitted the request");
					errors.add("error1", new ActionMessage("error.resubmit"));
					//saveErrors(request, errors);			            
		            return mapping.findForward("renew_fail");
		        } 
		     
		    ANetHandler paymentHandler = new ANetHandler();
		     
			KeywordForm mForm = (KeywordForm)form;
			User user = userDAO.login(mForm.getUser().getKeyword(), mForm.getUser().getSiteId());
			if (user == null) {
				errors.add("error1", new ActionMessage("error.noKeyword", mForm.getUser().getKeyword()));
				//saveErrors(request, errors);			
				return mapping.findForward("renew_fail");	
			}
			
			String subscriptionId = user.getArbPaymentTxId();

			KeywordApplication kwAppl = mForm.getKeywordApplication();
			
			Customer customer = Customer.createCustomer();
			customer.setFirstName(mForm.getUser().getFirstName());
			customer.setLastName(mForm.getUser().getLastName());
			customer.setAddress(mForm.getUser().getBillingAddress1() + " " + mForm.getUser().getBillingAddress2());
			customer.setCity(mForm.getUser().getBillingCity());
			customer.setState(mForm.getUser().getBillingState());
			customer.setZipPostalCode(mForm.getUser().getBillingZip());
			
			CreditCard cc = CreditCard.createCreditCard();
			cc.setCreditCardNumber(mForm.getCcNumber());
			cc.setExpirationMonth(mForm.getCcExpirationMon());
			cc.setExpirationYear(mForm.getCcExpirationYear());
			
			String arbTxId = null;
			
			if (subscriptionId != null) { //update it
				paymentHandler.updateARB(cc, subscriptionId);
			} else { //create a new one
				arbTxId = paymentHandler.createARB(cc, customer, this.calculateARBPrice(), user.getKeyword(), 0);
				logger.debug("payment ret status: " + arbTxId);
				user.setArbPaymentTxId(arbTxId);						
				userDAO.saveUser(user);				
			}
			
			logger.debug("userId: " + user.getUserId());
			errors.add("error1", new ActionMessage("renew.ok"));
			//saveErrors(request, errors);			
			return mapping.findForward("renew_ok");
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("renew.error", e.getMessage()));
			//saveErrors(request, errors);			
			return mapping.findForward("renew_fail");
		}		
	}
	

	public BigDecimal calculateARBPrice() throws Exception {
		Properties props = PropertyUtil.load();
		
		BigDecimal monthlyCost = new BigDecimal(props.getProperty("kwappl.monthlyCost"));
		BigDecimal months = new BigDecimal(props.getProperty("kwappl.minMonths"));
		
		return monthlyCost.multiply(months);
	}
}
