/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Fast River Technologies Inc. Irvine, CA, USA 
 * All Rights Reserved.
 * 
 * $Id:					$: Id of last commit                
 * $Revision:			$: Revision of last commit 
 * $Author: cye			$: Author of last commit       
 * $Date:	10-10-2018	$: Date of last commit
 */
package com.frt.dr.service;

import javax.sql.DataSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * RepositoryConfiguration class
 * @author chaye
 */
@Configuration
@PropertySource("./config/application.properties")
public class RepositoryConfiguration {
	public static final String PERSISTENCE_UNIT_NAME = "FRT_DR_LOCAL_PERSISTENCE";
	
	@Autowired
	private Environment env;
	
	@Bean
	public DataSource dataSource() {
		String driveClassName = env.getProperty("spring.datasource.driver-class-name");
		String url = env.getProperty("spring.datasource.url");
		String username = env.getProperty("spring.datasource.username");
		String password = env.getProperty("spring.datasource.password");
		
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driveClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        
        return dataSource;
    }
	
	@Bean
	public LocalEntityManagerFactoryBean geEntityManagerFactoryBean() {
		LocalEntityManagerFactoryBean factoryBean = new LocalEntityManagerFactoryBean();
	    factoryBean.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
	    return factoryBean;
	}

	@Bean
	public JpaTransactionManager geJpaTransactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
	    transactionManager.setEntityManagerFactory(geEntityManagerFactoryBean().getObject());    	    
	    return transactionManager;
	}	

}
