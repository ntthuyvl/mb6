package com.fss.util;

import java.io.PrintWriter;
import java.io.StringWriter;

class m extends PrintWriter
{
  public m()
  {
    super(new StringWriter());
  }

  public m(int paramInt)
  {
    super(new StringWriter(paramInt));
  }

  public String a()
  {
    flush();
    return ((StringWriter)this.out).toString();
  }
}

/* Location:           E:\portable\FPTServiceManager_20101214.jar
 * Qualified Name:     com.fss.util.m
 * JD-Core Version:    0.6.0
 */