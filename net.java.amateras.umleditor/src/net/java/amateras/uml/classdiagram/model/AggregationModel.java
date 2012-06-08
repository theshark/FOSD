package net.java.amateras.uml.classdiagram.model;

import net.java.amateras.uml.model.AbstractUMLEntityModel;


public class AggregationModel extends AssociationModel {

	public void setSource(AbstractUMLEntityModel model) {
		super.setSource(model);

		if(this.getFromMultiplicity().equals("")) {
			setFromMultiplicity("0..*");
			firePropertyChange(P_FROM_MULTIPLICITY, null, getFromMultiplicity());
		}
	}

	public void setTarget(AbstractUMLEntityModel model) {
		super.setTarget(model);

		if(this.getToMultiplicity().equals("0..*")) {
			setToMultiplicity("0..1");
			firePropertyChange(P_TO_MULTIPLICITY, null, getToMultiplicity());
		}
	}
	
	
}
