package id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.view.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.R;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.listener.OnRepositoryChangedListener;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.model.Recording;
import id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.viewmodel.RecordingViewModel;

public class FileBrowserAdapter extends RecyclerView.Adapter<FileBrowserAdapter.RecordingsViewHolder>{
    private static final String LOG_TAG = "FileViewerAdapter";

//    private RecordingViewModel recordingViewModel;
//
//    Recording recording;
//    Context context;
//    LinearLayoutManager linearLayoutManager;
//
//    public FileBrowserAdapter(Context newContext, LinearLayoutManager llm, RecordingViewModel viewModel) {
//        super();
//        context = newContext;
//        recordingViewModel = viewModel;
////        mDatabase.setOnDatabaseChangedListener(this);
//        linearLayoutManager = llm;
//    }

    private final List<Recording> recordings;

    public FileBrowserAdapter(List<Recording> items) {
        recordings = items;
    }

    @Override
    public void onBindViewHolder(final RecordingsViewHolder holder, int position) {

        Recording recording = recordings.get(position);
        long itemDuration = recording.getRecordingLength();

        long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration)
                - TimeUnit.MINUTES.toSeconds(minutes);

        holder.vName.setText(recording.getRecordingName());
        holder.vLength.setText(String.format("%02d:%02d", minutes, seconds));
        holder.vDateAdded.setText(
                DateUtils.formatDateTime(
                        holder.cardView.getContext(),
                        recording.getRecordingCreatedAtTime(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
                )
        );

//        // define an on click listener to open PlaybackFragment
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    PlaybackFragment playbackFragment =
//                            new PlaybackFragment().newInstance(getItem(holder.getPosition()));
//
//                    FragmentTransaction transaction = ((FragmentActivity) mContext)
//                            .getSupportFragmentManager()
//                            .beginTransaction();
//
//                    playbackFragment.show(transaction, "dialog_playback");
//
//                } catch (Exception e) {
//                    Log.e(LOG_TAG, "exception", e);
//                }
//            }
//        });
//
//        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                ArrayList<String> entrys = new ArrayList<String>();
//                entrys.add(mContext.getString(R.string.dialog_file_share));
//                entrys.add(mContext.getString(R.string.dialog_file_rename));
//                entrys.add(mContext.getString(R.string.dialog_file_delete));
//
//                final CharSequence[] items = entrys.toArray(new CharSequence[entrys.size()]);
//
//
//                // File delete confirm
//                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                builder.setTitle(mContext.getString(R.string.dialog_title_options));
//                builder.setItems(items, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int item) {
//                        if (item == 0) {
//                            shareFileDialog(holder.getPosition());
//                        } if (item == 1) {
//                            renameFileDialog(holder.getPosition());
//                        } else if (item == 2) {
//                            deleteFileDialog(holder.getPosition());
//                        }
//                    }
//                });
//                builder.setCancelable(true);
//                builder.setNegativeButton(context.getString(R.string.dialog_action_cancel),
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//
//                AlertDialog alert = builder.create();
//                alert.show();
//
//                return false;
//            }
//        });
    }

    @Override
    public RecordingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_recordings, parent, false);
        return new RecordingsViewHolder(itemView);
    }

    public static class RecordingsViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vLength;
        protected TextView vDateAdded;
        protected View cardView;
        protected Recording recording;

        public RecordingsViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.file_name_text);
            vLength = (TextView) v.findViewById(R.id.file_length_text);
            vDateAdded = (TextView) v.findViewById(R.id.file_date_added_text);
            cardView = v.findViewById(R.id.card_view);
        }
    }

    @Override
    public int getItemCount() {
        return recordings == null ? 0 : recordings.size();
    }

//    public Recording getRecordingById(int position) {
//        return recordingViewModel.getRecordingById(position);
//    }

//    @Override
//    public void onNewDatabaseEntryAdded() {
//        //item added to top of the list
//        notifyItemInserted(getItemCount() - 1);
//        linearLayoutManager.scrollToPosition(getItemCount() - 1);
//    }
}
