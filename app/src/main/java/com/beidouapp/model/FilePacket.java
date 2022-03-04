package com.beidouapp.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FilePacket {
    public static final int UP_NEW_FILE = 0x00;                 //客户端发送文件请求
    public static final int UP_FILE_DATA = 0x01;                //客户端发送文件数据包
    public static final int UP_FILE_END = 0x02;                 //客户端发送文件结束

    public static final int UP_ACK_NEW_FILE = 0x00;             //服务器确认接受文件回执
    public static final int UP_ACK_FILE_END = 0x01;             //服务器接受文件结束回执

    public static final int DOWN_NEW_FILE = 0x04;               //客户端下载文件请求
    public static final int DOWN_FILE_REQUEST = 0x05;           //客户端确认开始下载回执
    public static final int DOWN_FILE_END = 0x06;               //客户端下载文件结束回执

    public static final int DOWN_ACK_FILE_REQUEST = 0x04;       //服务器确认发送请求
    public static final int DOWN_FILE_DATA = 0x05;              //服务器发送文件数据包
    public static final int DOWN_ACK_FILE_END = 0x06;           //服务器发送文件结束

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

    public static FilePacket constructAckFileEndPacket(int code) {
        ByteBuffer buffer = ByteBuffer.allocate(TYPE_LEN + 1);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put((byte) UP_ACK_FILE_END);
        buffer.put((byte)code);
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
