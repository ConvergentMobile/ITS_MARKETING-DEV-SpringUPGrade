package user;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.actions.DispatchAction;

import util.PropertyUtil;

public class CategoryAction extends DispatchAction {
	Logger logger = Logger.getLogger(CategoryAction.class);
	UserDAOManager dao = new UserDAOManager();
	ActionErrors errors = new ActionErrors();

	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		try {
			request.removeAttribute("previewFile");
			
			User user = (User) request.getSession().getAttribute("User");
			CategoryForm mForm = (CategoryForm) form;

			logger.debug("get: cForm id = " + mForm.getCategoryId());

			String forward = null;

			forward = mForm.populateForm(request, "get", 0);
			
			CategoryBase cb = mForm.getCategory();
			logger.debug("In get: userId = " + cb.getUserId());
			
			//For preview, etc., stay on the same page
			if (mForm.getCurrentPage() != null && mForm.getCurrentPage().length() > 0)
				forward = mForm.getCurrentPage();
			
			logger.debug("get: forward = " + forward);
			logger.debug("mapping info");
			//logger.debug("command: " + mapping.getCommand());
			logger.debug("forward: " + mapping.getForward());
			logger.debug("name: " + mapping.getName());
			logger.debug("path: " + mapping.getPath());
			logger.debug("type: " + mapping.getType());
			return mapping.findForward(forward);
		} catch (Exception e) {
			errors.clear();
			errors.add("error1", new ActionMessage("error.get", e.toString()));
			//saveErrors(request, errors);			
			e.printStackTrace();
			return mapping.findForward("fail");
		}
	}

	public ActionForward next(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		try {
			//save it first
			save(mapping, form, request, response);
			
			String to = request.getParameter("to");
			logger.debug("next: to = " + to);

			return mapping.findForward(to);
		} catch (Exception e) {
			errors.clear();
			errors.add("error1", new ActionMessage("error.next", e.toString()));
			//saveErrors(request, errors);
			e.printStackTrace();
			return mapping.findForward("fail");
		}
	}

	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
			
		try {
			errors.clear();

			User user = (User)request.getSession().getAttribute("User");
			
			CategoryForm mForm = (CategoryForm)form;
			CategoryBase cBase = mForm.getCategory();
			logger.debug("save: catgid, currentPage = " + cBase.getCategoryId() + ", " + mForm.getCurrentPage());
			logger.debug("save: phone = " + cBase.getPhone());
			
			/*
			errors = cBase.validate(mForm, request);
			if (errors != null && errors.size() > 0) {
				//saveErrors(request, errors);
				return mapping.findForward(mForm.getCurrentPage());
			}
			*/
			
			cBase.save(request, mForm.getCurrentPage()); //pass the session for toXml to get values from ids
			logger.debug("save: previewFile = " + cBase.getPreviewFile());
			
			for (Offer offer : cBase.getOfferList()) {
				logger.debug("save: offer name: " + offer.getName() + " --- " + offer.getStatus());
			}
			
			//refresh from db
			get(mapping, form, request, response);

			//show the Save successful msg only if the user has clicked Save
			String mode = request.getParameter("mode");
			if (mode != null && mode.equals("userClick")) {
				errors.add("error1", new ActionMessage("ok.save"));
				//saveErrors(request, errors);
				mForm.setPreviewFile(cBase.getPreviewFile()); //used in NN
			}
			
			//return mapping.findForward("save");		
			return mapping.findForward(mForm.getCurrentPage());
		} catch (Exception e) {
			get(mapping, form, request, response);
			errors.add("error1", new ActionMessage("error.save", e.toString()));
			//saveErrors(request, errors);
			return mapping.findForward("fail");
		}
	}

	public ActionForward add(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		try {
			User user = (User) request.getSession().getAttribute("User");
			CategoryForm mForm = (CategoryForm) form;
			
			//save it first
			//logger.debug("Saving it first ...");
			//save(mapping, form, request, response);

			String type = request.getParameter("itemType"); // indicates which
															// collection to add
															// to
			logger.debug("add:type = " + type);

			String forward = null;
			if (type.equals("event"))
				forward = mForm.populateForm(request, "add", 50); // 50 is itemtype for events
			else
				forward = mForm.populateForm(request, "add", Integer.parseInt(type));

			if (type.equals("42")) //Offer
				mForm.setIsAddOffer(true);
			
			logger.debug("add:forward = " + forward);

			// find out the page this was called from
			String currPage = request.getParameter("currPage");
			return mapping.findForward(currPage);
		} catch (Exception e) {
			errors.clear();
			errors.add("error1", new ActionMessage("error.add", e.toString()));
			//saveErrors(request, errors);
			e.printStackTrace();
			return mapping.findForward("fail");
		}
	}
	
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		try {
			HttpSession hps = request.getSession();
			
			//CategoryBase catg = (CategoryBase) hps.getAttribute("category");
			
			User user = (User) hps.getAttribute("User");
			CategoryForm mForm = (CategoryForm)form;
			String currPage = request.getParameter("currentPage");
			
			logger.debug("In preview");
			//not needed - 10/15/2010 - replaced by code below
			//String outFile = mForm.preview(user.getCategoryId(), request);
			
		    CategoryBase cbase = (CategoryBase)request.getSession().getAttribute("category");
		    logger.debug("cbase categoryId: " + cbase.getCategoryId());
		    String outFile = cbase.preview(request, currPage);
		    logger.debug("action preview outFile: " + outFile);
		    			
			/*
			String options = "height=550,width=600,scrollbars=yes,resizable=yes";
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("window.open('" + outFile+ "', 'Preview', '" + options + "')");
			logger.debug("options: window.open('" + outFile+ "', 'Preview', '" + options + "')");
			out.println("window.history.back(-1); ");
			out.println("</script>");
			out.close();
			*/
			
			//refresh from db
			get(mapping, form, request, response);
			
			mForm.setPreviewFile(outFile); //this will be the actual text for NN
			
			request.setAttribute("previewFile", outFile);
			
			return mapping.findForward(currPage);
		} catch (Exception e) {
			errors.clear();
			errors.add("error1", new ActionMessage("error.preview", e.toString()));
			//saveErrors(request, errors);
			e.printStackTrace();
			get(mapping, form, request, response);
			return mapping.findForward("fail");
		}
	}
	
	public ActionForward addSection(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		try {
			User user = (User) request.getSession().getAttribute("User");
			CategoryForm mForm = (CategoryForm) form;

			String type = request.getParameter("itemType"); // indicates which
															// collection to add
															// to
			logger.debug("add:type = " + type);

			String forward = null;
			if (type.equals("event"))
				forward = mForm.populateForm(request, "add", 50); // 50 is itemtype for events
			else
				forward = mForm.populateForm(request, "add", Integer.parseInt(type));

			logger.debug("add:forward = " + forward);

			// find out the page this was called from
			String currPage = request.getParameter("currPage");
			
			return null;
		} catch (Exception e) {
			errors.clear();
			errors.add("error1", new ActionMessage("error.add", e.toString()));
			//saveErrors(request, errors);
			e.printStackTrace();
			return mapping.findForward("fail");
		}
	}	
	
	public ActionForward deleteOffer(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		errors.clear();
		CategoryForm mForm = (CategoryForm) form;		
		String offerId = request.getParameter("offerId");
		
		try {
			CategoryBase cbase = (CategoryBase)request.getSession().getAttribute("category");
			
			//this is not really needed at this time since there will be only one offer
			List<Offer> offlist = new ArrayList<Offer>();
			for (Offer offer : cbase.getOfferList()) {
				if (! offer.getOfferId().equals(offerId)) {
					offlist.add(offer);
				}					
			}
			
			if (offlist.size() == 0)
				offlist.add(new Offer());
			
			cbase.setOfferList(offlist);
								
			errors.add("error1", new ActionMessage("ok.offer.delete"));
			//saveErrors(request, errors);			
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("error.offer.delete", e.toString()));
			//saveErrors(request, errors);
			return mapping.findForward("fail");
		}
		
		return mapping.findForward(mForm.getCurrentPage());
	}	
	
	//to access the configurator from the mobile profile page
	public ActionForward createHotspot(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = (User)request.getSession().getAttribute("User");
		
		//save it first
		save(mapping, form, request, response);
		
		response.sendRedirect("./keyword/keyword_preview.php?mode=FT&keyword=" + user.getKeyword());
		return null;
	}
	
	public ActionForward afterCreateHotspot(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = (User)request.getSession().getAttribute("User");
		String hotspotFile = request.getParameter("imgFile");
		logger.debug("afterCreateHotspot: file = " + hotspotFile);
		user.setHotspotFile(hotspotFile);
		dao.saveUser(user);
		
		return mapping.findForward("step_3");
	}
	
	public ActionForward getAllOffers(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		try {			
			User user = (User) request.getSession().getAttribute("User");
			CategoryForm mForm = (CategoryForm) form;
			CategoryBase cb = mForm.getCategory();

			String forward = null;

			List<Offer> allOffers = new UserDAOManager().getAllOffers(cb.getProfileId());
			request.getSession().setAttribute("allOffers", allOffers);
						
			//For preview, etc., stay on the same page
			if (mForm.getCurrentPage() != null && mForm.getCurrentPage().length() > 0)
				forward = mForm.getCurrentPage();

			return mapping.findForward("showAllOffers");
		} catch (Exception e) {
			errors.clear();
			errors.add("error1", new ActionMessage("error.get", e.toString()));
			//saveErrors(request, errors);			
			e.printStackTrace();
			return mapping.findForward("fail");
		}
	}
}
