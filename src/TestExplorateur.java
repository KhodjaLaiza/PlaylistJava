public class TestExplorateur {
    public static void main(String[] args){

        ExplorateurRepertoire ex = new ExplorateurRepertoire();

        // 👉 Mets ici un dossier qui contient des fichiers .mp3
        String dossier = "C:/Users/khodj/Documents/Java/Projet/mp3projet/src/Undertale";

        System.out.println("Exploration du dossier : " + dossier);

        var mp3s = ex.explorerDossier(dossier);

        System.out.println("Nombre de MP3 trouvés : " + mp3s.size());

        for (FichierMP3 f : mp3s) {
            System.out.println("--------------------------------");
            System.out.println("Fichier : " + f.getChemin());

            // extraction des métadonnées
            var meta = f.extraireMetadonnees();

            System.out.println("Titre   : " + meta.getTitle());
            System.out.println("Artiste : " + meta.getArtist());
            System.out.println("Album   : " + meta.getAlbum());
            System.out.println("Année   : " + meta.getYear());
        }
    }
}
