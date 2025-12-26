import com.mpatric.mp3agic.*;
import java.io.File;

public class MetaMP3 {

    private String title;
    private String artist;
    private String album;
    private String year;
    private long duree; // en secondes

    // ===== Constructeur principal =====
    public MetaMP3(File mp3File) {

        try {
            Mp3File mp3 = new Mp3File(mp3File);

            // ----- ID3v2 -----
            if (mp3.hasId3v2Tag()) {
                ID3v2 tag = mp3.getId3v2Tag();

                title  = tag.getTitle();
                artist = tag.getArtist();
                album  = tag.getAlbum();

                year = tag.getYear();
                if (year == null || year.isEmpty()) {
                    year = tag.getDate();
                }
            }
            // ----- ID3v1 -----
            else if (mp3.hasId3v1Tag()) {
                ID3v1 tag = mp3.getId3v1Tag();

                title  = tag.getTitle();
                artist = tag.getArtist();
                album  = tag.getAlbum();
                year   = tag.getYear();
            }

            // ----- Durée -----
            duree = mp3.getLengthInSeconds();

        } catch (Exception e) {
            // Fichier invalide ou erreur de lecture
            title = artist = album = year = null;
            duree = 0;
        }
    }

    // ===== Constructeur privé (objet vide) =====
    private MetaMP3() {
        title = artist = album = year = null;
        duree = 0;
    }

    // ===== Factory : métadonnées vides =====
    public static MetaMP3 vide() {
        return new MetaMP3();
    }

    // ===== Getters =====
    public String getTitle()  { return title; }
    public String getArtist(){ return artist; }
    public String getAlbum() { return album; }
    public String getYear()  { return year; }
    public long getDuree()   { return duree; }
}
