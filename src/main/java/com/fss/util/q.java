package com.fss.util;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.filechooser.FileFilter;

public class q extends FileFilter
{
  private static String jdField_if = "Type Unknown";
  private static String a = "Hidden File";
  private Hashtable jdField_for = null;
  private String jdField_do = null;
  private String jdField_new = null;
  private boolean jdField_int = true;

  public q()
  {
  }

  public q(String paramString)
  {
    this(paramString, null);
  }

  public q(String paramString1, String paramString2)
  {
    this();
    if (paramString1 != null)
      a(paramString1);
    if (paramString2 != null)
      jdField_if(paramString2);
  }

  public q(String[] paramArrayOfString)
  {
    this(paramArrayOfString, null);
  }

  public q(String[] paramArrayOfString, String paramString)
  {
    this();
    for (int i = 0; i < paramArrayOfString.length; i++)
      a(paramArrayOfString[i]);
    if (paramString != null)
      jdField_if(paramString);
  }

  public boolean accept(File paramFile)
  {
    if (paramFile != null)
    {
      if (paramFile.isDirectory())
        return true;
      String str = a(paramFile);
      if ((str != null) && (this.jdField_for.get(a(paramFile)) != null))
        return true;
    }
    return false;
  }

  public String a(File paramFile)
  {
    if (paramFile != null)
    {
      String str = paramFile.getName();
      int i = str.lastIndexOf('.');
      if ((i > 0) && (i < str.length() - 1))
        return str.substring(i + 1).toLowerCase();
    }
    return null;
  }

  public void a(String paramString)
  {
    if (this.jdField_for == null)
      this.jdField_for = new Hashtable(5);
    this.jdField_for.put(paramString.toLowerCase(), this);
    this.jdField_new = null;
  }

  public String getDescription()
  {
    if (this.jdField_new == null)
      if ((this.jdField_do == null) || (a()))
      {
        this.jdField_new = (this.jdField_do + " (");
        Enumeration localEnumeration = this.jdField_for.keys();
        if (localEnumeration != null)
          for (this.jdField_new = (this.jdField_new + "." + (String)localEnumeration.nextElement()); localEnumeration.hasMoreElements(); this.jdField_new = (this.jdField_new + ", ." + (String)localEnumeration.nextElement()));
        this.jdField_new += ")";
      }
      else
      {
        this.jdField_new = this.jdField_do;
      }
    return this.jdField_new;
  }

  public void jdField_if(String paramString)
  {
    this.jdField_do = paramString;
    this.jdField_new = null;
  }

  public void a(boolean paramBoolean)
  {
    this.jdField_int = paramBoolean;
    this.jdField_new = null;
  }

  public boolean a()
  {
    return this.jdField_int;
  }
}

/* Location:           E:\portable\FPTServiceManager_20101214.jar
 * Qualified Name:     com.fss.util.q
 * JD-Core Version:    0.6.0
 */