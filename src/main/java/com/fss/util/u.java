package com.fss.util;

import java.util.EventObject;

public class u extends EventObject
{
  public static int jdField_if = 1;
  public static int jdField_do = 2;
  public static int jdField_for = 3;
  public static int a = 4;
  private int jdField_int;

  public u(Object paramObject)
  {
    super(paramObject);
  }

  public u(Object paramObject, int paramInt)
  {
    super(paramObject);
    this.jdField_int = paramInt;
  }

  public int a()
  {
    return this.jdField_int;
  }

  public void a(int paramInt)
  {
    this.jdField_int = paramInt;
  }
}

/* Location:           E:\portable\FPTServiceManager_20101214.jar
 * Qualified Name:     com.fss.util.u
 * JD-Core Version:    0.6.0
 */