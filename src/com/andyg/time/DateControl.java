package com.andyg.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.joda.time.*;

/**
 * Created by Andy on 1/06/2016.
 */
public class DateControl {

    public static long toUnix(String date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date tempDate;
        try
        {
            tempDate = dateFormat.parse(date);
        }
        catch (ParseException e)
        {
            return Long.parseLong(date);
        }
        return tempDate.getTime() / 1000;
    }

    public static DateTime toDate(long unix)
    {
        return new DateTime(unix * 1000, DateTimeZone.UTC);
    }


}
