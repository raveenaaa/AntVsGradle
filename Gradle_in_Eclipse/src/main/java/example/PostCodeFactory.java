package example;

public class PostCodeFactory {
	enum Country {
		US, UK, IN;
	}
	
	public static PostCode getInstance(Country country, String postalCode)
	{
		if (country == Country.US)
			return new USPostCode(postalCode); //> 1
		if (country == Country.UK)
			return new UKPostCode(postalCode); //> 2
		if (country == Country.IN)
			return new INPostCode(postalCode); //> 3
		
		return null;
	}
}

