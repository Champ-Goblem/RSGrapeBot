package stumpy3toes.api.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {
    public static String formatTime(long time, boolean ms) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss" + (ms ? ".SSS" : ""));
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(new Date(time));
    }

    public static String formatTime(long time) {
        return formatTime(time, false);
    }

    public static String formatNumber(int number) {
        return new DecimalFormat("##,##,##,##,##,##,##0").format((double)number);
    }

    public static String formatNumber(double number) {
        return new DecimalFormat("##,##,##,##,##,##,##0.00").format(number);
    }
}
