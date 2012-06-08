package net.java.amateras.ExportPrologView;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;


public class FileLoadHelper {
	 public static String loadFile(Shell s, String title, String defDir, final String fileType) {
		 /*FileDialog fd = new FileDialog(f, title, FileDialog.LOAD);
		 fd.setDirectory(defDir);
		 fd.setLocation(50, 50);
		 fd.setFile(fileType);
		 //fd.setFilenameFilter(fileFilter);
		 fd.setVisible(true);
		 fd.toFront();
		 return new File(fd.getDirectory(), fd.getFile()).getAbsolutePath();
		 */
		 
		FileDialog fd = new FileDialog(s, SWT.OPEN);
		fd.setFilterPath(defDir);
		fd.setText(title);
		fd.setText(defDir);
		
		String[] filterExt1 = { fileType };
		String[] filterExt2 = { fileType, "*.*" };
		
		if ( fileType.equals("*.*")) {
			fd.setFilterExtensions(filterExt1);
		} else {
			fd.setFilterExtensions(filterExt2);
		}
		
		
		
		String selected = fd.open();
		return selected;
	 }
	 
	 public static String saveFile(Shell s, String title, String defDir) {
		 FileDialog fd = new FileDialog(s, SWT.SAVE);
		 fd.setText(title);
		 String fileName = null;
		 boolean done = false;
		 while (!done) {
		      fileName = fd.open();
		      if (fileName == null) {
		        done = true;
		      } 
		      else {
		        File file = new File(fileName);
		        if (file.exists()) {
		          MessageBox mb = new MessageBox(fd.getParent(), SWT.ICON_WARNING
		              | SWT.YES | SWT.NO);
		          mb.setMessage(fileName + " already exists. Do you want to replace it?");
		          done = mb.open() == SWT.YES;
		        } 
		        else {
		          done = true;
		        }
		      }
		    }
		 return fileName;
	 }
}
