import java.util.List;

/**
 * La classe CLI fournit une interface en ligne de commande pour :
 * <ul>
 *   <li>analyser un fichier MP3 et afficher ses métadonnées</li>
 *   <li>explorer un dossier contenant des MP3 et générer une playlist</li>
 * </ul>
 *
 * <p>
 * Modes disponibles :
 * </p>
 * <ul>
 *   <li><b>-f</b> : analyse un fichier MP3</li>
 *   <li><b>-d</b> : analyse un dossier et peut générer une playlist</li>
 * </ul>
 *
 * <p>
 * Formats de playlist (mode dossier) :
 * </p>
 * <ul>
 *   <li><b>--m3u8</b> (défaut)</li>
 *   <li><b>--xspf</b></li>
 *   <li><b>--jspf</b></li>
 * </ul>
 *
 * <p>
 * Option de sortie :
 * </p>
 * <ul>
 *   <li><b>-o &lt;fichier&gt;</b> : chemin du fichier de playlist généré</li>
 * </ul>
 *
 * @author nina
 * @version 1.0
 */
public class CLI {

    /**
     * Point d'entrée du programme.
     * Interprète les arguments et déclenche soit l'analyse d'un fichier MP3,
     * soit l'exploration d'un dossier et la génération d'une playlist.
     *
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            erreur("Aucun paramètre fourni.");
            return;
        }

        if (contient(args, "-h")) {
            afficherAide();
            return;
        }

        boolean fileMode = contient(args, "-f");
        boolean dirMode  = contient(args, "-d");

        if (fileMode == dirMode) {
            erreur("Vous devez utiliser soit -f soit -d (mais pas les deux).");
            return;
        }

        /* ========= MODE FICHIER ========= */
        if (fileMode) {
            String fichier = valeurApresOption(args, "-f");
            if (fichier == null) {
                erreur("Option -f utilisée sans fichier MP3.");
                return;
            }
            analyserFichierMP3(fichier);
        }

        /* ========= MODE DOSSIER ========= */
        if (dirMode) {
            String dossier = valeurApresOption(args, "-d");
            if (dossier == null) {
                erreur("Option -d utilisée sans dossier.");
                return;
            }

            String sortie = valeurApresOption(args, "-o");
            String type = detecterTypePlaylist(args);

            genererPlaylist(dossier, sortie, type);
        }
    }

    /**
     * Analyse un fichier MP3 et affiche ses métadonnées (titre, artiste, album, année, durée).
     *
     * @param chemin chemin du fichier MP3 à analyser
     */
    private static void analyserFichierMP3(String chemin) {
        System.out.println("Analyse du fichier MP3 : " + chemin);

        FichierMP3 mp3 = new FichierMP3(chemin);
        MetaMP3 meta = mp3.extraireMetadonnees();

        System.out.println("\n=== Métadonnées ===");
        System.out.println("Titre   : " + valeur(meta.getTitle()));
        System.out.println("Artiste : " + valeur(meta.getArtist()));
        System.out.println("Album   : " + valeur(meta.getAlbum()));
        System.out.println("Année   : " + valeur(meta.getYear()));
        System.out.println("Durée   : " + formatDuree(meta.getDuree()));
    }

    /**
     * Explore un dossier pour récupérer les fichiers MP3 et génère une playlist
     * dans le format demandé.
     *
     * @param dossier chemin du dossier à analyser
     * @param fichierSortie chemin du fichier de sortie (peut être {@code null} si non fourni)
     * @param typePlaylist type de playlist ("m3u8", "xspf" ou "jspf")
     */
    private static void genererPlaylist(String dossier,
                                        String fichierSortie,
                                        String typePlaylist) {

        ExplorateurRepertoire explorateur = new ExplorateurRepertoire();
        List<FichierMP3> fichiers = explorateur.explorerDossier(dossier);

        if (fichiers.isEmpty()) {
            System.out.println("Aucun fichier MP3 trouvé.");
            return;
        }

        Playlist playlist;

        switch (typePlaylist) {
            case "m3u8":
                playlist = new PlaylistM3U8("PlaylistAuto");
                break;
            case "xspf":
                playlist = new PlaylistXSPF("PlaylistAuto");
                break;
            case "jspf":
                playlist = new PlaylistJSPF("PlaylistAuto");
                break;
            default:
                erreur("Type de playlist inconnu.");
                return;
        }

        for (FichierMP3 mp3 : fichiers) {
            playlist.ajouterMorceau(mp3);
        }

        System.out.println("Nombre de fichiers MP3 : " + fichiers.size());
        System.out.println("Format de playlist     : " + typePlaylist);

        if (fichierSortie != null) {
            playlist.sauvegarder(fichierSortie);
            System.out.println("Playlist exportée : " + fichierSortie);
        } else {
            System.out.println("Aucun fichier de sortie spécifié (-o).");
        }
    }

    /**
     * Détecte le format de playlist à générer selon les options longues.
     * <p>
     * Par défaut, retourne "m3u8" si aucun format explicite n'est fourni.
     * </p>
     *
     * @param args arguments de la ligne de commande
     * @return "xspf", "jspf" ou "m3u8" (par défaut)
     */
    private static String detecterTypePlaylist(String[] args) {
        if (contient(args, "--xspf")) return "xspf";
        if (contient(args, "--jspf")) return "jspf";
        return "m3u8"; // défaut
    }

    /**
     * Indique si une option est présente dans la liste d'arguments.
     *
     * @param args tableau d'arguments
     * @param option option recherchée
     * @return {@code true} si l'option est présente, sinon {@code false}
     */
    private static boolean contient(String[] args, String option) {
        for (String s : args) {
            if (s.equals(option)) return true;
        }
        return false;
    }

    /**
     * Renvoie la valeur située juste après une option donnée.
     * <p>
     * Exemple : avec {@code -f song.mp3}, la valeur après {@code -f} est {@code song.mp3}.
     * </p>
     *
     * @param args tableau d'arguments
     * @param option option recherchée
     * @return la valeur après l'option, ou {@code null} si absente
     */
    private static String valeurApresOption(String[] args, String option) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(option)) {
                return args[i + 1];
            }
        }
        return null;
    }

    /**
     * Remplace une chaîne vide ou nulle par "Inconnu".
     *
     * @param s chaîne à vérifier
     * @return {@code s} si elle est non vide, sinon "Inconnu"
     */
    private static String valeur(String s) {
        return (s == null || s.isEmpty()) ? "Inconnu" : s;
    }

    /**
     * Formate une durée en secondes au format mm:ss.
     *
     * @param secondes durée en secondes
     * @return durée formatée en mm:ss, ou "Inconnue" si la durée est invalide
     */
    private static String formatDuree(long secondes) {
        if (secondes <= 0) return "Inconnue";
        long min = secondes / 60;
        long sec = secondes % 60;
        return String.format("%02d:%02d", min, sec);
    }

    /**
     * Affiche un message d'erreur standard et invite à utiliser l'aide.
     *
     * @param message message d'erreur
     */
    private static void erreur(String message) {
        System.out.println("Erreur : " + message);
        System.out.println("Utilisez -h pour afficher l'aide.");
    }

    /**
     * Affiche l'aide d'utilisation de la commande et des options disponibles.
     */
    private static void afficherAide() {
        System.out.println("UTILISATION :");
        System.out.println(" java CLI -f <fichier.mp3>");
        System.out.println(" java CLI -d <dossier> [--m3u8 | --xspf | --jspf] [-o fichier]");
        System.out.println();
        System.out.println("OPTIONS :");
        System.out.println(" -f        analyser un fichier MP3");
        System.out.println(" -d        analyser un dossier");
        System.out.println(" --m3u8    playlist M3U8 (défaut)");
        System.out.println(" --xspf    playlist XSPF");
        System.out.println(" --jspf    playlist JSPF");
        System.out.println(" -o        fichier de sortie");
        System.out.println(" -h        afficher l'aide");
        System.out.println();
        System.out.println("EXEMPLES :");
        System.out.println(" java CLI -f song.mp3");
        System.out.println(" java CLI -d Music --xspf -o playlist.xspf");
    }
}

