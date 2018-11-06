/**
 * 
 */
package com.frt.dr.dao;

import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.data.repository.Repository;

/**
 * @author cqye
 *
 */
public abstract class BaseDao<T,ID> implements Repository {
	  
	protected JdbcTemplate jdbcTemplate;
	
    @Autowired
    public void setDataSource(DataSource dataSource) {
    	this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public abstract T save(T entry) throws DaoException;

    public abstract Optional<T> findById(ID id) throws DaoException;
    
}
