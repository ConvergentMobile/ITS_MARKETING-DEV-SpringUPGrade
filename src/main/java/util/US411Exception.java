package util;

public class US411Exception extends Exception {
	private static final long serialVersionUID = 1L;
	private int intError;
	  
	public US411Exception(){
	  }

	public US411Exception(String strMessage){
	    super(strMessage);
	} 
}
