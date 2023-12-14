package kr.lindol.mycashbook.data.db;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static CashType fromIntFlag(Integer value) {
        if (value == 0) {
            return CashType.INCOME;
        } else {
            return CashType.EXPENSE;
        }
    }

    @TypeConverter
    public static Integer cashTypeToIntFlag(CashType type) {
        return type.getValue();
    }
}
