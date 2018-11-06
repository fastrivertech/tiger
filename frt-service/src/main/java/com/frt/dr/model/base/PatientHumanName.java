package com.frt.dr.model.base;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.Comparator;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Clob;
import java.sql.Blob;
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
@Table(name = "PATIENT_HUMANNAME")
@SequenceGenerator(name = "PATIENT_HUMANNAME_SEQ", sequenceName = "PATIENT_HUMANNAME_SEQ", allocationSize=1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "getById", query = "SELECT PH FROM PATIENT_HUMANNAME PH WHERE PH.patient_id = :id")
})
public class PatientHumanName implements Serializable {
    private static final long serialVersionUID = -8321293485415818761L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_HUMANNAME_SEQ")  
    @NotNull(message = "Humanname logical Id cannot be Null")
    @Column(name = "humanname_id", insertable = false, updatable = false)    
    private Long humannameId;
    
    @NotNull(message = "Patient logical Id cannot be Null")
    @Column(name = "patient_id")            
    private Long patient_id;
    
    @Size(max = 128)    
    @Column(name = "path")                                                
    private String path;
    
    @Size(max = 32)    
    @Column(name = "use")                                                
    private String use;

    @Size(max = 2048)    
    @Column(name = "use")                                                    
    private String txt;
    
    @Size(max = 32)    
    @Column(name = "family")                                                    
    private String family;

    @Column(name = "gievn")                                                        
    private Clob given;
    
    @Column(name = "prefix")                                                        
    private Clob prefix;
    
    @Column(name = "suffix")                                                        
    private Clob suffix;
    
    @Column(name = "period")                                                        
    private Clob period;
    
}
