package id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.R;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.model.Recording;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.repository.RecordingRepository;
import util.PathUtil;

public class RecordingService extends Service {
    private static final String LOG_TAG = RecordingService.class.getSimpleName();
    private static final String EXTRA_ACTIVITY_STARTER = "id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.EXTRA_ACTIVITY_STARTER";

    private String recordingFileName = null;
    private String recordingFilePath = null;

    private MediaRecorder mediaRecorder = null;

    private RecordingRepository recordingRepository;

    private long startingTimeMillis = 0;
    private long elapsedMillis = 0;
    private int elapsedSeconds = 0;
    private OnTimerChangedListener onTimerChangedListener = null;
    private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private Timer timer = null;
    private TimerTask incrementTimerTask = null;

    private TimerTask mIncrementTimerTask = null;

    private final IBinder myBinder = new LocalBinder();
    private boolean isRecording = false;

    public static int onCreateCalls = 0;
    public static int onDestroyCalls = 0;
    public static int onStartCommandCalls = 0;

    public static Intent makeIntent(Context context, boolean activityStarter) {
        Intent intent = new Intent(context.getApplicationContext(), RecordingService.class);
        intent.putExtra(EXTRA_ACTIVITY_STARTER, activityStarter);
        return intent;
    }

    public class LocalBinder extends Binder {
        public RecordingService getService() {
            return RecordingService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public interface OnTimerChangedListener {
        void onTimerChanged(int seconds);
    }

    public interface OnRecordingStatusChangedListener {
        void onRecordingStarted();

        void onTimerChanged(int seconds);

//        void onAmplitudeInfo(int amplitude);

        void onRecordingStopped(String filename, Long elapsedMillis, Long createdAt, String filePath);
    }

    private OnRecordingStatusChangedListener onRecordingStatusChangedListener = null;

    public void setOnRecordingStatusChangedListener(OnRecordingStatusChangedListener onRecordingStatusChangedListener) {
        this.onRecordingStatusChangedListener = onRecordingStatusChangedListener;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        onCreateCalls++;
        recordingRepository = new RecordingRepository(getApplication());
        System.out.println(("recordingRepository: " + recordingRepository == null));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStartCommandCalls++;
//        startRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        onDestroyCalls++;
        super.onDestroy();
        if (mediaRecorder != null) {
            stopRecording();
        }

        if (onRecordingStatusChangedListener != null) onRecordingStatusChangedListener = null;
    }

    public void startRecording() {
        System.out.println("masuk startRecording()");
        setFileNameAndPath();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(recordingFilePath);

        mediaRecorder.setAudioChannels(1);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setAudioEncodingBitRate(192000);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            startingTimeMillis = System.currentTimeMillis();

            //startTimer();
            //startForeground(1, createNotification());

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void setFileNameAndPath(){
        Integer count = 0;
        File f;

        do{
            count++;

            String defaultName = getString(R.string.default_file_name);
            recordingFileName = defaultName
//                    + "_" + (recordingRepository.getRecordingsCount(defaultName) + String.valueOf(count)) + ".mp4";
                    + "_" + (count) + ".mp4";
//            recordingFilePath = PathUtil.getDirectoryPath(this);
            recordingFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            recordingFilePath += "/Jokecorder/" + recordingFileName;

            System.out.println(recordingFilePath);

            f = new File(recordingFilePath);
        }while (f.exists() && !f.isDirectory());
    }

    public void stopRecording() {
        System.out.println(recordingFilePath + " 1");
        mediaRecorder.stop();
        System.out.println(recordingFilePath + " 2");
        elapsedMillis = (System.currentTimeMillis() - startingTimeMillis);
        System.out.println(recordingFilePath+ " 3");
        mediaRecorder.release();
        System.out.println(recordingFilePath+ " 4");
        Toast.makeText(this, getString(R.string.toast_recording_finish) + " " + recordingFilePath, Toast.LENGTH_LONG).show();

        //remove notification
        if (incrementTimerTask != null) {
            incrementTimerTask.cancel();
            incrementTimerTask = null;
        }

        isRecording = false;
        mediaRecorder = null;

        try {
            Recording newRecording = new Recording(recordingFileName, elapsedMillis, System.currentTimeMillis(), recordingFilePath);
            System.out.println(recordingFilePath);
            recordingRepository.insertRecording(newRecording);
        } catch (Exception e){
            Log.e(LOG_TAG, "exception", e);
        }

        if (onRecordingStatusChangedListener == null)
            stopSelf();

        stopForeground(true);
    }

    public boolean isRecording() {
        return isRecording;
    }
}
