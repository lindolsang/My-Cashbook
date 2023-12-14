package kr.lindol.mycashbook.util;

import static com.google.common.base.Preconditions.checkNotNull;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    /**
     * convert to Date object from the date string that is consisted with 'yyyyMMdd'
     *
     * @param date
     * @return Date object that was converted from string
     */
    public static Date fromStr(@NonNull String date) {
        checkNotNull(date, "date can not be null");

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        try {
            return sf.parse(date);
        } catch (ParseException e) {
            String msg =
                    String.format("date should be consisted with 'yyyyMMdd' format. but %s", date);
            throw new IllegalArgumentException(msg);
        }
    }
}
