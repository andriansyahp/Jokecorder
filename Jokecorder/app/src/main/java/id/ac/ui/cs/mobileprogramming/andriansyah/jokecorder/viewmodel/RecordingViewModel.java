package id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.viewmodel;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
//import androidx.lifecycle.ViewModel;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;

import java.util.List;

import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.R;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.model.Recording;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.repository.RecordingRepository;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.service.RecordingService;

import static android.content.Context.BIND_AUTO_CREATE;

public class RecordingViewModel extends AndroidViewModel {
    private RecordingRepository recordingRepository;
    private LiveData<List<Recording>> allRecordings;

    public final ObservableBoolean showPlayBack = new ObservableBoolean(false);
    private RecordingService recordingService;
    public Recording recording;

    public RecordingViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(Application application) {
        recordingRepository = new RecordingRepository(application);
        allRecordings = recordingRepository.getAllRecordingsLiveData();
    }

    public final ObservableBoolean serviceConnected = new ObservableBoolean(false);
    public final ObservableBoolean serviceRecording = new ObservableBoolean(false);
    public final ObservableInt secondsElapsed = new ObservableInt(0);

    public void connectService(Intent intent) {
        getApplication().startService(intent);
        getApplication().bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    public void disconnectAndStopService(Intent intent) {
        if (!serviceConnected.get()) return;

        getApplication().unbindService(serviceConnection);
        if (!serviceRecording.get())
            getApplication().stopService(intent);
        recordingService.setOnRecordingStatusChangedListener(null);
        recordingService = null;
        serviceConnected.set(false);
    }

    public void startRecording() {
        recordingService.startRecording();
        serviceRecording.set(true);
    }

    public void stopRecording() {
        recordingService.stopRecording();
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            recordingService = ((RecordingService.LocalBinder) iBinder).getService();
            serviceConnected.set(true);
            recordingService.setOnRecordingStatusChangedListener(onRecordingStatusChangedListener);
            serviceRecording.set(recordingService.isRecording());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            if (recordingService != null) {
                recordingService.setOnRecordingStatusChangedListener(null);
                recordingService = null;
            }
            serviceConnected.set(false);
        }
    };

    private final RecordingService.OnRecordingStatusChangedListener onRecordingStatusChangedListener =
            new RecordingService.OnRecordingStatusChangedListener() {
                @Override
                public void onRecordingStarted() {
                    serviceRecording.set(true);
                    showPlayBack.set(false);
                }

                @Override
                public  void onRecordingStopped(String filename, Long elapsedMillis, Long createdAt, String filePath){
                    serviceRecording.set(false);
                    secondsElapsed.set(0);

                    // Save the recording data in the database.
                    recording = new Recording(filename, elapsedMillis, createdAt, filePath);
                    System.out.println(filePath);
                    insertRecording(recording);
                    showPlayBack.set(true);
                }

                // This method is called from a separate thread.
                @Override
                public void onTimerChanged(int seconds) {
                    secondsElapsed.set(seconds);
                }
            };


    public LiveData<List<Recording>> getAllRecordings() {
        return allRecordings;
    }

    public void insertRecording(Recording recording) {
        recordingRepository.insertRecording(recording);
    }

    public void updateRecording(Recording recording){
        recordingRepository.updateRecording(recording);
    }

    public void deleteRecording(Recording recording){
        recordingRepository.deleteRecording(recording);
    }

    public LiveData<Recording> getRecordingById(int id){
        return recordingRepository.getRecordingById(id);
    }

    public LiveData<Integer> getRecordingsCount(String defaultName){
        return recordingRepository.getRecordingsCount(defaultName);
    }
}
