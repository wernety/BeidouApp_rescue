package com.beidouapp.model.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beidouapp.model.messages.regist;
import com.beidouapp.model.messages.registToken;
import com.beidouapp.model.messages.regist_fail;

import okhttp3.Response;


/**
 * 这里返回生成的token
 */
public class GenerateTokenDemo {
    private static regist regist;


    public static <curToken> String generate(Long timestamp, Context context) {
        final String[] curToken = new String[1];

            //1、生成数据签名
            String appId = "AppId5358eeb9b2ee399d308b6b98e98fa5c6";
            // secrerKey 授权码的密钥
            String secretKey = "c68bec2934c5b7295a66ab707cc955fd1640314984233nhQ";
            //时间戳
//        Long timestamp = System.currentTimeMillis();
            Log.d("zw", "generate: " + timestamp.toString());
            List<String> tokenParams = new ArrayList<>();
            tokenParams.add(appId);
            tokenParams.add(secretKey);
            tokenParams.add(timestamp + "");
            //自然排序
            Collections.sort(tokenParams);
            //生成token 签名
            String tokenSignature = MD5.MD5Encode(tokenParams.toString());
            Log.d("zw", "main: " + tokenSignature);
            regist = new regist(appId, String.valueOf(timestamp), tokenSignature);
            String json = JSONUtils.sendJson(regist);
            Log.d("zw", "generate: " + json);


            Handler handler = new Handler() {
                //           @Override
                public void handleMessage(Message message) {
                    if (message.what == 1) {
                        curToken[0] = message.getData().getString("curToken");
                        Log.d("zw", "handleMessage: 线程里的curtoken：" + curToken[0]);
                    }
                }
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("zw", "run: 进入子线程,context传入成功");
                        OkHttpUtils.getInstance(context).post("http://119.3.130.87:50099/openApi/openAuthApi/generateToken", json, new OkHttpUtils.MyCallback() {
                            @Override
                            public void success(Response response) throws IOException {
                                Log.d("zw", "success: 成功传入福大");
//                            String body =response.body().toString();
//                            Log.d("zw", "success: " + response.toString());
//                            Log.d("zw", "success: " + response.body().toString());
//                            Log.d("zw", "success: " + response.message());
                                JSONObject object = JSON.parseObject(response.body().string());
                                Log.d("zw", "success: 福大的json对象" + object);
                                JSONObject data = object.getJSONObject("data");
                                String token = data.getString("token");
//                            registToken registToken = JSONUtils.receiveTokenJson(body);
//                            Log.d("zw", "success: " + registToken);
                                Log.d("zw", "success: " + token);

                                Message message = new Message();
                                message.what = 1;
                                Bundle bundle = new Bundle();
                                bundle.putString("curToken", token);
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }

                            @Override
                            public void failed(IOException e) {
                                Log.d("zw", "failed: 失败，但是能连接福大");
                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("zw", "run: 获取Token时的线程炸了");
                    }
                }
            }).start();



        Log.d("zw", "generate: cruToken :" + curToken[0]);
        return curToken[0];
    }

}
