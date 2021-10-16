package kr.lindol.mycashbook.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CashLogDao {
    @Query("SELECT * FROM cash_logs WHERE day_tag = :dayTag")
    List<CashLog> loadByDate(String dayTag);

    @Insert
    void insertAll(CashLog... cashLogs);
}