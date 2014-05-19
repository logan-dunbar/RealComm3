package com.openbox.realcomm3.utilities.helpers;

import java.util.Collections;
import java.util.List;

public class StringHelper
{
	public static String join(List<String> array, String delimiter)
	{
		array.removeAll(Collections.singleton(null));
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
}
