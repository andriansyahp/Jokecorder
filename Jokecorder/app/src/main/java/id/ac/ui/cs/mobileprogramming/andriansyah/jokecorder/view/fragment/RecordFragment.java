package id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.R;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.service.RecordingService;
import util.PathUtil;

public class RecordFragment extends Fragment {
    private static final String LOG_TAG = RecordFragment.class.getSimpleName();

    private FloatingActionButton recordButton = null;
    private Button pauseButton = null;

    private TextView recordingPrompt;
    private int recordPromptCount = 0;

    private boolean startRecording = true;
    private boolean pauseRecording = true;

    private Chronometer chronometer = null;
    long timeWhenPaused = 0; //stores time when user clicks pause button

    public static RecordFragment newInstance(int position) {
        RecordFragment f = new RecordFragment();
        Bundle b = new Bundle();
        f.setArguments(b);

        return f;
    }

    public RecordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View recordView = inflater.inflate(R.layout.fragment_record, container, false);

        chronometer = (Chronometer) recordView.findViewById(R.id.chronometer);
        //update recording prompt text
        recordingPrompt = (TextView) recordView.findViewById(R.id.recording_status_text);

        recordButton = (FloatingActionButton) recordView.findViewById(R.id.btnRecord);
//        mRecordButton.setColorNormal(getResources().getColor(R.color.primary));
//        mRecordButton.setColorPressed(getResources().getColor(R.color.primary_dark));
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(startRecording);
                startRecording = !startRecording;
            }
        });

        pauseButton = (Button) recordView.findViewById(R.id.btnPause);
        pauseButton.setVisibility(View.GONE); //hide pause button before recording starts
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPauseRecord(pauseRecording);
                pauseRecording = !pauseRecording;
            }
        });

        return recordView;
    }

    // Recording Start/Stop
    //TODO: recording pause
    private void onRecord(boolean start){

        Intent intent = new Intent(getActivity(), RecordingService.class);

        if (start) {
            // start recording
            recordButton.setImageResource(R.drawable.ic_media_stop);
            //mPauseButton.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(),R.string.toast_recording_start,Toast.LENGTH_SHORT).show();
            File folder = new File(Environment.getExternalStorageDirectory() + "/Jokecorder");
            if (!folder.exists()) {
                folder.mkdir();
            }

            //start Chronometer
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (recordPromptCount == 0) {
                        recordingPrompt.setText(getString(R.string.record_in_progress) + ".");
                    } else if (recordPromptCount == 1) {
                        recordingPrompt.setText(getString(R.string.record_in_progress) + "..");
                    } else if (recordPromptCount == 2) {
                        recordingPrompt.setText(getString(R.string.record_in_progress) + "...");
                        recordPromptCount = -1;
                    }

                    recordPromptCount++;
                }
            });

            //start RecordingService
            getActivity().startService(intent);
            //keep screen on while recording
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            recordingPrompt.setText(getString(R.string.record_in_progress) + ".");
            recordPromptCount++;

        } else {
            //stop recording
            recordButton.setImageResource(R.drawable.ic_mic_white_36dp);
            //mPauseButton.setVisibility(View.GONE);
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            recordingPrompt.setText(getString(R.string.record_prompt));

            getActivity().stopService(intent);
            //allow the screen to turn off again once recording is finished
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    //TODO: implement pause recording
    private void onPauseRecord(boolean pause) {
        if (pause) {
            //pause recording
            pauseButton.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ic_media_play ,0 ,0 ,0);
            recordingPrompt.setText((String)getString(R.string.resume_recording_button).toUpperCase());
            timeWhenPaused = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.stop();
        } else {
            //resume recording
            pauseButton.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ic_media_pause ,0 ,0 ,0);
            recordingPrompt.setText((String)getString(R.string.pause_recording_button).toUpperCase());
            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenPaused);
            chronometer.start();
        }
    }
}
