package net.java.amateras.ExportPrologView;

import java.io.*;
import java.nio.CharBuffer;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.console.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;

public class VerifyConstraintsThread extends Thread
{
	private String m_constraintsFile;
	private String m_tableFile;
	public static String m_prologPath = "prolog";
	private Process pr;
	public VerifyConstraintsThread(String constraintsFile, String tableFile)
	{
		m_constraintsFile = constraintsFile;
		m_tableFile = tableFile;
	}
	
	private IOConsole findConsole(String name) {
	      ConsolePlugin plugin = ConsolePlugin.getDefault();
	      IConsoleManager conMan = plugin.getConsoleManager();

	      
	      IOConsole myConsole = new IOConsole(name, null);
	      //myConsole.activate();
	      conMan.addConsoles(new IConsole[]{myConsole});
	      return myConsole;
	  }
	
	private void concatenateFiles(String file1, String file2, String outFile) throws IOException
	{
		FileReader read1 = new FileReader(file1);
		FileReader read2 = new FileReader(file2);
		FileWriter write = new FileWriter(outFile);
		char[] buf = new char[64];
		int n;
		while((n = read1.read(buf)) >= 0){
			write.write(buf, 0, n);
		}
		while((n = read2.read(buf)) >= 0){
			write.write(buf, 0, n);
		}
		write.close();
		read1.close();
		read2.close();
	}
	
	public void run()
	{
		Color myRed = new Color(Display.getCurrent(), 255, 0, 0);
		Color myGreen = new Color(Display.getCurrent(), 0, 255, 0);
		IOConsole myConsole = findConsole("Prolog Console");
		IOConsoleOutputStream out = myConsole.newOutputStream();
		IOConsoleOutputStream err = myConsole.newOutputStream();
		err.setColor(myRed);
		
		try
		{
			if(m_prologPath == "")
				m_prologPath = "prolog";
			IOConsoleInputStream consoleInput = myConsole.getInputStream();
			consoleInput.setColor(myGreen);
			concatenateFiles(m_tableFile, m_constraintsFile, "verPrologInput.pro");
			
			Runtime rt = Runtime.getRuntime();
			pr = rt.exec(m_prologPath + "  -t verifyConstraints -tty -s verPrologInput.pro");
			
			PrologIOThread ioT = new PrologIOThread(pr, consoleInput, out, err);
			ioT.start();
			int exitVal = pr.waitFor();
			ioT.stopRunning();

			if(exitVal == 0)
	        	out.write("Passed!\n");
	        else
	        	err.write("FAILED!\n");
			
			
		}
		catch(Exception e) {
            try {
				err.write(e.getMessage() + "\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
	}
	
	public void finalize()
	{
		pr.destroy();
	}
}

class PrologIOThread extends Thread
{
	private IOConsoleInputStream m_cInput;
	private IOConsoleOutputStream m_cout;
	private IOConsoleOutputStream m_cerr;
	private BufferedWriter m_prIn;
	private BufferedReader m_prOut;
	private BufferedReader m_prErr;
	private Process m_pr;
	private int m_errCount;
	private boolean m_run;
	
	public PrologIOThread(Process pr, IOConsoleInputStream cInput, IOConsoleOutputStream cOut, IOConsoleOutputStream cErr )
	{
		m_pr = pr;
		m_cInput = cInput;
		m_cout = cOut;
		m_cerr = cErr;
		m_errCount = 0;
		m_prIn = new BufferedWriter(new OutputStreamWriter(pr.getOutputStream()));
		m_prOut = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		m_prErr = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
		m_run = true;
		
	}
	public void run()
	{
		while(m_run)
		{
			try{
				String line;
				if(m_prOut.ready()){
					if((line = m_prOut.readLine()) != null)
						m_cout.write(line + "\n");
				}
				if(m_prErr.ready()){
					if((line = m_prErr.readLine()) != null){
						m_errCount++;
						m_cerr.write(line + "\n");
					}
					if(m_errCount >= 100){
						m_pr.destroy();
						m_cerr.write("Maximum number of errors exceeded.\n");
						break;
					}
				}
				int n;
				int i;
				if((n = m_cInput.available()) > 0){
					i = m_cInput.read();
					m_prIn.write((char)i);
				}
		    }
			catch (IOException e){
				System.out.println(e);
			}
		}
	}
	public void stopRunning()
	{
		m_run = false;
	}
}
