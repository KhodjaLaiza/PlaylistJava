import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.ID3v2;
import java.io.File;

public class MetaMP3 {
    private String title;
    private String artist;
    private String album;
    private String year;
    //private long duree;

    public MetaMP3(File mp3File) {
        try {
            Mp3File mp3 = new Mp3File(mp3File);
            if (mp3.hasId3v2Tag()) {
                ID3v2 tag = mp3.getId3v2Tag();
                this.title = tag.getTitle();
                this.artist = tag.getArtist();
                this.album = tag.getAlbum();
                this.year = tag.getYear();
                //this.duree = mp3.getLenghthInSeconds();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getters
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public String getYear() { return year; }
    //public long getDuree() { return duree; }

    public static void main(String[] args) {
    MetaMP3 meta = new MetaMP3(new java.io.File("test.mp3"));
    System.out.println(meta.getTitle());
    System.out.println(meta.getArtist());
    }
}
