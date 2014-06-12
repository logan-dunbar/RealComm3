package com.openbox.realcomm.utilities.helpers;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.openbox.realcomm.application.RealCommApplication;

public class UtcDateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date>
{
	private final SimpleDateFormat dateFormat;

	public UtcDateTypeAdapter()
	{
		this.dateFormat = new SimpleDateFormat(RealCommApplication.UPLOAD_DATE_FORMAT, Locale.US);
		this.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Override
	public synchronized Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		try
		{
			synchronized (dateFormat)
			{
				return dateFormat.parse(json.getAsString());
			}
		}
		catch (ParseException e)
		{
			throw new JsonSyntaxException(json.getAsString(), e);
		}
	}

	@Override
	public synchronized JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context)
	{
		String dateFormatAsString = dateFormat.format(src);
		return new JsonPrimitive(dateFormatAsString);
	}
}
