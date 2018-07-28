package com.celestialapps.reverserecorder.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.celestialapps.reverserecorder.R;
import com.celestialapps.reverserecorder.presentation.presenter.RecorderPresenter;
import com.celestialapps.reverserecorder.presentation.presenter.ReverseMediaPlayerPresenter;
import com.celestialapps.reverserecorder.presentation.view.RecorderView;
import com.celestialapps.reverserecorder.presentation.view.ReverseMediaPlayerView;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends MvpAppCompatActivity
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, RecorderView, ReverseMediaPlayerView {

    public static final int PERMISSION_REQUEST_RECORD_AUDIO_AND_WRITE_EXTERNAL_STORAGE = 6278;

    @InjectPresenter
    RecorderPresenter mRecorderPresenter;
    @InjectPresenter
    ReverseMediaPlayerPresenter mReverseMediaPlayerPresenter;

    @BindView(R.id.elegant_num_btn_duration_in_seconds)
    ElegantNumberButton mElegantNumBtnDurationInSeconds;
    @BindView(R.id.ac_tv_time_left)
    AppCompatTextView mAcTvTimeLeft;
    @BindView(R.id.toggle_btn_launch_recorder)
    ToggleButton mToggleBtnLaunchRecorder;
    @BindView(R.id.ac_btn_listen_record)
    AppCompatButton mAcBtnListenRecord;
    @BindView(R.id.ac_btn_pause_record)
    AppCompatButton mAcBtnPauseRecord;
    @BindView(R.id.ac_btn_stop_record)
    AppCompatButton mAcBtnStopRecord;

    Thread mTimeLeftThread;
    boolean mIsTimeLeftTimerStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        ButterKnife.bind(this);
        checkRecordAudioAndWriteExternalStoragePermission();

        setupWidgets();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRecorderPresenter.stopRecording();
        mReverseMediaPlayerPresenter.pausePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRecorderPresenter.stopRecording();
        mReverseMediaPlayerPresenter.stopPlayer();
    }

    private void setupWidgets() {
        mElegantNumBtnDurationInSeconds.setOnValueChangeListener((view, oldValue, newValue) -> {
            if (!mIsTimeLeftTimerStarted) {
                updateTimerText();
            }
        });

        mAcTvTimeLeft.setText(mElegantNumBtnDurationInSeconds.getNumber());
        mAcTvTimeLeft.setOnClickListener(this);

        mToggleBtnLaunchRecorder.setOnCheckedChangeListener(this);

        mAcBtnListenRecord.setOnClickListener(this);
        mAcBtnPauseRecord.setOnClickListener(this);
        mAcBtnStopRecord.setOnClickListener(this);
    }

    private void checkRecordAudioAndWriteExternalStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_RECORD_AUDIO_AND_WRITE_EXTERNAL_STORAGE);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_RECORD_AUDIO_AND_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                checkRecordAudioAndWriteExternalStoragePermission();
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ac_btn_listen_record:
                mReverseMediaPlayerPresenter.playWavReverse(Environment.getExternalStorageDirectory() + "/record.wav");
                break;

            case R.id.ac_btn_pause_record:
                mReverseMediaPlayerPresenter.pausePlayer();
                break;

            case R.id.ac_btn_stop_record:
                mReverseMediaPlayerPresenter.stopPlayer();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {

            case R.id.toggle_btn_launch_recorder:
                if (b) {
                    mToggleBtnLaunchRecorder.setBackgroundResource(R.drawable.round_shape_red);
                    mRecorderPresenter.startRecording(Long.parseLong(mElegantNumBtnDurationInSeconds.getNumber()),
                            Environment.getExternalStorageDirectory() + "/record.wav");
                } else {
                    mToggleBtnLaunchRecorder.setBackgroundResource(R.drawable.round_shape_green);
                    mRecorderPresenter.stopRecording();
                }
                break;
        }
    }

    @Override
    public void onRecordingStarted() {
        disablePlayerButtons();
        startRecordingAutoStopTimer();
    }

    private void disablePlayerButtons() {
        makeViewNotClickable(mAcBtnListenRecord);
        makeViewNotClickable(mAcBtnPauseRecord);
        makeViewNotClickable(mAcBtnStopRecord);
    }

    private void startRecordingAutoStopTimer() {
        mIsTimeLeftTimerStarted = true;

        mTimeLeftThread = new Thread(() -> {
            try {
                while (!mTimeLeftThread.isInterrupted()) {
                    Thread.sleep(1000);
                    int timeLeft = Integer.parseInt(mAcTvTimeLeft.getText().toString()) - 1;

                    runOnUiThread(() -> mAcTvTimeLeft.setText(timeLeft + ""));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        mTimeLeftThread.start();
    }

    @Override
    public void onRecordingStopped() {
        updateLaunchButton();
        enablePlayerButtons();
        stopRecordingAutoStopTimer();
        updateTimerText();
    }

    private void enablePlayerButtons() {
        makeViewClickable(mAcBtnListenRecord, R.drawable.round_shape_green);
        makeViewClickable(mAcBtnPauseRecord, R.drawable.round_shape_yellow);
        makeViewClickable(mAcBtnStopRecord, R.drawable.round_shape_red);
    }

    private void updateLaunchButton() {
        mToggleBtnLaunchRecorder.setOnCheckedChangeListener(null);
        mToggleBtnLaunchRecorder.setBackgroundResource(R.drawable.round_shape_green);
        mToggleBtnLaunchRecorder.setChecked(false);
        mToggleBtnLaunchRecorder.setOnCheckedChangeListener(this);
    }

    private void stopRecordingAutoStopTimer() {
        if (!mTimeLeftThread.isInterrupted()) {
            mTimeLeftThread.interrupt();
        }

        mTimeLeftThread = null;
        mIsTimeLeftTimerStarted = false;
    }

    private void updateTimerText() {
        new Handler().postDelayed(() -> mAcTvTimeLeft.setText(mElegantNumBtnDurationInSeconds.getNumber()), 80);
    }

    @Override
    public void reversePlayWavStarted() {
        makeViewNotClickable(mToggleBtnLaunchRecorder);
    }

    @Override
    public void playerPaused() {

    }

    @Override
    public void playerStopped() {
        makeViewClickable(mToggleBtnLaunchRecorder, R.drawable.round_shape_green);
    }

    private void makeViewNotClickable(View view) {
        view.setClickable(false);
        view.setBackgroundResource(R.drawable.round_shape_gray);
    }

    private void makeViewClickable(View view, @DrawableRes int backgroundRes) {
        view.setClickable(true);
        view.setBackgroundResource(backgroundRes);
    }

}
