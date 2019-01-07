package com.frt.stream;

public class FhirMessageProducerConsumer {

	public static void main(String[] args) {
		FhirMessageProducer p = new FhirMessageProducer();

		FhirMessageConsumer c = new FhirMessageConsumer();
		
		p.send("patient");
		c.receives();
		
	}
}
