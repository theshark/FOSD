package net.java.amateras.uml.ExportEcore;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.java.amateras.umleditor.ExportEcore";

	// The shared instance
	private static Activator plugin;
	
	private ResourceBundle bundle;
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
		try {
			bundle = ResourceBundle.getBundle("net.java.amateras.uml.ExportEcore.resource");
		} catch (MissingResourceException x) {
			bundle = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public String getResourceString(String string) {
		return bundle.getString(string);
	}

	public void warnning(String type, String name, Exception e) {
		getLog().log(new Status(IStatus.WARNING, "net.amateras.uml.ExportEcore", 0, type + ":" + name + getResourceString("warnning.message"), e));		
	}
}
