import java.io.File;

public class FichierMP3 {

    private String chemin;
    private MetaMP3 metadonnees;

    public FichierMP3(String chemin) {
        this.chemin = chemin;
    }

    public String getChemin() {
        return chemin;
    }

    public MetaMP3 extraireMetadonnees() {

        // Cache : on lit une seule fois
        if (metadonnees != null) {
            return metadonnees;
        }

        File fichier = new File(chemin);

        if (!fichier.exists() || !fichier.isFile()) {
            System.err.println("Fichier introuvable : " + chemin);
            metadonnees = MetaMP3.vide();
            return metadonnees;
        }

        metadonnees = new MetaMP3(fichier);
        return metadonnees;
    }
}
