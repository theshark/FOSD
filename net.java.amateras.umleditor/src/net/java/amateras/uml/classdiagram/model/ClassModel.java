package net.java.amateras.uml.classdiagram.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.model.AbstractUMLConnectionModel;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.properties.AnnotationLabelPropertyDescriptor;
import net.java.amateras.uml.properties.AnnotationTypePropertyDescriptor;
import net.java.amateras.uml.properties.BooleanPropertyDescriptor;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Class model that contains the data of a class shown in class diagram.
 *
 * @author Naoki Takezoe
 */
public class ClassModel extends CommonEntityModel {

	public static final String P_ABSTRACT = "_abstract";

	public static final String P_FILTER = "_filter";

	private boolean isAbstract = false;

	private static int number = 1;

	List<String> fields;


	//private AnnotationLabelPropertyDescriptor aLabel;

	/**
	 * Default constructor
	 */
	public ClassModel() {
		setName("Class" + number);
		number++;



		fields = new ArrayList<String>();

		fields.add("No Label");

		//TextPropertyDescriptor aType = new TextPropertyDescriptor(P_SZ_TYPE, UMLPlugin.getDefault().getResourceString("property.szType"));

		AnnotationTypePropertyDescriptor aType = new AnnotationTypePropertyDescriptor(P_SZ_TYPE, UMLPlugin.getDefault().getResourceString("property.szType"), new String[] {"None","Node","Link"});

		PropertyDescriptor aTable = new PropertyDescriptor(P_SZ_TABLE, UMLPlugin.getDefault().getResourceString("property.szTableName"));

		//TextPropertyDescriptor aLabel =	new TextPropertyDescriptor(P_SZ_LABEL, UMLPlugin.getDefault().getResourceString("property.szLabel"));

		AnnotationLabelPropertyDescriptor aLabel = new AnnotationLabelPropertyDescriptor(P_SZ_LABEL, UMLPlugin.getDefault().getResourceString("property.szLabel"),fields);

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


		TextPropertyDescriptor STEREO_TYPE = new TextPropertyDescriptor(StereoTypeModel.P_STEREO_TYPE, UMLPlugin.getDefault().getResourceString("property.stereoType"));
		PropertyDescriptor SIMPLE_ENTITY_NAME = new PropertyDescriptor(P_SIMPLE_ENTITY_NAME, UMLPlugin.getDefault().getResourceString("property.simpleName"));
		TextPropertyDescriptor ENTITY_NAME = new TextPropertyDescriptor(P_ENTITY_NAME, UMLPlugin.getDefault().getResourceString("property.name"));
		ColorPropertyDescriptor BACKGROUND_COLOR = new ColorPropertyDescriptor(P_BACKGROUND_COLOR, UMLPlugin.getDefault().getResourceString("property.background"));
		PropertyDescriptor ATTRIBUTES = new PropertyDescriptor(P_ATTRIBUTES, UMLPlugin.getDefault().getResourceString("property.attributes"));
		PropertyDescriptor OPERATTIONS = new PropertyDescriptor(P_OPERATIONS, UMLPlugin.getDefault().getResourceString("property.operations"));
		BooleanPropertyDescriptor ABSTRACT = new BooleanPropertyDescriptor(P_ABSTRACT, "Abstract");

		STEREO_TYPE.setCategory("Class properties");
		SIMPLE_ENTITY_NAME.setCategory("Class properties");
		ENTITY_NAME.setCategory("Class properties");
		BACKGROUND_COLOR.setCategory("Class properties");
		ATTRIBUTES.setCategory("Class properties");
		OPERATTIONS.setCategory("Class properties");
		ABSTRACT.setCategory("Class properties");

		propertyDescriptors = new IPropertyDescriptor[] {
				STEREO_TYPE, SIMPLE_ENTITY_NAME, ENTITY_NAME,BACKGROUND_COLOR,ATTRIBUTES,OPERATTIONS, ABSTRACT,
				aTable,aType,aLabel,aSource,aSourceDec,aTarget,aTargetDec,
		};
	}

	/**
	 * Copy constructor, copy the given ClassModel model to this
	 * @param toCopy ClassModel to copy
	 */
	public ClassModel(ClassModel toCopy) {
		super(toCopy);
		setAbstract(toCopy.isAbstract());
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
		firePropertyChange(P_ABSTRACT, null, new Boolean(isAbstract));
	}

	public Object getPropertyValue(Object id) {
		if (id.equals(P_ABSTRACT)) {
			return new Boolean(isAbstract());
		}
		if (id.equals(P_ATTRIBUTES) && (getSzType().equals("Node")  || getSzType().equals("Link"))) {
			fields.clear();
			fields.add("No Label");
			List<AbstractUMLModel> children = getChildren();
			for (int i = 0; i < children.size(); i++) {
				AbstractUMLModel child = children.get(i);
				if (child instanceof AttributeModel) {
					fields.add(((AttributeModel) child).getName());
				}
			}
			
			addAllParentAttributes(this);

		}
		return super.getPropertyValue(id);
	}
	
	//Recursively add all inherited attributes to current class' fields
	private void addAllParentAttributes (ClassModel model) {
		List<AbstractUMLConnectionModel> sources = model.getModelSourceConnections();
		for (int i = 0; i < sources.size(); i++) {
			AbstractUMLConnectionModel acm = sources.get(i);
			if (acm instanceof GeneralizationModel) {
				List<AbstractUMLModel> parents = acm.getTarget().getChildren();
				for (int j = 0; j < parents.size(); j++) {
					AbstractUMLModel attribute = parents.get(j);
					if (attribute instanceof AttributeModel) {
						fields.add(((AttributeModel) attribute).getName());
					}
				}
				
				if (acm.getTarget() instanceof ClassModel) {
					addAllParentAttributes((ClassModel)acm.getTarget());
				}
		
			}
		}
	}

	public boolean isPropertySet(Object id) {
		if (id.equals(P_ABSTRACT)) {
			return true;
		}
		return super.isPropertySet(id);
	}

	public void setPropertyValue(Object id, Object value) {
		if (id.equals(P_ABSTRACT)) {
			setAbstract(((Boolean) value).booleanValue());
		}
		super.setPropertyValue(id, value);
	}

	
	public void setSzSource(String szSource) {
	
		if(szSource.trim().equals("")) {
			super.setSzSource("");
			return;
		}
		
		boolean found = false;
		List<AbstractUMLConnectionModel> children = getModelSourceConnections();
		
		for (int i = 0; i < children.size(); i++) {
			
			AbstractUMLConnectionModel acm = children.get(i);
			
			if(acm.getSzFromLabel().equals(szSource) || acm.getSzToLabel().equals(szSource)) {
				super.setSzSource(szSource);
				return;
			}
		}
		
		children = getModelTargetConnections();
		
		for (int i = 0; i < children.size(); i++) {
			
			AbstractUMLConnectionModel acm = children.get(i);
			
			if(acm.getSzFromLabel().equals(szSource) || acm.getSzToLabel().equals(szSource)) {
				super.setSzSource(szSource);
				return;
			}
		}
		
		MessageBox M = new MessageBox(new Shell());
		M.setMessage("Could not find association end label for Link Target");
		M.open();
		
		//javax.swing.JOptionPane.showMessageDialog(new javax.swing.JFrame(), "Could not find association end label for Link source");
	}
	
	public void setSzTarget(String szSource) {
		
		if(szSource.trim().equals("")) {
			super.setSzTarget("");
			return;
		}
 		
		boolean found = false;
		List<AbstractUMLConnectionModel> children = getModelSourceConnections();
		
		for (int i = 0; i < children.size(); i++) {
			
			AbstractUMLConnectionModel acm = children.get(i);
			
			if(acm.getSzFromLabel().equals(szSource) || acm.getSzToLabel().equals(szSource)) {
				super.setSzTarget(szSource);
				return;
			}
		}
		
		children = getModelTargetConnections();
		
		for (int i = 0; i < children.size(); i++) {
			
			AbstractUMLConnectionModel acm = children.get(i);
			
			if(acm.getSzFromLabel().equals(szSource) || acm.getSzToLabel().equals(szSource)) {
				super.setSzTarget(szSource);
				return;
			}
		}
		
		
		MessageBox M = new MessageBox(new Shell());
		M.setMessage("Could not find association end label for Link Target");
		M.open();
	}
	
	public void setSzType(String szType) {
		
		if(szType.equals("None") || szType.equals("Compartment")) {
			fields.clear();
			fields.add("No Label");
			setSzLabel("");
		} else {
			fields.clear();
			fields.add("No Label");
			List<AbstractUMLModel> children = getChildren();
			for (int i = 0; i < children.size(); i++) {
				AbstractUMLModel child = children.get(i);
				if (child instanceof AttributeModel) {
					fields.add(((AttributeModel) child).getName());
				}
			}
		}
		super.setSzType(szType);
	}



	/**
	 * Clone this object. TODO, duplicated code with InterfaceModel.clone(), be careful to override clone!
	 *@deprecated
	 */
	public Object clone() {
		ClassModel newModel = new ClassModel();

		newModel.setAbstract(isAbstract());
		newModel.setBackgroundColor(getBackgroundColor().getRGB());
		newModel.setConstraint(new Rectangle(getConstraint()));
		newModel.setForegroundColor(getForegroundColor().getRGB());
		newModel.setName(getName());
		newModel.setSimpleName(getSimpleName());
		newModel.setParent(getParent());
		newModel.setShowIcon(isShowIcon());
		newModel.setStereoType(getStereoType());

		List<AbstractUMLModel> children = getChildren();
		for (int i = 0; i < children.size(); i++) {
			AbstractUMLModel child = children.get(i);
			if (child instanceof AttributeModel) {
				newModel.addChild((AttributeModel) ((AttributeModel) child).clone());
			} else if (child instanceof OperationModel) {
				newModel.addChild((OperationModel) ((OperationModel) child).clone());
			}
		}

		return newModel;
	}

}
