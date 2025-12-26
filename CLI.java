import java.util.List;

public class CLI {

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

    /* =======================
       MODE FICHIER (-f)
       ======================= */
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

    /* =======================
       MODE DOSSIER (-d)
       ======================= */
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

    /* =======================
       OPTIONS LONGUES
       ======================= */
    private static String detecterTypePlaylist(String[] args) {
        if (contient(args, "--xspf")) return "xspf";
        if (contient(args, "--jspf")) return "jspf";
        return "m3u8"; // défaut
    }

    /* =======================
       OUTILS
       ======================= */
    private static boolean contient(String[] args, String option) {
        for (String s : args) {
            if (s.equals(option)) return true;
        }
        return false;
    }

    private static String valeurApresOption(String[] args, String option) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(option)) {
                return args[i + 1];
            }
        }
        return null;
    }

    private static String valeur(String s) {
        return (s == null || s.isEmpty()) ? "Inconnu" : s;
    }

    private static String formatDuree(long secondes) {
        if (secondes <= 0) return "Inconnue";
        long min = secondes / 60;
        long sec = secondes % 60;
        return String.format("%02d:%02d", min, sec);
    }

    private static void erreur(String message) {
        System.out.println("Erreur : " + message);
        System.out.println("Utilisez -h pour afficher l'aide.");
    }

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
