package com.theah64.soundclouddownloader.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.theah64.soundclouddownloader.utils.DownloadUtils;
import com.theah64.soundclouddownloader.utils.SingletonToast;
import com.theah64.soundclouddownloader.widgets.ThemedSnackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TracksFragment extends BaseMusicFragment implements ITSAdapter.TracksCallback, PopupMenu.OnMenuItemClickListener, TrackListener {


    private static final String X = TracksFragment.class.getSimpleName();
    private static final int TRACK_POSITION_UNKNOWN = -1;
    private List<Track> trackList;
    private Tracks tracksTable;
    private ITSAdapter itsAdapter;
    private View layout;
    private App app;
    private MainActivityCallback callback;
    private RecyclerView rvTracks;
    private boolean isLaunchedToListPlaylistTracks;
    private DownloadUtils downloadUtils;


    public TracksFragment() {
        // Required empty public constructor
    }

    public static TracksFragment getNewInstance(String playlistId) {
        final TracksFragment tracksFragment = new TracksFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(Playlists.COLUMN_ID, playlistId);
        tracksFragment.setArguments(bundle);
        return tracksFragment;
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

        Log.d(X, "isLaunchedToListPlaylistTracks " + isLaunchedToListPlaylistTracks);

        if (isLaunchedToListPlaylistTracks) {
            app.setPlaylistTrackListener(this);
        } else {
            app.setMainTrackListener(this);
        }
    }

    @Override
    public void onDestroy() {

        final TrackListener playlistTrackListener = app.getPlaylistTrackListener();
        if (this.equals(playlistTrackListener)) {
            app.setPlaylistTrackListener(null);
        }


        final TrackListener trackListener = app.getMainTrackListener();
        if (this.equals(trackListener)) {
            app.setMainTrackListener(null);
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

        downloadUtils = new DownloadUtils(getActivity());

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
        itsAdapter = new ITSAdapter(getActivity(), trackList, this, downloadUtils);
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

        final Track track = trackList.get(position);

        if (track.isDownloaded()) {
            //playing track
            playTrack(track);
        } else if (track.getSubtitle3(downloadUtils) == null) {
            onPopUpMenuClicked(popUpAnchor, position);
        }
    }

    @Override
    public void onPopUpMenuClicked(View anchor, final int position) {

        final Track track = trackList.get(position);

        final PopupMenu trackMenu = new PopupMenu(getActivity(), anchor);

        final Menu menu = trackMenu.getMenu();

        if (track.isDownloaded() && track.getFile().exists()) {

            //Build  R.menu.menu_track_downloaded
            menu.add(position, R.id.miPlay, 1, R.string.Play);
            menu.add(position, R.id.miShareFile, 2, R.string.Share_file);
            menu.add(position, R.id.miShareURL, 3, R.string.Share_URL);
            menu.add(position, R.id.miDownload, 4, R.string.Download_again);
            menu.add(position, R.id.miOpenTrackDirectory, 5, R.string.Open_track_directory);
            menu.add(position, R.id.miRemove, 6, R.string.Remove);

        } else {
            //Build R.menu.menu_track_not_downloaded
            menu.add(position, R.id.miDownload, 1, R.string.Download);
            menu.add(position, R.id.miShareURL, 2, R.string.Share_URL);
            menu.add(position, R.id.miRemove, 3, R.string.Remove);
        }
        trackMenu.setOnMenuItemClickListener(this);

        trackMenu.show();

    }

    @Override
    public boolean onMenuItemClick(final MenuItem item) {

        final Track track = trackList.get(item.getGroupId());

        switch (item.getItemId()) {

            case R.id.miPlay:
                playTrack(track);
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
                    SingletonToast.makeText(getActivity(), R.string.File_not_found, Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.miShareURL:

                final Intent shareUrlIntent = new Intent(Intent.ACTION_SEND);
                shareUrlIntent.setType("text/plain");
                shareUrlIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.Download_track_using_soundcloud_downloader_s, App.APK_DOWNLOAD_URL, track.getTitle(), track.getSoundCloudUrl()));
                startActivity(Intent.createChooser(shareUrlIntent, getString(R.string.Share_using)));

                return true;

            case R.id.miRemove:

                ThemedSnackbar.make(getActivity(), getActivity().findViewById(android.R.id.content), R.string.Do_you_really, Snackbar.LENGTH_LONG)
                        .setAction(R.string.YES, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //just call this method and everything will be cared.
                                tracksTable.delete(Tracks.COLUMN_ID, track.getId(), null);
                                //noinspection ResultOfMethodCallIgnored
                                track.getFile().delete();


                            }
                        }).show();

                return true;

            case R.id.miDownload:

                final Intent downloadIntent = new Intent(getActivity(), DownloaderService.class);
                downloadIntent.putExtra(Tracks.COLUMN_SOUNDCLOUD_URL, track.getSoundCloudUrl());
                getActivity().startService(downloadIntent);

                SingletonToast.makeText(getActivity(), R.string.initializing_download, Toast.LENGTH_SHORT).show();

                return true;

            case R.id.miOpenTrackDirectory:

                final Intent storageIntent = new Intent(Intent.ACTION_VIEW);
                storageIntent.setDataAndType(Uri.fromFile(track.getFile().getParentFile()), "resource/folder");
                if (storageIntent.resolveActivityInfo(getActivity().getPackageManager(), 0) != null) {
                    getActivity().startActivity(storageIntent);
                } else {
                    SingletonToast.makeText(getActivity(), R.string.No_file_browser_found, Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return false;
        }

    }

    private void playTrack(Track track) {

        if (track.getFile().exists()) {
            //Opening audio file
            final Intent playIntent = new Intent(Intent.ACTION_VIEW);
            playIntent.setDataAndType(Uri.fromFile(track.getFile()), "audio/*");
            startActivity(playIntent);

        } else {
            SingletonToast.makeText(getActivity(), R.string.File_not_found, Toast.LENGTH_SHORT).show();
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
        rvTracks.scrollToPosition(0);

        updateTabsAndNothingFounds();
    }

    private void updateTabsAndNothingFounds() {

        callback.setTabTracksCount(trackList.size());

        if (trackList.isEmpty()) {
            //showing no tracks downloaded text view.
            layout.findViewById(R.id.llNoTracksFound).setVisibility(View.VISIBLE);
        } else {
            layout.findViewById(R.id.llNoTracksFound).setVisibility(View.GONE);
        }

    }

    @Override
    public void onTrackRemoved(@NonNull Track removedTrack) {
        final int removedTrackPosition = Track.getTrackPosition(trackList, removedTrack.getId());
        if (removedTrackPosition != -1) {
            trackList.remove(removedTrackPosition);
            itsAdapter.notifyItemRemoved(removedTrackPosition);

            updateTabsAndNothingFounds();
        } else {
            Log.e(X, "TSH: Couldn't find track " + removedTrack.getId());
        }
    }

    @Override
    public void onTrackUpdated(@NonNull Track updatedTrack) {
        final int updatedTrackPosition = Track.getTrackPosition(trackList, updatedTrack.getId());
        if (updatedTrackPosition != -1) {
            trackList.remove(updatedTrackPosition);
            trackList.add(updatedTrackPosition, updatedTrack);
            itsAdapter.notifyItemChanged(updatedTrackPosition);
        } else {
            Log.e(X, "TSH: Couldn't find track " + updatedTrack.getId());
        }
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

            updateTabsAndNothingFounds();
        }

    }
}
