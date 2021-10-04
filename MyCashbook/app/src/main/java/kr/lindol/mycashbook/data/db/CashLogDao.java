package kr.lindol.mycashbook.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CashLogDao {
    @Query("SELECT * FROM cash_logs WHERE date_tag = :dateTag")
    List<CashLog> loadByDate(String dateTag);

    @Insert
    void insertAll(CashLog... cashLogs);
}