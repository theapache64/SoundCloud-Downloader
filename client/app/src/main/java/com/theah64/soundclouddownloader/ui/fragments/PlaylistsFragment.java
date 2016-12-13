package com.theah64.soundclouddownloader.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.adapters.ITSAdapter;
import com.theah64.soundclouddownloader.database.Playlists;
import com.theah64.soundclouddownloader.models.Playlist;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistsFragment extends Fragment implements ITSAdapter.TracksCallback {


    private List<Playlist> playlists;

    public PlaylistsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View row = inflater.inflate(R.layout.fragment_playlists, container, false);

        playlists = Playlists.getInstance(getContext()).getAll();

        if (playlists != null) {
            final RecyclerView rvPlaylists = (RecyclerView) row.findViewById(R.id.rvPlaylists);
            rvPlaylists.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            final ITSAdapter itsAdapter = new ITSAdapter(playlists, this);
            rvPlaylists.setAdapter(itsAdapter);
        } else {
            //showing no tracks downloaded text view.
            row.findViewById(R.id.tvNoPlaylistDownloaded).setVisibility(View.VISIBLE);
        }


        return row;
    }


    @Override
    public void onRowClicked(int position) {

    }

    @Override
    public void onDownloadButtonClicked(int position) {

    }
}
