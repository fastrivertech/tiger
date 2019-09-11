package com.frt.fhir.mpi;

import java.util.List;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Patient.PatientLinkComponent;
import org.hl7.fhir.r4.model.Patient.LinkType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Identifier.IdentifierUse;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Address.AddressUse;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;

public class MpiMerge {

	public static Patient execute(Patient source, Patient  target) {
		
	    PatientLinkComponent sourceLink = new PatientLinkComponent();
		sourceLink.setType(LinkType.REPLACES);
		Reference replaces = new Reference();
		replaces.setReference(source.getId());
		sourceLink.setOther(replaces);
		target.addLink(sourceLink);

		PatientLinkComponent targetLink = new PatientLinkComponent();
		targetLink.setType(LinkType.REPLACEDBY);
		Reference replaced = new Reference();
		replaced.setReference(target.getId());
		targetLink.setOther(replaced);
		source.addLink(targetLink);
			 
		List<Identifier> identifiers = source.getIdentifier();
		identifiers.forEach(identifier->{
			Identifier instance = identifier.copy();			
			if (instance.getSystem().contains(("localid"))) {
				instance.setUse(IdentifierUse.OLD);
			}
			target.addIdentifier(instance);
		});		
		List<Address> addresses = source.getAddress();
		addresses.forEach(address->{
			Address instance = address.copy();
			instance.setUse(AddressUse.OLD);
			target.addAddress(instance);
		});		
		List<HumanName> names = source.getName();
		names.forEach(name->{
			HumanName instance = name.copy();
			instance.setUse(NameUse.OLD);
			target.addName(instance);
		});
		
		source.setActive(false);
		
		return target;
	}
	
}
