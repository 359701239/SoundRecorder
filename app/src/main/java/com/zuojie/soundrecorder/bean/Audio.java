package com.zuojie.soundrecorder.bean;

/**
 * Created by zuojie on 2018/12/06.
 */
public class Audio implements Comparable<Audio> {

    public String name;
    public String fullName;
    public String date;
    public String path;
    private long lastModified;

    public Audio(String name, String date, String path, long lastModified) {
        this.name = name.substring(0, name.lastIndexOf("."));
        this.fullName = name;
        this.date = date;
        this.path = path;
        this.lastModified = lastModified;
    }

    public Audio(String path) {
        this.name = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
        this.fullName = path.substring(path.lastIndexOf("/") + 1);
        this.path = path;
    }

    @Override
    public int compareTo(Audio o) {
        return (int) (o.lastModified - lastModified);
    }
}
