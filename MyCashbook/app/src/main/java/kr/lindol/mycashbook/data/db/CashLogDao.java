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

    @Query("SELECT * FROM cash_logs WHERE month_tag = :monthTag " +
            "ORDER BY date_tag ASC")
    List<CashLog> loadByMonth(String monthTag);

    @Query("SELECT * FROM cash_logs WHERE date_tag BETWEEN :dateFrom AND :dateTo " +
            "ORDER BY date_tag ASC")
    List<CashLog> loadByDateRange(int dateFrom, int dateTo);

    @Insert
    void insertAll(CashLog... cashLogs);

    @Delete
    void delete(CashLog log);

    @Query("SELECT sum(amount) FROM cash_logs WHERE month_tag = :monthTag AND type = :type")
    long sumOnMonth(String monthTag, int type);

    @Query("SELECT sum(amount) FROM cash_logs WHERE day_tag = :date AND type = :type")
    long sumOnDate(String date, int type);

    @Query("SELECT sum(amount) FROM cash_logs WHERE date_tag BETWEEN :dateFrom AND :dateTo AND type = :type")
    long sumOnDateRange(int dateFrom, int dateTo, int type);
}