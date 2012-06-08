package net.java.amateras.uml.properties;

import java.util.List;

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
public class AnnotationLabelPropertyDescriptor extends PropertyDescriptor {


	private List<String> Options;

	public AnnotationLabelPropertyDescriptor(Object id,String displayName, List<String> Options){
		super(id,displayName);
		this.Options = Options;
	}

	public CellEditor createPropertyEditor(Composite parent) {
		
		String[] op = new String[Options.size()];
		
		Options.toArray(op);
		
		ComboBoxCellEditor editor = new ComboBoxCellEditor(
				parent, op,SWT.READ_ONLY){

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
				int index = ((Integer)super.doGetValue()).intValue();
				if(index <= 0 || index > Options.size()) return new String("");
				return new String(Options.get(index));
			}
		};

		if (getValidator() != null)
			editor.setValidator(getValidator());

		return editor;
	}

//	public void replaceList(List<String> Options){
//
//		String[] op = new String[Options.size()];
//		try {
//			Options.toArray(op);
//			editor.setItems(op);
//		} catch (Exception e) {
//			System.err.println("List length : " + op.length);
//		}
//
//	}





}
