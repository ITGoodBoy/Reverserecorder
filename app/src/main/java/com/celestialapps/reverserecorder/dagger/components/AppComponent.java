package com.celestialapps.reverserecorder.dagger.components;


import com.celestialapps.reverserecorder.dagger.modules.ContextModule;
import com.celestialapps.reverserecorder.presentation.presenter.RecorderPresenter;
import com.celestialapps.reverserecorder.presentation.presenter.ReverseMediaPlayerPresenter;

import javax.inject.Singleton;

import dagger.Component;




@Component(modules = {ContextModule.class})
@Singleton
public interface AppComponent {

    void inject(RecorderPresenter recorderPresenter);

    void inject(ReverseMediaPlayerPresenter reverseMediaPlayerPresenter);
}
