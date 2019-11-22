package com.scodash.android;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Component(modules = { AndroidInjectionModule.class, ScodashApplicationModule.class})
public interface ScodashApplicationComponent extends AndroidInjector<ScodashApplication> {
}
