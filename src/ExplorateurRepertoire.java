import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ExplorateurRepertoire {

    public ExplorateurRepertoire() {}

    public List<FichierMP3> explorerDossier(String cheminDossier) {
        File folder = new File(cheminDossier);
        List<FichierMP3> resultat = new ArrayList<>();

        explorerRecursif(folder, resultat);

        return resultat;
    }

    private void explorerRecursif(File folder, List<FichierMP3> liste) {

        if (!folder.exists() || !folder.isDirectory()) {
            return;
        }

        for (File file : folder.listFiles()) {

            if (file.isDirectory()) {
                explorerRecursif(file, liste);
            }

            else if (estMP3Valide(file)) {
                // On crée un FichierMP3 conforme à ton UML
                FichierMP3 mp3 = new FichierMP3(file.getAbsolutePath());
                liste.add(mp3);
            }
        }
    }

    private boolean estMP3Valide(File file) {
        try {
            if (!file.getName().toLowerCase().endsWith(".mp3")) {
                return false;
            }

            String mime = Files.probeContentType(file.toPath());
            return "audio/mpeg".equals(mime);

        } catch (Exception e) {
            return false;
        }
    }
}

