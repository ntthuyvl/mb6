package com.fss.util;

import java.util.ArrayList;
import java.util.List;

public class ac
{
  public static final int a = 1;
  public static final int jdField_do = 2;
  private String jdField_for;
  private int jdField_if = 1;
  private List jdField_int;

  public ac()
  {
    this(1);
  }

  public ac(int paramInt)
  {
    this(paramInt, new ArrayList());
  }

  public ac(int paramInt, List paramList)
  {
    this.jdField_if = paramInt;
    this.jdField_int = paramList;
  }

  public void a(String paramString)
  {
    this.jdField_for = paramString;
  }

  public void a(int paramInt)
  {
    this.jdField_if = paramInt;
  }

  public void a(List paramList)
  {
    this.jdField_int = paramList;
  }

  public String jdField_for()
  {
    return this.jdField_for;
  }

  public int jdField_if()
  {
    return this.jdField_if;
  }

  public List jdField_do()
  {
    return this.jdField_int;
  }

  public int a()
  {
    return this.jdField_int.size();
  }

  public boolean a(l paraml)
  {
    return this.jdField_int.add(paraml);
  }
}

/* Location:           E:\portable\FPTServiceManager_20101214.jar
 * Qualified Name:     com.fss.util.ac
 * JD-Core Version:    0.6.0
 */