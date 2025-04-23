package appnghenhac.com.model;

import java.io.Serializable;

public class Song implements Serializable {
    private long id; // Thêm trường id
    private String title;
    private String artist;
    private String album;
    private String genre;
    private String audioUri;
    private String coverUri;

    public Song(long id, String title, String artist, String album, String genre, String audioUri, String coverUri) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.audioUri = audioUri;
        this.coverUri = coverUri;
    }

    public Song(String title, String artist, String album, String genre, String audioUri, String coverUri) {
        this.id = -1; // -1 nghĩa là chưa có id (sẽ được gán sau khi chèn vào DB)
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.audioUri = audioUri;
        this.coverUri = coverUri;
    }

    // Getter và Setter cho id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // Các getter và setter khác giữ nguyên
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAudioUri() {
        return audioUri;
    }

    public void setAudioUri(String audioUri) {
        this.audioUri = audioUri;
    }

    public String getCoverUri() {
        return coverUri;
    }

    public void setCoverUri(String coverUri) {
        this.coverUri = coverUri;
    }


}
