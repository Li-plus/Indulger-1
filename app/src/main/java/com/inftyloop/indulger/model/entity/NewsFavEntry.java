package com.inftyloop.indulger.model.entity;

import android.annotation.SuppressLint;
import com.bumptech.glide.request.FutureTarget;
import com.inftyloop.indulger.MainApplication;
import com.inftyloop.indulger.util.FileUtils;
import com.inftyloop.indulger.util.GlideApp;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsFavEntry extends LitePalSupport {
    public NewsFavEntry() {
        super();
    }

    @SuppressLint("DefaultLocale")
    public NewsFavEntry(NewsEntry entry){
        super();
        publisherName = entry.getPublisherName();
        publisherAvatarUrl = entry.getPublisherAvatarUrl();
        publishTime = entry.getPublishTime();
        title = entry.getTitle();
        url = entry.getUrl();
        content = entry.getContent();
        category = entry.getCategory();
        keywords.addAll(entry.getKeywords());
        videoUrl = entry.getVideoUrl();
        markFavoriteTime = new Date().getTime();
        uuid = entry.getUuid();
        // download image to buffer
        for(int i = 0; i < entry.getImageUrls().size(); ++i) {
            String url = entry.getImageUrls().get(i);
            FutureTarget<File> res = GlideApp.with(MainApplication.getContext()).asFile().load(url).submit();
            try{
                File r = res.get();
                if(r == null)
                    continue;
                String dataDir = MainApplication.getContext().getDataDir().getAbsolutePath() + "/" + "favImgs";
                FileUtils.createDirs(dataDir);
                File dst = new File(dataDir, String.format("%s_%d", uuid, i));
                if(!dst.exists())
                    dst.createNewFile();
                FileUtils.copy(r, dst);
                imgUrls.add(String.format("http://127.0.0.1/%s:%d", uuid, i));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if(imgUrls.size() > 0)
            content = content.replaceFirst("src=\".*?\"", "src=\"" + imgUrls.get(0) + "\"");
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getPublisherAvatarUrl() {
        return publisherAvatarUrl;
    }

    public void setPublisherAvatarUrl(String publisherAvatarUrl) {
        this.publisherAvatarUrl = publisherAvatarUrl;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public long getMarkFavoriteTime() {
        return markFavoriteTime;
    }

    public void setMarkFavoriteTime(long markFavoriteTime) {
        this.markFavoriteTime = markFavoriteTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<String> getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(List<String> imgUrls) {
        this.imgUrls = imgUrls;
    }

    private String publisherName;
    private String publisherAvatarUrl;
    private long publishTime;
    private String title;
    private String url;
    private String content;
    private String category;
    private List<String> imgUrls = new ArrayList<>();
    private List<String> keywords = new ArrayList<>();
    private String videoUrl;
    private long markFavoriteTime;
    @Column(nullable = false, unique = true)
    private String uuid;
}
