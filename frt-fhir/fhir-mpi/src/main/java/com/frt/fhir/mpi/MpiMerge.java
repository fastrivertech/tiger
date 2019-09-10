package com.frt.fhir.mpi;

import java.util.List;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Patient.PatientLinkComponent;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Identifier.IdentifierUse;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Address.AddressUse;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;


public class MpiMerge {

	public static Patient execute(Patient source, Patient  target) {
		
		PatientLinkComponent sourceLink = new PatientLinkComponent();
		sourceLink.setId(source.getId());
		target.addLink(sourceLink);

		PatientLinkComponent targetLink = new PatientLinkComponent();
		source.addLink(targetLink);
		source.setActive(false);

		List<Identifier> identifiers = source.getIdentifier();
		identifiers.forEach(identifier->{
			if (identifier.getSystem().contains(("localid"))) {
				identifier.setUse(IdentifierUse.OLD);
			}
			target.addIdentifier(identifier);
		});
		
		List<Address> addresses = source.getAddress();
		addresses.forEach(address->{
			address.setUse(AddressUse.OLD);
			target.addAddress(address);
		});
		
		List<HumanName> names = source.getName();
		names.forEach(name->{
			name.setUse(NameUse.OLD);
			target.addName(name);
		});
		
		
		return target;
	}
	
}
