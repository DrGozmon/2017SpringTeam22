/**
 * Sources:
 * [DataTransferOperationsImplTest.java](https://goo.gl/n2GPfP)
 * [RemoteExecuteServiceImplTest.java](https://goo.gl/5EhmO7)
 * 
 */	
package com.emc.metalnx.irods;

import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.IRODSFileSystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.SettableJargonProperties;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.OverwriteException;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;

/**
 * @author mphayes2
 */
public class iRODSFileTransfer {

	private static IRODSFileSystem irodsFileSystem = null;

	final int PORT = 1247;
	final String HOME_DIR = "/tempZone/home/rods";
	final String ZONE = "tempZone";
	final String RESOURCE = "demoResc";
	
	// path for iRODS grid
	String IRODS_PATH = "/tempZone/home/rods/.rulecache/";
	
	// print streams
	PrintStream originalOut;
	PrintStream originalErr;
	
	public iRODSFileTransfer(PrintStream originalOut, PrintStream originalErr) {
		this.originalOut = originalOut;
		this.originalErr = originalErr;
	}
	
	/**
	 * puts a file up to the iRODS grid resource server
	 * Files can be removed by running "/home/dlgross/removeFiles.txt"
	 * 
	 * @param filename
	 * @return 0 if completed successful
	 * @throws JargonException - Will throw an exception if the files are already on the remote servers
	 * 						Must remove them from remote server using "irm <filename>"
	 */
	public int putFile(String filename, String host, String user, String password) throws JargonException {
		originalOut.println("PUT " + filename + " to " + host);
		
		// create files
		String targetIrodsFile = IRODS_PATH + filename;
		File localFile = new File(filename);
		
		// authorize with iRODS
		IRODSAccount irodsAccount = new IRODSAccount(host, PORT, user, password, HOME_DIR, ZONE, RESOURCE);
		IRODSFileFactory irodsFileFactory = irodsFileSystem.getIRODSFileFactory(irodsAccount);
		IRODSFile iRODSFile = irodsFileFactory.instanceIRODSFile(targetIrodsFile);
		DataTransferOperations dataTransferOperationsAO = irodsFileSystem.getIRODSAccessObjectFactory().getDataTransferOperations(irodsAccount);

		// transfer file to iRODS grid
		dataTransferOperationsAO.putOperation(localFile, iRODSFile, null, null);
		
		return 0;
	}
	
	/**
	 * Function to attempt to get file back from iRODS server. Attempts every 1s until success or
	 * an error other than "File not found"
	 * 
	 * @param timestamp		timestamp to ensure unique filenames on iRODS server
	 * @param filename		local filename
	 * @return				results integer
	 * @throws JargonException 
	 */
	public int getResponse(String filename, String host, String user, String password) throws JargonException {
		originalOut.println("GET " + filename + ".res");
		
		// authorize with iRODS
		IRODSAccount irodsAccount = new IRODSAccount(host, PORT, user, password, HOME_DIR, ZONE, RESOURCE);
		IRODSFileFactory irodsFileFactory = irodsFileSystem.getIRODSFileFactory(irodsAccount);
		DataTransferOperations dataTransferOperationsAO = irodsFileSystem.getIRODSAccessObjectFactory().getDataTransferOperations(irodsAccount);
		
		// generate the files
		// localFile
		File localFile = new File("output_" + filename + "s");
		if (localFile.exists()) {
			localFile.delete();
		}
		// iRODS file
		String iRODSFilename = IRODS_PATH + filename + ".res";
		IRODSFile iRODSFile = irodsFileFactory.instanceIRODSFile(iRODSFilename);
		
		// get operation - retry until success. checks once per second
		boolean done = false;
		while (!done) {
			try {
				dataTransferOperationsAO.getOperation(iRODSFile, localFile, null, null);
				done = true;
			} catch (JargonException e) {
				if (!e.getMessage().equals("File not found")) {
					throw e;
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {
					ie.printStackTrace(originalErr);
				};
			}
		}
		
		return 0;
	}

	/**
	 * Handles timestamping and put/get operations for a Jargon iRODS transfer
	 * @param filename	name of file to be transmitted
	 * @param command	deploy/delete command to be executed
	 * @param index		index of server being uploaded to
	 * @param host		server's hostname
	 * @param user		iRODS username
	 * @param password	iRODS password
	 * @return
	 * @throws JargonException
	 */
	 public String transmit(String filename, boolean overwrite, String command, int index, String host, String user, String password) throws JargonException {
		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		
		String newFilename;
		
		try {
			newFilename = prepareFile(command, overwrite, index, timestamp, filename);
			putFile(newFilename, host, user, password);
			getResponse(newFilename, host, user, password);
			new File(newFilename).delete();
		} catch (OverwriteException e) {
			originalErr.println("File " + filename + " already exists on target.");
			e.printStackTrace(originalErr);
		}  catch (IOException e) {
			e.printStackTrace(originalErr);
		}
		
		return timestamp;
	}
	
	/**
	 * creates a new file with the appropriate timestamped name
	 * prepends the given file contents with the given command
	 * @param command
	 * @param timestamp
	 * @param filename
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String prepareFile(String command, boolean overwrite, int index, String timestamp, String filename) throws FileNotFoundException, IOException {
		String indexString = Integer.toString(index);
		String newfileName = indexString + "_" + timestamp + "-" + filename;
		
		// open streams
		OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(new File(newfileName)));
		BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
		
		// create new file, prepended with command and overwrite settings
		os.write(command + ",");
		if (overwrite) {
			os.write("overwrite=yes\n");
		} else {
			os.write("overwrite=no\n");
		}
		
		String line;
		while ((line = br.readLine()) != null) {
			os.write(line + "\n");
		}
		
		// close streams
		os.close();
		br.close();
		
		return newfileName;
	}
	
	public void breakdown() {
		// MUST COME LAST
		irodsFileSystem.closeAndEatExceptions();
	}

	public void configure() throws JargonException {
		// From testing setup().
		irodsFileSystem = IRODSFileSystem.instance();
		SettableJargonProperties settableJargonProperties = new SettableJargonProperties(irodsFileSystem.getJargonProperties());
		settableJargonProperties.setInternalCacheBufferSize(-1);
		settableJargonProperties.setInternalOutputStreamBufferSize(65535);
		irodsFileSystem.getIrodsSession().setJargonProperties(settableJargonProperties);
	}
}
