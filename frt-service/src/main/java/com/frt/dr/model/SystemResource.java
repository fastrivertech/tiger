package com.frt.dr.model;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.Comparator;
import java.sql.Date;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "SYSTEM_RESOURCE")
@SequenceGenerator(name = "SYSTEM_RESOURCE_SEQ", sequenceName = "SYSTEM_RESOURCE_SEQ", allocationSize=1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "getById", query = "SELECT SR FROM SYSTEM_RESOURCE SR WHERE SR.system_id = :id")
})
public class SystemResource {

	private Long systemId;
	
	private Long versionId;
	
	private String Status;
	
	private Timestamp createDate;
	
	private String createUser;
	
	private Timestamp updateDate;
	
	private String updateUser;
	
}
