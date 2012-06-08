package net.java.amateras.uml.properties;

import java.util.ArrayList;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * boolean型のプロパティを編集するためのPropertyDescriptor。
 * 
 * @author Naoki Takezoe
 */
public class AnnotationTypePropertyDescriptor extends PropertyDescriptor {
	
	String[] Options;

	public AnnotationTypePropertyDescriptor(Object id,String displayName){
		super(id,displayName);
		this.Options = new String[] {"None","Node","Link","Compartment"};
	}
	
	public AnnotationTypePropertyDescriptor(Object id,String displayName, String[] Options){
		super(id,displayName);
		this.Options = Options;
	}
	
    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new ComboBoxCellEditor(
        		parent, Options,SWT.READ_ONLY){
        	
        	public void doSetValue(Object value){
        		
        		int i=0;
        		for(String a : Options) {
        			if(a.equals(value)) {
        				super.doSetValue(new Integer(i));
        				break;
        			}
        			i++;
        		}
        	}
        	public Object doGetValue(){
        		return Options[((Integer)super.doGetValue()).intValue()];
        	}
        };
        
        if (getValidator() != null)
            editor.setValidator(getValidator());
        
        return editor;
    }

	
	
}
