package kr.lindol.mycashbook.list;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import java.text.DecimalFormat;

public class CashLogListFragmentTest {

    @Test
    public void decimalFormat() {
        DecimalFormat df = new DecimalFormat("###,###");

        assertThat(df.format(1), equalTo("1"));
        assertThat(df.format(11), equalTo("11"));
        assertThat(df.format(111), equalTo("111"));
        assertThat(df.format(1111), equalTo("1,111"));
        assertThat(df.format(11111), equalTo("11,111"));
        assertThat(df.format(111111), equalTo("111,111"));
        assertThat(df.format(1111111), equalTo("1,111,111"));
        assertThat(df.format(11111111), equalTo("11,111,111"));
        assertThat(df.format(111111111), equalTo("111,111,111"));
        assertThat(df.format(1111111111), equalTo("1,111,111,111"));
    }
}