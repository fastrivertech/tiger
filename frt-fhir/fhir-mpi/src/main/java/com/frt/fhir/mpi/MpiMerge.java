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
		replaces.setDisplay(source.getName().get(0).getNameAsSingleString());
		sourceLink.setOther(replaces);
		target.addLink(sourceLink);

		PatientLinkComponent targetLink = new PatientLinkComponent();
		targetLink.setType(LinkType.REPLACEDBY);
		Reference replaced = new Reference();
		replaced.setReference(target.getId());
		replaced.setDisplay(target.getName().get(0).getNameAsSingleString());
		targetLink.setOther(replaced);
		source.addLink(targetLink);
			 
		List<Identifier> identifiers = source.getIdentifier();
		target.getIdentifier().clear();
		identifiers.forEach(identifier->{
			Identifier instance = identifier.copy();			
			if (instance.getSystem().contains(("localid")) ||
				instance.getSystem().contains(("urn:oid"))) {
				instance.setUse(IdentifierUse.OLD);
			}
			target.addIdentifier(instance);
		});		
		source.getIdentifier().clear();
		
		List<Address> addresses = source.getAddress();
		target.getAddress().clear();
		addresses.forEach(address->{
			Address instance = address.copy();
			instance.setUse(AddressUse.OLD);
			target.addAddress(instance);
		});
		source.getAddress().clear();
		
		List<HumanName> names = source.getName();
		target.getName().clear();
		names.forEach(name->{
			HumanName instance = name.copy();
			instance.setUse(NameUse.OLD);
			target.addName(instance);
		});
		source.getName().clear();
		
		source.setActive(false);
		
		return target;
	}
	
}
