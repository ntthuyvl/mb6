package com.fss.cdrbin.thread;

import java.io.FileInputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.TimeZone;

import com.fss.sql.Database;
import com.fss.thread.ProcessorListener;
import com.fss.thread.ThreadManager;
import com.fss.thread.ThreadProcessor;
import com.fss.util.DBTableAuthenticator;
import com.fss.util.DBTableLogUtil;
import com.fss.util.Global;
import com.fss.util.LogOutputStream;
import com.fss.util.StringUtil;

public class CDRBinManagerTnt implements ProcessorListener {
	// //////////////////////////////////////////////////////
	// Variables
	// //////////////////////////////////////////////////////
	@SuppressWarnings("rawtypes")
	private Hashtable mprtConfig = new Hashtable();
	ThreadManager cs;

	// //////////////////////////////////////////////////////
	// Constructor
	// //////////////////////////////////////////////////////
	public CDRBinManagerTnt() throws Exception {

		try {
			loadServerConfig("ServerConfig.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// //////////////////////////////////////////////////////
	// Purpose: Load parameter from resource
	// Date: 29/04/2003
	// //////////////////////////////////////////////////////
	public void loadServerConfig(String strFileName) throws Exception {
		mprtConfig = Global.loadHashtable(new FileInputStream(strFileName));
	}

	// //////////////////////////////////////////////////////
	// Purpose: Get parameter from memory
	// Date: 29/04/2003
	// //////////////////////////////////////////////////////
	public String getParameter(String strKey) {
		return StringUtil.nvl(mprtConfig.get(strKey), "");
	}

	// //////////////////////////////////////////////////////
	// Purpose: Get parameter from memory
	// Date: 29/04/2003
	// //////////////////////////////////////////////////////
	public Connection getConnection() throws Exception {
		if (getParameter("DatabaseExist").equals("N")) {
			return null;
		}
		return Database.getConnection(StringUtil.nvl(mprtConfig.get("DBUrl"), ""),
				StringUtil.nvl(mprtConfig.get("UserName"), ""), StringUtil.nvl(mprtConfig.get("Password"), ""));
	}

	public void onCreate(ThreadProcessor processor) throws Exception {
		if (getParameter("DatabaseExist").equals("N")) {
			return;
		}
		processor.log = new DBTableLogUtil();
		processor.authenticator = new com.fss.util.DBTableAuthenticator();
		// ((DBTableAuthenticator)processor.authenticator).setUsingCache(false);
	}

	public void onOpen(ThreadProcessor processor) throws Exception {
		if (getParameter("DatabaseExist").equals("N")) {
			return;
		}
		processor.mcnMain = getConnection();
		((DBTableLogUtil) processor.log).setConnection(processor.mcnMain);
		if (processor.channel != null && processor.channel.msckMain != null) {
			((DBTableLogUtil) processor.log).setIPAddress(processor.channel.msckMain.getInetAddress().getHostAddress());
		}
		((DBTableAuthenticator) processor.authenticator).setConnection(processor.mcnMain);
	}

	public void stop() {
		cs.close();
	}

	public void start() {
		try {
			// Change system output to file
			// CDRBinManagerTnt lsn = new CDRBinManagerTnt();
			// set timezone
			try {
				String strTimeZone = getParameter("TimeZone");
				if (!strTimeZone.equals("")) {
					TimeZone mtzMain = TimeZone.getTimeZone(strTimeZone);
					TimeZone.setDefault(mtzMain);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			String strLogFile = getParameter("LogFile");
			if (strLogFile == null || strLogFile.equals("")) {
				strLogFile = "error.log";
			}
			String strWorkingDir = System.getProperty("user.dir");
			if (!strWorkingDir.endsWith("/") || !strWorkingDir.endsWith("\\") && !strWorkingDir.equals("")) {
				strWorkingDir += "/";
			}
			PrintStream ps = new PrintStream(new LogOutputStream(strWorkingDir + strLogFile));
			System.setOut(ps);
			System.setErr(ps);

			// Application name
			Global.APP_NAME = "BG Thread Manager";
			Global.APP_VERSION = "3.0";

			// Port id
			int iPortID = 2301;
			try {
				iPortID = Integer.parseInt(getParameter("PortID"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			cs = new ThreadManager(iPortID, this);
			// Set action log file
			strLogFile = getParameter("ActionLogFile");
			if (strLogFile != null && !strLogFile.equals("")) {
				cs.setActionLogFile(strLogFile);
			}

			// Set max connection
			try {
				int iMaxConnectionAllowed = Integer.parseInt(getParameter("MaxConnectionAllowed"));
				if (iMaxConnectionAllowed > 0) {
					cs.setMaxConnectionAllowed(iMaxConnectionAllowed);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Set loading method
			try {
				int iLoadingMethod = Integer.parseInt(getParameter("LoadingMethod"));
				if (iLoadingMethod == ThreadManager.LOAD_FROM_FILE || iLoadingMethod == ThreadManager.LOAD_FROM_DB
						|| iLoadingMethod == ThreadManager.LOAD_FROM_FILE_AND_DB) {
					cs.setLoadingMethod(iLoadingMethod);
				}
			} catch (Exception e) {
				e.printStackTrace();
				String strLoadingMethod = getParameter("LoadingMethod");
				if (strLoadingMethod.equals("Database")) {
					cs.setLoadingMethod(ThreadManager.LOAD_FROM_DB);
				} else if (strLoadingMethod.equals("File")) {
					cs.setLoadingMethod(ThreadManager.LOAD_FROM_FILE);
				} else {
					cs.setLoadingMethod(ThreadManager.LOAD_FROM_FILE_AND_DB);
				}
			}
			// Start manager
			cs.start();
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

	// /////////////////////////////////////////////////////////
	// Main entry
	// /////////////////////////////////////////////////////////
	public static void main(String args[]) {
		try {
			// Change system output to file
			CDRBinManagerTnt lsn = new CDRBinManagerTnt();
			// set timezone
			try {
				String strTimeZone = lsn.getParameter("TimeZone");
				if (!strTimeZone.equals("")) {
					TimeZone mtzMain = TimeZone.getTimeZone(strTimeZone);
					TimeZone.setDefault(mtzMain);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			String strLogFile = lsn.getParameter("LogFile");
			if (strLogFile == null || strLogFile.equals("")) {
				strLogFile = "error.log";
			}
			String strWorkingDir = System.getProperty("user.dir");
			if (!strWorkingDir.endsWith("/") || !strWorkingDir.endsWith("\\") && !strWorkingDir.equals("")) {
				strWorkingDir += "/";
			}
			PrintStream ps = new PrintStream(new LogOutputStream(strWorkingDir + strLogFile));
			System.setOut(ps);
			System.setErr(ps);

			// Application name
			Global.APP_NAME = "BG Thread Manager";
			Global.APP_VERSION = "3.0";

			// Port id
			int iPortID = 2301;
			try {
				iPortID = Integer.parseInt(lsn.getParameter("PortID"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			ThreadManager cs = new ThreadManager(iPortID, lsn);
			// Set action log file
			strLogFile = lsn.getParameter("ActionLogFile");
			if (strLogFile != null && !strLogFile.equals("")) {
				cs.setActionLogFile(strLogFile);
			}

			// Set max connection
			try {
				int iMaxConnectionAllowed = Integer.parseInt(lsn.getParameter("MaxConnectionAllowed"));
				if (iMaxConnectionAllowed > 0) {
					cs.setMaxConnectionAllowed(iMaxConnectionAllowed);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Set loading method
			try {
				int iLoadingMethod = Integer.parseInt(lsn.getParameter("LoadingMethod"));
				if (iLoadingMethod == ThreadManager.LOAD_FROM_FILE || iLoadingMethod == ThreadManager.LOAD_FROM_DB
						|| iLoadingMethod == ThreadManager.LOAD_FROM_FILE_AND_DB) {
					cs.setLoadingMethod(iLoadingMethod);
				}
			} catch (Exception e) {
				e.printStackTrace();
				String strLoadingMethod = lsn.getParameter("LoadingMethod");
				if (strLoadingMethod.equals("Database")) {
					cs.setLoadingMethod(ThreadManager.LOAD_FROM_DB);
				} else if (strLoadingMethod.equals("File")) {
					cs.setLoadingMethod(ThreadManager.LOAD_FROM_FILE);
				} else {
					cs.setLoadingMethod(ThreadManager.LOAD_FROM_FILE_AND_DB);
				}
			}
			// Start manager
			cs.start();

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
