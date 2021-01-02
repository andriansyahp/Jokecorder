package id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.model.Recording;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.dao.RecordingDao;

@Database(entities = {Recording.class}, version = 1, exportSchema = false)
public abstract class JokecorderRoomDatabase extends RoomDatabase {
    private static JokecorderRoomDatabase INSTANCE;

    public abstract RecordingDao recordingDao();

    public static synchronized JokecorderRoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    JokecorderRoomDatabase.class, "jokecorder_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return INSTANCE;
    }
}
