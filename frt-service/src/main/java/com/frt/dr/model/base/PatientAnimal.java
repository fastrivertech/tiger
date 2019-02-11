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
package com.frt.dr.model.base;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import com.frt.dr.model.BackboneElement;
import com.frt.dr.model.ResourceComplexType;

@Entity
@Table(name = "PATIENT_ANIMAL")
@SequenceGenerator(name = "PATIENT_ANIMAL_SEQ", sequenceName = "PATIENT_ANIMAL_SEQ", allocationSize = 1)
@XmlRootElement
public class PatientAnimal implements Serializable, ResourceComplexType, BackboneElement {
	private static final long serialVersionUID = -3321293485415818761L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_ANIMAL_SEQ")
	@Basic(optional = false)
	@NotNull(message = "Animal physical Id cannot be Null")
	@Column(name = "animal_id")
	private BigInteger animalId;

	@JoinColumn(name = "resource_id", referencedColumnName = "resource_id")
	@ManyToOne(optional = false)
	private Patient patient;

	@Lob
	@Column(name = "species")
	private String species;

	@Lob
	@Column(name = "breed")
	private String breed;

	@Lob
	@Column(name = "genderstatus")
	private String genderStatus;

  //private List<PatientExtension> extensions;
  //private List<PatientElementExtension> elementExtensions;

	public PatientAnimal() {
	}

	public Patient getPatient() {
		return this.patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public BigInteger getAnimalId() {
		return animalId;
	}

	public void setAnimalId(BigInteger animalId) {
		this.animalId = animalId;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getBreed() {
		return breed;
	}

	public void setBreed(String breed) {
		this.breed = breed;
	}

	public String getGenderStatus() {
		return genderStatus;
	}

	public void setGenderStatus(String genderStatus) {
		this.genderStatus = genderStatus;
	}

	@Override
	public void setPath(String path) {
		// TODO Auto-generated method stub

	}

}
