package com.theah64.soundclouddownloader.ui.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.adapters.ITSAdapter;
import com.theah64.soundclouddownloader.database.Playlists;
import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.models.Playlist;
import com.theah64.soundclouddownloader.models.Track;
import com.theah64.soundclouddownloader.services.DownloaderService;
import com.theah64.soundclouddownloader.ui.activities.PlaylistTracksActivity;
import com.theah64.soundclouddownloader.utils.App;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistsFragment extends Fragment implements ITSAdapter.TracksCallback, PopupMenu.OnMenuItemClickListener {


    private List<Playlist> playlists;
    private int position;
    private Playlist playlist;
    private Playlists playlistsTable;
    private Tracks tracksTable;

    public PlaylistsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View row = inflater.inflate(R.layout.fragment_playlists, container, false);

        playlistsTable = Playlists.getInstance(getContext());
        tracksTable = Tracks.getInstance(getContext());

        playlists = playlistsTable.getAll();

        if (playlists != null) {
            final RecyclerView rvPlaylists = (RecyclerView) row.findViewById(R.id.rvPlaylists);
            rvPlaylists.setLayoutManager(new LinearLayoutManager(getActivity()));
            final ITSAdapter itsAdapter = new ITSAdapter(playlists, this);
            rvPlaylists.setAdapter(itsAdapter);
        } else {
            //showing no tracks downloaded text view.
            row.findViewById(R.id.tvNoPlaylistDownloaded).setVisibility(View.VISIBLE);
        }


        return row;
    }


    @Override
    public void onRowClicked(int position, View popUpAnchor) {
        final Playlist playlist = playlists.get(position);
        final Intent playlistTracksIntent = new Intent(getActivity(), PlaylistTracksActivity.class);
        playlistTracksIntent.putExtra(Playlist.KEY, playlist);
        startActivity(playlistTracksIntent);
    }

    @Override
    public void onPopUpMenuClicked(View anchor, int position) {

        this.position = position;
        playlist = playlists.get(position);

        final PopupMenu playlistMenu = new PopupMenu(getActivity(), anchor);
        playlistMenu.getMenuInflater().inflate(playlist.isDownloaded() ? R.menu.menu_playlist_downloaded : R.menu.menu_playlist_not_downloaded, playlistMenu.getMenu());
        playlistMenu.setOnMenuItemClickListener(this);

        playlistMenu.show();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.miShareSavedTracks:
            case R.id.miShareTracks:

                List<Track> trackList = playlist.getTracks();

                if (trackList == null) {
                    trackList = tracksTable.getAll(playlist.getId());
                    playlist.setTracks(trackList);
                }


                final ArrayList<Uri> existingTracks = new ArrayList<>();
                for (final Track track : trackList) {
                    if (track.getFile() != null && track.getFile().exists()) {
                        existingTracks.add(Uri.fromFile(track.getFile()));
                    }
                }

                final Intent shareTracksIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                shareTracksIntent.putExtra(Intent.EXTRA_SUBJECT, playlist.getTitle()); //TOOD: Modify sub
                shareTracksIntent.setType("audio/*");
                shareTracksIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, existingTracks);
                startActivity(shareTracksIntent);

                Toast.makeText(getActivity(), getResources().getQuantityString(R.plurals.sharing_d_tracks, existingTracks.size(), existingTracks.size()), Toast.LENGTH_SHORT).show();

                return true;

            case R.id.miSharePlaylistURL:

                final Intent sharePlaylistUrlIntent = new Intent(Intent.ACTION_SEND);
                sharePlaylistUrlIntent.setType("text/plain");
                sharePlaylistUrlIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.Download_track_using_soundcloud_downloader_s, App.STORE_URL, playlist.getTitle(), playlist.getSoundCloudUrl()));
                startActivity(Intent.createChooser(sharePlaylistUrlIntent, getString(R.string.Share_using)));

                return true;


            case R.id.miDownloadPlaylist:

                boolean isAllTrackSaved = true;

                List<Track> tracks = playlist.getTracks();

                if (tracks == null) {
                    tracks = tracksTable.getAll(playlist.getId());
                    playlist.setTracks(tracks);
                }

                for (final Track track : tracks) {
                    if (track.getFile() == null || !track.getFile().exists()) {
                        isAllTrackSaved = false;
                        break;
                    }
                }


                if (!isAllTrackSaved) {

                    //Launching downloader service
                    final Intent downloadIntent = new Intent(getActivity(), DownloaderService.class);
                    downloadIntent.putExtra(Tracks.COLUMN_SOUNDCLOUD_URL, playlist.getSoundCloudUrl());
                    getActivity().startService(downloadIntent);
                    Toast.makeText(getActivity(), R.string.initializing_download, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), R.string.All_tracks_are_downloaded, Toast.LENGTH_SHORT).show();
                }

                return true;

            default:
                return false;
        }
    }
}
