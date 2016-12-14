package com.theah64.soundclouddownloader.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.models.Track;

import java.util.List;

/**
 * Created by theapache64 on 10/12/16.
 */

public class PlaylistDownloadAdapter extends RecyclerView.Adapter<PlaylistDownloadAdapter.ViewHolder> {

    private final List<Track> tracks;
    private LayoutInflater inflater;
    private final PlaylistListener callback;

    public PlaylistDownloadAdapter(List<Track> tracks, PlaylistListener callback) {
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


        if (track.getFile() != null && track.getFile().exists()) {
            holder.tvTrackTitle.setText(track.getTitle() + " (Saved)");
            holder.cbDownload.setClickable(false);
        } else {
            holder.tvTrackTitle.setText(track.getTitle());
            holder.cbDownload.setChecked(track.isChecked());
        }
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        protected final CheckBox cbDownload;
        protected final TextView tvTrackTitle;

        public ViewHolder(View itemView) {
            super(itemView);


            this.cbDownload = (CheckBox) itemView.findViewById(R.id.cbDownload);
            this.tvTrackTitle = (TextView) itemView.findViewById(R.id.tvTrackTitle);

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
