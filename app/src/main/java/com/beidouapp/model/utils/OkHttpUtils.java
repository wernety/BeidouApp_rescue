package com.beidouapp.model.utils;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.beidouapp.ui.MainActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {
    private OkHttpClient client;
    private static OkHttpUtils instance;
    private Context context;

    public OkHttpUtils(Context c) {
        context = c;
        client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build();
    }

    /**
     * 获取句柄
     * @param c
     * @return
     */
    public static OkHttpUtils getInstance(Context c) {
        if (instance == null)
            instance = new OkHttpUtils(c);
        return instance;
    }


    /**
     * Get请求
     * @param url
     * @param params
     * @param callback
     */
    public void get (String url, HashMap<String, String> params, MyCallback callback) {
        if (!params.isEmpty()) {
            StringBuffer buffer = new StringBuffer(url);
            buffer.append('?');
            for (Map.Entry<String, String> entry: params.entrySet()) {
                buffer.append(entry.getKey());
                buffer.append('=');
                buffer.append(entry.getValue());
                buffer.append('&');
            }
            buffer.deleteCharAt(buffer.length() - 1);
            url = buffer.toString();
        }
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.success(response);
            }
        });
    }

    public void get (String url, HashMap<String, String> params, String token, MyCallback callback) {
        if (!params.isEmpty()) {
            StringBuffer buffer = new StringBuffer(url);
            buffer.append('?');
            for (Map.Entry<String, String> entry: params.entrySet()) {
                buffer.append(entry.getKey());
                buffer.append('=');
                buffer.append(entry.getValue());
                buffer.append('&');
            }
            buffer.deleteCharAt(buffer.length() - 1);
            url = buffer.toString();
        }
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.success(response);
            }
        });
    }

    public void get (String url, MyCallback callback) {
        get(url, new HashMap<String, String>(), callback);
    }

    public void get (String url, String token, MyCallback callback) {
        get(url, new HashMap<String, String>(), token, callback);
    }


    /**
     * post请求
     * @param url
     * @param params
     * @param callback
     */
    public void post(String url, HashMap<String, String> params, MyCallback callback) {
        FormBody.Builder formBody = new FormBody.Builder();
        if (!params.isEmpty()) {
            for (Map.Entry<String, String> entry:params.entrySet()) {
                formBody.add(entry.getKey(), entry.getValue());
            }
        }
        RequestBody requestBody = formBody.build();
        Request request = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.success(response);
            }
        });
    }

    /**
     * post传参
     * @param url
     * @param json
     * @param callback
     */
    public void post(String url, String json, MyCallback callback) {
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.success(response);
            }
        });


    }

    /**
     * post使用后端token，无传参
     * @param url
     * @param callback
     * @param token
     */
    public void post(String url, MyCallback callback, String token) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create(null, ""))
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.success(response);
            }
        });
    }

    /**
     * post使用后端token，传参
     * @param url
     * @param json
     * @param callback
     * @param token
     */
    public void post(String url, String json, MyCallback callback, String token) {
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.success(response);
            }
        });
    }

    public void postBD(String url, String json, MyCallback callback, String token) {
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-FD-Token",  token)
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.success(response);
            }
        });
    }
    public void post(String url, MyCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(null, ""))
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.success(response);
            }
        });
    }


    /**
     * delete请求
     * @param url
     * @param token
     * @param callback
     */
    public void delete(String url, String token, MyCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .delete(RequestBody.create(null, ""))
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.success(response);
            }
        });
    }

    public void del(String url, String json, MyCallback callback){
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .delete(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.success(response);
            }
        });
    }

    /**
     * put请求
     * @param url
     * @param params
     * @param callback
     */
    public void put(String url, HashMap<String, String> params, MyCallback callback) {
        FormBody.Builder formBody = new FormBody.Builder();
        if (!params.isEmpty()) {
            for (Map.Entry<String, String> entry:params.entrySet()) {
                formBody.add(entry.getKey(), entry.getValue());
            }
        }
        RequestBody requestBody = formBody.build();
        Request request = new Request.Builder()
                .put(requestBody)
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.success(response);
            }
        });
    }

    /**
     * put with token
     * @param url
     * @param params
     * @param token
     * @param callback
     */
    public void put(String url, HashMap<String, String> params, String token, MyCallback callback) {
        FormBody.Builder formBody = new FormBody.Builder();
        if (!params.isEmpty()) {
            for (Map.Entry<String, String> entry:params.entrySet()) {
                formBody.add(entry.getKey(), entry.getValue());
            }
        }
        RequestBody requestBody = formBody.build();
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + token)
                .put(requestBody)
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.success(response);
            }
        });
    }

    public void put(String url, String json, MyCallback callback) {
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.success(response);
            }
        });


    }



    public interface MyCallback {
        void success(Response response) throws IOException;
        void failed(IOException e);
    }
}
