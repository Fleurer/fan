package com.googolmo.fanfou.utils.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * User: googolmo
 * Date: 12-12-9
 * Time: 下午7:45
 */
public class DateUtils {
	public static final String UTC_TIME_PATTEN = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
	public static final String LOCAL_TIME_PATTER = "yyyy-MM-dd HH:mm:ss";
	public static final String IMAGE_FILE_NAME_PATTER = "yyyyMMddHHmmssssss";

	public static String utc2Local(String utcTime, String localTimePatten) {
		SimpleDateFormat utcFormater = new SimpleDateFormat(UTC_TIME_PATTEN, Locale.ENGLISH);
		utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date gpsUTCDate = null;
		try {
			gpsUTCDate = utcFormater.parse(utcTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat localFormater = new SimpleDateFormat(localTimePatten);
		localFormater.setTimeZone(TimeZone.getDefault());
		String localTime = localFormater.format(gpsUTCDate.getTime());
		return localTime;
	}

	public static String getCurrentDateTime() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(LOCAL_TIME_PATTER);
		return format.format(calendar.getTime());
	}

	public static String getDateTimeString(String patten) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(patten);
		return format.format(calendar.getTime());
	}

}
