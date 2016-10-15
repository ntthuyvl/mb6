package com.fss.util;

import java.io.File;
import java.io.FileFilter;

public class i
  implements FileFilter
{
  private String a;
  private boolean jdField_if;

  public i(String paramString, boolean paramBoolean)
  {
    this.a = paramString;
    this.jdField_if = paramBoolean;
  }

  public boolean accept(File paramFile)
  {
    if (paramFile.isDirectory())
      return this.jdField_if;
    return d.a(this.a, paramFile.getName());
  }
}

/* Location:           E:\portable\FPTServiceManager_20101214.jar
 * Qualified Name:     com.fss.util.i
 * JD-Core Version:    0.6.0
 */