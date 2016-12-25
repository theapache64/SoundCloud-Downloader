package com.theah64.soundclouddownloader.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theah64.musicdog.R;
import com.theah64.soundclouddownloader.models.ITSNode;

import java.util.List;

/**
 * Created by theapache64 on 10/12/16.
 */

public class PlaylistDownloadAdapter extends RecyclerView.Adapter<PlaylistDownloadAdapter.ViewHolder> {

    private static final String X = PlaylistDownloadAdapter.class.getSimpleName();
    private final List<? extends ITSNode> itsNodes;
    private final PlaylistListener callback;
    private final Context context;
    private LayoutInflater inflater;

    public PlaylistDownloadAdapter(Context context, List<? extends ITSNode> tracks, PlaylistListener callback) {
        this.context = context;
        this.itsNodes = tracks;
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
        final ITSNode track = itsNodes.get(position);

        holder.tvTitle.setText(track.getTitle());
        holder.cbDownload.setChecked(track.isChecked());
        holder.tvSubtitle1.setText(track.getSubtitle1());
        holder.tvSubtitle2.setText(track.getSubtitle2());
        holder.tvSubtitle3.setText(track.getSubtitle3(null));

        ImageLoader.getInstance().displayImage(track.getArtworkUrl(), holder.ivArtwork);
    }

    @Override
    public int getItemCount() {
        return itsNodes.size();
    }

    public interface PlaylistListener {
        void onChecked(int position);

        void onUnChecked(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView ivArtwork;
        final CheckBox cbDownload;
        final TextView tvSubtitle1, tvSubtitle2, tvTitle, tvSubtitle3;

        ViewHolder(View itemView) {
            super(itemView);


            this.cbDownload = (CheckBox) itemView.findViewById(R.id.cbDownload);
            this.ivArtwork = (ImageView) itemView.findViewById(R.id.ivArtwork);
            this.tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            this.tvSubtitle1 = (TextView) itemView.findViewById(R.id.tvSubtitle1);
            this.tvSubtitle2 = (TextView) itemView.findViewById(R.id.tvSubtitle2);
            this.tvSubtitle3 = (TextView) itemView.findViewById(R.id.tvSubtitle3);

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
}
