package com.theah64.soundclouddownloader.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.adapters.ImageTitleSubtitleAdapter;
import com.theah64.soundclouddownloader.database.Playlists;
import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.models.ITSNode;
import com.theah64.soundclouddownloader.models.Playlist;
import com.theah64.soundclouddownloader.models.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistsFragment extends Fragment implements ImageTitleSubtitleAdapter.TracksCallback {


    public PlaylistsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View row = inflater.inflate(R.layout.fragment_playlists, container, false);

        final RecyclerView rvTracks = (RecyclerView) row.findViewById(R.id.rvPlaylist);
        rvTracks.setLayoutManager(new LinearLayoutManager(getContext()));
        final List<Playlist> playlists = Playlists.getInstance(getContext()).getAll();

        if (playlists != null) {
            //Converting to ITS node
            final List<ITSNode> itsNodes = new ArrayList<>(playlists.size());
            for (final Playlist playlist : playlists) {
                itsNodes.add(new ITSNode(playlist.getArtworkUrl(), playlist.getTitle(), "CALC"));
            }

            final ImageTitleSubtitleAdapter itsAdapter = new ImageTitleSubtitleAdapter(itsNodes, this);
            rvTracks.setAdapter(itsAdapter);
        } else {
            //showing no tracks downloaded text view.
            row.findViewById(R.id.tvNoPlaylistDownloaded).setVisibility(View.VISIBLE);
        }


        return row;
    }


    @Override
    public void onRowClicked(int position) {

    }
}
