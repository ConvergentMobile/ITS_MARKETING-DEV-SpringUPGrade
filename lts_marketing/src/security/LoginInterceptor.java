package security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import user.User;

public class LoginInterceptor implements HandlerInterceptor {
	protected static final Logger logger = Logger.getLogger(LoginInterceptor.class);

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		logger.error("after completion");
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub
		logger.error("postHandle");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2) throws Exception {
		logger.debug("Got request: " + request.getRequestURI());
		
		User user = (User)request.getSession().getAttribute("User");
		if (user != null)
			return true;
		
		logger.error("Not logged in");
		request.setAttribute("errorMsg", "You cannot access this page directly. <br/> Please log in through the Liberty Tax portal.");
		
		request.getRequestDispatcher("errorFromInt").forward(request, response);
		
		return false;
	}

}
