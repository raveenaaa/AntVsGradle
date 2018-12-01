package example;

import example.PostCodeFactory.Country;

public class FactoryClient {

	public static void main(String[] args) {
		PostCode postCode = PostCodeFactory.getInstance(Country.US, "27606");
		postCode = PostCodeFactory.getInstance(Country.IN, "576222");
		postCode = PostCodeFactory.getInstance(Country.UK, "SW15 5PU");
	}

}