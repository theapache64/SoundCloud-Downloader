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

public class ITSAdapter extends RecyclerView.Adapter<ITSAdapter.ViewHolder> {

    private final List<? extends ITSNode> itsNodes;
    private final TracksCallback callback;
    private LayoutInflater inflater;

    public ITSAdapter(List<? extends ITSNode> itsNodes, TracksCallback callback) {
        this.itsNodes = itsNodes;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        final View row = inflater.inflate(R.layout.its_row, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final ITSNode itsNode = itsNodes.get(position);

        ImageLoader.getInstance().displayImage(itsNode.getArtworkUrl(), holder.ivImage);
        holder.tvTitle.setText(itsNode.getTitle());

        if (itsNode.getSubtitle() != null) {
            holder.tvSubtitle.setVisibility(View.VISIBLE);
            holder.tvSubtitle.setText(itsNode.getSubtitle());
        } else {
            holder.tvSubtitle.setVisibility(View.GONE);
        }

        holder.ibDownloadButton.setVisibility(itsNode.isDownloadVisible() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return itsNodes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView ivImage;
        private final TextView tvTitle, tvSubtitle;
        private View ibDownloadButton;

        ViewHolder(View itemView) {
            super(itemView);

            this.ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            this.tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            this.tvSubtitle = (TextView) itemView.findViewById(R.id.tvSubtitle);
            this.ibDownloadButton = itemView.findViewById(R.id.ibDownloadButton);

            itemView.setOnClickListener(this);
            this.ibDownloadButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.ibDownloadButton:
                    callback.onDownloadButtonClicked(getLayoutPosition());
                    break;

                default:
                    callback.onRowClicked(getLayoutPosition());
            }

        }
    }

    public interface TracksCallback {
        void onRowClicked(int position);

        void onDownloadButtonClicked(int position);
    }
}
