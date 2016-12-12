package com.theah64.soundclouddownloader.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.adapters.ImageTitleSubtitleAdapter;
import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.models.ITSNode;
import com.theah64.soundclouddownloader.models.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TracksFragment extends Fragment implements ImageTitleSubtitleAdapter.TracksCallback {


    private static final String X = TracksFragment.class.getSimpleName();

    public TracksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View row = inflater.inflate(R.layout.fragment_tracks, container, false);


        final List<Track> trackList = Tracks.getInstance(getActivity()).getAll();

        if (trackList != null) {

            Log.d(X, "Converting to ITS nodes : " + trackList.size());

            //Converting to ITS node
            final List<ITSNode> itsNodes = new ArrayList<>(trackList.size());
            for (final Track track : trackList) {
                Log.d(X, track.toString());
                itsNodes.add(new ITSNode(track.getArtWorkUrl(), track.getTitle(), getDownloadStatus(track.getDownloadId())));
            }

            final ImageTitleSubtitleAdapter itsAdapter = new ImageTitleSubtitleAdapter(itsNodes, this);
            final RecyclerView rvTracks = (RecyclerView) row.findViewById(R.id.rvTracks);
            rvTracks.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rvTracks.setAdapter(itsAdapter);

        } else {

            Log.d(X, "No tracks found");

            //showing no tracks downloaded text view.
            row.findViewById(R.id.tvNoTracksDownloaded).setVisibility(View.VISIBLE);
        }


        return row;
    }

    private String getDownloadStatus(String downloadId) {
        //TODO:
        return "NONE";
    }

    @Override
    public void onRowClicked(int position) {

    }
}
