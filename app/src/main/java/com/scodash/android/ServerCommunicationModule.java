package com.scodash.android;

import com.scodash.android.services.impl.ServerWebsocketServiceProvider;

import java.io.IOException;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

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

    private class WebSocketSchemeInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {

            HttpUrl newUrl = chain.request().url();

            chain.request().newBuilder();
            return chain.proceed(chain.request());
        }
    }
}
