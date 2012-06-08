/**
 * 
 */
package net.java.amateras.uml.ExportEcore.importWizards;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.java.amateras.uml.ExportEcore.Activator;
import net.java.amateras.uml.ExportEcore.EcoreExporter;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.RootModel;

import org.eclipse.core.filebuffers.IDocumentFactory;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;


/**
 * @author shida
 *
 */
public class EcoreExportWizard extends Wizard {

	private EcoreExportWizardPage creationPage;

	private RootModel model;
	
	private ISelection selection;

	public EcoreExportWizard() {
		/*creationPage = new WizardNewFileCreationPage(Activator.getDefault().getResourceString("export.wizard.name"), new StructuredSelection());
		creationPage.setTitle(Activator.getDefault().getResourceString("export.wizard.name"));
		creationPage.setDescription(Activator.getDefault().getResourceString("export.wizard.description"));
		//		String pathName;
		//		IResource initial = ResourcesPlugin.getWorkspace().getRoot().
		//                .findMember(pathName);
		//		
		String fileName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput().getName();
		fileName = fileName.substring(0,fileName.lastIndexOf(".")-1);
		System.err.println(getCurrentFileRealPath());
		
		creationPage.setContainerFullPath(getCurrentIPath());
		creationPage.setFileName(fileName);
		creationPage.setFileExtension("ecore");
		
		*/
		super();
		setNeedsProgressMonitor(true);
		
		//PlatformUI.getWorkbench().saveAllEditors(false);
		
		IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		selection = page.getSelection();
		//init(PlatformUI.getWorkbench(), page.getSelection());
		//creationPage = new WizardNewFileCreationPage(Activator.getDefault().getResourceString("export.wizard.name"), new StructuredSelection());
		//creationPage.setTitle(Activator.getDefault().getResourceString("export.wizard.name"));
		//creationPage.setDescription(Activator.getDefault().getResourceString("export.wizard.description"));

		

	}

	public void setModel(RootModel model) {
		this.model = model;
	}

	public void addPages() {
		super.addPages();
		//super.addPages();
		//addPage(creationPage);
		creationPage = new EcoreExportWizardPage(selection);
		addPage(creationPage);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {

		String fileName = creationPage.getFileName();

		fileName = fileName.substring(0,fileName.lastIndexOf("."));

		EcoreExporter exporter = new EcoreExporter(fileName);

		List children = model.getChildren();

		for (Iterator iter = children.iterator(); iter.hasNext();) {
			AbstractUMLEntityModel element = (AbstractUMLEntityModel) iter.next();
			exporter.convertType(element);
		}
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			AbstractUMLEntityModel element = (AbstractUMLEntityModel) iter.next();
			exporter.convertStructure(element);
		}
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			AbstractUMLEntityModel element = (AbstractUMLEntityModel) iter.next();
			exporter.convertLink(element);
		}




		if(exporter.valid) {

			exporter.createDiagram();

			String name = creationPage.getContainerName() + "/" + creationPage.getFileName();
			ResourceSet resourceSet = new ResourceSetImpl();
			Resource resource = resourceSet.createResource(URI.createPlatformResourceURI(name));
			resource.getContents().add(exporter.getRoot());
			Map options = new HashMap();
			options.put(XMIResource.OPTION_ENCODING, "UTF-8");
			try {
				resource.save(options);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public static IPath getCurrentFileIRealPath(){
		IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		IWorkbenchPage page = win.getActivePage();
		if (page != null) {
			IEditorPart editor = page.getActiveEditor();
			if (editor != null) {
				IEditorInput input = editor.getEditorInput();
				if (input instanceof IFileEditorInput) {
					return ((IFileEditorInput)input).getFile().getProjectRelativePath();
					//return ((IFileEditorInput)input).getFile().getLocation().toOSString();
					
				}
			}
		}
		return null;
	}
	
	public static String getCurrentFileRealPath(){
		IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		IWorkbenchPage page = win.getActivePage();
		if (page != null) {
			IEditorPart editor = page.getActiveEditor();
			if (editor != null) {
				IEditorInput input = editor.getEditorInput();
				if (input instanceof IFileEditorInput) {
					return ((IFileEditorInput)input).getFile().getLocation().toOSString();
				}
			}
		}
		return null;
	}

	public static IPath getCurrentIPath(){
		IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		IWorkbenchPage page = win.getActivePage();
		if (page != null) {
			IEditorPart editor = page.getActiveEditor();
			if (editor != null) {
				IEditorInput input = editor.getEditorInput();
				if (input instanceof IFileEditorInput) {
					return ((IFileEditorInput)input).getFile().getLocation();
				}
			}
		}
		return null;
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}
