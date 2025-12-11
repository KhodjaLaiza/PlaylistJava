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
        if (metadonnees == null) {
            File fichier = new File(chemin);

            if (fichier.exists() && fichier.isFile()) {
                metadonnees = new MetaMP3(fichier);
            } else {
                System.err.println("Fichier introuvable : " + chemin);
                metadonnees = new MetaMP3(null); // ou un objet vide
            }
        }
        return metadonnees;
    }
}
