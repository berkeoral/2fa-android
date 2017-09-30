package com.group11.blg439e.a2phase_auth;

/**
 * Created by berke on 10/1/2017.
 */
public class WebImageInformation {
    String url;
    String fileName;
    String contentType;
    String contentLength;
    String key;
    String keySize;
    String iv;
    String blockSize;
    String deletionKey;

    public WebImageInformation( String url, String fileName, String contentType, String contentLength, String key,
        String keySize, String iv, String blockSize, String deletionKey){
        this.url = url;
        this.fileName = fileName;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.key = key;
        this.keySize = keySize;
        this.iv = iv;
        this.blockSize = blockSize;
        this.deletionKey = deletionKey;
    }

    public WebImageInformation( String url, String fileName, String contentType, String contentLength,
                                String keySize, String blockSize){
        this.url = url;
        this.fileName = fileName;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.keySize = keySize;
        this.blockSize = blockSize;
    }

}
