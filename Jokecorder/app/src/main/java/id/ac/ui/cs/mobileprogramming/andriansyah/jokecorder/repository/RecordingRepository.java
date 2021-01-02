package id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.dao.RecordingDao;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.database.JokecorderRoomDatabase;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.model.Recording;

public class RecordingRepository {
    private RecordingDao recordingDao;
    private LiveData<List<Recording>> allRecordings;

    public RecordingRepository(Application application) {
        JokecorderRoomDatabase database = JokecorderRoomDatabase.getInstance(application);
        recordingDao = database.recordingDao();
        allRecordings = recordingDao.getAllRecordingsLiveData();
    }

    public void insertRecording(Recording recording){
        new InsertRecordingAsyncTask(recordingDao).execute(recording);
    }

    public void updateRecording(Recording recording){
        new UpdateRecordingAsyncTask(recordingDao).execute(recording);
    }

    public void deleteRecording(Recording recording){
        new DeleteRecordingAsyncTask(recordingDao).execute(recording);
    }

    public LiveData<List<Recording>> getAllRecordingsLiveData(){
        return allRecordings;
    }

    public LiveData<Recording> getRecordingById(int id){
        return recordingDao.getRecordingById(id);
    }

    public LiveData<Integer> getRecordingsCount(String defaultName){
        return recordingDao.getRecordingsCount(defaultName);
    }

    private static class InsertRecordingAsyncTask extends AsyncTask<Recording, Void, Void> {
        private RecordingDao recordingDao;

        private InsertRecordingAsyncTask(RecordingDao recordingDao) {
            System.out.println("insert executed");
            this.recordingDao = recordingDao;
        }

        @Override
        protected Void doInBackground(Recording... recordings) {
            recordingDao.insertRecording(recordings[0]);
            return null;
        }
    }

    private static class UpdateRecordingAsyncTask extends AsyncTask<Recording, Void, Void> {
        private RecordingDao recordingDao;

        private UpdateRecordingAsyncTask(RecordingDao recordingDao) {
            this.recordingDao = recordingDao;
        }

        @Override
        protected Void doInBackground(Recording... recordings) {
            recordingDao.updateRecording(recordings[0]);
            return null;
        }
    }

    private static class DeleteRecordingAsyncTask extends AsyncTask<Recording, Void, Void> {
        private RecordingDao recordingDao;

        private DeleteRecordingAsyncTask(RecordingDao recordingDao) {
            this.recordingDao = recordingDao;
        }

        @Override
        protected Void doInBackground(Recording... recordings) {
            recordingDao.deleteRecording(recordings[0]);
            return null;
        }
    }
}
