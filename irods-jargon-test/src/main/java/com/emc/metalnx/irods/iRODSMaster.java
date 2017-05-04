package com.emc.metalnx.irods;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.irods.jargon.core.exception.JargonException;

public class iRODSMaster {
	// iRODS args
	static final String USER = "rods";
	static final String PASSWORD = "irods";
	
	public static ArrayList<String> hosts;
	
	// files on local system
	final static String acPostProcForPut_file = "acPostProcForPutRule.re";
	final static String REJECT_FILE = "rejectfile.re";
	final static String NEITHER_FILE = "neitherfile.re";
	
	// print streams
	public static PrintStream originalOut = System.out;
	public static PrintStream originalErr = System.err;
	static PrintStream hiddenStream;
	
	// local log file
	static File logFile;
	static FileWriter logFileWriter;
	
	/**
	 * @param args
	 * @throws JargonException
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws JargonException {
		silence(false);
		
		// Jargon setup
		iRODSFileTransfer iRODSInstance = new iRODSFileTransfer(originalOut, originalErr);
		iRODSInstance.configure();
		
		// generate host list
		generateHostsLists();

		// runs transmit for the acPost rule
		String[] commands = {"deploy","deploy","delete","delete"};
		for (int i = 0; i < commands.length; i++) {
			for (int index = 0; index < hosts.size(); index++) {
				iRODSInstance.transmit(acPostProcForPut_file, false, commands[i], index, hosts.get(index), USER, PASSWORD);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {
					ie.printStackTrace(originalErr);
				}
			}
		}
		
		// Jargon teardown
		iRODSInstance.breakdown();
	}

	/**
	 * Read from servers.txt file to generate a host list
	 */
	public static void generateHostsLists() {
		hosts = new ArrayList<String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("servers.txt")));
			
			String line;
			while ((line = br.readLine()) != null) {
				hosts.add(line);
			}
			
			br.close();
		} catch (IOException e) {
			originalErr.println("Unable to create host list.");
			System.exit(1);
		}
	}

	/**
	 * Creates a new PrintStream to direct System.out and System.err to.
	 * Can create a logfile or drop print data.
	 * 
	 * @param createLog		creates log file if true
	 */
	public static void silence(boolean createLog) {
		// attempt to create logFile to store API printed data
		try {
			if (!createLog) {
				// by throwing this exception, the code to create the log file will 
				// the same code should be run as if the file could not exist (an empty stream)
				throw new IOException();
			}
			
			String timestamp = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss").format(new Date());
			logFile = new File("log_" + timestamp + ".log");
			logFileWriter = new FileWriter(logFile);
			// create hidden stream to store API output in file
			hiddenStream = new PrintStream(new OutputStream(){
				public void write(int b) {
					try {
						logFileWriter.write(b);
					} catch (IOException e) {}
				}
			});
		} catch (IOException e) {
			// create hidden stream to hide API output
			hiddenStream = new PrintStream(new OutputStream(){
				public void write(int b) {}
			});
		}
		
		System.setOut(hiddenStream);
		System.setErr(hiddenStream);
	}
}
