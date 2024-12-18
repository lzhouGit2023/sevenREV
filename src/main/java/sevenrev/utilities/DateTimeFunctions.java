package sevenrev.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by roy on 2017-02-21.
 */
public class DateTimeFunctions {
    private static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;

    public static String DateToString(Date date) {
        //DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public static Date StringToDate(String DateStr) throws java.text.ParseException {
        //DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd");
        return iso8601Format.parse(DateStr);
    }

    public static Date removeHMS(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Date(calendar.getTime().getTime() + MILLIS_IN_A_DAY);
    }
}
