package com.celestialapps.reverserecorder.presentation.view;

import com.arellomobile.mvp.MvpView;

public interface RecorderView extends MvpView {

    void onRecordingStarted();
    void onRecordingStopped();
}
