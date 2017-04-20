package common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.tiles.TilesRequestProcessor;

public class MyRequestProcessor extends TilesRequestProcessor {
	protected boolean processPreprocess (
            HttpServletRequest request,
            HttpServletResponse response) {
            HttpSession session = request.getSession(false);

      	  Logger logger = Logger.getLogger(MyRequestProcessor.class);
      	  
      	  if (session == null)
      		  logger.error("MyRequestProcessor: session is null");

      	  logger.debug("MyRequestProcessor: path = [" + request.getServletPath() + "]");
        //If user is trying to access login page or create new login page
        // then don't check
        if (request.getServletPath().equals("/do_login.do") 
        		|| request.getServletPath().equals("/getFormPageData.do") //form to be filled by user on mobile device
        		|| request.getServletPath().equals("/createLoginFB.do")
        		|| request.getServletPath().equals("/loginFB.do")
        		|| request.getServletPath().equals("/kw.do")
        		|| request.getServletPath().equals("/kw_appl.do")
        		|| request.getServletPath().equals("/kw_reserve.do")
        		|| request.getServletPath().equals("/kw_purchase.do")
        		|| request.getServletPath().equals("/kw_decals.do")      //N&N  		
        		|| request.getServletPath().equals("/KeywordAction.do")
        		|| request.getServletPath().equals("/AdminAction.do")
        		|| request.getServletPath().equals("/createKeyword.do")
        		|| request.getServletPath().equals("/reset_password.do")
        		|| request.getServletPath().equals("/forgot_password.do")) {
        	//session.invalidate();
        	logger.debug("Got /do_login.do. Returning true...");
            return true;
        }
        if (request.getServletPath().equals("/registration.do") ||
        		request.getServletPath().equals("/CreateLogin.do") ||
        		request.getServletPath().equals("/create_login.do")) {
        	logger.debug("Got /registration.do. Returning true...");
            return true;
        }
        //Check if user attribute is there in session.
        //If so, it means user has already logged in
        if (session != null && (session.getAttribute("User") != null)) {
        	logger.debug("Got User. Returning true");
            return true;
        } else if (! request.getServletPath().equals("/UserAction.do")
        		&& ! request.getServletPath().equals("/logout.do")) {
            try {
                //If not, report error
               logger.error("MyRequestProcessor: Null session. Please log in");
               request.getRequestDispatcher
               ("/do_login.do").forward(request,response);
 //              return false;
            } catch(Exception ex){
            	logger.error("error: " + ex);
            }
        } else {
        	logger.debug("Returning true...");
        	return true;
        }

        return false;
    }
}