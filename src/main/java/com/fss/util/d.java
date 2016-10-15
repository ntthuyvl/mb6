package com.fss.util;

import java.io.File;
import java.io.FilenameFilter;

public class d
  implements FilenameFilter
{
  private String a;

  public d(String paramString)
  {
    this.a = paramString;
  }

  public static boolean a(String paramString1, String paramString2)
  {
    return a(paramString1, paramString2, true);
  }

  public static boolean a(String paramString1, String paramString2, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramString2 = paramString2.toUpperCase();
      paramString1 = paramString1.toUpperCase();
    }
    int i = 0;
    int j = 0;
    int k = paramString1.length();
    int m = paramString2.length();
    while ((j < m) && (i < k))
    {
      if ((paramString1.charAt(i) == paramString2.charAt(j)) || (paramString1.charAt(i) == '?'))
      {
        i++;
        j++;
        continue;
      }
      if (paramString1.charAt(i) == '*')
      {
        i++;
        if (i >= k)
          return true;
        while (true)
        {
          if ((j < m) && (paramString1.charAt(i) != paramString2.charAt(j)))
          {
            j++;
            continue;
          }
          if (j >= m)
            return false;
          String str1 = paramString1.substring(i, k);
          String str2 = paramString2.substring(j, m);
          if (a(str1, str2, paramBoolean))
            return true;
          j++;
        }
      }
      return false;
    }
    return (j >= m) && ((i >= k) || (a(paramString1.substring(i, paramString1.length()))));
  }

  public static boolean a(String paramString)
  {
    for (int i = 0; i < paramString.length(); i++)
      if (paramString.charAt(i) != '*')
        return false;
    return true;
  }

  public boolean accept(File paramFile, String paramString)
  {
    return a(this.a, paramString);
  }
}

/* Location:           E:\portable\FPTServiceManager_20101214.jar
 * Qualified Name:     com.fss.util.d
 * JD-Core Version:    0.6.0
 */