package net.java.amateras.uml.model;

import java.util.ArrayList;
import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.model.ClassModel;
import net.java.amateras.uml.classdiagram.model.StereoTypeModel;
import net.java.amateras.uml.editpart.ConnectionBendpoint;

import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public abstract class AbstractUMLConnectionModel extends AbstractUMLModel {

	private AbstractUMLEntityModel source;

	private AbstractUMLEntityModel target;

	private String szFromLabel = "";
	private String szToLabel = "";

	private List<ConnectionBendpoint> bendpoints = new ArrayList<ConnectionBendpoint>();

	public static final String P_BEND_POINT = "_bend_point";
	
	public static final String P_SZ_FROM_LABEL = "_szFromLabel";
	
	public static final String P_SZ_TO_LABEL = "_szToLabel";
	
	public void addBendpoint(int index, ConnectionBendpoint point) {
		bendpoints.add(index, point);
		firePropertyChange(P_BEND_POINT, null, null);
	}

	public List<ConnectionBendpoint> getBendpoints() {
		if (bendpoints == null) {
			bendpoints = new ArrayList<ConnectionBendpoint>();
		}
		return bendpoints;
	}

	public void removeBendpoint(int index) {
		bendpoints.remove(index);
		firePropertyChange(P_BEND_POINT, null, null);
	}

	public void removeBendpoint(ConnectionBendpoint point) {
		bendpoints.remove(point);
		firePropertyChange(P_BEND_POINT, null, null);
	}

	public void replaceBendpoint(int index, ConnectionBendpoint point) {
		bendpoints.set(index, point);
		firePropertyChange(P_BEND_POINT, null, null);
	}

	// このコネクションの根元をsourceに接続
	public void attachSource() {
		// このコネクションが既に接続されている場合は無視
		if (!source.getModelSourceConnections().contains(this)) {
			source.addSourceConnection(this);
		}
	}

	// このコネクションの先端をtargetに接続
	public void attachTarget() {
		if (!target.getModelTargetConnections().contains(this)) {
			target.addTargetConnection(this);
		}
	}

	// このコネクションの根元をsourceから取り外す
	public void detachSource() {
		if (source != null) {
			source.removeSourceConnection(this);
		}
	}

	// このコネクションの先端をtargetから取り外す
	public void detachTarget() {
		if (target != null) {
			target.removeTargetConnection(this);
		}
	}

	public AbstractUMLEntityModel getSource() {
		return source;
	}

	public AbstractUMLEntityModel getTarget() {
		return target;
	}

	public void setSource(AbstractUMLEntityModel model) {
		source = model;
		if(model instanceof ClassModel) {
			ClassModel c = (ClassModel)model;
			
			String Name = c.getName()+"Start";
			
			if(getSzToLabel().equals(Name)) {
				Name += "_";
			}
			
			if(target != null) {
				ClassModel t = (ClassModel)target;
				setSzTable(c.getName()+"_"+t.getName());
				firePropertyChange(P_SZ_TABLE, null, szTable);
			}
			setSzFromLabel(Name);
			firePropertyChange(P_SZ_FROM_LABEL, null, szFromLabel);
		}
		
	}

	public void setTarget(AbstractUMLEntityModel model) {
		target = model;
		if(model instanceof ClassModel) {
			ClassModel c = (ClassModel)model;
			String Name = c.getName()+"End";
			
			if(getSzFromLabel().equals(Name)) {
				Name += "_";
			}
			
			ClassModel s = (ClassModel)source;
			
			setSzTable(s.getName()+"_"+c.getName());
			
			if(source != null) {
				ClassModel t = (ClassModel)source;
				setSzTable(t.getName()+"_"+c.getName());
				firePropertyChange(P_SZ_TABLE, null, szTable);
			}
			
			System.err.println(getSzTarget());
			
			setSzToLabel(Name);
			firePropertyChange(P_SZ_TO_LABEL, null, szToLabel);
		}
	}
	
	public void setSzToLabel(String szToLabel) {
		szToLabel = szToLabel.trim();
		this.szToLabel = szToLabel;
		firePropertyChange(P_SZ_TO_LABEL, null, szToLabel);
	}

	public String getSzToLabel() {
		return this.szToLabel;
	}	
	
	public void setSzFromLabel(String szFromLabel) {
		szFromLabel = szFromLabel.trim();
		
		this.szFromLabel = szFromLabel;
		firePropertyChange(P_SZ_FROM_LABEL, null, szFromLabel);
	}

	public String getSzFromLabel() {
		return this.szFromLabel;
	}
	
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
//				new ColorPropertyDescriptor(P_FOREGROUND_COLOR, UMLPlugin
//						.getDefault().getResourceString("property.foreground")),
//				new TextPropertyDescriptor(P_SZ_FROM_LABEL, UMLPlugin
//						.getDefault().getResourceString("property.szFromLabel")),
//				new TextPropertyDescriptor(P_SZ_TO_LABEL, UMLPlugin
//						.getDefault().getResourceString("property.szToLabel")),
//				new TextPropertyDescriptor(P_SZ_TYPE, UMLPlugin
//						.getDefault().getResourceString("property.szType")),
//				new TextPropertyDescriptor(P_SZ_LABEL, UMLPlugin
//						.getDefault().getResourceString("property.szLabel")),
//				new TextPropertyDescriptor(P_SZ_SOURCE, UMLPlugin
//						.getDefault().getResourceString("property.szSource")),
//				new TextPropertyDescriptor(P_SZ_SOURCE_DECORATION, UMLPlugin
//						.getDefault().getResourceString("property.szSourceDecoration")),
//				new TextPropertyDescriptor(P_SZ_TARGET, UMLPlugin
//						.getDefault().getResourceString("property.szTarget")),
//				new TextPropertyDescriptor(P_SZ_TARGET_DECORATION, UMLPlugin
//						.getDefault().getResourceString("property.szTargetDecoration")),
				};	
	}
	
	public Object getPropertyValue(Object id) {
		if (id.equals(P_SZ_FROM_LABEL)) {
			return getSzFromLabel();
		} else if (id.equals(P_SZ_TO_LABEL)) {
			return getSzToLabel();
		} 
		
		return super.getPropertyValue(id);
	}

	public void setPropertyValue(Object id, Object value) {
		if (id.equals(P_SZ_FROM_LABEL)) {
			setSzFromLabel((String) value);
		} else if (id.equals(P_SZ_TO_LABEL)) {
			setSzToLabel((String) value);
		} 
		
		super.setPropertyValue(id, value);
	}
}
