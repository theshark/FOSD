package net.java.amateras.uml.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.java.amateras.uml.UMLColorRegistry;
import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.properties.BooleanPropertyDescriptor;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * GEFのエディタで使用するモデルの基底クラス。
 */
public abstract class AbstractUMLModel implements Serializable, IPropertySource {

	private String szType = "None";
	
	public String szTable = "";
	
	private String szLabel = "";
	
	private String szSource = "";
	private String szSourceDecoration = "";
	
	private String szTarget = "";
	private String szTargetDecoration = "arrow";
	
	public static final String P_BACKGROUND_COLOR = "_background";
	
	public static final String P_FOREGROUND_COLOR = "_foreground";
	
	public static final String P_SHOW_ICON = "_showicon";
	
	public static final String P_SZ_TO_LABEL = "_szToLabel";
	
	public static final String P_SZ_TYPE = "_szType";
	
	public static final String P_SZ_TABLE = "_szTable";
	
	public static final String P_SZ_LABEL = "_szLabel";
	
	public static final String P_SZ_SOURCE = "_szSource";
	
	public static final String P_SZ_SOURCE_DECORATION = "_szSourceDecoration";
	
	public static final String P_SZ_TARGET = "_szTarget";
	
	public static final String P_SZ_TARGET_DECORATION = "_szTargetDecoration";

	private RGB backgroundColor;
	
	private RGB foregroundColor;
	
	private boolean showIcon = true;
	
	private AbstractUMLEntityModel parent;
	
	/** リスナのリスト */
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	/** リスナの追加 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	/** モデルの変更を通知 */
	public void firePropertyChange(String propName, Object oldValue,Object newValue) {
		listeners.firePropertyChange(propName, oldValue, newValue);
	}

	/** リスナの削除 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

	public Object getEditableValue() {
		return this;
	}

	public void setParent(AbstractUMLEntityModel parent) {
		this.parent = parent;
	}
	
	public AbstractUMLEntityModel getParent() {
		return parent;
	}
	

	public String getSzType() {
		return this.szType;
	}
	
	public void setSzType(String szType) {
		
		szType = szType.trim();
		
		if ( szType.toLowerCase().equals("link")) {
			szType = "Link";
		} else if ( szType.toLowerCase().equals("compartment")) {
			szType = "Compartment";
		} else if ( szType.toLowerCase().equals("node"))  {
			szType = "Node";
		} else {
			szType = "None";
		}
		
		this.szType = szType;
		firePropertyChange(P_SZ_TYPE, null, szType);
		
		if ( !getSzType().equals("Link")) {
			this.szSource = "";
			firePropertyChange(P_SZ_SOURCE, null, szSource);
			
			this.szSourceDecoration = "";
			firePropertyChange(P_SZ_SOURCE_DECORATION, null, szSourceDecoration);
			
			this.szTarget = "";
			firePropertyChange(P_SZ_TARGET, null, szTarget);
			
			this.szTargetDecoration = "";
			firePropertyChange(P_SZ_TARGET_DECORATION, null, szTargetDecoration);
		}
	}

	public String getSzTable() {
		return this.szTable.toLowerCase();
	}
	
	public void setSzTable(String szTable) {
		szTable = szTable.trim().toLowerCase();
		this.szTable = szTable;
		firePropertyChange(P_SZ_TABLE, null, szTable);
	}
	
	public String getSzLabel() {
		return this.szLabel;
	}
	
	public void setSzLabel(String szLabel) {
		szLabel = szLabel.trim();
		this.szLabel = szLabel;
		
		/*
		 * 
		 *  This needs to be an attribute...
		 * 
		 */
		
		firePropertyChange(P_SZ_LABEL, null, szLabel);
	}
	
	public String getSzSource() {
		return this.szSource;
	}
	
	public void setSzSource(String szSource) {
		
		if ( getSzType().equals("Link")) {
			szSource = szSource.trim();
			
			this.szSource = szSource;
			firePropertyChange(P_SZ_SOURCE, null, szSource);
		}
	}
	
	public String getSzSourceDecoration(){
		return this.szSourceDecoration;
	}
	
	public void setSzSourceDecoration(String szSourceDecoration) {
		if ( getSzType().equals("Link")) {
			szSourceDecoration = szSourceDecoration.trim();
			
			this.szSourceDecoration = szSourceDecoration;
			firePropertyChange(P_SZ_SOURCE_DECORATION, null, szSourceDecoration);
		}
	}
	
	public String getSzTarget() {
		return this.szTarget;
	}
	
	public void setSzTarget(String szTarget) {
		if ( getSzType().equals("Link")) {
			szTarget = szTarget.trim();
			
			this.szTarget = szTarget;
			firePropertyChange(P_SZ_TARGET, null, szTarget);
		}
	}
	
	public String getSzTargetDecoration() {
		return this.szTargetDecoration;
	}
	
	public void setSzTargetDecoration(String szTargetDecoration) {
		if ( getSzType().equals("Link")) {
			szTargetDecoration = szTargetDecoration.trim();
			
			this.szTargetDecoration = szTargetDecoration;
			firePropertyChange(P_SZ_TARGET_DECORATION, null, szTargetDecoration);
		}
	}
	
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		
		return new IPropertyDescriptor[] {
//				new ColorPropertyDescriptor(P_BACKGROUND_COLOR, UMLPlugin
//						.getDefault().getResourceString("property.background")),
//				new ColorPropertyDescriptor(P_FOREGROUND_COLOR, UMLPlugin
//						.getDefault().getResourceString("property.foreground")),
//				new BooleanPropertyDescriptor(P_SHOW_ICON, UMLPlugin
//						.getDefault().getResourceString("property.showicon")),
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
		if (id.equals(P_BACKGROUND_COLOR)) {
			return backgroundColor;
		} else if (P_FOREGROUND_COLOR.equals(id)) {
			return foregroundColor;
		} else if (P_SHOW_ICON.equals(id)) {
			return new Boolean(isShowIcon());
		} else if (id.equals(P_SZ_TYPE)) {
			return getSzType();
		} else if (id.equals(P_SZ_TABLE)) {
			return getSzTable();
		} else if (id.equals(P_SZ_LABEL)) {
			return getSzLabel();
		} else if (id.equals(P_SZ_SOURCE)) {
			return getSzSource();
		} else if (id.equals(P_SZ_SOURCE_DECORATION)) {
			return getSzSourceDecoration();
		} else if (id.equals(P_SZ_TARGET)) {
			return getSzTarget();
		} else if (id.equals(P_SZ_TARGET_DECORATION)) {
			return getSzTargetDecoration();
		}
		
		return null;
	}

	public boolean isPropertySet(Object id) {
		return P_BACKGROUND_COLOR.equals(id) || P_FOREGROUND_COLOR.equals(id)
				|| P_SHOW_ICON.equals(id);
	}

	public void setPropertyValue(Object id, Object value) {
		if (P_BACKGROUND_COLOR.equals(id)) {
			setBackgroundColor((RGB) value);
		} else if (P_FOREGROUND_COLOR.equals(id)) {
			setForegroundColor((RGB) value);
		} else if (P_SHOW_ICON.equals(id)) {
			setShowIcon(((Boolean) value).booleanValue());
		} else if (id.equals(P_SZ_TYPE)) {
			setSzType((String) value);
		} else if (id.equals(P_SZ_TABLE)) {
			setSzTable((String) value);
		} else if (id.equals(P_SZ_LABEL)) {
			setSzLabel((String) value);
		} else if (id.equals(P_SZ_SOURCE)) {
			setSzSource((String) value);
		} else if (id.equals(P_SZ_SOURCE_DECORATION)) {
			setSzSourceDecoration((String) value);
		} else if (id.equals(P_SZ_TARGET)) {
			setSzTarget((String) value);
		} else if (id.equals(P_SZ_TARGET_DECORATION)) {
			setSzTargetDecoration((String) value);
		}	
	}

	public void resetPropertyValue(Object id) {
	}

	public Color getBackgroundColor() {
		return UMLColorRegistry.getColor(backgroundColor);
	}

	public void setBackgroundColor(RGB backgroundColor) {
		this.backgroundColor = backgroundColor;
		firePropertyChange(P_BACKGROUND_COLOR, null, backgroundColor);
	}

	public Color getForegroundColor() {
		return UMLColorRegistry.getColor(foregroundColor);
	}

	public void setForegroundColor(RGB foregroundColor) {
		this.foregroundColor = foregroundColor;
		firePropertyChange(P_FOREGROUND_COLOR, null, foregroundColor);
	}

	public boolean isShowIcon() {
		return showIcon;
	}

	public void setShowIcon(boolean showIcon) {
		this.showIcon = showIcon;
		firePropertyChange(P_SHOW_ICON, null, new Boolean(showIcon));
	}
	
	public void copyPresentation(AbstractUMLModel model) {
		if (backgroundColor != null) {
			model.setBackgroundColor(backgroundColor);
		}
		if (foregroundColor != null) {
			model.setForegroundColor(foregroundColor);
		}
		model.setShowIcon(showIcon);
	}
//	/**
//	 * 引数で渡されたオブジェクトがこのオブジェクトと等しいかどうかを判定します。
//	 * デフォルトではRuntimeExceptionがthrowされるようになっており、
//	 * ビジュアルモデルはこのメソッドを適切に実装する必要があります。
//	 */
//	public boolean equals(Object obj){
//		throw new RuntimeException("equals is not implemented!");
//	}
}
