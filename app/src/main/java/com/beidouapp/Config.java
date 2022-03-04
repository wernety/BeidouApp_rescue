package com.beidouapp;

import android.os.Environment;

/**
 * Created by lewis on 2012/03/04.
 * 服务端配置接口
 */

public interface Config {

    // 定义服务端地址
    String BeiDou_SERVER_HOST = "139.196.122.222";
    int BeiDou_SERVER_PORT = 8080;
    // 定义福大北斗地址
    String FuDa_SERVER_HOST = "119.3.133.44";
    int FuDa_SERVER_PORT = 50874;

    int DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND = 120;

    String SP_NAME = "config";
    String SP_KEY_SHOW_GROUP_MEMBER_ALIAS = "show_group_member_alias:%s";

    //    定义本地存储文件夹
    String VIDEO_SAVE_DIR = Environment.getExternalStorageDirectory().getPath() + "/navigate/video";
    String AUDIO_SAVE_DIR = Environment.getExternalStorageDirectory().getPath() + "/navigate/audio";
    String PHOTO_SAVE_DIR = Environment.getExternalStorageDirectory().getPath() + "/navigate/photo";
    String FILE_SAVE_DIR = Environment.getExternalStorageDirectory().getPath() + "/navigate/file";
}
