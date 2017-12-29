package com.example.brona.picasaconnect;


import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

//@Singleton
public class PicasaService {

    public interface Api {
        String
                TAG_FEED = "feed",
                TAG_ENTRY = "entry",
                TAG_GPHOTO_ID = "gphoto$id",
                TAG_MEDIA_GROUP = "media$group",
                TAG_MEDIA_CONTENT = "media$content",
                TAG_MEDIA_THUMBNAIL = "media$thumbnail",
                TAG_TITLE = "title";


        @GET("{accountName}/*?alt=json&thumbsize=160c*/")
        Observable<retrofit2.Response<Albums>> getAlbums(@Path("accountName") String accountName);

        @GET("{accountName}/albumid/{albumId}?alt=json&imgmax=1600&thumbsize=160c")
        Observable<retrofit2.Response<PhotoResponse>> getPhotos(
                @Path("accountName") String accountName,
                @Path("albumId") String albumId);
    }

    private static final String SERVICE_URL =
            "https://picasaweb.google.com/data/feed/api/user/";

    private static final String AUTH_HEADER_TAG = "GoogleLogin auth=";

    private static final String TAG = PicasaService.class.getSimpleName();
    private OkHttpClient mClient;
    private final Api mApiService;
    private String mToken;
    private String mAccountName;
    private AccountManager mAccountManager;

    private static String getAuthHeaderString(String token) {
        Log.d("token", token.toString());
        return AUTH_HEADER_TAG + token;
    }

    private String getToken() {
        return mToken;
    }

    private void setToken(String token) {
        mToken = token;
    }

    private String getAccountName() {
        return mAccountName;
    }

    private void setAccountName(String accountName) {
        mAccountName = accountName;
    }

    PicasaService(Context context) {
        mClient = provideNetworkClient(
                context.getCacheDir(),
                new PicasaHeadersInterceptor(),
                new TimeoutsInterceptor(),
                new FilterNetworkErrorsInterceptor());
        mApiService = provideRetrofitClient(mClient).create(Api.class);

        mAccountManager = AccountManager.get(context);
    }


    private static Retrofit provideRetrofitClient(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(SERVICE_URL)
                .build();
    }


    private static OkHttpClient provideNetworkClient(File cacheDir,
                                                     PicasaHeadersInterceptor headersInterceptor,
                                                     TimeoutsInterceptor timeoutsInterceptor,
                                                     FilterNetworkErrorsInterceptor networkErrorsInterceptor

    ) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(headersInterceptor);
        builder.addInterceptor(timeoutsInterceptor);
        builder.addInterceptor(networkErrorsInterceptor);
        builder.addNetworkInterceptor(
                new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC));
        int cacheSize = 40 * 1024 * 1024;
        Cache cache = new Cache(cacheDir, cacheSize);
        return builder.cache(cache).build();
    }

    private class PicasaHeadersInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder ongoing = chain.request().newBuilder();
            ongoing.addHeader("Content-Type", "application/json");
            ongoing.addHeader("Authorization", getAuthHeaderString(getToken()));
            ongoing.addHeader("User-Agent", "lh2 GData-Java/0.0");
            ongoing.addHeader("GData-Version", "3.0");
            return chain.proceed(ongoing.build());
        }
    }

    // todo set count to make sure it only runs once
    private class FilterNetworkErrorsInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Response orginalResponse = chain.proceed(originalRequest);
            // todo magic number
            /*
                int MAX_REQUESTS = 1
                int requestCount = 0
                ++requestCount
                while (requestCount < MAX_REQUESTS) {
                    handle errors -- do simple testcase elsewhere
                }
             */

            if (orginalResponse.code() == 403) {
                // invalidate old token and get new token
                // redo original request with new token
                Log.e(TAG, "FilterNetworkErrorsInterceptor -- got a 403");
                String newToken = AuthenticationManager.blockingRequestToken(mAccountManager, getAccountName(), getToken());
                if (!newToken.isEmpty()) {
                    Request newRequest = originalRequest.newBuilder()
                            .header("Authorization", getAuthHeaderString(newToken))
                            .build();
                    Log.e(TAG, "FilterNetworkErrorsInterceptor -- got a new token, retrying request");
                    return chain.proceed(newRequest);
                } else {
                    // fail here should not be retried
                    Log.e(TAG, "FilterNetworkErrorsInterceptor -- token request failed, passing error along");
                }
            }
            return orginalResponse;
        }
    }

    private static class TimeoutsInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request().newBuilder()
                    .cacheControl(new CacheControl.Builder()
                            .maxAge(365, TimeUnit.DAYS)
                            .maxStale(365, TimeUnit.DAYS)
                            .build())
                    .build();
            return chain.proceed(request);
        }
    }

    // save name and token to use in interceptor in case of 403
    Observable<retrofit2.Response<Albums>> getAlbums(String accountName, String token) {
        setAccountName(accountName);
        setToken(token);
        return mApiService.getAlbums(accountName);
    }

    Observable<retrofit2.Response<PhotoResponse>> getPhotos(String accountName, String albumId) {
        return mApiService.getPhotos(accountName, albumId);
    }

    // used for testing
    public void clearNetworkCache() {
        Log.e(TAG, "clearCache - BEFORE");
        try {
            dumpCacheInfo();
            mClient.cache().evictAll();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        Log.e(TAG, "clearCache - AFTER");
    }

    private void dumpCacheInfo() {
        try {
            Cache cache = mClient.cache();
            Iterator<String> iterator = cache.urls();
            while (iterator.hasNext()) {
                String url = iterator.next();
                Log.d(TAG, url);
            }
            Log.d(TAG, String.format("CACHE - size: %s, hitCount: %s, requestCount: %s",
                    cache.size(), cache.hitCount(), cache.requestCount()));
        } catch (IOException e) {
            Log.e(TAG, "dumpCacheInfo");
        }
    }

}