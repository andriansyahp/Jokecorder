package id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.model.Recording;

@Dao
public interface RecordingDao {
    @Insert
    void insertRecording(Recording transaction);

    @Update
    void updateRecording(Recording transaction);

    @Delete
    void deleteRecording(Recording transaction);

    @Query("SELECT * FROM recording_table")
    LiveData<List<Recording>> getAllRecordingsLiveData();

    @Query("SELECT * FROM recording_table WHERE recordingId =:id")
    LiveData<Recording> getRecordingById(int id);

    @Query("SELECT COUNT(*) FROM recording_table WHERE recordingName LIKE :defaultName")
    LiveData<Integer> getRecordingsCount(String defaultName);
}
