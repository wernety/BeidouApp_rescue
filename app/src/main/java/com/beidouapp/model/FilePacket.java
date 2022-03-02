package com.beidouapp.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class FilePacket {
    public static final int P_NEW_FILE = 0x01;
    public static final int P_ACK_NEW_FILE = 0x02;
    public static final int P_FILE_DATA = 0x03;
    public static final int P_FILE_END = 0x04;
    public static final int P_ACK_FILE_END = 0x05;

    public static final int SUCCESS_CODE = 0;
    public static final int ERROR_CODE = -1;

    private static final int TYPE_LEN = 1;

    private int type;

    private final ByteBuffer buffer;

    public FilePacket(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public static FilePacket constructNewFilePacket(String fileName) {
        byte[] bytes = fileName.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(TYPE_LEN + 4 + bytes.length);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put((byte)P_NEW_FILE);
        buffer.putInt(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        return new FilePacket(buffer);
    }

    public static FilePacket constructNewFilePacket() {
        ByteBuffer buffer = ByteBuffer.allocate(TYPE_LEN);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put((byte)P_NEW_FILE);
        buffer.flip();
        return new FilePacket(buffer);
    }

    public static FilePacket constructAckNewFilePacket(int code) {
        ByteBuffer buffer = ByteBuffer.allocate(TYPE_LEN + 1);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put((byte)P_ACK_NEW_FILE);
        buffer.put((byte)code);
        buffer.flip();
        return new FilePacket(buffer);
    }

    public static FilePacket constructFileEndPacket(String digest) {
        byte[] bytes = digest.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(TYPE_LEN + 4 + bytes.length);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put((byte)P_FILE_END);
        buffer.putInt(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        return new FilePacket(buffer);
    }

    public static FilePacket constructAckFileEndPacket(int code) {
        ByteBuffer buffer = ByteBuffer.allocate(TYPE_LEN + 1);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put((byte)P_ACK_FILE_END);
        buffer.put((byte)code);
        buffer.flip();
        return new FilePacket(buffer);
    }

    public static FilePacket parseByteBuffer(ByteBuffer buffer){
        FilePacket p = new FilePacket(buffer);
        p.parseType();
        return p;
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
