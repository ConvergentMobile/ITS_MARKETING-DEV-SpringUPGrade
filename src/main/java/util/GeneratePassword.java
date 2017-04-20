package util;

import java.util.Random;

public class GeneratePassword {
	 
    private static final String charset = "!$%&*@0123456789abcdefghijklmnopqrstuvwxyz";
 
    public static String getRandomString() {
    	int length = 8;
        Random rand = new Random(System.currentTimeMillis());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int pos = rand.nextInt(charset.length());
            sb.append(charset.charAt(pos));
        }
        return sb.toString();
    }
}
