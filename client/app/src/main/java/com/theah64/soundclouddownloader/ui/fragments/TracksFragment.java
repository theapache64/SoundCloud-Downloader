package com.theah64.soundclouddownloader.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.adapters.ITSAdapter;
import com.theah64.soundclouddownloader.database.Playlists;
import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.models.Track;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TracksFragment extends Fragment implements ITSAdapter.TracksCallback {


    private static final String X = TracksFragment.class.getSimpleName();

    public TracksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View row = inflater.inflate(R.layout.fragment_tracks, container, false);

        final String playlistId = getArguments().getString(Playlists.COLUMN_ID);

        final List<Track> trackList = Tracks.getInstance(getActivity()).getAll(playlistId);

        if (trackList != null) {

            final ITSAdapter itsAdapter = new ITSAdapter(trackList, this);
            final RecyclerView rvTracks = (RecyclerView) row.findViewById(R.id.rvTracks);
            rvTracks.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvTracks.setAdapter(itsAdapter);

        } else {

            Log.d(X, "No tracks found");

            //showing no tracks downloaded text view.
            row.findViewById(R.id.tvNoTracksDownloaded).setVisibility(View.VISIBLE);
        }


        return row;
    }


    @Override
    public void onRowClicked(int position) {

    }

    @Override
    public void onPopUpMenuClicked(View anchor, int position) {

    }

    public static TracksFragment getNewInstance(String playlistId) {
        final TracksFragment tracksFragment = new TracksFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(Playlists.COLUMN_ID, playlistId);
        tracksFragment.setArguments(bundle);
        return tracksFragment;
    }
}
