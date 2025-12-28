import java.io.File;

/**
 * La classe FichierMP3 représente un fichier audio MP3 identifié
 * par son chemin sur le système de fichiers.
 * <p>
 * Elle permet d'extraire et de mettre en cache les métadonnées
 * associées au fichier via la classe {@link MetaMP3}.
 * </p>
 *
 * @author nina
 * @version 1.0
 */
public class FichierMP3 {

    /** Chemin du fichier MP3 */
    private String chemin;

    /** Métadonnées associées au fichier MP3 */
    private MetaMP3 metadonnees;

    /**
     * Construit un objet FichierMP3 à partir du chemin du fichier.
     *
     * @param chemin chemin vers le fichier MP3
     */
    public FichierMP3(String chemin) {
        this.chemin = chemin;
    }

    /**
     * Retourne le chemin du fichier MP3.
     *
     * @return le chemin du fichier
     */
    public String getChemin() {
        return chemin;
    }

    /**
     * Extrait les métadonnées du fichier MP3.
     * <p>
     * Les métadonnées sont mises en cache afin d'éviter
     * plusieurs lectures du même fichier.
     * </p>
     *
     * @return les métadonnées du fichier MP3
     */
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

