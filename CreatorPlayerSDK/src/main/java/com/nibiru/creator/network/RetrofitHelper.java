package com.nibiru.creator.network;


import com.google.gson.Gson;
import com.nibiru.creator.BuildConfig;
import com.nibiru.creator.data.ResUrlData;
import com.nibiru.creator.utils.Constants;

import java.io.IOException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    private static final String HOST = "http://test116.1919game.net/";

    private static RetrofitHelper self;
    private ApiService apiService;
    private CompositeDisposable compositeDisposable;

    public static RetrofitHelper getInstance() {
        if (self == null) {
            synchronized (RetrofitHelper.class) {
                if (self == null) {
                    self = new RetrofitHelper();
                }
            }
        }
        return self;
    }

    private RetrofitHelper() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            // Log信息拦截器
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // 这里可以选择拦截级别
            builder.addInterceptor(loggingInterceptor);
        }

        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            }
            };
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(ApiService.class);
        compositeDisposable = new CompositeDisposable();
    }

    public void getNptContent(String url, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = apiService.getNptContent(url);
        call.enqueue(callback);
    }

    public void getResUrl(String inputUrl, final OnGetResUrlListener listener) {
        Call<ResponseBody> call = apiService.getResUrl(Constants.URL, inputUrl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        try {
                            Gson gson = new Gson();
                            ResUrlData resUrlData = gson.fromJson(response.body().string(), ResUrlData.class);
                            if (resUrlData != null && resUrlData.getRecode() == 1) {
                                if (resUrlData.getData() != null && resUrlData.getData().getItems() != null) {
                                    if (listener != null) {
                                        listener.onSuccess(resUrlData.getData().getItems().get(0).getPlayUrl());
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            if (listener != null) {
                                listener.onFailure();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null) {
                    listener.onFailure();
                }
            }
        });
    }

    public interface OnGetResUrlListener {
        void onSuccess(String playUrl);
        void onFailure();
    }

    // 防止内存泄露
    public void clear() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }
}
