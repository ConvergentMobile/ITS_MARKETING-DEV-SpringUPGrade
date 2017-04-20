package util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class InputDecoder {
	protected Logger logger = Logger.getLogger(InputDecoder.class);

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	private static final String SALT = "u5QY1ZjFq-P2kLYn";
	
	public InputDecoder() {
		
	}
	
	//called to validate a SSO user by checking that the Fp matches
	public Boolean fpMatch(String role, String eId, String fp) throws Exception {		
		String myFP = new InputDecoder().decodeIt(role, eId, SALT);
		
		String theFPUC = new InputDecoder().UrlEncodeUpperCase(fp);
		
		logger.debug("fpMatch - in fp: " + fp);
		logger.debug("fpMatch - theFPUC: " + theFPUC);
		logger.debug("fpMatch - myFP: " + myFP);
		logger.debug("fpMatch - decoded myFP: " + URLDecoder.decode(myFP, "UTF-8"));

		//decode myFP as the one we get from the url is decoded
		if (URLDecoder.decode(myFP, "UTF-8").equals(theFPUC))
			return true;
		
		return false;
	}
	
	public String UrlEncodeUpperCase(String value) {
		Pattern pattern = Pattern.compile("(%[0-9a-f][0-9a-f])");
		Matcher matcher = pattern.matcher(value);
		
		/*
		while (matcher.find())
			for (int j = 1; j <= matcher.groupCount(); j++)
				System.out.println("Here j: " + j + " : " +  matcher.group(j));
		 */
		
		//System.out.println("value: " + value);
		
		StringBuffer sb = new StringBuffer();
		
		while (matcher.find()) {
			String stmp = matcher.group(1);
			System.out.println("Here: " + stmp);
			
			matcher.appendReplacement(sb, stmp.toUpperCase());
			
			//System.out.println("sb: " + sb.toString());
		}
				
		matcher.appendTail(sb);
		
		System.out.println("sb final: " + sb.toString());

		return sb.toString();
	}
	
	public String calculateHMAC(String secret, String data) {
		try {
			SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(),	HMAC_SHA1_ALGORITHM);
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(data.getBytes());
			String result = new String(Base64.encodeBase64(rawHmac));
			return result;
		} catch (GeneralSecurityException e) {
			logger.warn("Unexpected error while creating hash: " + e.getMessage(),	e);
			throw new IllegalArgumentException();
		}
	}
	
	public String getPlainText(String encodedStr) throws Exception {
		String s = URLDecoder.decode(encodedStr, "UTF-16LE");
		System.out.println("s: " + s);
		
		byte[] bs = Base64.decodeBase64(s.getBytes("UTF-16LE"));
		
		String ds = new String(bs, "UTF-16LE");
		System.out.println("ds: " + ds);

		return ds;
	}
	
	public String decodeIt(String role, String id, String salt) throws Exception {
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		SecretKeySpec signingKey = new SecretKeySpec(salt.getBytes(), HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);
		
		String thisStr = role + id + salt;

		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(thisStr.getBytes("UTF-16LE"));
		
		String result = URLEncoder.encode(Base64.encodeBase64String(md.digest()), "UTF-8");
				
		return result;
	}
}
