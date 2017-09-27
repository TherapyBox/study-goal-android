package com.studygoal.jisc.Utils.GlideConfig;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpStreamFetcher implements DataFetcher<InputStream> {
    private final OkHttpClient mClient;
    private final GlideUrl mUrl;
    private InputStream mStream;
    private ResponseBody mResponseBody;

    public OkHttpStreamFetcher(OkHttpClient client, GlideUrl url) {
        mClient = client;
        mUrl = url;
    }

    @Override
    public void loadData(Priority priority, DataCallback<? super InputStream> callback) {
        try {
            Request.Builder requestBuilder = new Request.Builder()
                    .url(mUrl.toStringUrl());

            for (Map.Entry<String, String> headerEntry : mUrl.getHeaders().entrySet()) {
                String key = headerEntry.getKey();
                requestBuilder.addHeader(key, headerEntry.getValue());
            }

            Request request = requestBuilder.build();

            Response response = mClient.newCall(request).execute();
            mResponseBody = response.body();
            if (!response.isSuccessful()) {
                throw new IOException("Request failed with code: " + response.code());
            }

            long contentLength = mResponseBody.contentLength();
            mStream = ContentLengthInputStream.obtain(mResponseBody.byteStream(), contentLength);
            callback.onDataReady(mStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() {
        if (mStream != null) {
            try {
                mStream.close();
            } catch (IOException e) {
                // Ignored
            }
        }
        if (mResponseBody != null) {
            mResponseBody.close();
        }
    }

    @Override
    public void cancel() {
        // do nothing
    }

    @Override
    public Class<InputStream> getDataClass() {
        return null;
    }

    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}