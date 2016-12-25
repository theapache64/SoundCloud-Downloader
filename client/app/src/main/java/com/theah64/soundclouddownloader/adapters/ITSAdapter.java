package com.theah64.soundclouddownloader.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theah64.musicdog.R;
import com.theah64.soundclouddownloader.models.ITSNode;
import com.theah64.soundclouddownloader.utils.DownloadUtils;

import java.util.List;

/**
 * Created by theapache64 on 9/12/16.
 */

public class ITSAdapter extends RecyclerView.Adapter<ITSAdapter.ViewHolder> {

    private final List<? extends ITSNode> itsNodes;
    private final TracksCallback callback;
    private LayoutInflater inflater;
    private final DownloadUtils downloadUtils;

    private final String downloadString;
    private final Animation blinkAnimation;

    public ITSAdapter(Context context, List<? extends ITSNode> itsNodes, TracksCallback callback, DownloadUtils downloadUtils) {
        this.itsNodes = itsNodes;
        this.callback = callback;
        this.downloadUtils = downloadUtils;
        this.downloadString = context.getString(R.string.Downloading);
        this.blinkAnimation = AnimationUtils.loadAnimation(context, R.anim.blink);
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

        ImageLoader.getInstance().displayImage(itsNode.getArtworkUrl(), holder.ivArtwork);
        holder.tvTitle.setText(itsNode.getTitle());

        holder.tvSubtitle1.setText(itsNode.getSubtitle1());
        holder.tvSubtitle2.setText(itsNode.getSubtitle2());

        final String subTitle3 = itsNode.getSubtitle3(downloadUtils);
        holder.tvSubtitle3.setText(subTitle3);

        if (subTitle3 != null && subTitle3.equals(downloadString)) {
            holder.tvSubtitle3.startAnimation(blinkAnimation);
        } else {
            holder.tvSubtitle3.clearAnimation();
        }
    }

    @Override
    public int getItemCount() {
        return itsNodes.size();
    }

    public interface TracksCallback {
        void onRowClicked(int position, final View popUpAnchor);

        void onPopUpMenuClicked(final View anchor, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView ivArtwork;
        private final TextView tvTitle, tvSubtitle1, tvSubtitle2, tvSubtitle3;
        private View ibShowPopUpuMenu;

        ViewHolder(View itemView) {
            super(itemView);

            this.ivArtwork = (ImageView) itemView.findViewById(R.id.ivArtwork);

            this.tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            this.tvSubtitle1 = (TextView) itemView.findViewById(R.id.tvSubtitle1);
            this.tvSubtitle2 = (TextView) itemView.findViewById(R.id.tvSubtitle2);
            this.tvSubtitle3 = (TextView) itemView.findViewById(R.id.tvSubtitle3);

            this.ibShowPopUpuMenu = itemView.findViewById(R.id.ibShowPopUpuMenu);

            itemView.setOnClickListener(this);
            this.ibShowPopUpuMenu.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.ibShowPopUpuMenu:
                    callback.onPopUpMenuClicked(view, getLayoutPosition());
                    break;

                default:
                    callback.onRowClicked(getLayoutPosition(), ibShowPopUpuMenu);
            }

        }
    }
}
