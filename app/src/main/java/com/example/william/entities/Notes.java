package com.example.william.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "NoteTable")
public class Notes implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "datetime")
    private String datetime;
    @ColumnInfo(name = "subtilte")
    private String subtilte;
    @ColumnInfo(name = "content")
    private String content;
    @ColumnInfo(name = "colorr")
    private String colorr;
    @ColumnInfo(name = "imgphoto")
    private String imgphoto;
    @ColumnInfo(name = "weblinkk")
    private String weblinkk;


    public String getImgphoto() {
        return imgphoto;
    }

    public void setImgphoto(String imgphoto) {
        this.imgphoto = imgphoto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getSubtilte() {
        return subtilte;
    }

    public void setSubtilte(String subtilte) {
        this.subtilte = subtilte;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getColorr() {
        return colorr;
    }

    public void setColorr(String colorr) {
        this.colorr = colorr;
    }

    public String getWeblinkk() {
        return weblinkk;
    }

    public void setWeblinkk(String weblinkk) {
        this.weblinkk = weblinkk;
    }
}
