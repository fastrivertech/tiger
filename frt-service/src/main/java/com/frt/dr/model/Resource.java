package com.frt.dr.model;

import javax.persistence.Entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Resource class
 * @author chaye
 */
@Entity
@Table(name = "RESOURCE")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name="DOMAIN_RESOURCE_TYPE")
//@SequenceGenerator(name = "PATIENT_SEQ", sequenceName = "PATIENT_SEQ", allocationSize=1)
@NamedQueries({
    @NamedQuery(name = "getResourceById", query = "SELECT R FROM Resource R WHERE R.id = :id")
})
public class Resource implements Serializable {
    private static final long serialVersionUID = -8321293485415818760L;

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "RESOURCE_SEQ")  
//    @Basic(optional = false)
//    @NotNull(message = "Resource logical Id cannot be Null")
//    @Size(max = 64)    
//    @Column(name = "resource_id", nullable = false, updatable=false)
//	private String resourceId;
//
//	@Size(max = 64)    
//    @Column(name = "systemId")            
//	private String systemId;
//	
//    @Size(max = 32)    
//    @Column(name = "id")            
//	private String id;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_SEQ")  
	@Basic(optional = false)
	@NotNull(message = "Resource logical Id cannot be Null")
	@Size(max = 64)    
	@Column(name = "id", nullable = false, updatable=false)
	private String id;

    @Size(max = 64)    
    @Column(name = "system_id")            
	private String systemId; 

	@Lob
    @Column(name = "meta")                        
	private String meta;
	
    @Size(max = 2048)    
    @Column(name = "implicitRules")            
	private String implicitRules;
	
    @Size(max = 32)    
    @Column(name = "language")            
	private String language; 
	
	
//    public String getResourceId() {
//		return resourceId;
//	}
//
//	public void setResourceId(String resourceId) {
//		this.resourceId = resourceId;
//	}
//
//	public String getSystemId() {
//		return systemId;
//	}
//
//	public void setSystemId(String systemId) {
//		this.systemId = systemId;
//	}

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	
	public String getMeta() {
		return meta;
	}

	public void setMeta(String meta) {
		this.meta = meta;
	}

	public String getImplicitRules() {
		return implicitRules;
	}

	public void setImplicitRules(String implicitRules) {
		this.implicitRules = implicitRules;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
