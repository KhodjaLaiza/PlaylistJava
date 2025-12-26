import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PlaylistXSPF extends Playlist {

    public PlaylistXSPF(String nom) {
        super(nom);
    }

    @Override
    public void sauvegarder(String cheminSortie) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cheminSortie))) {

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<playlist version=\"1\" xmlns=\"http://xspf.org/ns/0/\">\n");
            writer.write("  <title>" + getNom() + "</title>\n");
            writer.write("  <trackList>\n");

            for (FichierMP3 mp3 : getMorceaux() ) {
                MetaMP3 meta = mp3.extraireMetadonnees();

                writer.write("    <track>\n");
                writer.write("      <location>file:///" + mp3.getChemin() + "</location>\n");
                writer.write("      <title>" + meta.getTitle() + "</title>\n");
                writer.write("      <creator>" + meta.getArtist() + "</creator>\n");
                writer.write("      <album>" + meta.getAlbum() + "</album>\n");
                writer.write("    </track>\n");
            }

            writer.write("  </trackList>\n");
            writer.write("</playlist>\n");

            System.out.println("Playlist XSPF sauvegardée.");

        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde XSPF");
            e.printStackTrace();
        }
    }
}
