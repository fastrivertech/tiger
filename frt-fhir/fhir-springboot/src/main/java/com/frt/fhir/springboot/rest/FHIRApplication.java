package com.frt.fhir.springboot.rest;

//import org.apache.log4j.BasicConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FHIRApplication {

	public static void main(String[] args) {
		System.out.println("IN FHIRApplication main(...");
//		BasicConfigurator.configure();
		SpringApplication.run(FHIRApplication.class, args);
	}

}
