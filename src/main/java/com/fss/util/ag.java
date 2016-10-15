package com.fss.util;

public class ag extends Exception
{
  private String jdField_do = null;
  private String jdField_if = null;
  private String a = null;

  public ag(String paramString)
  {
    jdField_do(paramString);
  }

  public ag(String paramString1, String paramString2)
  {
    jdField_if(paramString2);
    jdField_do(paramString1);
  }

  public ag(String paramString1, String paramString2, String paramString3)
  {
    jdField_if(paramString2);
    a(paramString3);
    jdField_do(paramString1);
  }

  public ag(Exception paramException, String paramString)
  {
    super(paramException);
    jdField_if(paramString);
    if ((paramException instanceof ag))
    {
      jdField_do(((ag)paramException).a());
      a(((ag)paramException).jdField_do());
    }
    else
    {
      jdField_do(paramException.getMessage());
    }
  }

  public ag(Exception paramException, String paramString1, String paramString2)
  {
    super(paramException);
    jdField_if(paramString1);
    a(paramString2);
    if ((paramException instanceof ag))
      jdField_do(((ag)paramException).a());
    else
      jdField_do(paramException.getMessage());
  }

  public String getLocalizedMessage()
  {
    return this.jdField_do;
  }

  public String getMessage()
  {
    String str = this.jdField_do;
    if ((this.jdField_if != null) && (this.jdField_if.length() > 0))
      str = str + "\r\nContext: " + this.jdField_if;
    if ((this.a != null) && (this.a.length() > 0))
      str = str + "\r\nAdditional info: " + this.a;
    return str;
  }

  public String toString()
  {
    return getMessage();
  }

  public String jdField_if()
  {
    return this.jdField_if;
  }

  public void jdField_if(String paramString)
  {
    this.jdField_if = paramString;
  }

  public String jdField_do()
  {
    return this.a;
  }

  public void a(String paramString)
  {
    this.a = paramString;
  }

  public String a()
  {
    return this.jdField_do;
  }

  public void jdField_do(String paramString)
  {
    if (paramString == null)
      this.jdField_do = "Null pointer exception";
    else
      this.jdField_do = paramString.trim();
  }
}

/* Location:           E:\portable\FPTServiceManager_20101214.jar
 * Qualified Name:     com.fss.util.ag
 * JD-Core Version:    0.6.0
 */