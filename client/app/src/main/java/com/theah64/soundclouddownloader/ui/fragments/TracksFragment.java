package com.theah64.soundclouddownloader.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.theah64.soundclouddownloader.R;
import com.theah64.soundclouddownloader.adapters.ITSAdapter;
import com.theah64.soundclouddownloader.database.Playlists;
import com.theah64.soundclouddownloader.database.Tracks;
import com.theah64.soundclouddownloader.interfaces.MainActivityCallback;
import com.theah64.soundclouddownloader.interfaces.TrackListener;
import com.theah64.soundclouddownloader.models.Track;
import com.theah64.soundclouddownloader.services.DownloaderService;
import com.theah64.soundclouddownloader.utils.App;
import com.theah64.soundclouddownloader.utils.CommonUtils;
import com.theah64.soundclouddownloader.widgets.ThemedSnackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TracksFragment extends BaseMusicFragment implements ITSAdapter.TracksCallback, PopupMenu.OnMenuItemClickListener, TrackListener {


    private static final String X = TracksFragment.class.getSimpleName();
    private List<Track> trackList;
    private Tracks tracksTable;
    private ITSAdapter itsAdapter;
    private Track track;
    private int position;
    private View layout;
    private App app;
    private MainActivityCallback callback;
    private RecyclerView rvTracks;
    private boolean isLaunchedToListPlaylistTracks;


    public TracksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        app = (App) context.getApplicationContext();
        callback = (MainActivityCallback) getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();

        isLaunchedToListPlaylistTracks = getArguments().getString(Playlists.COLUMN_ID) != null;

        if (isLaunchedToListPlaylistTracks) {
            app.setPlaylistTrackListener(this);
        } else {
            app.setTrackListener(this);
        }
    }

    @Override
    public void onDestroy() {

        if (isLaunchedToListPlaylistTracks) {
            final TrackListener playlistTrackListener = app.getPlaylistTrackListener();
            if (this.equals(playlistTrackListener)) {
                app.setPlaylistTrackListener(null);
            }
        } else {
            final TrackListener trackListener = app.getTrackListener();
            if (this.equals(trackListener)) {
                app.setTrackListener(null);
            }
        }

        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_tracks, container, false);
        final String playlistId = getArguments().getString(Playlists.COLUMN_ID);
        tracksTable = Tracks.getInstance(getActivity());

        trackList = tracksTable.getAll(playlistId);

        if (trackList != null) {

            initAdapter();

        } else {

            Log.d(X, "No tracks found");

            //showing no tracks downloaded text view.
            layout.findViewById(R.id.llNoTracksFound).setVisibility(View.VISIBLE);

        }

        layout.findViewById(R.id.bOpenSoundCloud).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSoundCloud();
            }
        });

        return layout;
    }

    private void initAdapter() {
        itsAdapter = new ITSAdapter(trackList, this);
        rvTracks = (RecyclerView) layout.findViewById(R.id.rvTracks);
        rvTracks.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvTracks.setAdapter(itsAdapter);

        layout.findViewById(R.id.llNoTracksFound).setVisibility(View.GONE);

        rvTracks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    callback.hideFabAdd();
                } else if (dy < 0) {
                    callback.showFabAdd();
                }
            }

        });
    }

    @Override
    public void onRowClicked(int position, View popUpAnchor) {

        this.position = position;
        track = trackList.get(position);

        if (track.isDownloaded()) {
            //playing track
            playTrack();
        } else {
            onPopUpMenuClicked(popUpAnchor, position);
            Toast.makeText(getActivity(), R.string.Please_download_the_track, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPopUpMenuClicked(View anchor, final int position) {

        this.position = position;
        track = trackList.get(position);

        final PopupMenu trackMenu = new PopupMenu(getActivity(), anchor);
        trackMenu.getMenuInflater().inflate(track.isDownloaded() && track.getFile().exists() ? R.menu.menu_track_downloaded : R.menu.menu_track_not_downloaded, trackMenu.getMenu());
        trackMenu.setOnMenuItemClickListener(this);

        trackMenu.show();

    }

    public static TracksFragment getNewInstance(String playlistId) {
        final TracksFragment tracksFragment = new TracksFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(Playlists.COLUMN_ID, playlistId);
        tracksFragment.setArguments(bundle);
        return tracksFragment;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.miPlay:
                playTrack();
                return true;

            case R.id.miShareFile:

                if (track.getFile().exists()) {
                    //Opening audio file
                    final Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    final String mimeType = CommonUtils.getMIMETypeFromUrl(track.getFile(), "audio/*");
                    sendIntent.setType(mimeType);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(track.getFile()));
                    startActivity(sendIntent);

                } else {
                    Toast.makeText(getActivity(), R.string.File_not_found, Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.miShareURL:

                final Intent shareUrlIntent = new Intent(Intent.ACTION_SEND);
                shareUrlIntent.setType("text/plain");
                shareUrlIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.Download_track_using_soundcloud_downloader_s, App.STORE_URL, track.getTitle(), track.getSoundCloudUrl()));
                startActivity(Intent.createChooser(shareUrlIntent, getString(R.string.Share_using)));

                return true;

            case R.id.miRemove:

                ThemedSnackbar.make(getActivity(), getActivity().findViewById(android.R.id.content), R.string.Do_you_really, Snackbar.LENGTH_LONG)
                        .setAction(R.string.YES, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (tracksTable.delete(Tracks.COLUMN_ID, track.getId())) {
                                    trackList.remove(position);
                                    itsAdapter.notifyItemRemoved(position);

                                    Toast.makeText(getActivity(), R.string.Removed, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), R.string.Failed_to_remove, Toast.LENGTH_SHORT).show();
                                }

                                if (trackList.isEmpty()) {
                                    //showing no tracks downloaded text view.
                                    layout.findViewById(R.id.llNoTracksFound).setVisibility(View.VISIBLE);
                                }

                                if (track.getPlaylistId() != null) {
                                    callback.onRemovePlaylistTrack(track.getPlaylistId());
                                }

                                //Updating tabcount
                                callback.setTabTracksCount(trackList.size());

                            }
                        }).show();

                return true;

            case R.id.miDownload:

                final Intent downloadIntent = new Intent(getActivity(), DownloaderService.class);
                downloadIntent.putExtra(Tracks.COLUMN_SOUNDCLOUD_URL, track.getSoundCloudUrl());
                getActivity().startService(downloadIntent);

                Toast.makeText(getActivity(), R.string.initializing_download, Toast.LENGTH_SHORT).show();

                return true;

            default:
                return false;
        }

    }

    private void playTrack() {

        Log.d(X, "Track is " + track);

        if (track.getFile().exists()) {
            //Opening audio file
            final Intent playIntent = new Intent(Intent.ACTION_VIEW);
            playIntent.setDataAndType(Uri.fromFile(track.getFile()), "audio/*");
            startActivity(playIntent);

        } else {
            Toast.makeText(getActivity(), R.string.File_not_found, Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onNewTrack(Track newTrack) {

        if (trackList == null) {
            trackList = new ArrayList<>();
            initAdapter();
        }

        trackList.add(0, newTrack);
        itsAdapter.notifyItemInserted(0);
        callback.setTabTracksCount(trackList.size());
        rvTracks.scrollToPosition(0);


        if (trackList.isEmpty()) {
            //showing no tracks downloaded text view.
            layout.findViewById(R.id.llNoTracksFound).setVisibility(View.VISIBLE);
        } else {
            layout.findViewById(R.id.llNoTracksFound).setVisibility(View.GONE);
        }
    }

    @Override
    public void onTrackRemoved(Track removedTrack) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public void onTrackUpdated(Track downloadedTrack) {
        for (int i = 0; i < trackList.size(); i++) {

            final Track track = trackList.get(i);

            if (track.getId().equals(downloadedTrack.getId())) {
                Log.d(X, "Downloaded track found : " + track);

                //Removing the old track
                trackList.remove(i);
                trackList.add(i, downloadedTrack);
                itsAdapter.notifyItemChanged(i);
                return;
            }
        }

        throw new IllegalArgumentException("Couldn't find the downloaded track");
    }

    public void onPlaylistRemoved(String removedPlaylistId) {

        if (trackList != null) {


            Log.d(X, "Total tracks: " + trackList.size());
            Log.d(X, "Removed playlist ID " + removedPlaylistId);

            final List<Track> unDeletedTracks = new ArrayList<>();

            for (final Track track : trackList) {

                Log.d(X, "CurrentPlaylistId: " + track.getPlaylistId());

                if (track.getPlaylistId() == null || !track.getPlaylistId().equals(removedPlaylistId)) {
                    unDeletedTracks.add(track);
                }
            }

            Log.d(X, "Remaining track size : " + unDeletedTracks.size());

            trackList.clear();
            trackList.addAll(unDeletedTracks);
            itsAdapter.notifyDataSetChanged();

            if (trackList.isEmpty()) {
                layout.findViewById(R.id.llNoTracksFound).setVisibility(View.VISIBLE);
            }

            callback.setTabTracksCount(trackList.size());

        }

    }

    public int getTracksCount() {
        return trackList != null ? trackList.size() : 0;
    }
}
