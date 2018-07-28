package com.celestialapps.reverserecorder.utils;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WavReverseMediaPlayer {

    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private int mLength;

    public WavReverseMediaPlayer(Context context) {
        this.mContext = context;
        this.mMediaPlayer = new MediaPlayer();
    }

    public void playWavReverse(String path) {
        File file = new File(path);
        try {
            FileInputStream fin = new FileInputStream(file);
            byte[] bytes = convertStreamToByteArray(fin, file.length());
            fin.close();

            bytes = reverseWavFormat(bytes);
            playWavReverse(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pausePlayer() {
        mMediaPlayer.pause();
        mLength = mMediaPlayer.getCurrentPosition();
    }


    public void stopPlayer() {
        mLength = 0;

        mMediaPlayer.stop();
        mMediaPlayer.release();

        mMediaPlayer = null;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
        mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
            mLength = 0;
            onCompletionListener.onCompletion(mediaPlayer);
        });
    }

    private void playWavReverse(byte[] soundByteArray) {
        try {
            File tempMp3 = File.createTempFile("temp", "wav", mContext.getCacheDir());
            tempMp3.deleteOnExit();

            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(soundByteArray);
            fos.close();

            FileInputStream fis = new FileInputStream(tempMp3);

            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(fis.getFD());

            mMediaPlayer.prepare();
            mMediaPlayer.seekTo(mLength);
            mMediaPlayer.start();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private byte[] convertStreamToByteArray(InputStream is, long size) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[(int) size];
        int i;

        while ((i = is.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }

        return baos.toByteArray();
    }

    private byte[] reverseWavFormat(byte[] array) {
        byte[] result = new byte[array.length];
        System.arraycopy(array, 0, result, 0, 44);

        int length = array.length;

        for (int i = 44; i < length; i++) {
            byte value = array[length - i];
            result[i] = value;
        }

        return result;
    }
}
