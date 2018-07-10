package com.example.root.sender;

import java.io.Serializable;


public class FileBean implements Serializable {

    public static final String serialVersionUID = "6321689524634663223356";

    public String filePath;

    public long fileLength;



    public FileBean(String filePath, long fileLength) {
        this.filePath = filePath;
        this.fileLength = fileLength;
    }
}
