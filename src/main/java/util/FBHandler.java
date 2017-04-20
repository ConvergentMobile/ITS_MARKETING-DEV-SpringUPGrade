package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

//https://graph.facebook.com/oauth/authorize?client_id=323054551071446&redirect_uri=http://textus411.com/us411/test.jsp&scope=user_photos,email,user_birthday,user_online_presence

public class FBHandler extends HttpServlet {
	private static Logger logger = Logger.getLogger(FBHandler.class);
	private static final String clientId = "323054551071446";
	private static final String secretKey = "530d4a8196a7bbdf9496bcbce888a54f";
	private String authUrl = "https://graph.facebook.com/oauth/authorize";
	private String accessTokenUrl = "https://graph.facebook.com/oauth/access_token?";
	private String redirectUrl = "http://textus411.com/us411/FBHandler";

	private String meUrl = "https://graph.facebook.com/me?access_token=";
	private String authCode;
	private String accessToken;
	private String scope = "email";
	
	public void getAccessToken() throws Exception {
		PostMethod postMethod = new PostMethod(accessTokenUrl);
		postMethod.setRequestHeader("Keep-Alive", "Keep-Alive");
		postMethod.addParameter("client_id", clientId);
		postMethod.addParameter("client_secret", secretKey);
		postMethod.addParameter("code", this.authCode);
		postMethod.addParameter("grant_type", "authorization_code");
		postMethod.addParameter("redirect_uri", redirectUrl);

		String result = null;
		
		int statusCode = new HttpClient().executeMethod(postMethod);
		if (statusCode == 200) {
			result = postMethod.getResponseBodyAsString();
			logger.debug("result: " + result);
			String[] ftmp = result.split("&");
			this.accessToken = ftmp[0].substring(ftmp[0].indexOf("=")+1);
			logger.debug("accessToken: " + accessToken);

		} else {
			logger.error("Error: " + postMethod.getStatusLine());
		}

		postMethod.releaseConnection();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String action = req.getParameter("action");
		if (action != null && action.equals("auth")) { //authorize
			try {
				authUrl += "?client_id=" + clientId + "&redirect_uri=" + redirectUrl + "&scope=" + scope;
				resp.sendRedirect(authUrl);
				return;
			} catch (Exception e) {
				throw new ServletException("Error calling authorize");
			}
		}
		this.authCode = req.getParameter("code");
		logger.debug("authCode = " + authCode);
		try {
			this.getAccessToken();
			String fbid = this.getFBProfile();
			req.getSession().setAttribute("fbid", fbid);
			String returnUrl = (String)req.getSession().getAttribute("returnUrl");
			resp.sendRedirect(returnUrl);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String readURL(URL url) throws Exception {
		InputStream is = url.openStream();

		InputStreamReader inStreamReader = new InputStreamReader(is);
		BufferedReader reader = new BufferedReader(inStreamReader);
		String s = "";
		int r;
		while ((r = is.read()) != -1) {
			s = reader.readLine();
		}
		reader.close();
		return s;
	}
	
	public String getFBProfile() throws Exception {
		GetMethod getMethod = new GetMethod(meUrl + this.accessToken);

		String result = null;
		String fbid = null;
		
		int statusCode = new HttpClient().executeMethod(getMethod);
		if (statusCode == 200) {
			result = getMethod.getResponseBodyAsString();
			logger.debug("result: " + result);
			JSONObject obj = (JSONObject)JSONValue.parse(result);
			logger.debug("fbid: " + (String)obj.get("id"));
			fbid = (String)obj.get("id");
		} else {
			logger.error("Error: " + getMethod.getStatusLine());
		}

		getMethod.releaseConnection();
		return fbid;
	}

}
