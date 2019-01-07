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
import java.sql.Blob;
import java.sql.Clob;
import javax.persistence.FetchType;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import com.frt.dr.model.ResourceComplexType;

/****
 * FHIR composite type: Attachment Name Flags Card. Type Description &
 * Constraints
 * ----------------------------------------------------------------------------------
 * Attachment I Element Content in a format defined elsewhere + It the
 * Attachment has data, it SHALL have a contentType Elements defined in
 * Ancestors: id, extension . contentType Σ 0..1 code Mime type of the content,
 * with charset etc. MimeType (Required) . language Σ 0..1 code Human language
 * of the content (BCP-47) Common Languages (Extensible but limited to All
 * Languages) . data 0..1 base64Binary Data inline, base64ed . url Σ 0..1 uri
 * Uri where the data can be found . size Σ 0..1 unsignedInt Number of bytes of
 * content (if url provided) . hash Σ 0..1 base64Binary Hash of the data (sha-1,
 * base64ed) . title Σ 0..1 string Label to display in place of the data .
 * creation Σ 0..1 dateTime Date attachment was first created
 * 
 * @author JIMFUQIAN
 *
 */

@Entity
@Table(name = "PATIENT_ATTACHMENT")
@SequenceGenerator(name = "PATIENT_ATTACHMENT_SEQ", sequenceName = "PATIENT_ATTACHMENT_SEQ", allocationSize = 1)
@XmlRootElement
public class PatientAttachment implements Serializable, ResourceComplexType {
	private static final long serialVersionUID = -2321293485415818761L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PATIENT_ATTACHMENT_SEQ")
	@Basic(optional = false)
	@NotNull(message = "Attachment logical Id cannot be Null")
	@Column(name = "attachment_id")
	private String attachmentId;

//	@JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
//	@ManyToOne(optional = false)
//	private Patient patient;

	@JoinColumn(name = "patient_id", referencedColumnName = "id")
	@ManyToOne(optional = false)
	private Patient patient;

	@Size(max = 128)
	@Column(name = "path")
	private String path;

	@Size(max = 32)
	@Column(name = "contenttype")
	private String contenttype;

	@Size(max = 32)
	@Column(name = "language")
	private String language;

	@Lob
	@Column(name = "data")
	private String data; // base64Binary

	@Size(max = 1024)
	@Column(name = "url")
	private String url;

	@Column(name = "size")
	private Integer size;

	@Lob
	@Column(name = "hash")
	private String hash; // base64Binary

	@Size(max = 32)
	@Column(name = "title")
	private String title;

	@Column(name = "creation")
	private java.sql.Timestamp creation;

	// private List<PatientExtension> extensions;
	//
	// private List<PatientElementExtension> elementExtensions;

	public PatientAttachment() {
	}

	public Patient getPatient() {
		return this.patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}

	public String getContenttype() {
		return contenttype;
	}

	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data; // yes, it is string - but it is base64 encoded bytes e.g iamge
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getHash() {
		return hash; // base64 encoded
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public java.sql.Timestamp getCreation() {
		return creation;
	}

	public void setCreation(java.sql.Timestamp creation) {
		this.creation = creation;
	}

}
