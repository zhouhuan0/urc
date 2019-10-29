package com.yks.urc.excel;

public class FileDataVO {
	private String filename;
    private String extension;
    private String contentType;
    private String path;
    private String awsPath;
    private String size;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAwsPath() {
        return awsPath;
    }

    public void setAwsPath(String awsPath) {
        this.awsPath = awsPath;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
