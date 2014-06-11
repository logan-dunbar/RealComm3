package com.openbox.realcomm.utilities.helpers;

import java.util.Collections;
import java.util.List;

public class StringHelper
{
	public static final String NEW_LINE = System.getProperty("line.separator");
	public static final String EMPTY_STRING = "";

	public static String join(List<String> array, String delimiter)
	{
		if (array == null || delimiter == null)
		{
			return null;
		}

		for (String string : array)
		{
			string = removeWhiteSpace(string);
		}

		array.removeAll(Collections.singleton(null));
		array.removeAll(Collections.singleton(EMPTY_STRING));

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.size(); i++)
		{
			if (i != 0)
			{
				sb.append(delimiter);
			}

			sb.append(array.get(i));
		}

		return sb.toString();
	}

	public static boolean isNullOrEmpty(String string)
	{
		string = removeWhiteSpace(string);

		return string == null ? true : string.equals(EMPTY_STRING);
	}

	public static String removeWhiteSpace(String string)
	{
		if (string == null)
		{
			return null;
		}

		return string.replaceAll("\\s", EMPTY_STRING);
	}
	
	public static String nullOrTrim(String string)
	{
		if (string == null)
		{
			return null;
		}
		
		return string.trim();
	}
}
