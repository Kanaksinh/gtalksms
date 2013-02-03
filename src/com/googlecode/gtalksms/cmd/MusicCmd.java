package com.googlecode.gtalksms.cmd;

import android.content.Context;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;

import com.googlecode.gtalksms.MainService;
import com.googlecode.gtalksms.tools.Tools;

public class MusicCmd extends CommandHandlerBase {

    private static AudioManager audioManager;
    
    public MusicCmd(MainService mainService) {
        super(mainService, CommandHandlerBase.TYPE_MEDIA, "Music", new Cmd("music", "zic"), new Cmd("volume", "vol"));
        audioManager = (AudioManager) sContext.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void execute(String command, String args) {
        if (isMatchingCmd("music", command)) {
            if (args.toLowerCase().equals("next")) {
                sendKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
            } else if (args.toLowerCase().equals("previous")) {
                sendKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
            } else if (args.toLowerCase().equals("play")) {
                sendKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY);
            } else if (args.toLowerCase().equals("pause")) {
                sendKeyEvent(KeyEvent.KEYCODE_MEDIA_PAUSE);
            } else if (args.toLowerCase().equals("stop")) {
                sendKeyEvent(KeyEvent.KEYCODE_MEDIA_STOP);
            } else if (args.toLowerCase().equals("playpause")) {
                sendKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
            }
        }
        if (isMatchingCmd("music", command) || isMatchingCmd("volume", command)) {
            if (args.toLowerCase().equals("up")) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
            } else if (args.toLowerCase().equals("down")) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
            } else if (args.toLowerCase().equals("mute")) {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            } else if (args.toLowerCase().equals("unmute")) {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            } else {
                Integer value = Tools.parseInt(args);
                if (value != null) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, 0);
                }
            }
        }
    }  

    private void sendKeyEvent(int key) {
        Log.d(Tools.LOG_TAG, "Sending event key " + key);
        handleMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, key));
        handleMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, key));
    }

    private void handleMediaKeyEvent(KeyEvent keyEvent) {
        try {
            // Get binder from ServiceManager.checkService(String)
            IBinder iBinder = (IBinder) Class.forName("android.os.ServiceManager")
                    .getDeclaredMethod("checkService", String.class).invoke(null, Context.AUDIO_SERVICE);

            // get audioService from IAudioService.Stub.asInterface(IBinder)
            Object audioService = Class.forName("android.media.IAudioService$Stub")
                    .getDeclaredMethod("asInterface", IBinder.class).invoke(null, iBinder);

            // Dispatch keyEvent using IAudioService.dispatchMediaKeyEvent(KeyEvent)
            Class.forName("android.media.IAudioService").getDeclaredMethod("dispatchMediaKeyEvent", KeyEvent.class)
                    .invoke(audioService, keyEvent);
        } catch (Exception e) {
            Log.e(Tools.LOG_TAG, "Error sending event key " + e.getMessage(), e);
        }
    }

    @Override
    protected void initializeSubCommands() {
        Cmd music = mCommandMap.get("music");
        music.setHelp(-1, null);
        music.AddSubCmd("play", -1, null);
        music.AddSubCmd("pause", -1, null);
        music.AddSubCmd("playpause", -1, null);
        music.AddSubCmd("stop", -1, null);
        music.AddSubCmd("next", -1, null);
        music.AddSubCmd("previous", -1, null);
        music.AddSubCmd("up", -1, null);
        music.AddSubCmd("down", -1, null);
        music.AddSubCmd("mute", -1, null);
        music.AddSubCmd("unmute", -1, null);

        Cmd volume = mCommandMap.get("music");
        volume.setHelp(-1, null);
        volume.AddSubCmd("up", -1, null);
        volume.AddSubCmd("down", -1, null);
        volume.AddSubCmd("mute", -1, null);
        volume.AddSubCmd("unmute", -1, null);
    }
}