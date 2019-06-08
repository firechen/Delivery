package com.james.deliveryapp.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// generate the retrofit service for getting delivery data
public class ServiceGenerator {
    private static final String DELIVERY_BASE_URL = "https://mock-api-mobile.dev.lalamove.com/";
    public static final int DEFAULT_OFFSET = 0;
    public static final int LIMIT = 20;
    private static Context myContext;
    private static long cacheSize = 5 * 1024 * 1024; // cache for 5 Mb data

    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(DELIVERY_BASE_URL)
                                                            .addConverterFactory(GsonConverterFactory.create());
    static Retrofit retrofit = builder.build();

    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
                                                                .setLevel(HttpLoggingInterceptor.Level.BODY);
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


    public static <S> S createService(Class<S> serviceClass, final Context context){
        myContext = context;
        Cache myCache = new Cache(context.getCacheDir(), cacheSize);
        if(!httpClient.interceptors().contains(logging)){
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        /*
         *  If there is Internet, get the cache that was stored 5 seconds ago.
         *  If the cache is older than 5 seconds, then discard it,
         *  and indicate an error in fetching the response.
         *  The 'max-age' attribute is responsible for this behavior.
         */
        /*
         *  If there is no Internet, get the cache that was stored 7 days ago.
         *  If the cache is older than 7 days, then discard it,
         *  and indicate an error in fetching the response.
         *  The 'max-stale' attribute is responsible for this behavior.
         *  The 'only-if-cached' attribute indicates to not retrieve new data; fetch the cache only instead.
         */
        httpClient.cache(myCache).addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originRequest = chain.request();
                HttpUrl originalHttpUrl = originRequest.url();
                HttpUrl url = originalHttpUrl.newBuilder().build();
                originRequest = hasNetwork(context)?
                        originRequest.newBuilder().header("Cache-Control", "public, max-age=" + 5).url(url).build() :
                        originRequest.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).url(url).build();
                return chain.proceed(originRequest);
            }
        });
        builder.client(httpClient.build());
        retrofit = builder.build();
        return  retrofit.create(serviceClass);
    }

    // check for network connection status
    private static boolean hasNetwork(Context context) {
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            isConnected = true;
        }
        return isConnected;
    }
}
