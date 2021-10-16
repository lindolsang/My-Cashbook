package kr.lindol.mycashbook.data.db;

import static com.google.common.base.Preconditions.checkNotNull;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CashLog.class}, version = 1)
public abstract class CashLogDatabase extends RoomDatabase {
    public abstract CashLogDao cashLogDao();

    private static CashLogDatabase INSTANCE;

    public static CashLogDatabase getInstance(@NonNull Context context) {
        checkNotNull(context, "context cannot be null");

        //TODO 2021/09/22 needs to improve for thread safe
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    CashLogDatabase.class,
                    "MyCashLog")
                    .build();
        }

        return INSTANCE;
    }
}
