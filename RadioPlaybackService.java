package com.balkanradio.app;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

@UnstableApi
public class RadioPlaybackService extends MediaSessionService {
    public static final String ACTION_PLAY = "com.balkanradio.app.PLAY";
    public static final String ACTION_STOP = "com.balkanradio.app.STOP";
    public static final String EXTRA_URL = "stream_url";
    public static final String EXTRA_TITLE = "station_title";
    private ExoPlayer player;
    private MediaSession mediaSession;

    @Override public void onCreate() {
        super.onCreate();
        AudioAttributes attrs = new AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build();
        player = new ExoPlayer.Builder(this).setAudioAttributes(attrs, true).setHandleAudioBecomingNoisy(true).build();
        mediaSession = new MediaSession.Builder(this, player).build();
    }

    @Nullable @Override public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) { return mediaSession; }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_PLAY.equals(intent.getAction())) {
            String url = intent.getStringExtra(EXTRA_URL);
            String title = intent.getStringExtra(EXTRA_TITLE);
            if (url != null && (url.startsWith("https://") || url.startsWith("http://"))) {
                MediaMetadata metadata = new MediaMetadata.Builder().setTitle(title == null ? "Radio uživo" : title).setArtist("RadioBalkan24").setAlbumTitle("Radio uživo").build();
                player.setMediaItem(new MediaItem.Builder().setUri(url).setMediaMetadata(metadata).build());
                player.prepare(); player.play();
            }
        } else if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            player.stop(); player.clearMediaItems(); stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override public void onDestroy() {
        if (mediaSession != null) { mediaSession.release(); mediaSession = null; }
        if (player != null) { player.release(); player = null; }
        super.onDestroy();
    }
}
