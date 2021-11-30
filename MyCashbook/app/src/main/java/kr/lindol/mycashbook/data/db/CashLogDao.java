package kr.lindol.mycashbook.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CashLogDao {
    @Query("SELECT * FROM cash_logs WHERE day_tag = :dayTag")
    List<CashLog> loadByDate(String dayTag);

    @Insert
    void insertAll(CashLog... cashLogs);

    @Delete
    void delete(CashLog log);

    @Query("SELECT sum(amount) FROM cash_logs WHERE month_tag = :monthTag AND type = :type")
    long sumOnMonth(String monthTag, int type);

    @Query("SELECT sum(amount) FROM cash_logs WHERE day_tag = :date AND type = :type")
    long sumOnDate(String date, int type);
}