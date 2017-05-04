package com.emc.metalnx.irods.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.OverwriteException;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.emc.metalnx.irods.iRODSFileTransfer;
import com.emc.metalnx.irods.iRODSMaster;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class iRODSPutFileTest {
	
	// files on local system
	final String acPostProcForPut_file = "acPostProcForPutRule.re";
	
	// iRODS args
	static final String HOST = "sd-vm14.csc.ncsu.edu";
	static final String USER = "rods";
	static final String PASSWORD = "irods";
	
	// print streams
	public static PrintStream originalErr = System.err;
	
	iRODSFileTransfer iRODSInstance;

	@Before
	public void setUp() throws JargonException, InterruptedException {
		iRODSMaster.silence(false);
		
		iRODSInstance = new iRODSFileTransfer(iRODSMaster.originalOut, iRODSMaster.originalErr);
		iRODSInstance.configure();
		
		Thread.sleep(1000);
	}
	
	@After
	public void tearDown() {
		iRODSInstance.breakdown();
	}
	
	@Test
	/**
	 * Puts the rule file up to remote server with the message "deploy"
	 */
	public void aaatestDeployFile() {
		try {
			uploadFileWithVerif(acPostProcForPut_file, false, 0, HOST, "deploy", "Deployed");
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	/**
	 * Puts the rule file up to remote server with the command "delete"
	 */
	public void aabtestDeleteFile() {
		try {
			uploadFileWithVerif(acPostProcForPut_file, false, 0, HOST, "delete", "Deleted");
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	/**
	 * Puts a file with a command that is neither deploy nor accept
	 * The file should be ignored by the server
	 */
	public void bbbtestIgnoreFile() {
		try {
			uploadFileWithVerif(acPostProcForPut_file, false, 0, HOST, "ignore", "No action taken");
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Accepts file, command, and results strings. Tests that put/get (transmit) behavior works as expected and
	 * that the correct string is returned in the results file.
	 * @throws IOException
	 * @throws JargonException
	 */
	public void uploadFileWithVerif(String file, boolean overwrite, int index, String host, String command, String res) throws IOException, JargonException {
		try {
			// run transmit file
			String timestamp = iRODSInstance.transmit(file, overwrite, command, index, host, USER, PASSWORD);
			String indexString = Integer.toString(index);
			
			// create local file
			File outfile = new File("output_" + indexString + "_" + timestamp + "-" + file + "s");
			
			// check for correct files, file contents
			assertTrue(outfile.exists());
			FileReader fr = new FileReader(outfile);
			BufferedReader br = new BufferedReader(fr);
			assertEquals(br.readLine(), res);
			br.close();
		} catch (OverwriteException e) {
			// fail on unexpected exception
			fail("File " + file + " already exists on target system. Must be removed using \'irm\' on target.");
		}
	}
	
	@Test
	public void zzvtestMultipleDeploys() {
		String[] commands = {"deploy","deploy","delete"};
		String[] results = {"Deployed", "File already present; did not deploy", "Deleted"};
		for (int i = 0; i < commands.length; i++) {
			try {
				uploadFileWithVerif(acPostProcForPut_file, false, 0, HOST, commands[i], results[i]);
			} catch (Exception e) {
				fail(e.getMessage());
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace(originalErr);
			}
		}
	}
	
	@Test
	public void zzwtestMultipleDeletes() {
		String[] commands = {"deploy","delete","delete"};
		String[] results = {"Deployed", "Deleted", "File not found or not present in server_config.json"};
		for (int i = 0; i < commands.length; i++) {
			try {
				uploadFileWithVerif(acPostProcForPut_file, false, 0, HOST, commands[i], results[i]);
			} catch (Exception e) {
				fail(e.getMessage());
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace(originalErr);
			}
		}
	}
	
	@Test
	public void zzxtestAccessMultipleHosts() {
		iRODSMaster.generateHostsLists();
		
		String[] commands = {"deploy","delete", "ignore"};
		String[] results = {"Deployed", "Deleted", "No action taken"};
		for (int i = 0; i < commands.length; i++) {
			for (int index = 0; index < iRODSMaster.hosts.size(); index++) {
				try {
					uploadFileWithVerif(acPostProcForPut_file, false, index, iRODSMaster.hosts.get(index), commands[i], results[i]);
				} catch (Exception e) {
					fail(e.getMessage());
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {
					ie.printStackTrace(originalErr);
				}
			}
		}
	}
	
	@Test
	/**
	 * Checks for correct error on attempted overwrite on target system.
	 * Uses acceptfile.txt as this file was previously written in another test.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws JargonException
	 */
	public void zzytestRemoteFilenameOverwrite() throws FileNotFoundException, IOException, JargonException {
		iRODSMaster.generateHostsLists();
		
		String[] commands = {"deploy","deploy", "deploy", "delete"};
		boolean[] overwrites = {false, false, true, false};
		String[] results = {"Deployed", "File already present; did not deploy", "Deployed", "Deleted"};
		for (int i = 0; i < commands.length; i++) {
			try {
				uploadFileWithVerif(acPostProcForPut_file, overwrites[i], 0, HOST, commands[i], results[i]);
			} catch (Exception e) {
				fail(e.getMessage());
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace(originalErr);
			}
		}
	}
	
	@Test
	/**
	 * Checks for correct error on attempted overwrite on target system.
	 * Uses acceptfile.txt as this file was previously written in another test.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws JargonException
	 */
	public void zzztestLocalFilenameOverwrite() throws FileNotFoundException, IOException, JargonException {
		String newFilename = "";
		
		try {
			String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

			// run put - with duplicated timestamp, one of these will fail for sure
			newFilename = iRODSInstance.prepareFile("deploy", true, 0, timestamp, acPostProcForPut_file);
			iRODSInstance.putFile(newFilename, HOST, USER, PASSWORD);
			iRODSInstance.putFile(newFilename, HOST, USER, PASSWORD);
			
			new File(newFilename).delete();
			
			fail("File should not have successfully written to target system.");
		} catch (OverwriteException e) {
			assertTrue(e.getMessage().contains("attempt to overwrite file, target file already exists"));
			
			// run this to remove the rule file from the remote server
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace(originalErr);
			};
			
			uploadFileWithVerif(acPostProcForPut_file, false, 0, HOST, "delete", "Deleted");
		}
		
		new File(newFilename).delete();
	}
}
