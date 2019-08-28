package com.frt.mpi;

import java.util.List;
import java.util.Date;
import java.util.Optional;
import com.frt.dr.model.base.Patient;
import com.frt.dr.model.base.PatientIdentifier;
import com.frt.dr.model.base.PatientHumanName;
import com.frt.dr.model.base.PatientAddress;
import com.sun.mdm.index.webservice.client.PatientEJB;
import com.sun.mdm.index.webservice.client.PatientBean;
import com.sun.mdm.index.webservice.client.AddressBean;
import com.sun.mdm.index.webservice.client.MergePatientResult;
import com.sun.mdm.index.webservice.client.MatchColResult;
import com.sun.mdm.index.webservice.client.SystemPatient;
import com.sun.mdm.index.webservice.client.ProcessingException_Exception; 
import com.sun.mdm.index.webservice.client.UserException_Exception;

public class MpiProviderImpl implements MpiProvider {
	private PatientEJB mpi;
	
	public MpiProviderImpl() {
		mpi = MpiProviderLocator.getInstance().mpi();
	}
	
	public Optional<Patient> merge(PatientIdentifier target, PatientIdentifier source)
		throws MpiProviderException {
		try {
			Optional<Patient> merged = Optional.empty();				
			MergePatientResult result = mpi.mergeSystemRecord(target.getSystem(), source.getValue(), target.getValue(), false);					
			return merged;		
		} catch (ProcessingException_Exception | UserException_Exception ex) {
			throw new MpiProviderException(ex);
		}
	}

	public Optional<Patient> unmerge(PatientIdentifier target, PatientIdentifier source)
		throws MpiProviderException {
		try {
			Optional<Patient> unmerged = Optional.empty();				
			MergePatientResult result = mpi.unmergeSystemRecord(target.getSystem(), source.getValue(), target.getValue(), false);		
			return unmerged;	
		} catch (ProcessingException_Exception | UserException_Exception ex) {
			throw new MpiProviderException(ex);
		}		
	}
	
	public Optional<Patient> link(PatientIdentifier target, PatientIdentifier source)
		throws MpiProviderException {
		try {
			Optional<Patient> linked = Optional.empty();
			String targetEuid = mpi.getEUID(target.getSystem(), target.getValue());
			String sourceEuid = mpi.getEUID(source.getSystem(), source.getValue());
			MergePatientResult result = mpi.mergeEnterpriseRecord(sourceEuid, targetEuid, false);		
			return linked;
		} catch (ProcessingException_Exception | UserException_Exception ex) {
			throw new MpiProviderException(ex);
		}		
	}
	
	public Optional<Patient> unlink(PatientIdentifier identifier)
		throws MpiProviderException {
		try {
			Optional<Patient> unlinked = Optional.empty();
			String euid = mpi.getEUID(identifier.getSystem(), identifier.getValue());
			MergePatientResult result = mpi.unmergeEnterpriseRecord(euid, false);		
			return unlinked;
		} catch (ProcessingException_Exception | UserException_Exception ex) {
			throw new MpiProviderException(ex);
		}
	}
		
	public Optional<Patient> save(Patient patient)
		throws MpiProviderException {
		try {			
			Optional<Patient> created = Optional.empty();
			SystemPatient systemPatient = new SystemPatient();
			PatientBean beanPatient = new PatientBean();
			
			List<PatientHumanName> names = patient.getNames();
			PatientHumanName name = names.get(0);
			String gender = patient.getGender();			
			Date date = patient.getBirthDate();
			List<PatientAddress> addresses = patient.getAddresses();
			PatientAddress address = addresses.get(0);
			
			beanPatient.setFirstName(name.getGiven());
			beanPatient.setLastName(name.getFamily());
			beanPatient.setGender(gender);
			beanPatient.setDOB(date.toString());
			List<AddressBean> addressBeans = beanPatient.getAddress();
			AddressBean addressBean = new AddressBean();
			addressBean.setAddressType(address.getType());
			addressBean.setAddressLine1(address.getLine().toString());			
			addressBeans.add(addressBean);
						
			List<PatientIdentifier> identifiers = patient.getIdentifiers();
			PatientIdentifier localId = identifiers.get(0);
			
			systemPatient.setSystemCode(localId.getSystem());
			systemPatient.setLocalId(localId.getValue());
			systemPatient.setPatient(beanPatient);
			MatchColResult result = mpi.executeMatchUpdate(systemPatient);					
		    int code = result.getResultCode();	
			if ( code == 1 /* NEW_EO */ || 
			     code == 2 /* SYS_ID_MATCH */ || 
			     code == 3 /* ASSUMED_MATCH */) {		    		
				 created.of(patient);
			} else {
				throw new MpiProviderException("mpi failed to execute match");
			}
			return created;
		} catch (ProcessingException_Exception | UserException_Exception ex) {
			throw new MpiProviderException(ex);
		}
	}
	
	
}
