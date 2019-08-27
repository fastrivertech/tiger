package com.frt.mpi;

import java.util.List;
import java.util.Date;
import java.util.Optional;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
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
	
	public Optional<Patient> merge(Identifier target, Identifier source)
		throws MpiProviderException {
		try {
			Optional<Patient> merged = Optional.empty();				
			MergePatientResult result = mpi.mergeSystemRecord(target.getSystem(), source.getValue(), target.getValue(), false);					
			return merged;		
		} catch (ProcessingException_Exception | UserException_Exception ex) {
			throw new MpiProviderException(ex);
		}
	}

	public Optional<Patient> unmerge(Identifier target, Identifier source)
		throws MpiProviderException {
		try {
			Optional<Patient> unmerged = Optional.empty();				
			MergePatientResult result = mpi.unmergeSystemRecord(target.getSystem(), source.getValue(), target.getValue(), false);		
			return unmerged;	
		} catch (ProcessingException_Exception | UserException_Exception ex) {
			throw new MpiProviderException(ex);
		}		
	}
	
	public Optional<Patient> link(Identifier target, Identifier source)
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
	
	public Optional<Patient> unlink(Identifier identifier)
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
	
	public Optional<Patient> update(Patient patient)
		throws MpiProviderException {
		try {
			Optional<Patient> created = Optional.empty();
			SystemPatient systemPatient = new SystemPatient();
			PatientBean beanPatient = new PatientBean();
			
			List<HumanName> names = patient.getName();
			HumanName name = names.get(0);
			AdministrativeGender gender = patient.getGender();			
			Date date = patient.getBirthDate();
			List<Address> addresses = patient.getAddress();
			Address address = addresses.get(0);
			
			beanPatient.setFirstName(name.getGivenAsSingleString());
			beanPatient.setLastName(name.getFamily());
			beanPatient.setGender(gender.name());
			beanPatient.setDOB(date.toString());
			List<AddressBean> addressBeans = beanPatient.getAddress();
			AddressBean addressBean = new AddressBean();
			addressBean.setAddressType(address.getType().name());
			addressBean.setAddressLine1(address.getLine().toString());			
			addressBeans.add(addressBean);
						
			List<Identifier> identifiers = patient.getIdentifier();
			Identifier localId = identifiers.get(0);
			
			systemPatient.setSystemCode(localId.getSystem());
			systemPatient.setLocalId(localId.getValue());
			systemPatient.setPatient(beanPatient);
			MatchColResult result = mpi.executeMatchUpdate(systemPatient);		
			return created;
		} catch (ProcessingException_Exception | UserException_Exception ex) {
			throw new MpiProviderException(ex);
		}
	}
	
	
}
