package com.fss.util;

import java.io.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: FSS-FPT</p>
 * @author Thai Hoang Hiep
 * @version 1.0
 */

public class BufferOutputStream extends OutputStream
{
	////////////////////////////////////////////////////////
	// Constant
	////////////////////////////////////////////////////////
	public static int EXTEND_SIZE = 8192;
	////////////////////////////////////////////////////////
	// Member variable
	////////////////////////////////////////////////////////
	public int miLength = 0;
	private byte[] mbtBuffer;
	////////////////////////////////////////////////////////
	// CBuffer constructor
	////////////////////////////////////////////////////////
	public BufferOutputStream()
	{
		mbtBuffer = new byte[miLength];
	}
	////////////////////////////////////////////////////////
	public BufferOutputStream(int iLength)
	{
		mbtBuffer = new byte[iLength];
	}
	////////////////////////////////////////////////////////
	//
	////////////////////////////////////////////////////////
	public void extend()
	{
		byte[] btBufferNew = new byte[mbtBuffer.length + EXTEND_SIZE];
		System.arraycopy(mbtBuffer,0,btBufferNew,0,miLength);
		mbtBuffer = btBufferNew;
	}
	////////////////////////////////////////////////////////
	//
	////////////////////////////////////////////////////////
	public void append(byte[] btBuffer)
	{
		int iNewLength = btBuffer.length + miLength;
		while(mbtBuffer.length < iNewLength)
			extend();
		System.arraycopy(btBuffer,0,mbtBuffer,miLength,btBuffer.length);
		miLength = iNewLength;
	}
	////////////////////////////////////////////////////////
	//
	////////////////////////////////////////////////////////
	public void append(byte[] btBuffer,int iOffset,int iLength)
	{
		int iNewLength = iLength + miLength;
		while(mbtBuffer.length < iNewLength)
			extend();
		System.arraycopy(btBuffer,iOffset,mbtBuffer,miLength,iLength);
		miLength = iNewLength;
	}
	////////////////////////////////////////////////////////
	//
	////////////////////////////////////////////////////////
	public void append(byte btValue)
	{
		if(miLength >= mbtBuffer.length)
			extend();
		mbtBuffer[miLength] = btValue;
		miLength++;
	}
	////////////////////////////////////////////////////////
	//
	////////////////////////////////////////////////////////
	public void load(InputStream in) throws IOException
	{
		miLength = in.available();
		mbtBuffer = new byte[miLength];
		int iByteRead = 0;
		while(iByteRead < miLength)
			iByteRead += in.read(mbtBuffer,iByteRead,miLength - iByteRead);
	}
	////////////////////////////////////////////////////////
	// Override function
	////////////////////////////////////////////////////////
	public void write(int iValue)
	{
		append((byte)iValue);
	}
	////////////////////////////////////////////////////////
	public void write(byte bt[])
	{
		append(bt);
	}
	////////////////////////////////////////////////////////
	public void write(byte bt[],int iOffset,int iLength)
	{
		append(bt,iOffset,iLength);
	}
	////////////////////////////////////////////////////////
	public byte[] getBuffer()
	{
		return mbtBuffer;
	}
}
