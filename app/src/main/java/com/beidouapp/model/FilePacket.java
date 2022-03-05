package com.beidouapp.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.beidouapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class FilePacket {
    public static final int UP_NEW_FILE = 0x00;                 //客户端发送文件请求
    public static final int UP_FILE_DATA = 0x01;                //客户端发送文件数据包
    public static final int UP_FILE_END = 0x02;                 //客户端发送文件结束

    public static final int UP_ACK_NEW_FILE = 0x00;             //服务器确认接受文件回执
    public static final int UP_ACK_FILE_END = 0x01;             //服务器接受文件结束回执

    public static final int DOWN_NEW_FILE = 0x04;               //客户端下载文件请求
    public static final int DOWN_FILE_REQUEST = 0x05;           //客户端确认开始下载回执
    public static final int DOWN_ACK_FILE_END = 0x06;           //客户端下载文件结束回执

    public static final int DOWN_ACK_NEW_FILE = 0x04;           //服务器确认发送请求
    public static final int DOWN_FILE_DATA = 0x05;              //服务器发送文件数据包
    public static final int DOWN_FILE_END = 0x06;               //服务器发送文件结束

    public static final int SUCCESS_CODE = 0;
    public static final int ERROR_CODE = -1;

    private static final int TYPE_LEN = 1;

    private int type;

    private final ByteBuffer buffer;

    public FilePacket(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public static FilePacket constructUpNewFilePacket(String fileName) {
        byte[] bytes = fileName.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(TYPE_LEN + 4 + bytes.length);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put((byte)UP_NEW_FILE);
        buffer.putInt(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        return new FilePacket(buffer);
    }

    public static FilePacket constructDownNewFilePacket(String fileName) {
        byte[] bytes = fileName.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(TYPE_LEN + 4 + bytes.length);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put((byte)DOWN_NEW_FILE);
        buffer.putInt(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        return new FilePacket(buffer);
    }

    public static FilePacket constructAckNewFilePacket(int code) {
        ByteBuffer buffer = ByteBuffer.allocate(TYPE_LEN + 1);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put((byte) UP_ACK_NEW_FILE);
        buffer.put((byte)code);
        buffer.flip();
        return new FilePacket(buffer);
    }

    public static FilePacket constructUpFileEndPacket(String digest) {
        byte[] bytes = digest.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(TYPE_LEN + 4 + bytes.length);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put((byte) UP_FILE_END);
        buffer.putInt(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        return new FilePacket(buffer);
    }

    public static FilePacket constructDownAckFileEndPacket(int code) {
        ByteBuffer buffer = ByteBuffer.allocate(TYPE_LEN + 1);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put((byte) UP_ACK_FILE_END);
        buffer.put((byte)code);
        buffer.flip();
        return new FilePacket(buffer);
    }
    public static FilePacket constructDownFileRequestPacket(int code) {
        ByteBuffer buffer = ByteBuffer.allocate(TYPE_LEN + 1);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put((byte) DOWN_FILE_REQUEST);
        buffer.put((byte) code);
        buffer.flip();
        return new FilePacket(buffer);
    }

    public static FilePacket parseByteBuffer(ByteBuffer buffer){
        FilePacket p = new FilePacket(buffer);
        p.parseType();
        return p;
    }

    public static String getUpFileServerPath(ByteBuffer buffer) {
        int length = buffer.getInt();
        byte[] bytes = new byte[length];
        buffer.get(bytes, 0 ,length);
        return new String(bytes);
    }

    public static String createFilePath(Context context) {
        String path = null;
        //            path = Environment.getExternalStorageDirectory().getCanonicalPath() + File.separator
//                    + context.getResources().getString(R.string.app_name) + File.separator + "Files";
        path = "/data/data/com.beidouapp/files";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        Log.d("TAG", "getCreateFilePath: " + path);
        return path;
    }

    public void   saveImageToGallery(Context context, byte[] data, String fileName) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        fileName = fileName + format.format(new Date())+".JPEG";
        // 保存图片至指定路径
        String storePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"LS" ;
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片(80代表压缩20%)
            boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
            // 其次把文件插入到系统图库
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //发送广播通知系统图库刷新数据
            System.out.println("发送广播通知系统图库刷新数据");
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

            if (isSuccess) {
                Toast.makeText(context,"图片已保存至"+file, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context,"图片保存失败",Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void parseType() {
        this.type = (int)this.buffer.get();
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public int getType() {
        return type;
    }

}
