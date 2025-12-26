public class TestPlaylist {
    public static void main(String[] args) {

        Playlist p = new PlaylistM3U8("TestPlaylist");

        System.out.println("Nombre de morceaux : " + p.getMorceaux().size());

        for (FichierMP3 f : p.getMorceaux()) {
            System.out.println(f.getChemin());
        }
    }
}
