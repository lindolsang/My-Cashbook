package kr.lindol.mycashbook.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

//TODO 2023-09-15 needs to check to run Robolectric test
//@RunWith(RobolectricTestRunner.class)
public class DateUtilsTest {

    @Test
    public void fromStrThenReturnSpecifiedDateObject() {
        assertThat(DateUtils.fromStr("20230915"),
                equalTo(date(2023, 9, 15)));
    }

    @Test(expected = NullPointerException.class)
    public void fromStrThenThrowNullPointerExceptionWhenPassingNull() {
        DateUtils.fromStr(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromStrThenThrowIllegalArgumentExceptionWhenPassingInvalidDateFormat() {
        DateUtils.fromStr("230915");
    }

    private Date date(int year, int month, int date) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, date, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }
}