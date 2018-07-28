package com.celestialapps.reverserecorder.presentation.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.celestialapps.reverserecorder.App;
import com.celestialapps.reverserecorder.presentation.view.ReverseMediaPlayerView;
import com.celestialapps.reverserecorder.utils.WavReverseMediaPlayer;

import javax.inject.Inject;

@InjectViewState
public class ReverseMediaPlayerPresenter extends MvpPresenter<ReverseMediaPlayerView> {

    @Inject
    Context mContext;
    WavReverseMediaPlayer mWavReverseMediaPlayer;

    public ReverseMediaPlayerPresenter() {
        App.getAppComponent().inject(this);
    }

    public void playWavReverse(String filePath) {
        if (mWavReverseMediaPlayer == null) {
            mWavReverseMediaPlayer = new WavReverseMediaPlayer(mContext);
        }

        mWavReverseMediaPlayer.playWavReverse(filePath);

        getViewState().reversePlayWavStarted();

        mWavReverseMediaPlayer.setOnCompletionListener(mediaPlayer -> {
            stopPlayer();
        });
    }

    public void pausePlayer() {
        if (mWavReverseMediaPlayer != null) {
            mWavReverseMediaPlayer.pausePlayer();
        }

        getViewState().playerPaused();
    }

    public void stopPlayer() {
        if (mWavReverseMediaPlayer != null) {
            mWavReverseMediaPlayer.stopPlayer();
            mWavReverseMediaPlayer = null;
        }

        getViewState().playerStopped();
    }

}
