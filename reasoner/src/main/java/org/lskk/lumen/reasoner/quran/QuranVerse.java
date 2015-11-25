package org.lskk.lumen.reasoner.quran;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by aina on 25/11/2015.
 */
@Entity
@Table(schema = "sanad")
public class QuranVerse implements Serializable {

    @Id
    private String id;
    private String canonicalSlug;
    private String name;
    private String slug;
    private String author;
    private String inLanguage;
    private Integer verseNum;
    private String chapter_id;
    private String text_id;
    private String textWithoutTashkeel_id;
    private String transliteration_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCanonicalSlug() {
        return canonicalSlug;
    }

    public void setCanonicalSlug(String canonicalSlug) {
        this.canonicalSlug = canonicalSlug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getInLanguage() {
        return inLanguage;
    }

    public void setInLanguage(String inLanguage) {
        this.inLanguage = inLanguage;
    }

    public Integer getVerseNum() {
        return verseNum;
    }

    public void setVerseNum(Integer verseNum) {
        this.verseNum = verseNum;
    }

    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
    }

    public String getText_id() {
        return text_id;
    }

    public void setText_id(String text_id) {
        this.text_id = text_id;
    }

    public String getTextWithoutTashkeel_id() {
        return textWithoutTashkeel_id;
    }

    public void setTextWithoutTashkeel_id(String textWithoutTashkeel_id) {
        this.textWithoutTashkeel_id = textWithoutTashkeel_id;
    }

    public String getTransliteration_id() {
        return transliteration_id;
    }

    public void setTransliteration_id(String transliteration_id) {
        this.transliteration_id = transliteration_id;
    }

    @Override
    public String toString() {
        return "QuranVerse{" +
                "id='" + id + '\'' +
                ", canonicalSlug='" + canonicalSlug + '\'' +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", author='" + author + '\'' +
                ", inLanguage='" + inLanguage + '\'' +
                ", verseNum=" + verseNum +
                ", chapter_id='" + chapter_id + '\'' +
                ", text_id='" + text_id + '\'' +
                ", textWithoutTashkeel_id='" + textWithoutTashkeel_id + '\'' +
                ", transliteration_id='" + transliteration_id + '\'' +
                '}';
    }
}