package com.beidouapp.model.utils;
/**
 * 一个废弃的方法，原本用于获取方向，现在看来不可行,废弃这个封装类
 * 这个类的最根本的方法其实就是
 */

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class OrientationListener {
    private static OrientationListener instance;
    private DirectionListener listener;
    Context context;
    SensorManager sensorManager;

    public static OrientationListener getInstance(Context context) {
        if (instance == null) {
            instance = new OrientationListener(context);
        }
        Log.d("zw", "getInstance: 方向传感器创建实例了");
        return instance;
    }


    private OrientationListener(Context context) {
        this.context = context.getApplicationContext();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void startSensorService() {
        Log.d("zw", "startSensorService: 方向传感器的实例开始启动服务");
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void startSensorService(DirectionListener listener) {
        this.listener = listener;
        startSensorService();
    }

    public DirectionListener getListener() {
        return listener;
    }

    public void setListener(DirectionListener listener) {
        this.listener = listener;
    }

    public void stopSensorService(){
        sensorManager.unregisterListener(sensorEventListener);
        listener = null;
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        float[] accelerometerValues = new float[3];
        float[] magneticFieldValues = new float[3];
        float olDegree;
        final float sensitivity = 5;


        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticFieldValues = event.values.clone();
                Log.d("zw", "onSensorChanged: 现在传感器的类型是磁性：" + event.sensor.getType());
            }
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = event.values.clone();
                Log.d("zw", "onSensorChanged: 现在传感器的类型是传统" + event.sensor.getType());
            }

            Log.d("zw", "onSensorChanged: 现在传感器的类型是：" + event.sensor.getType());

            float[] values = new float[3];
            float[] R = new float[9];
            SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
            SensorManager.getOrientation(R, values);
            float degree = (float) Math.toDegrees(values[0]);
            Log.d("zw", "onSensorChanged: 角度是：" + degree);

            if (Math.abs(degree - olDegree) > 0) {
                olDegree = degree;
                if(listener != null){
                    listener.onDirectionListener(degree);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public interface DirectionListener {
        void onDirectionListener(float degree);
    }
}
