package com.studygoal.jisc.Managers.xApi;

import android.util.Log;

import com.studygoal.jisc.Managers.DataManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Eugene Krasnopolskiy (eugene.krasnopolskiy@gmail.com) on 14.08.2017.
 */
public class XApiManager {
    private static final String TAG = XApiManager.class.getSimpleName();

    private static final String SERVER_BASE = "https://api.x-dev.data.alpha.jisc.ac.uk";
    private static final String TOKEN_PREFIX = "Bearer ";

    private static XApiManager sInstance = null;

    public static XApiManager getInstance() {
        if (sInstance == null) {
            sInstance = new XApiManager();
        }

        return sInstance;
    }

    public void sendLogActivity(String verb) {
        Observable<Boolean> observable = Observable.create(subscriber -> {
            boolean result = false;

            try {
                if (verb != null) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(SERVER_BASE)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    XApi api = retrofit.create(XApi.class);
                    String token = TOKEN_PREFIX + DataManager.getInstance().get_jwt();
                    Call<ResponseBody> call = api.getLogActivity(token, verb);
                    Response<ResponseBody> response = call.execute();

                    if (response != null && response.code() == 200) {
                        result = true;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            subscriber.onNext(result);
            subscriber.onCompleted();
        });

        observable.subscribeOn(Schedulers.io()).retry(1).subscribe();
    }

    public void sendLogActivity(String verb, String contentId, String contentName) {
        Observable<Boolean> observable = Observable.create(subscriber -> {
            boolean result = false;

            try {
                if (verb != null && contentId != null && contentName != null) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(SERVER_BASE)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    XApi api = retrofit.create(XApi.class);
                    String token = TOKEN_PREFIX + DataManager.getInstance().get_jwt();
                    Call<ResponseBody> call = api.getLogActivity(token, verb, contentId, contentName);
                    Response<ResponseBody> response = call.execute();

                    if (response != null && response.code() == 200) {
                        result = true;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            subscriber.onNext(result);
            subscriber.onCompleted();
        });

        observable.subscribeOn(Schedulers.io()).retry(1).subscribe();
    }

    public void sendLogActivity(String verb, String contentId, String contentName, String modId) {
        Observable<Boolean> observable = Observable.create(subscriber -> {

            boolean result = false;

            try {
                if (verb != null && contentId != null && contentName != null && modId != null) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(SERVER_BASE)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    XApi api = retrofit.create(XApi.class);
                    String token = TOKEN_PREFIX + DataManager.getInstance().get_jwt();
                    Call<ResponseBody> call = api.getLogActivity(token, verb);
                    Response<ResponseBody> response = call.execute();

                    if (response != null && response.code() == 200) {
                        result = true;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            subscriber.onNext(result);
            subscriber.onCompleted();
        });

        observable.subscribeOn(Schedulers.io()).retry(1).subscribe();
    }
}
