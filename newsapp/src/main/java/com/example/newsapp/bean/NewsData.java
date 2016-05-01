package com.example.newsapp.bean;

public class NewsData {

    /**
     * 新闻的编号
     */
    public int id;
    /**
     * 新闻的编号
     */
    public int oid;
    /**
     * 新闻标题
     */
    public String subject;
    /**
     * 新闻内容摘要
     */
    public String summary;
    /**
     * 新闻图片地址
     */
    public String cover;
    /**
     * 新闻发布时间
     */
    public String changed;

    public NewsData(int id, int oid, String subject, String summary,
                    String cover, String changed) {
        this.id = id;
        this.oid = oid;
        this.subject = subject;
        this.summary = summary;
        this.cover = cover;
        this.changed = changed;
    }

    @Override
    public String toString() {
        return "NewsData [id=" + id + ", oid=" + oid + ", subject=" + subject
                + ", summary=" + summary + ", cover=" + cover + ", changed="
                + changed + "]";
    }
}
