package net.java.amateras.ExportPrologView.views;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.java.amateras.ExportPrologView.ExportToProlog;
import net.java.amateras.ExportPrologView.FileLoadHelper;
import net.java.amateras.ExportPrologView.VerifyConstraintsThread;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.*;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;



public class ExportPrologView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.java.amateras.ExportPrologView";
	
	private IWorkbenchWindow window;
	private IResource activeProject;
	private TreeViewer viewer;
	private Action specifyMetamodel;
	private Action specifyModel;
	private Action specifyRules;
	private Action specifyOutput;
	private Action generateProlog;
	private Action validateProlog;
	private Action doubleClickAction;
	private ISelectionListener selectionListener;
	private HashMap<String, HashMap<String, String>> projectToFileMap;
	
	//	private HashMap<String, String> fileMap;
	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	class TreeObject implements IAdaptable {
		private String name;
		private TreeParent parent;
		
		public TreeObject(String name) {
			this.name = name.trim();
		}
		public String getName() {
			return name;
		}
		public void setParent(TreeParent parent) {
			this.parent = parent;
		}
		public TreeParent getParent() {
			return parent;
		}
		public String toString() {
			return getName();
		}
		public Object getAdapter(Class key) {
			return null;
		}
	}
	
	class TreeParent extends TreeObject {
		private ArrayList<TreeObject> children;
		public TreeParent(String name) {
			super(name);
			children = new ArrayList<TreeObject>();
		}
		public void addChild(TreeObject child) {
			children.add(child);
			child.setParent(this);
		}
		public void removeChild(TreeObject child) {
			children.remove(child);
			child.setParent(null);
		}
		public TreeObject [] getChildren() {
			return (TreeObject [])children.toArray(new TreeObject[children.size()]);
		}
		public boolean hasChildren() {
			return children.size()>0;
		}
	}
	class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {
		private TreeParent invisibleRoot;
		
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		
		public void dispose() {
			ISelectionService s = getSite().getWorkbenchWindow().getSelectionService();
		    s.removeSelectionListener(selectionListener);
		}
		
		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				initialize();
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}
	
		private void initialize() {
//			showMessage("In init");
			//activeProject = getActiveProject();
			
			TreeParent root = new TreeParent("Imported Files");
			invisibleRoot = new TreeParent("");
			invisibleRoot.addChild(root);
			
			HashMap<String, String> fileMap = ExportPrologView.this.getFileMap();
			if(fileMap == null) {
				generateProlog.setEnabled(false);
				validateProlog.setEnabled(false);
				return;
			}
			
			if(fileMap.containsKey("Metamodel")) {
				TreeParent meta = new TreeParent("Metamodel");
				TreeObject metafile = new TreeObject(fileMap.get("Metamodel"));
				meta.addChild(metafile);
				root.addChild(meta);
			}
			
			if(fileMap.containsKey("Model")) {
				TreeParent model = new TreeParent("Model");
				TreeObject modelFile = new TreeObject(fileMap.get("Model"));
				model.addChild(modelFile);
				root.addChild(model);
			}
			
			if(fileMap.containsKey("Rules")) {
				TreeParent rules = new TreeParent("Rules");
				TreeObject rulesFile = new TreeObject(fileMap.get("Rules"));
				rules.addChild(rulesFile);
				root.addChild(rules);
			}
			
			if(fileMap.containsKey("Output")) {
				TreeParent output = new TreeParent("Output");
				TreeObject outputFile = new TreeObject(fileMap.get("Output"));
				output.addChild(outputFile);
				root.addChild(output);
			}
			
			if(fileMap.containsKey("Metamodel") && fileMap.containsKey("Model") && fileMap.containsKey("Rules") && fileMap.containsKey("Output")) {
				generateProlog.setEnabled(true);
				validateProlog.setEnabled(true);
			}
			else {
				generateProlog.setEnabled(false);
				validateProlog.setEnabled(false);
			}
		}

		@Override
		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject)child).getParent();
			}
			return null;
		}
		
		public Object [] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent)parent).getChildren();
			}
			return new Object[0];
		}
		
		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent)parent).hasChildren();
			return false;
		}		
	}
	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		
		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (obj instanceof TreeParent)
			   imageKey = ISharedImages.IMG_OBJ_FOLDER;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
	}
	class NameSorter extends ViewerSorter {
	}

	public void noActiveProject() {
		
		showMessage("Before proceeding, please select a project to associate with");
	}
	
	public String getActiveProjectName() {
		
		String activeProjectName = null;
		
		if ( activeProject != null ) {
			activeProjectName = activeProject.getName();
		} else {
			// get the list of projects in the current workspace
			IProject[] listProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		
			for ( int i = 0; i < listProjects.length; i++ ) {
				if ( listProjects[i].isOpen() ) {
					activeProjectName = listProjects[i].getName();
					break;
				}
			}
		}
		
		return activeProjectName;
	}
	
	public HashMap<String, String> getFileMap() {
		HashMap<String, String> fileMap = null;

		String activeProjectName = getActiveProjectName();
		
		if (activeProjectName == null) {
			return null;				
		}
		
		fileMap = projectToFileMap.get(activeProjectName);
		
		if (fileMap == null) {
			fileMap = new HashMap<String, String>();
			projectToFileMap.put(activeProjectName, fileMap);
		}
		
		viewer.refresh();
		viewer.expandAll();
		return fileMap;
	}
	/**
	 * The constructor.
	 */
	public ExportPrologView() {
	
		projectToFileMap = new HashMap<String, HashMap<String, String>>();
		
		generateProlog = new Action() {
			public void run() {
				HashMap<String, String> fileMap = ExportPrologView.this.getFileMap();
				if(fileMap == null) {
					noActiveProject();
					return;
				}
				if(fileMap.containsKey("Metamodel") && fileMap.containsKey("Model") && fileMap.containsKey("Rules") && fileMap.containsKey("Output")) {
					String metamodel = fileMap.get("Metamodel");
					String model = fileMap.get("Model");
					String rules = fileMap.get("Rules");
					String output = fileMap.get("Output");
					
					//showMessage("Call Generate prolog with MM = "+metamodel+" Model = "+model+" rules = "+rules);
					//String outputFileName = FileLoadHelper.saveFile(window.getShell(), "Specify output filename", System.getProperty("user.dir"));
					
					ExportToProlog etp = new ExportToProlog(metamodel, model, rules, output);
				}
			}
		};

		generateProlog.setText("Generate Prolog");
		generateProlog.setToolTipText("Generate prolog");
		
		validateProlog = new Action() {
			public void run() {
				HashMap<String, String> fileMap = ExportPrologView.this.getFileMap();
				if(fileMap == null) {
					noActiveProject();
					return;
				}
				
				if(fileMap.containsKey("Metamodel") && fileMap.containsKey("Model") && fileMap.containsKey("Rules") && fileMap.containsKey("Output")) {
					String metamodel = fileMap.get("Metamodel");
					String model = fileMap.get("Model");
					String rules = fileMap.get("Rules");
					String output = fileMap.get("Output");
//					showMessage("Call Validate prolog with MM = "+metamodel+" Model = "+model+" rules = "+rules);
					VerifyConstraintsThread thread = new VerifyConstraintsThread(rules, output);
					thread.start();
					
				}
			}
		};

		validateProlog.setText("Validate Prolog");
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		
		this.window = this.getViewSite().getWorkbenchWindow();
		
		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "UML2Prolog.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		ISelectionService s = getSite().getWorkbenchWindow().getSelectionService();
		selectionListener = new ISelectionListener() {
			
			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				if ((part!=ExportPrologView.this) && (selection instanceof IStructuredSelection)) {
					ExportPrologView.this.setProject((IStructuredSelection) selection);
				}						
			}
		};
		
		s.addSelectionListener(selectionListener);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ExportPrologView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(specifyMetamodel);
		manager.add(specifyModel);
		manager.add(specifyRules);
		manager.add(specifyOutput);
		manager.add(generateProlog);
		manager.add(validateProlog);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(specifyMetamodel);
		manager.add(specifyModel);
		manager.add(specifyRules);
		manager.add(specifyOutput);
		manager.add(generateProlog);
		manager.add(validateProlog);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	public String getActiveProjectDirectory() {
	
		String activeProjectDirectory = null;
		
		if ( activeProject != null ) {
			activeProjectDirectory = activeProject.getLocation().toString();
		} else {
			// get the list of projects in the current workspace
			IProject[] listProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				
			for ( int i = 0; i < listProjects.length; i++ ) {
				if ( listProjects[i].isOpen() ) {
					activeProjectDirectory = listProjects[i].getLocation().toString();
					break;
				}
			}
		}
		
		if( activeProjectDirectory == null ) {
			activeProjectDirectory = System.getProperty("user.dir");
		}
		
		return activeProjectDirectory;
	}
	
	
	private void makeActions() {
		specifyMetamodel = new Action() {
			public void run() {
				HashMap<String, String> fileMap = ExportPrologView.this.getFileMap();
				if(fileMap == null) {
					noActiveProject();
					return;
				}
				
				
				
				String fileName = FileLoadHelper.loadFile(window.getShell(), "Open Metamodel", getActiveProjectDirectory(), "*.ecore");
				if (fileName==null || fileName.equals(""))
					return;
				String key = "Metamodel";
				if(fileMap.containsKey(key))
					fileMap.remove(key);
				fileMap.put(key, fileName);
				viewer.refresh();
				viewer.expandAll();
			}
		};
		
		specifyMetamodel.setText("Specify Metamodel file");
		specifyMetamodel.setToolTipText("Metamodel");
		specifyMetamodel.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		
		specifyModel = new Action() {			
			public void run() {
				HashMap<String, String> fileMap = ExportPrologView.this.getFileMap();
				if(fileMap == null) {
					noActiveProject();
					return;
				}
				
				String fileName = FileLoadHelper.loadFile(window.getShell(), "Open Model",  getActiveProjectDirectory(), "*.*");
				if (fileName==null || fileName.equals(""))
					return;
				String key = "Model";
				if(fileMap.containsKey(key))
					fileMap.remove(key);
				fileMap.put(key, fileName);
				viewer.refresh();
				viewer.expandAll();
			}
		};
		
		specifyModel.setText("Specify Model file");
		specifyModel.setToolTipText("Model");
		specifyModel.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		
		specifyRules = new Action() {
			public void run() {
				HashMap<String, String> fileMap = getFileMap();
				if(fileMap == null) {
					noActiveProject();
					return;
				}
				
				String fileName = FileLoadHelper.loadFile(window.getShell(), "Open Rules",  getActiveProjectDirectory(), "*.pl");
				if (fileName==null || fileName.equals(""))
					return;
				String key = "Rules";
				if(fileMap.containsKey(key))
					fileMap.remove(key);
				fileMap.put(key, fileName);
				viewer.refresh();
				viewer.expandAll();
			}
		};
		
		specifyRules.setText("Specify Rules file");
		specifyRules.setToolTipText("Rules");
		specifyRules.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		
		specifyOutput = new Action() {			
			public void run() {
				HashMap<String, String> fileMap = ExportPrologView.this.getFileMap();
				if(fileMap == null) {
					noActiveProject();
					return;
				}
				
				String fileName = FileLoadHelper.loadFile(window.getShell(), "Open Model",  getActiveProjectDirectory(), "*.*");
				if (fileName==null || fileName.equals(""))
					return;
				String key = "Output";
				if(fileMap.containsKey(key))
					fileMap.remove(key);
				fileMap.put(key, fileName);
				viewer.refresh();
				viewer.expandAll();
			}
		};
		
		specifyOutput.setText("Specify Output File");
		specifyOutput.setToolTipText("Output");
		specifyOutput.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				if((obj instanceof TreeParent)==false) {
					File fileToOpen = new File(obj.toString());
					if(fileToOpen.exists() && fileToOpen.isFile()) {
						String fullPath = obj.toString();
						IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
						IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						 
					    try {
					        IDE.openEditorOnFileStore( page, fileStore );
					    } catch ( PartInitException e ) {
					    }
					}
					else {
						showMessage(fileToOpen.getName()+" doesn't exist");
					}
				}
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Sample View",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private void setProject(IStructuredSelection selection) {
		Object obj = selection.getFirstElement();
		IProject newActiveProject = null;
		if (obj instanceof IResource)
			newActiveProject = ((IResource)obj).getProject();
		else if (obj instanceof IAdaptable) {
			IAdaptable adapter = (IAdaptable)obj;
			newActiveProject = ((IResource)adapter.getAdapter(IResource.class)).getProject();
		}
		
		if (activeProject==null || newActiveProject.getName().equals(activeProject.getName())==false) {
			activeProject = newActiveProject;
			viewer.refresh();
			viewer.expandAll();
		}
	}
}