package com.fss.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class aq extends OutputStream
{
  private int jdField_if = 1048576;
  private File a;

  public aq(String paramString)
    throws IOException
  {
    this.a = new File(paramString);
    if (!this.a.exists())
      this.a.createNewFile();
  }

  public void write(int paramInt)
  {
    FileUtil.backup(this.a.getAbsolutePath(), this.jdField_if);
    FileOutputStream localFileOutputStream = null;
    try
    {
      localFileOutputStream = new FileOutputStream(this.a, true);
      localFileOutputStream.write(paramInt);
    }
    catch (Exception localException)
    {
    }
    finally
    {
      FileUtil.safeClose(localFileOutputStream);
    }
  }

  public void write(byte[] paramArrayOfByte)
  {
    FileUtil.backup(this.a.getAbsolutePath(), this.jdField_if);
    FileOutputStream localFileOutputStream = null;
    try
    {
      localFileOutputStream = new FileOutputStream(this.a, true);
      localFileOutputStream.write(paramArrayOfByte);
    }
    catch (Exception localException)
    {
    }
    finally
    {
      FileUtil.safeClose(localFileOutputStream);
    }
  }

  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    FileUtil.backup(this.a.getAbsolutePath(), this.jdField_if);
    FileOutputStream localFileOutputStream = null;
    try
    {
      localFileOutputStream = new FileOutputStream(this.a, true);
      if (paramInt2 > 2)
      {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
        String str = localSimpleDateFormat.format(new Date()) + " ";
        localFileOutputStream.write(str.getBytes());
      }
      localFileOutputStream.write(paramArrayOfByte, paramInt1, paramInt2);
    }
    catch (Exception localException)
    {
    }
    finally
    {
      FileUtil.safeClose(localFileOutputStream);
    }
  }

  public void a(int paramInt)
    throws Exception
  {
    if (paramInt <= 0)
      throw new Exception("FileSize must greater than 0 byte");
    this.jdField_if = paramInt;
  }

  public int a()
  {
    return this.jdField_if;
  }
}

/* Location:           E:\portable\FPTServiceManager_20101214.jar
 * Qualified Name:     com.fss.util.aq
 * JD-Core Version:    0.6.0
 */