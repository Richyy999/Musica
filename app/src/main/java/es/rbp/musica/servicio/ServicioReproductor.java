package es.rbp.musica.servicio;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import java.util.List;

import es.rbp.musica.modelo.AccesoFichero;
import es.rbp.musica.modelo.Ajustes;
import es.rbp.musica.modelo.entidad.Cancion;
import es.rbp.musica.modelo.entidad.Cola;

/**
 * Servicio encargado de manejar la reproducción de la música en la {@link Cola}.
 *
 * @author Ricardo Bordería Pi
 * @see <a href="https://github.com/RockBoyEmy/GESMediaPlayer">GES-Media-Player</a>
 */
public class ServicioReproductor extends MediaBrowserServiceCompat implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
        AudioManager.OnAudioFocusChangeListener {

    private MediaPlayer reproductor;
    private MediaSessionCompat sesion;
    private MediaControllerCompat.TransportControls controles;

    private Cola cola;


    private MediaSessionCompat.Callback llamada = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            super.onPlay();
        }
    };

    private BroadcastReceiver recibidorRuido = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (reproductor != null && cola != null && cola.seEstaReproduciendo()){
                pausa();
            }
        }
    };

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onAudioFocusChange(int i) {

    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable @org.jetbrains.annotations.Nullable Bundle rootHints) {
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

    /**
     * Inicializa el reproductor
     */
    private void iniciarMediaPlayer() {
        reproductor = new MediaPlayer();
        reproductor.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        reproductor.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());
        reproductor.setVolume(1.0f, 1.0f);

        reproductor.setOnErrorListener(this);
        reproductor.setOnCompletionListener(this);
        reproductor.setOnCompletionListener(this);
    }

    /**
     * Inicializa la {@link MediaSessionCompat}
     */
    private void iniciarMediaSession() {
        ComponentName recibidorBotones = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        sesion = new MediaSessionCompat(getApplicationContext(), "ServicioTuMusica", recibidorBotones, null);

        sesion.setCallback(llamada);
        sesion.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        controles = sesion.getController().getTransportControls();

        setSessionToken(sesion.getSessionToken());
    }

    /**
     * Inicializa los metadatos de la canción actual
     *
     * @see {@link Cola#getCancionActual()}
     */
    private void iniciarMetadatos() {
        Cancion cancionActual = cola.getCancionActual();
        Ajustes ajustes = Ajustes.getInstance(getApplicationContext());
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();

        if (ajustes.isUtilizarNombreDeArchivo())
            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, cancionActual.getNombreArchivo());
        else
            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, cancionActual.getNombre());

        builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, cancionActual.getAlbum());
        builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, cancionActual.getArtista());

        sesion.setMetadata(builder.build());
    }

    private void registrarRecibidorRuido(){
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(recibidorRuido, intentFilter);
    }

    private void registrarRecibidorCargaAudio(){
        IntentFilter intentFilter = new IntentFilter();
    }

    private void setMediaPlaybackstate(int estado){
        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();
        if (estado == PlaybackStateCompat.STATE_PLAYING)
            builder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PAUSE);
        else
            builder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY);

        builder.setState(estado, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
        sesion.setPlaybackState(builder.build());
    }

    private void pausa(){
        if (reproductor != null && cola!= null && cola.seEstaReproduciendo()){
            reproductor.pause();
            cola.setEstaReproduciendo(false);
            cola.setProgresoActual(reproductor.getCurrentPosition());

            setMediaPlaybackstate(PlaybackStateCompat.STATE_PAUSED);
            unregisterReceiver(recibidorRuido);
            stopForeground(false);
        }
    }
}
