package com.fss.ddtp;

import java.util.*;
import java.net.*;

import com.fss.util.*;

/**
 * <p>Title: Listen from socket </p>
 * <p>Description:
 *  - Always listen from socket
 *  - Data which it's read then is passed to 2 queue: request and response
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: FSS-FPT</p>
 * @author
 * - Thai Hoang Hiep
 * - Dang Dinh Trung
 * @version 1.0
 */

public class SocketTransmitter implements Runnable,Transmitter
{
	public static final int MAX_QUEUE_SIZE = 1024;
	protected Vector mvtRequest = new Vector();
	protected Vector mvtResponse = new Vector();
	protected Thread mthrMain;
	protected String mstrUserName;
	protected String mstrUserID;
	private String mstrPackage = null;
	public Object mobjParent;
	public Socket msckMain;
	public java.util.Date dtStart = null;
	public SocketServer processor = new SocketServer(this);
	public static int iWaitTimeOut = 120;
	////////////////////////////////////////////////////////
	/**
	 * @return current class package
	 * @author Thai Hoang Hiep
	 */
	////////////////////////////////////////////////////////
	public String getPackage()
	{
		return mstrPackage;
	}
	////////////////////////////////////////////////////////
	/**
	 * @param strPackage String
	 * @author Thai Hoang Hiep
	 */
	////////////////////////////////////////////////////////
	public void setPackage(String strPackage)
	{
		mstrPackage = strPackage;
	}
	////////////////////////////////////////////////////////////
	/**
	 * Create transmitter thread from socket
	 * @param sck socket
	 */
	////////////////////////////////////////////////////////////
	public SocketTransmitter(Socket sck)
	{
		msckMain = sck;
	}
	////////////////////////////////////////////////////////////
	/**
	 * always add ddtp from socket to two queues:
	 *   Vector mvtRequest or Vector mvtResponse
	 * @author
	 *    - HiepTH
	 *    - TrungDD
	 */
	////////////////////////////////////////////////////////////
	public void run()
	{
		try
		{
			dtStart = new java.util.Date();
			while(isOpen())
			{
				DDTP ddtp = new DDTP(msckMain.getInputStream());
				if(ddtp != null)
				{
					if(ddtp.getResponseID().length() > 0) // Is response
					{
						while(mvtResponse.size() >= MAX_QUEUE_SIZE)
							mvtResponse.removeElementAt(0);
						mvtResponse.addElement(ddtp);
					}
					else
					{
						while(mvtRequest.size() >= MAX_QUEUE_SIZE)
							mvtRequest.removeElementAt(0);
						mvtRequest.addElement(ddtp);
					}
				}
				else
				{
					try
					{
						Thread.sleep(100);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		catch(SocketException e)
		{
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			close();
		}
	}
	////////////////////////////////////////////////////////////
	/**
	 * Start transmitter thread
	 */
	////////////////////////////////////////////////////////////
	public void start()
	{
		// Start transmitter
		if(mthrMain != null)
			mthrMain.stop();
		mthrMain = new Thread(this);
		mthrMain.start();

		// Start processor for transmitter
		processor.start();
	}
	////////////////////////////////////////////////////////////
	/**
	 * gets DDTP request from request queue and removes it
	 * @param iIndex request index
	 * @return request data
	 * Author: TrungDD
	 */
	////////////////////////////////////////////////////////////
	public DDTP getRequest(int iIndex)
	{
		if(mvtRequest==null)
			return null;
		else if (iIndex < 0 || iIndex >= mvtRequest.size())
			return null;

		DDTP ddtpReturn = (DDTP)mvtRequest.elementAt(iIndex);
		mvtRequest.removeElementAt(iIndex);
		return ddtpReturn;
	}
	////////////////////////////////////////////////////////////
	/**
	 * gets DDTP response from response queue and removes it
	 * @param iIndex response index
	 * @return response data
	 * Author: TrungDD
	 */
	////////////////////////////////////////////////////////////
	public DDTP getResponse(int iIndex)
	{
		if(mvtResponse==null)
			return null;
		else if(iIndex < 0 || iIndex >= mvtResponse.size())
			return null;

		DDTP ddtpReturn = (DDTP)mvtResponse.elementAt(iIndex);
		mvtResponse.removeElementAt(iIndex);
		return ddtpReturn;
	}
	////////////////////////////////////////////////////////////
	/**
	 * send and synchronized a packet through a socket
	 * @param packet contain request data
	 * @throws java.lang.Exception
	 * @author TrungDD
	 */
	////////////////////////////////////////////////////////////
	public synchronized void send(DDTP packet) throws Exception
	{
		if(isOpen())
			packet.store(msckMain.getOutputStream());
	}
	////////////////////////////////////////////////////////////
	/**
	 * sends request thought socket
	 * @param strClass name of class contain function to invoke
	 * @param strFunctionName name of function to invoke
	 * @return response data
	 * @throws java.lang.Exception
	 * @author TrungDD
	 */
	////////////////////////////////////////////////////////////
	public DDTP sendRequest(String strClass,String strFunctionName) throws Exception
	{
		return sendRequest(strClass,strFunctionName,null);
	}
	////////////////////////////////////////////////////////////
	/**
	 * sends request thought socket and wait for response
	 * @param request contain request data
	 * @param strClass name of class contain function to invoke
	 * @param strFunctionName name of function to invoke
	 * @return response data
	 * @throws java.lang.Exception
	 * @author TrungDD
	 */
	////////////////////////////////////////////////////////////
	public DDTP sendRequest(String strClass,String strFunctionName,DDTP request) throws Exception
	{
		// Create empty request if passed value is null
		if(request == null)
			request = new DDTP();

		// Set function name & class name
		request.setFunctionName(strFunctionName);
		if(strClass.indexOf(".") < 0)
			strClass = StringUtil.nvl(mstrPackage,"") + strClass;
		request.setClassName(strClass);

		// Call servlet
		return sendRequest(request);
	}
	////////////////////////////////////////////////////////////
	/**
	 * sends response though socket only, do not wait
	 * @param response conatain response data
	 * @throws java.lang.Exception
	 * @author TrungDD
	 */
	////////////////////////////////////////////////////////////
	public void sendResponse(DDTP response) throws Exception
	{
		send(response);
	}
	////////////////////////////////////////////////////////////
	/**
	 * send request and wait response
	 * @param request contain request data
	 * @return response data
	 * @throws java.lang.Exception
	 * @author TrungDD
	 */
	////////////////////////////////////////////////////////////
	private DDTP sendRequest(DDTP request) throws Exception
	{
		if(isOpen())
		{
			// Send request
			String strRequestID = request.getRequestID();
			send(request);
			if(strRequestID.length() == 0)
				return null;

			// Wait response
			DDTP response = null;
			int iIndex = 0;
			while(isOpen() && (iIndex < iWaitTimeOut))
			{
				response = getResponse(strRequestID);
				if(response != null)
				{
					// Test error
					AppException e = response.getException();
					if(e != null)
						throw e;
					return response;
				}
				iIndex++;
				Thread.sleep(1000);
			}

			// Response timeout
			if(isOpen())
			{
				String strDescription = "Quá thời gian time out: " + String.valueOf(iWaitTimeOut) + " giây";
				String strContext = "SocketTransmitter.sendRequest";
				String strInfo = "";
				throw new AppException(strDescription,strContext,strInfo);
			}
		}
		return null;
	}
	///////////////////////////////////////////////////////
	/**
	 * get response in mvtResponse depends on requestID
	 * @param strRequestID request id
	 * @return response data
	 * @author TrungDD
	 */
	///////////////////////////////////////////////////////
	private DDTP getResponse(String strRequestID)
	{
		int iThreadIndex = 0;
		int size = mvtResponse.size();
		while(iThreadIndex < size)
		{
			DDTP ddtpResponseInfo = (DDTP)mvtResponse.elementAt(iThreadIndex);
			if(ddtpResponseInfo.getResponseID().equals(strRequestID))
			{
				mvtResponse.removeElementAt(iThreadIndex);
				return ddtpResponseInfo;
			}
			iThreadIndex++;
		}
		return null;
	}
	///////////////////////////////////////////////////////
	/**
	 * Set user id for transmitter
	 * @param strUserID String
	 * @author TrungDD
	 */
	///////////////////////////////////////////////////////
	public void setUserID(String strUserID)
	{
		mstrUserID = strUserID;
	}
	///////////////////////////////////////////////////////
	/**
	 * @return user id
	 */
	///////////////////////////////////////////////////////
	public String getUserID()
	{
		return mstrUserID;
	}
	///////////////////////////////////////////////////////
	/**
	 * Set user name for transmitter
	 * @param strUserName String
	 * @author TrungDD
	 */
	///////////////////////////////////////////////////////
	public void setUserName(String strUserName)
	{
		mstrUserName = strUserName;
	}
	///////////////////////////////////////////////////////
	/**
	 * @return user name
	 */
	///////////////////////////////////////////////////////
	public String getUserName()
	{
		return mstrUserName;
	}
	///////////////////////////////////////////////////////
	/**
	 * Test connection
	 * @return true if connected otherwise false
	 * @author HiepTH
	 */
	///////////////////////////////////////////////////////
	public boolean isOpen()
	{
		return (msckMain != null && !msckMain.isClosed());
	}
	///////////////////////////////////////////////////////
	/**
	 * Close connection
	 * @author HiepTH
	 */
	///////////////////////////////////////////////////////
	public void close()
	{
		if(msckMain != null)
		{
			Socket sck = msckMain;
			msckMain = null;
			try
			{
				sck.close();
			}
			catch(Exception e)
			{
			}
		}
	}
}
