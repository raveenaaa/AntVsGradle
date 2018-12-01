package example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UKPostCode implements PostCode {
	String postalCode;
	
	public UKPostCode(String postalCode)
    {
    	if(!isValidPostalCode(postalCode)) {
    		throw new IllegalArgumentException("Invalid postcode");
    	}
    	
    	this.postalCode = postalCode;
    }
	
	@Override
	public boolean isValidPostalCode(String postalCode) {
		System.out.println("I'm in UK");
		String pattern = "^([A-PR-UWYZ](([0-9](([0-9]|[A-HJKSTUW])?)?)|([A-HK-Y][0-9]([0-9]|[ABEHMNPRVWXY])?)) [0-9][ABD-HJLNP-UW-Z]{2})"; 
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(postalCode);
		return m.matches();
	}

}