package com.theah64.soundclouddownloader.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.models.ITSNode;

import java.util.List;

/**
 * Created by theapache64 on 9/12/16.
 */

public class ImageTitleSubtitleAdapter extends RecyclerView.Adapter<ImageTitleSubtitleAdapter.ViewHolder> {

    private final List<ITSNode> itsNodes;
    private final TracksCallback callback;
    private LayoutInflater inflater;

    public ImageTitleSubtitleAdapter(List<ITSNode> itsNodes, TracksCallback callback) {
        this.itsNodes = itsNodes;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        final View row = inflater.inflate(R.layout.image_title_subtitle_row, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final ITSNode itsNode = itsNodes.get(position);

        ImageLoader.getInstance().displayImage(itsNode.getImageUrl(), holder.ivImage);
        holder.tvTitle.setText(itsNode.getTitle());
        holder.tvSubtitle.setText(itsNode.getSubtitle());
    }

    @Override
    public int getItemCount() {
        return itsNodes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivImage;
        private final TextView tvTitle, tvSubtitle;

        ViewHolder(View itemView) {
            super(itemView);

            this.ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            this.tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            this.tvSubtitle = (TextView) itemView.findViewById(R.id.tvSubtitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onRowClicked(getLayoutPosition());
                }
            });
        }
    }

    public interface TracksCallback {
        void onRowClicked(int position);
    }
}
