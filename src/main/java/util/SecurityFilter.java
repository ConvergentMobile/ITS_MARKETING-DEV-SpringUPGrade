package util;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class SecurityFilter implements Filter {
	Logger logger = Logger.getLogger(SecurityFilter.class);

	private FilterConfig filterConfig = null;
	private EncryptDecrypt ed = new EncryptDecrypt();
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		 HttpServletRequest request = (HttpServletRequest) req;
		 HttpServletResponse response = (HttpServletResponse) res;

		 logger.debug("In SecurityFilter");
		 StringBuffer sb = new StringBuffer();
		 Map<String, String> params = new HashMap<String, String>();
		 try {
			Enumeration e = request.getParameterNames();
			//we assume that these are all encrypted params & UTF-8 encoded
			//the "base" is a base url to which name-value pairs are appended
			 while (e.hasMoreElements()) {
					String name = URLDecoder.decode((String)e.nextElement(), "UTF-8");
					String value = URLDecoder.decode(request.getParameter(name), "UTF-8");
					String dn = ed.decrypt(name);
					logger.debug("n, v: " + ed.encrypt(name) + ", " + ed.encrypt(value));
					logger.debug("en, ev: " + dn + ", " + ed.decrypt(value));
					if (dn.equals("base")) {
						sb.append(ed.decrypt(value));
						continue;
					}
					params.put(dn, ed.decrypt(value));
			 }
			 
			 int i = 0;
			 for (Map.Entry<String, String> entry : params.entrySet()) {
				 if (i > 0)
					 sb.append("&");
				 else
					 sb.append("?");
				 sb.append(entry.getKey()).append("=").append(URLDecoder.decode(entry.getValue(), "UTF-8"));
				 i++;
			 }
			 logger.debug("url: " + sb.toString());
			 
		     RequestDispatcher requestDispatcher = request.getRequestDispatcher(sb.toString());
		     requestDispatcher.forward(request,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		 // Continue processing of the chain
		 chain.doFilter(request, response);	
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

}
