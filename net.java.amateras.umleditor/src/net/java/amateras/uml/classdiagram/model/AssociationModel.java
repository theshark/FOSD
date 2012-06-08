package net.java.amateras.uml.classdiagram.model;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.model.AbstractUMLConnectionModel;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.properties.AnnotationTypePropertyDescriptor;

import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class AssociationModel extends AbstractUMLConnectionModel implements StereoTypeModel {

	private String stereoType = "";

	private String fromMultiplicity = "";

	private String toMultiplicity = "";
	
	public static final String P_FROM_MULTIPLICITY = "_from";

	public static final String P_TO_MULTIPLICITY = "_to";

	public void setStereoType(String stereoType) {
		this.stereoType = stereoType;
		firePropertyChange(StereoTypeModel.P_STEREO_TYPE, null, stereoType);
	}

	public String getStereoType() {
		return this.stereoType;
	}

	public void setFromMultiplicity(String fromMultiplicity) {
		fromMultiplicity = fromMultiplicity.trim();
		
		this.fromMultiplicity = fromMultiplicity;
		firePropertyChange(P_FROM_MULTIPLICITY, null, fromMultiplicity);
	}

	public String getFromMultiplicity() {
		return this.fromMultiplicity;
	}

	public void setToMultiplicity(String toMultiplicity) {
		toMultiplicity = toMultiplicity.trim();
		
		this.toMultiplicity = toMultiplicity;
		firePropertyChange(P_TO_MULTIPLICITY, null, toMultiplicity);
	}

	public String getToMultiplicity() {
		return this.toMultiplicity;
	}
	
	public void setSzType(String szType) {
		
		if ( !getSzType().equals("Link") && szType.equals("Link")) {
			super.setSzType(szType);
			
			setSzSource(getSzFromLabel());
			firePropertyChange(P_SZ_SOURCE, null, getSzSource());
			firePropertyChange(P_SZ_SOURCE_DECORATION, null, "");
			
			setSzTarget(getSzToLabel());
			firePropertyChange(P_SZ_TARGET, null, getSzTarget());
			firePropertyChange(P_SZ_TARGET_DECORATION, null, "");
			
			this.szTable = ((ClassModel)getSource()).getName() + "_" + ((ClassModel)getTarget()).getName();
			
			firePropertyChange(P_SZ_TABLE, "", this.szTable);
			
			return;
		}
		
		if ( !getSzType().equals("Compartment") && szType.equals("Compartment")) {
			super.setSzType(szType);

			this.szTable = ((ClassModel)getSource()).getName() + "s";
			
			firePropertyChange(P_SZ_TABLE, "", this.szTable);
			
			return;
		}
		
		super.setSzType(szType);
		
	}
	
	public void setSource(AbstractUMLEntityModel model) {
		super.setSource(model);
		
		if(this.fromMultiplicity.equals("")) {
			fromMultiplicity="0..*";
			firePropertyChange(P_FROM_MULTIPLICITY, null, fromMultiplicity);
		}
	}

	public void setTarget(AbstractUMLEntityModel model) {
		super.setTarget(model);
		
		if(this.toMultiplicity.equals("")) {
			toMultiplicity="0..*";
			firePropertyChange(P_TO_MULTIPLICITY, null, toMultiplicity);
		}
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		
		//TextPropertyDescriptor aType = new TextPropertyDescriptor(P_SZ_TYPE, UMLPlugin.getDefault().getResourceString("property.szType"));
		
		AnnotationTypePropertyDescriptor aType = new AnnotationTypePropertyDescriptor(P_SZ_TYPE, UMLPlugin.getDefault().getResourceString("property.szType"), new String[] {"None","Link","Compartment"});

		TextPropertyDescriptor aLabel =	new TextPropertyDescriptor(P_SZ_LABEL, UMLPlugin.getDefault().getResourceString("property.szLabel"));

		
		TextPropertyDescriptor aTable =	new TextPropertyDescriptor(P_SZ_TABLE, UMLPlugin.getDefault().getResourceString("property.szTableName"));
		TextPropertyDescriptor aSource = new TextPropertyDescriptor(P_SZ_SOURCE, UMLPlugin.getDefault().getResourceString("property.szSource"));
		//TextPropertyDescriptor aSourceDec =	new TextPropertyDescriptor(P_SZ_SOURCE_DECORATION, UMLPlugin.getDefault().getResourceString("property.szSourceDecoration"));
		AnnotationTypePropertyDescriptor aSourceDec = new AnnotationTypePropertyDescriptor(P_SZ_SOURCE_DECORATION, UMLPlugin.getDefault().getResourceString("property.szSourceDecoration"), new String[] {"arrow","closedarrow","filledclosedarrow","rhomb","filledrhomb","square","filledsquare",""});

		TextPropertyDescriptor aTarget = new TextPropertyDescriptor(P_SZ_TARGET, UMLPlugin.getDefault().getResourceString("property.szTarget"));
		//TextPropertyDescriptor aTargetDec =	new TextPropertyDescriptor(P_SZ_TARGET_DECORATION, UMLPlugin.getDefault().getResourceString("property.szTargetDecoration"));	
		AnnotationTypePropertyDescriptor aTargetDec = new AnnotationTypePropertyDescriptor(P_SZ_TARGET_DECORATION, UMLPlugin.getDefault().getResourceString("property.szTargetDecoration"), new String[] {"arrow","closedarrow","filledclosedarrow","rhomb","filledrhomb","square","filledsquare",""});

		aType.setCategory("Annotation");
		aTable.setCategory("Annotation");
		aLabel.setCategory("Annotation");
		aSource.setCategory("Annotation");
		aSourceDec.setCategory("Annotation");
		aTarget.setCategory("Annotation");
		aTargetDec.setCategory("Annotation");
		
		TextPropertyDescriptor STEREO_TYPE = new TextPropertyDescriptor(StereoTypeModel.P_STEREO_TYPE, UMLPlugin
				.getDefault().getResourceString("property.stereoType"));
		ColorPropertyDescriptor FOREGROUND_COLOR = new ColorPropertyDescriptor(P_FOREGROUND_COLOR, UMLPlugin
				.getDefault().getResourceString("property.foreground"));
		
		AnnotationTypePropertyDescriptor FROM_MULTIPLICITY = new AnnotationTypePropertyDescriptor(P_FROM_MULTIPLICITY, UMLPlugin.getDefault().getResourceString("property.multiplicityA"), new String[] {"0..*","1..*","1..1",""});

//		TextPropertyDescriptor FROM_MULTIPLICITY =new TextPropertyDescriptor(P_FROM_MULTIPLICITY, UMLPlugin
//				.getDefault().getResourceString("property.multiplicityA"));
		
		AnnotationTypePropertyDescriptor TO_MULTIPLICITY = new AnnotationTypePropertyDescriptor(P_TO_MULTIPLICITY, UMLPlugin.getDefault().getResourceString("property.multiplicityB"), new String[] {"0..*","1..*","1..1",""});

//		TextPropertyDescriptor TO_MULTIPLICITY=  new TextPropertyDescriptor(P_TO_MULTIPLICITY, UMLPlugin
//				.getDefault().getResourceString("property.multiplicityB"));
		
		TextPropertyDescriptor SZ_FROM_LABEL = new TextPropertyDescriptor(P_SZ_FROM_LABEL, UMLPlugin
				.getDefault().getResourceString("property.szFromLabel"));
		TextPropertyDescriptor SZ_TO_LABEL = new TextPropertyDescriptor(P_SZ_TO_LABEL, UMLPlugin
				.getDefault().getResourceString("property.szToLabel"));
		
				
		STEREO_TYPE.setCategory("Association");
		FOREGROUND_COLOR.setCategory("Association");
		FROM_MULTIPLICITY.setCategory("Association");
		TO_MULTIPLICITY.setCategory("Association");
		SZ_FROM_LABEL.setCategory("Association");
		SZ_TO_LABEL.setCategory("Association");
				
				
		return new IPropertyDescriptor[] {
				STEREO_TYPE, FOREGROUND_COLOR, FROM_MULTIPLICITY, TO_MULTIPLICITY, SZ_FROM_LABEL, SZ_TO_LABEL,
				aType,aLabel,aTable,aSource,aSourceDec,aTarget,aTargetDec,
		};
	}

	public Object getPropertyValue(Object id) {
		if (id.equals(StereoTypeModel.P_STEREO_TYPE)) {
			return getStereoType();
		} else if (id.equals(P_FROM_MULTIPLICITY)) {
			return getFromMultiplicity();
		} else if (id.equals(P_TO_MULTIPLICITY)) {
			return getToMultiplicity();
		} 
		
		return super.getPropertyValue(id);
	}

	public void setPropertyValue(Object id, Object value) {
		if (id.equals(StereoTypeModel.P_STEREO_TYPE)) {
			setStereoType((String) value);
		} else if (id.equals(P_FROM_MULTIPLICITY)) {
			setFromMultiplicity((String) value);
		} else if (id.equals(P_TO_MULTIPLICITY)) {
			setToMultiplicity((String) value);
		} 
		
		super.setPropertyValue(id, value);
	}
}