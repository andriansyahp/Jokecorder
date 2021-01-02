package id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.FileObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.R;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.model.Recording;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.view.adapter.FileBrowserAdapter;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.viewmodel.RecordingViewModel;
import util.PathUtil;

public class FileBrowserFragment extends Fragment {
    private static final String ARG_PAGE = "ARG_PAGE";
    private static final String LOG_TAG = "FileViewerFragment";

    private Context context;
    private int tabPage;
    private FileBrowserAdapter fileBrowserAdapter;

    static RecordingViewModel recordingViewModel;

    public static FileBrowserFragment newInstance(int page, RecordingViewModel viewModel) {
        FileBrowserFragment f = new FileBrowserFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_PAGE, page);
        f.setArguments(b);
        recordingViewModel = viewModel;

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabPage = getArguments().getInt(ARG_PAGE);
        context = getContext();
        observer.startWatching();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_file_browser, container, false);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.file_browser_recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        //newest to oldest order (database stores from oldest to newest)
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);

        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recordingViewModel.getAllRecordings().observe(getViewLifecycleOwner(), new Observer<List<Recording>>() {
            @Override
            public void onChanged(List<Recording> recordings) {
                recyclerView.setAdapter(new FileBrowserAdapter(recordings));
            }
        });

//        fileBrowserAdapter = new FileBrowserAdapter(getActivity(), llm, recordingViewModel);
//        recyclerView.setAdapter(fileBrowserAdapter);

        return v;
    }

    FileObserver observer =
            new FileObserver(android.os.Environment.getExternalStorageDirectory().toString()
                    + "/SoundRecorder") {
                // set up a file observer to watch this directory on sd card
                @Override
                public void onEvent(int event, String file) {
                    if(event == FileObserver.DELETE){
                        // user deletes a recording file out of the app

                        String filePath = android.os.Environment.getExternalStorageDirectory().toString()
                                + "/SoundRecorder" + file + "]";

                        Log.d(LOG_TAG, "File deleted ["
                                + android.os.Environment.getExternalStorageDirectory().toString()
                                + "/SoundRecorder" + file + "]");

                        // remove file from database and recyclerview
//                        mFileViewerAdapter.removeOutOfApp(filePath);
                    }
                }
            };
}
