package id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="recording_table")
public class Recording implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int recordingId;

    private String recordingName;

    private long recordingLength;

    private long recordingCreatedAtTime;

    private String recordingFilePath;

    public int getRecordingId() {
        return recordingId;
    }

    public void setRecordingId(int recordingId) {
        this.recordingId = recordingId;
    }

    public String getRecordingName() {
        return recordingName;
    }

    public void setRecordingName(String recordingName) {
        this.recordingName = recordingName;
    }

    public long getRecordingLength() {
        return recordingLength;
    }

    public void setRecordingLength(long recordingLength) {
        this.recordingLength = recordingLength;
    }

    public long getRecordingCreatedAtTime() {
        return recordingCreatedAtTime;
    }

    public void setRecordingCreatedAtTime(long recordingCreatedAtTime) {
        this.recordingCreatedAtTime = recordingCreatedAtTime;
    }

    public String getRecordingFilePath() {
        return recordingFilePath;
    }

    public void setRecordingFilePath(String recordingFilePath) {
        this.recordingFilePath = recordingFilePath;
    }

    public Recording(String recordingName, long recordingLength, long recordingCreatedAtTime, String recordingFilePath) {
        this.recordingName = recordingName;
        this.recordingLength = recordingLength;
        this.recordingCreatedAtTime = recordingCreatedAtTime;
        this.recordingFilePath = recordingFilePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(recordingId);
        dest.writeString(recordingName);
        dest.writeLong(recordingLength);
        dest.writeLong(recordingCreatedAtTime);
        dest.writeString(recordingFilePath);
    }

    public Recording(){}

    public Recording(Parcel in){
        recordingId = in.readInt();
        recordingName = in.readString();
        recordingLength = in.readInt();
        recordingCreatedAtTime = in.readLong();
        recordingFilePath = in.readString();
    }

    public static final Parcelable.Creator<Recording> CREATOR = new Parcelable.Creator<Recording>() {
        @Override
        public Recording createFromParcel(Parcel in) {
            return new Recording(in);
        }

        @Override
        public Recording[] newArray(int size) {
            return new Recording[size];
        }
    };
}
