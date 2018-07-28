package com.celestialapps.reverserecorder.presentation.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.celestialapps.reverserecorder.App;
import com.celestialapps.reverserecorder.presentation.view.RecorderView;
import com.celestialapps.reverserecorder.utils.WavRecorder;

import javax.inject.Inject;


@InjectViewState
public class RecorderPresenter extends MvpPresenter<RecorderView> {

    @Inject
    Context mContext;
    WavRecorder mWavRecorder;

    public RecorderPresenter() {
        App.getAppComponent().inject(this);
    }


    public void startRecording(long durationInSeconds, String path) {
        if (mWavRecorder == null) {
            mWavRecorder = new WavRecorder(mContext, path);
        }
        mWavRecorder.startRecording(durationInSeconds, path);

        getViewState().onRecordingStarted();

        mWavRecorder.setOnCompletedListener(this::stopRecording);
    }

    public void stopRecording() {
        mWavRecorder.setOnCompletedListener(null);
        mWavRecorder.stopRecording();
        mWavRecorder = null;

        getViewState().onRecordingStopped();
    }

}
