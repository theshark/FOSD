package net.java.amateras.ExportPrologView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;


class ObjectStruct {

	public String Name;
	public EClass classBase;
	public HashSet<String> Attr;
	public HashMap<String,EReference> Ref;

	public HashMap<String, EObject> Instances;

	HashMap<String,ObjectStruct> Rows;
	
	public ObjectStruct(String n, EClass c, HashMap<String,ObjectStruct> R) {
		Name = n;
		classBase = c;
		Attr = new HashSet<String>();
		Ref = new HashMap<String,EReference>();
		initAttr();
		Instances =  new HashMap<String, EObject>();
		Rows = R;
	}
	
	
	public void initAttr() {
		System.err.println("Class: " + classBase.getName());
		
		TreeIterator<EObject> r = classBase.eAllContents();
		
		for( ; r.hasNext() ;) {
			
			EObject li = r.next();
			if(li instanceof EReference) {
				Ref.put(((EReference)li).getName(), ((EReference)li));
			}
		}
		
		EList<EStructuralFeature> fl = classBase.getEAllStructuralFeatures();

		for(EStructuralFeature li : fl) {
			if(!li.isMany() && !Ref.containsKey(li.getName())) {
				Attr.add(li.getName());
			}
		}
	}
	
	
	
	public String toString() {

		String r = "%" + Name.toLowerCase().trim() + "(ID," ;
		
		EList<EStructuralFeature> fl = classBase.getEAllStructuralFeatures();

		
		for(EStructuralFeature li : fl) {
			if(li.isMany()) continue;
			r += "'"+li.getName() +"',";
		}
		
		r= r.substring(0, r.length()-1)+")\n";
		
//		for(EReference ref : Ref.values()) {
//			if ( ref.isContainment() ) r += "%" + ref.getName().toLowerCase().trim()+"('"+Name.toLowerCase().toString()+"ID','"+ref.getEReferenceType().getName().toLowerCase().trim()+"ID')\n";
//		}
		
		
		r+=instances();
		r+=references();
		return r;
	}

	public String instances() {
		String r = "\n";

		for(String  oInstanceKey : Instances.keySet()) {

			EObject oInstance = Instances.get(oInstanceKey);
			
			String ints = Name.toLowerCase().trim() + "("+  oInstanceKey.toLowerCase()  +",";
			
			EList<EStructuralFeature> fl = oInstance.eClass().getEAllStructuralFeatures();
			
			for(EStructuralFeature li : fl) {
								
				if(li.isMany()) continue;
				
				// Get attribute
				if(Attr.contains(li.getName())) {
					ints += "'" + oInstance.eGet(li) + "',";
					continue;
				}
				
				// Get reference
				if(Ref.containsKey(li.getName())) {
					
					EReference ref = Ref.get(li.getName());
					
					if(!ref.isContainment()) {
						String cl = ref.getEReferenceType().getName();
						EObject eo = (EObject)oInstance.eGet(li);
						String key = FindKey(cl, eo);
						if(key!=null) ints += "" + key.toLowerCase() + ",";
					}
					continue;
				}
				
				
				
			}
			ints = ints.substring(0,ints.length()-1)+").";
			r+=ints+"\n";
		}
		return r;
	}

	public String references() {
		String ret = "\n";

		if(Ref.isEmpty()) return "";

		for(String  lk : Instances.keySet()) {

			EObject l = Instances.get(lk);
			
			EList<EStructuralFeature> fl = l.eClass().getEAllStructuralFeatures();
			
			for(EStructuralFeature li : fl) {
				if(Ref.containsKey(li.getName())) {
					
					EReference ref = Ref.get(li.getName());
					String cl = ref.getEReferenceType().getName();
				
					if(ref.isContainment()) {
						for(EObject eo : l.eContents()) {
							if(eo.eClass().getName().equals(cl)) {
								String key = FindKey(cl, eo);
								if(key!=null) ret +=(li.getName().toLowerCase().trim()+"("+lk.toLowerCase()+","+key.toLowerCase()+").\n");
							}	
						}
					}
				}
			}
			
			
		}

		return ret;
	}

	
	private String FindKey(String cl, EObject eo) {
		String key = null;
		ObjectStruct os = Rows.get(cl);
		
		for(String s : os.Instances.keySet()) {
			if(eo == os.Instances.get(s)) {
				key = s;
				return key;
			}
		}
		
		for(ObjectStruct OS : Rows.values()) {
			for(String s : OS.Instances.keySet()) {
				if(eo == OS.Instances.get(s)) {
					key = s;
					return key;
				}
			}
		}
		
		
		return key;
	}
	
	
	private int counter = 0;
	public int incCounter() {
		return counter++;
	}
}

public class ExportToProlog {
	String MModelFilename;
	String ModelFilename;
	String VRFilename;
	String OutputFilename;

	HashMap<String,ObjectStruct> Rows;
	HashSet<String> Prolog;
	

	public ExportToProlog(String MModel, String Model, String VR, String Output) {
		MModelFilename = MModel;
		ModelFilename = Model;
		VRFilename = VR;
		OutputFilename = Output;

		Rows = new HashMap<String, ObjectStruct>();
		Prolog = new HashSet<String>();
		
		
		parseMetaModel();
		parseModel();
		
	    String inRules = "";
		try {
		    BufferedReader in = new BufferedReader(new FileReader(VRFilename));
		    String str;
		    while ((str = in.readLine()) != null) {
		       inRules += str + "\n";
		    }
		    in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
						
		BufferedWriter bufferedWriter = null;
        
        try {
            
            //Construct the BufferedWriter object
            bufferedWriter = new BufferedWriter(new FileWriter(OutputFilename));
            
            
    		for(ObjectStruct os : Rows.values()) {
    			bufferedWriter.write(os.toString());
                bufferedWriter.newLine();
    		}
    		//bufferedWriter.write(inRules);
    		//bufferedWriter.newLine();

            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

	}

	@SuppressWarnings({ "unchecked", "null" })
	public void parseMetaModel() {

		ResourceSet resourceSet = new ResourceSetImpl(); 
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl()); 

		File myEcoreFile = new File(MModelFilename); 
		URI myEcoreURI = URI.createFileURI(myEcoreFile.getAbsolutePath()); 
		Resource myEcoreResource = resourceSet.getResource(myEcoreURI, true); 

		ObjectStruct os = null;

		for (TreeIterator<EObject> i = myEcoreResource.getAllContents(); i.hasNext();) 
		{ 


			EObject eObject = (EObject) i.next(); 
			String string = eObject.eClass().getName(); 
			try {
				if(eObject instanceof EClass) {
					if(os != null) Rows.put(os.Name,os);
					ENamedElement ene = ((ENamedElement)eObject);
					os = new ObjectStruct(ene.getName(), (EClass)eObject ,Rows);

				}
			} catch (NullPointerException ne) {

			}

			if (eObject instanceof ENamedElement) 
			{ 
				ENamedElement ene = ((ENamedElement)eObject);
				string = string + " - " + ene.getName(); 
			} 
		} 
	}

	@SuppressWarnings({ "unchecked", "null" })
	public void parseModel() {
		ResourceSet resourceSet = new ResourceSetImpl(); 
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl()); 
		try{ 

			HashMap options = new HashMap();
			options.put(XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
			Resource resource = resourceSet.createResource(URI.createFileURI(ModelFilename));
			resource.load(options);
			
			TreeIterator<EObject> i = resource.getAllContents();
			for (; i.hasNext();) 
			{
				EObject eObject = (EObject) i.next(); 

				String cl = eObject.eClass().getName();
				
				if(Rows.containsKey(cl)) {
					ObjectStruct os = Rows.get(cl);
					os.Instances.put(cl+os.incCounter(), eObject);
				}

			}
		}
		catch (Exception e){ 
			e.getStackTrace(); 
		}
	}

}

