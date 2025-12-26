import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PlaylistM3U8 extends Playlist {

    public PlaylistM3U8(String nom) {
        super(nom);
    }

    @Override
    public void sauvegarder(String cheminSortie) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cheminSortie))) {

            writer.write("#EXTM3U");
            writer.newLine();

            for (FichierMP3 mp3 : getMorceaux()) {
                writer.write(mp3.getChemin());
                writer.newLine();
                
            }

            System.out.println("Playlist M3U8 sauvegardée.");

        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde M3U8");
            e.printStackTrace();
        }
    }
}
