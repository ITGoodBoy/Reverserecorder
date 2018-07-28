package com.celestialapps.reverserecorder.presentation.view;

import com.arellomobile.mvp.MvpView;

public interface ReverseMediaPlayerView extends MvpView {


    void reversePlayWavStarted();
    void playerPaused();
    void playerStopped();
}
