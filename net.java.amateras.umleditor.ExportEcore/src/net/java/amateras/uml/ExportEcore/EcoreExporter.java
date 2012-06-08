/**
 * 
 */
package net.java.amateras.uml.ExportEcore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.ClassModel;
import net.java.amateras.uml.classdiagram.model.InterfaceModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.classdiagram.model.Visibility;
import net.java.amateras.uml.classdiagram.model.GeneralizationModel;
import net.java.amateras.uml.classdiagram.model.RealizationModel;
import net.java.amateras.uml.classdiagram.model.AssociationModel;
import net.java.amateras.uml.classdiagram.model.AggregationModel;
import net.java.amateras.uml.classdiagram.model.CompositeModel;
import net.java.amateras.uml.model.AbstractUMLConnectionModel;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author shida
 * 
 */
public class EcoreExporter {
	private EcorePackage epackage = EcorePackage.eINSTANCE;
	private EcoreFactory efactory = EcoreFactory.eINSTANCE;

	private EPackage eroot;

	private Map typeMap = new HashMap();

	public boolean valid = true;

	public EcoreExporter(String PackageName) {

		eroot = efactory.createEPackage();
		eroot.setName(PackageName);
		eroot.setNsURI("http://"+PackageName+"/1.0");

		//Every class has this boiler plate annotation
		EAnnotation annot = efactory.createEAnnotation();
		annot.setSource("gmf");
		annot.getDetails().put("onefile","true");
		annot.getDetails().put("diagram.extension","Graph");
		eroot.getEAnnotations().add(annot);

		EDataType shortType = epackage.getEShort();
		EDataType intType = epackage.getEInt();
		EDataType longType = epackage.getELong();
		EDataType floatType = epackage.getEFloat();
		EDataType doubleType = epackage.getEDouble();
		EDataType booleanType = epackage.getEBoolean();
		EDataType charType = epackage.getEChar();
		EDataType stringType = epackage.getEString();
		EDataType dateType = epackage.getEDate();
		EClass classType = epackage.getEClass();

		typeMap.put("short", shortType);
		typeMap.put("int", intType);
		typeMap.put("long", longType);
		typeMap.put("float", floatType);
		typeMap.put("double", doubleType);
		typeMap.put("boolean", booleanType);
		typeMap.put("char", charType);
		typeMap.put("string", stringType);
		typeMap.put("date", dateType);
		typeMap.put("class", classType);
	}

	//Methods used by XMIExportWizard
	public void convertType(AbstractUMLEntityModel model) {
		if (model instanceof ClassModel) {
			createClass((ClassModel) model);
		}
	}

	//Build up the EClasses
	public void convertStructure(AbstractUMLEntityModel model) {
		if (model.getSzType().equals("Node")) {
			addAttributes(model);
		} else if (model.getSzType().equals("Link")) {
			addLinkClass(model);
		} else if (model.getSzType().equals("None")) {
			addNoneClass(model);
		}
	}

	//Create all the EReferences and related stuff
	public void convertLink(AbstractUMLEntityModel model) {
		createReference(model);
	}

	//Create the Diagram EClassifier for encapsulation of classes
	public void createDiagram() {

		if(!valid) return;

		EClass eclass = efactory.createEClass();
		eclass.setName("Diagram");
		EAnnotation annot = efactory.createEAnnotation();
		annot.setSource("gmf.diagram");
		annot.getDetails().put("foo","bar");
		eclass.getEAnnotations().add(annot);

		//Create a EReference containment linked to all classes that are not contained
		EList classList = eroot.getEClassifiers();
		for (int i = 0; i < classList.size(); i++) {
			EClassifier element = (EClassifier) classList.get(i);
			EList elemAnnot = element.getEAnnotations();
			if (elemAnnot.size() > 0) {
				//Check if it has any annotations
				EAnnotation type = (EAnnotation) elemAnnot.get(0);
				if (!type.getSource().equals("gmf.compartment")) {
					//Check if it's a container
					EReference assoc = efactory.createEReference();
					assoc.setName(element.getName());
					assoc.setUpperBound(-1);
					assoc.setEType(element);
					assoc.setContainment(true);

					eclass.getEStructuralFeatures().add(assoc);
				}
			}
		}

		eroot.getEClassifiers().add(eclass);
	}

	public EPackage getRoot() {
		return eroot;
	}

	private void createClass(ClassModel model) {

		if(!valid) return;

		EClass eclass = efactory.createEClass();
		eclass.setName(getSimpleName(model.getName()));

		List attrs = getAttributes(model);

		for (Iterator iter = attrs.iterator(); iter.hasNext();) {
			AttributeModel element = (AttributeModel) iter.next();
			EAttribute eattr = efactory.createEAttribute();
			eattr.setName(element.getName());
			if (typeMap.get(element.getType()) == null) {
				createEDataType(element.getType());
			}
			EDataType t = (EDataType) typeMap.get(element.getType());
			eattr.setEType(t);
			eclass.getEStructuralFeatures().add(eattr);
		}

		typeMap.put(eclass.getName(), eclass);
	}

	private EDataType createEDataType(String fqcn) {
		String simpleName = getSimpleName(fqcn);
		EDataType etype = efactory.createEDataType();
		etype.setName(simpleName);
		typeMap.put(fqcn, etype);
		return etype;
	}

	//Create the Node-tye EClasses by adding attributes etc
	private void addAttributes(AbstractUMLEntityModel model) {

		if(!valid) return;

		EClass eclass = (EClass) typeMap.get(getName(model));
		EAnnotation annot = efactory.createEAnnotation();
		String type = getAnnotationType(model.getSzType());
		annot.setSource(type);

		if (!model.getSzLabel().equals("")) {
			annot.getDetails().put("label", model.getSzLabel());
		} else {
			MessageDialog.openError(new Shell(), "Error", "Entity " +getName(model) + " does not have a label.");
			this.valid = false;
			return;
		}
		boolean found = false;
		for(AttributeModel a : getAttributesT(model)) {
			if(a.getName().equals( model.getSzLabel())) {
				found = true; break;
			}
		}

		if(!found) {
			MessageDialog.openError(new Shell(), "Error", "Label " +model.getSzLabel() + " is not an attribute of " + getName(model));
			this.valid = false;
			return;
		}

		eclass.getEAnnotations().add(annot);

		eroot.getEClassifiers().add(eclass);
	}

	private void addNoneClass(AbstractUMLEntityModel model) {
		EClass eclass = (EClass) typeMap.get(getName(model));
		eroot.getEClassifiers().add(eclass);		
	}

	//Handles the Link-type classes
	private void addLinkClass(AbstractUMLEntityModel model) {

		if(!valid) return;

		EClass eclass = (EClass) typeMap.get(getName(model));

		String sourceLabel = model.getSzSource().trim();
		String targetLabel = model.getSzTarget().trim();
		
		System.err.println("*"+sourceLabel+"*");

		if(sourceLabel.equals(targetLabel)) {
			MessageDialog.openError(new Shell(), "Error", "Entity " +getName(model) + " has the same link source and target.");
			valid = false;
			return;
		}

		//Get all the connections coming into the model
		List<AbstractUMLConnectionModel> connections = new ArrayList<AbstractUMLConnectionModel>();
		connections.addAll(model.getModelSourceConnections());
		connections.addAll(model.getModelTargetConnections());

		EClass linkSource = null;
		EClass linkTarget = null;

		boolean sourceFound = false;
		boolean targetFound = false;

		for (int i = 0; i < connections.size(); i++) {

			AbstractUMLConnectionModel element = (AbstractUMLConnectionModel) connections.get(i);

			if (element instanceof AssociationModel) {

				AssociationModel am = (AssociationModel) element;

				if (am.getSzType().equals("None")) {

					AbstractUMLEntityModel aSource = am.getSource();
					AbstractUMLEntityModel aTarget = am.getTarget();

					String aSourceName = getSimpleName(getName(aSource));
					String aTargetName = getSimpleName(getName(aTarget));

					System.err.println("+"+ am.getSzFromLabel()+"+");
					System.err.println("+"+ am.getSzToLabel()+"+");
					
					
					if(!sourceFound && am.getSzFromLabel().equals(sourceLabel)) {
						linkSource = (EClass) typeMap.get(aSourceName);
						sourceFound = true;
					}

					if(!sourceFound && am.getSzToLabel().equals(sourceLabel)) {
						linkSource = (EClass) typeMap.get(aTargetName);
						sourceFound = true;
					}

					if(!targetFound && am.getSzFromLabel().equals(targetLabel)) {
						linkTarget = (EClass) typeMap.get(aSourceName);
						targetFound = true;
					}

					if(!targetFound && am.getSzToLabel().equals(targetLabel)) {
						linkTarget = (EClass) typeMap.get(aTargetName);
						targetFound = true;
					}				
				}
			}
		}


		if( !sourceFound || !targetFound) {
			if(!sourceFound) MessageDialog.openError(new Shell(), "Error", "Entity " +getName(model) + " source link label "+sourceLabel+" not found.");
			if(!targetFound) MessageDialog.openError(new Shell(), "Error", "Entity " +getName(model) + " target link label not found.");
			valid = false;
			return;
		}

		EAnnotation annot = efactory.createEAnnotation();
		annot.setSource("gmf.link");

		if(!model.getSzLabel().equals("")) {
			
			annot.getDetails().put("label", model.getSzLabel());

			boolean found = false;
			for(AttributeModel a : getAttributesT(model)) {
				if(a.getName().equals( model.getSzLabel())) {
					found = true; break;
				}
			}

			if(!found) {
				MessageDialog.openError(new Shell(), "Error", "Label " +model.getSzLabel() + " is not an attribute of." + getName(model));
				this.valid = false;
				return;
			}
		}

		annot.getDetails().put("source", sourceLabel);

		if(!model.getSzSourceDecoration().equals("")) annot.getDetails().put("source.decoration", model.getSzSourceDecoration());
		annot.getDetails().put("target", targetLabel);
		if(!model.getSzTargetDecoration().equals("")) annot.getDetails().put("target.decoration", model.getSzTargetDecoration());	

		eclass.getEAnnotations().add(annot);

		//		//Create EAttribute
		//		EAttribute eattr = efactory.createEAttribute();
		//		eattr.setName(model.getSzLabel());
		//		EDataType t = (EDataType) typeMap.get("string");
		//		eattr.setEType(t);
		//		eclass.getEStructuralFeatures().add(eattr);

		//Create EReferences for the two links
		EReference assocSource = efactory.createEReference();
		assocSource.setName(sourceLabel);
		assocSource.setLowerBound(1);
		assocSource.setUpperBound(1);
		assocSource.setEType(linkSource);
		assocSource.setContainment(false);
		eclass.getEStructuralFeatures().add(assocSource);

		EReference assocTarget = efactory.createEReference();
		assocTarget.setName(targetLabel);
		assocTarget.setLowerBound(1);
		assocTarget.setUpperBound(1);
		assocTarget.setEType(linkTarget);
		assocTarget.setContainment(false);
		eclass.getEStructuralFeatures().add(assocTarget);

		eroot.getEClassifiers().add(eclass);		
	}

	private String getName(AbstractUMLEntityModel model) {
		if (model instanceof ClassModel) {
			ClassModel classModel = (ClassModel) model;
			return getSimpleName(classModel.getName());
		} else if (model instanceof InterfaceModel) {
			InterfaceModel interfaceModel = (InterfaceModel) model;
			return getSimpleName(interfaceModel.getName());
		}
		return "";
	}

	private List getAttributes(AbstractUMLEntityModel model) {
		List list = model.getChildren();
		List rv = new ArrayList();
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			AbstractUMLModel element = (AbstractUMLModel) iter.next();
			if (element instanceof AttributeModel) {
				rv.add((AttributeModel)element);
			}
		}
		return rv;
	}


	private List<AttributeModel> getAttributesT(AbstractUMLEntityModel model) {
		List list = model.getChildren();
		List<AttributeModel> rv = new ArrayList();
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			AbstractUMLModel element = (AbstractUMLModel) iter.next();
			if (element instanceof AttributeModel) {
				rv.add((AttributeModel)element);
			}
		}
		return rv;
	}

	private String getSimpleName(String fqcn) {
		String[] pkgs = fqcn.split("\\.");
		return pkgs[pkgs.length - 1];
	}

	private String getAnnotationType(String type) {
		String s = "";
		if (type.equals("Node")) {
			s = "gmf.node";
		} else if (type.equals("Link")) {
			s = "gmf.link";
		} else if (type.equals("Compartment")) {
			s = "gmf.compartment";
		} else {
			//Anything else?
		}
		return s;
	}

	//TODO: handle container references?
	private void createReference(AbstractUMLEntityModel model) {
		EClass source = (EClass) typeMap.get(getSimpleName(getName(model)));
		List connections = model.getModelSourceConnections();

		for (Iterator iter = connections.iterator(); iter.hasNext();) {
			AbstractUMLConnectionModel element = (AbstractUMLConnectionModel) iter.next();
			if (element instanceof GeneralizationModel) {
				EClass target = (EClass) typeMap.get(getSimpleName(getName(element.getTarget())));
				source.getESuperTypes().add(target);
			} else if (element instanceof AssociationModel) {

				AssociationModel am = (AssociationModel)element;

				if (!am.getSzType().equals("None")) {
					AbstractUMLEntityModel aSource = am.getSource();
					AbstractUMLEntityModel aTarget = am.getTarget();
					EClass tSource = (EClass) typeMap.get(getSimpleName(getName(aSource)));
					EClass tTarget = (EClass) typeMap.get(getSimpleName(getName(aTarget)));

					//Find the multiplicity
					String sBoundStr = am.getFromMultiplicity();
					String tBoundStr = am.getToMultiplicity();

					String sBoundSplt[] = sBoundStr.split("\\.\\.");
					String tBoundSplt[] = tBoundStr.split("\\.\\.");

					int sBound[] = {0,-1};
					int tBound[] = {0,-1};

					for(int i=0 ; i < sBoundSplt.length; i++) {
						if(i==0 && (sBoundSplt[i].trim().isEmpty() || sBoundSplt[i].trim().equals("*"))) break;
						if(i>=1 && sBoundSplt[i].trim().isEmpty()) sBound[i] = sBound[i-1];
						if(i>=1 && sBoundSplt[i].trim().equals("*")) break;
						sBound[i] = Integer.parseInt(sBoundSplt[i]);
					}
					for(int i=0 ; i < tBoundSplt.length; i++) {
						if(i==0 && (tBoundSplt[i].trim().isEmpty() || tBoundSplt[i].trim().equals("*"))) break;
						if(i>=1 && tBoundSplt[i].trim().isEmpty()) tBound[i] = tBound[i-1];
						if(i>=1 && tBoundSplt[i].trim().equals("*")) break;
						tBound[i] = Integer.parseInt(tBoundSplt[i]);
					}

					
					
					if( am.getSzType().equals("Link") ) {
						createLink(am, tSource, tTarget);
					}
					
					
					if (  am.getSzType().equals("Compartment") ) {
						
						if( ! (tBound[1] == -1 || sBound[1] == -1) ) {
							MessageDialog.openError(new Shell(), "Error", "Compartment " +am.getSzTable() + " does not have the proper multiplicity");
							this.valid = false;
							return;
						}
						
						EAnnotation annot = efactory.createEAnnotation();
						annot.setSource("gmf.compartment");
						annot.getDetails().put("layout","list");

						if (tBound[1] == -1) {
							EReference assocSource = efactory.createEReference();
							assocSource.setName(am.getSzTable());
							assocSource.setLowerBound(tBound[0]);
							assocSource.setUpperBound(tBound[1]);
							assocSource.setEType(tTarget);
							assocSource.setContainment(true);

							assocSource.getEAnnotations().add(annot);
							tSource.getEStructuralFeatures().add(assocSource);

						} else {
							EReference assocTarget = efactory.createEReference();
							assocTarget.setName(am.getSzTable());
							assocTarget.setLowerBound(sBound[0]);
							assocTarget.setUpperBound(sBound[1]);
							assocTarget.setEType(tSource);
							assocTarget.setContainment(true);

							assocTarget.getEAnnotations().add(annot);
							tTarget.getEStructuralFeatures().add(assocTarget);
						}
						
					}
//					//Create a many-to-many connection 
//					if (tBound[1] == -1 && sBound[1] == -1 && am.getSzType().equals("Link")) {
//
//						createLink(am, tSource, tTarget);
//
//					} else if (tBound[1] == -1 || sBound[1] == -1) {
//						//Create a one-to-many connection
//						
//					} else {
//						//TODO: Create a one-to-one connection
//					}
				}
			} else {
				//Anything else?
			}
		}
	}

	//This many-to-many code has been pulled out for now, but it's not being used in multiple places at this point
	//Will figure out Link-type classes before I worry about this...
	private void createLink(AssociationModel am, EClass tSource, EClass tTarget) {
		EClass elink = efactory.createEClass();

		elink.setName(am.getSzTable());

		EAnnotation annot = efactory.createEAnnotation();

		annot.setSource("gmf.link");
		if(!am.getSzLabel().equals("")) annot.getDetails().put("label", am.getSzLabel());
		annot.getDetails().put("source", am.getSzSource());
		annot.getDetails().put("target", am.getSzTarget());

		if(!am.getSzSourceDecoration().equals("")) annot.getDetails().put("source.decoration", am.getSzSourceDecoration());
		if(!am.getSzTargetDecoration().equals("")) annot.getDetails().put("target.decoration", am.getSzTargetDecoration());	

		elink.getEAnnotations().add(annot);

		//Create EReferences for the two links
		EReference assocSource = efactory.createEReference();
		assocSource.setName(am.getSzFromLabel());
		assocSource.setLowerBound(1);
		assocSource.setUpperBound(1);
		assocSource.setEType(tTarget);
		assocSource.setContainment(false);
		elink.getEStructuralFeatures().add(assocSource);

		EReference assocTarget = efactory.createEReference();
		assocTarget.setName(am.getSzToLabel());
		assocTarget.setLowerBound(1);
		assocTarget.setUpperBound(1);
		assocTarget.setEType(tSource);
		assocTarget.setContainment(false);
		elink.getEStructuralFeatures().add(assocTarget);

		if(!am.getSzLabel().equals("")) {
			//Create EAttribute for the label
			EAttribute eattr = efactory.createEAttribute();
			eattr.setName(am.getSzLabel());
			EDataType t = (EDataType) typeMap.get("string");
			eattr.setEType(t);
			elink.getEStructuralFeatures().add(eattr);
		}
		eroot.getEClassifiers().add(elink);


	}

}
