package appnghenhac.com;

public class Song {
    private String title;
    private String artist;
    private String album;
    private String genre;
    private String audioUri;
    private String coverUri;  // Lưu đường dẫn ảnh bìa

    public Song(String title, String artist, String album, String genre, String audioUri, String coverUri) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.audioUri = audioUri;
        this.coverUri = coverUri;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAudioUri(String audioUri) {
        this.audioUri = audioUri;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    // Getters và Setters
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public String getGenre() { return genre; }
    public String getAudioUri() { return audioUri; }
    public String getCoverUri() { return coverUri; }

    public void setCoverUri(String coverUri) {
        this.coverUri = coverUri;
    }
}
