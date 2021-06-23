package hn.techcom.com.hnapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class File {
    @SerializedName("media")
    @Expose
    private String media;
    @SerializedName("aspect")
    @Expose
    private String aspect;

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getAspect() {
        return aspect;
    }

    public void setAspect(String aspect) {
        this.aspect = aspect;
    }
}