package kr.lindol.mycashbook.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cash_logs")
public class CashLog {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "item")
    public String item;

    @ColumnInfo(name = "type")
    @NonNull
    public CashType type;

    @ColumnInfo(name = "amount")
    public int amount;

    @ColumnInfo(name = "date_tag")
    public int dateTag;

    @ColumnInfo(name = "day_tag")
    public String dayTag;

    @ColumnInfo(name = "month_tag")
    public String monthTag;

    @ColumnInfo(name = "created_by")
    public long createdBy;

    @ColumnInfo(name = "description")
    public String description;
}
