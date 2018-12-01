package example;

public class INPostCode implements PostCode{
	String postalCode;
	
	public INPostCode(String postalCode)
    {
    	if(!isValidPostalCode(postalCode)) {
    		throw new IllegalArgumentException("Invalid postcode");
    	}
    	
    	this.postalCode = postalCode;
    }
	@Override
	public boolean isValidPostalCode(String postalCode) {
		System.out.println("I'm in India");
		return postalCode.matches("[0-9]{6}");
	}

}
