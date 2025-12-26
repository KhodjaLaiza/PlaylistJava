import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PlaylistJSPF extends Playlist {

    public PlaylistJSPF(String nom) {
        super(nom);
    }

    @Override
    public void sauvegarder(String cheminSortie) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cheminSortie))) {

            writer.write("{\n");
            writer.write("  \"playlist\": {\n");
            writer.write("    \"title\": \"" + getNom() + "\",\n");
            writer.write("    \"track\": [\n");

            for (int i = 0; i < getMorceaux().size(); i++) {
                FichierMP3 mp3 = getMorceaux().get(i);
                MetaMP3 meta = mp3.extraireMetadonnees();

                writer.write("      {\n");
                writer.write("        \"location\": \"" + mp3.getChemin() + "\",\n");
                writer.write("        \"title\": \"" + meta.getTitle() + "\",\n");
                writer.write("        \"creator\": \"" + meta.getArtist() + "\",\n");
                writer.write("        \"album\": \"" + meta.getAlbum() + "\"\n");
                writer.write("      }");

                if (i < getMorceaux().size() - 1) {
                    writer.write(",");
                }
                writer.newLine();
            }
 
            writer.write("    ]\n");
            writer.write("  }\n");
            writer.write("}\n");

            System.out.println("Playlist JSPF sauvegardée.");

        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde JSPF");
            e.printStackTrace();
        }
    }
}
