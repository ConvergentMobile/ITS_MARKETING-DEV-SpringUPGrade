package security;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class LTSAccessDeniedHandler implements AccessDeniedHandler {
	protected static final Logger logger = Logger.getLogger(LTSAccessDeniedHandler.class);

	private String errorPage;
	
	public String getErrorPage() {
		return errorPage;
	}

	public void setErrorPage(String errorPage) {
		this.errorPage = errorPage;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException adException) 
							throws IOException, ServletException {
		logger.debug("In LTSAccessDeniedHandler");
		
		//RequestDispatcher rd = request.getRequestDispatcher(errorPage);
		//rd.forward(request, response);		
		
		response.sendRedirect(errorPage);
	}

}
