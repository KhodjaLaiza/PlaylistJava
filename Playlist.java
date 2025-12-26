import java.util.ArrayList;
import java.util.List;

public abstract class Playlist {

    private String nom;
    private List<FichierMP3> morceaux;

    public Playlist(String nom) {
        this.nom = nom;
        this.morceaux = new ArrayList<>();
    }

    public String getNom() {
        return nom;
    }

    public void ajouterMorceau(FichierMP3 mp3) {
        if (mp3 != null) {
            morceaux.add(mp3);
        }
    }

    public void retirerMorceau(FichierMP3 mp3) {
        morceaux.remove(mp3);
    }

    public List<FichierMP3> getMorceaux() {
        return morceaux;
    }


    public abstract void sauvegarder(String cheminSortie);
}
