package com.scodash.android;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = {AndroidInjectionModule.class, ScodashApplicationModule.class, ServerCommunicationModule.class})
public interface ScodashApplicationComponent extends AndroidInjector<ScodashApplication> {
}
