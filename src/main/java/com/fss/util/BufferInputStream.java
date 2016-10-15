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

public class BufferInputStream extends InputStream
{
	////////////////////////////////////////////////////////
	// Member variables
	////////////////////////////////////////////////////////
	public byte[] mbtData;
	public int miFirstOffset = 0;
	public int miLastOffset = 0;
	////////////////////////////////////////////////////////
	// Constructor
	////////////////////////////////////////////////////////
	public BufferInputStream(byte bt[])
	{
		create(bt,0,bt.length);
	}
	////////////////////////////////////////////////////////
	public BufferInputStream(byte bt[],int iFirstOffset,int iLastOffset)
	{
		create(bt,iFirstOffset,iLastOffset);
	}
	////////////////////////////////////////////////////////
	public void create(byte bt[],int iFirstOffset,int iLastOffset)
	{
		mbtData = bt;
		miFirstOffset = iFirstOffset;
		miLastOffset = iLastOffset;
	}
	////////////////////////////////////////////////////////
	// Override function
	////////////////////////////////////////////////////////
	public int read()
	{
		if(available() <= 0)
			return -1;
		return mbtData[miFirstOffset++];
	}
	////////////////////////////////////////////////////////
	public int read(byte[] bt)
	{
		if(available() <= 0)
			return -1;
		int iLength = bt.length;
		if(iLength > miLastOffset - miFirstOffset)
			iLength = miLastOffset - miFirstOffset;
		System.arraycopy(mbtData,miFirstOffset,bt,0,iLength);
		miFirstOffset += iLength;
		return iLength;
	}
	////////////////////////////////////////////////////////
	public int read(byte[] bt,int iOffset,int iLength)
	{
		if(available() <= 0)
			return -1;
		if(iLength > miLastOffset - miFirstOffset)
			iLength = miLastOffset - miFirstOffset;
		System.arraycopy(mbtData,miFirstOffset,bt,iOffset,iLength);
		miFirstOffset += iLength;
		if(iLength == 0)
			return - 1;
		return iLength;
	}
	////////////////////////////////////////////////////////
	public long skip(long lBytesSkip)
	{
		if(available() <= 0)
			return -1;
		if(lBytesSkip > miLastOffset - miFirstOffset)
			lBytesSkip = miLastOffset - miFirstOffset;
		miFirstOffset += lBytesSkip;
		return lBytesSkip;
	}
	////////////////////////////////////////////////////////
	public int available()
	{
		return miLastOffset - miFirstOffset;
	}
	////////////////////////////////////////////////////////
	public boolean markSupported()
	{
		return false;
	}
	////////////////////////////////////////////////////////
	public String toString()
	{
		return new String(mbtData);
	}
}
