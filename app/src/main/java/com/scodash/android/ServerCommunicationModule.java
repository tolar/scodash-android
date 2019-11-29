package com.scodash.android;

import com.scodash.android.services.impl.ServerWebsocketServiceProvider;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
public class ServerCommunicationModule {

    @Provides
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient().newBuilder().build();
    }

    @Provides
    ServerWebsocketServiceProvider provideServerWebsocketService(OkHttpClient client) {
        return new ServerWebsocketServiceProvider(client);
    }

}
