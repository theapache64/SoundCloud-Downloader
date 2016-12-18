package com.theah64.soundclouddownloader.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.models.Track;

import java.io.File;
import java.util.List;

/**
 * Created by theapache64 on 10/12/16.
 */

public class PlaylistDownloadAdapter extends RecyclerView.Adapter<PlaylistDownloadAdapter.ViewHolder> {

    private static final String X = PlaylistDownloadAdapter.class.getSimpleName();
    private final List<Track> tracks;
    private LayoutInflater inflater;
    private final PlaylistListener callback;
    private final Context context;

    public PlaylistDownloadAdapter(Context context, List<Track> tracks, PlaylistListener callback) {
        this.context = context;
        this.tracks = tracks;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        final View row = inflater.inflate(R.layout.playlist_row, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Track track = tracks.get(position);

        holder.tvTrackTitle.setText(track.getTitle());
        holder.cbDownload.setChecked(track.isChecked());

        if (track.getFile() != null && track.getFile().exists()) {
            final File trackFile = track.getFile();

            final String trackName = trackFile.getName();
            final String parentFileName = trackFile.getParentFile().getName();

            holder.tvSubtitle.setText(context.getString(R.string.Existing_track_s_s, parentFileName, trackName));
        } else {
            holder.tvSubtitle.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final CheckBox cbDownload;
        final TextView tvTrackTitle, tvSubtitle;

        ViewHolder(View itemView) {
            super(itemView);


            this.cbDownload = (CheckBox) itemView.findViewById(R.id.cbDownload);
            this.tvTrackTitle = (TextView) itemView.findViewById(R.id.tvTrackTitle);
            this.tvSubtitle = (TextView) itemView.findViewById(R.id.tvSubtitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cbDownload.setChecked(!cbDownload.isChecked());
                }
            });

            this.cbDownload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        callback.onChecked(getLayoutPosition());
                    } else {
                        callback.onUnChecked(getLayoutPosition());
                    }
                }
            });
        }
    }

    public interface PlaylistListener {
        void onChecked(int position);

        void onUnChecked(int position);
    }
}
