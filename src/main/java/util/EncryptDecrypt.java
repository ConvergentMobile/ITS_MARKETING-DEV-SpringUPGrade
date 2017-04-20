package util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.BCodec;

public class EncryptDecrypt {
	private BCodec bCodec = new BCodec();
	
	public String encrypt(String intext) throws Exception {
	    try {
	        return bCodec.encode(intext);
	    }
	    catch (EncoderException e) {
	        throw new Exception(e.getCause());
	    }
	}
	
	public String decrypt(String intext) throws Exception {
	    try {
	        return bCodec.decode(intext);
	    }
	    catch (DecoderException e) {
	        throw new Exception(e.getCause());
	    }
	}
}
