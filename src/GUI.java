import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GUI extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextArea metaArea;
    private JComboBox<String> typeCombo;

    private List<FichierMP3> fichiersMP3 = new ArrayList<>();

    public GUI() {
        super("Créateur de playlist");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLayout(new BorderLayout());

        // ===== Haut : sélection dossier =====
        JButton selectFolderButton = new JButton("Sélectionner un dossier");
        selectFolderButton.addActionListener(e -> ouvrirDossier());
        add(selectFolderButton, BorderLayout.NORTH);

        // ===== Centre : table fichiers =====
        String[] colonnes = {"Inclure", "Nom du fichier", "Chemin"};
        tableModel = new DefaultTableModel(colonnes, 0) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(
                e -> afficherMetadonnees(table.getSelectedRow())
        );

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== Droite : métadonnées =====
        metaArea = new JTextArea();
        metaArea.setEditable(false);
        metaArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(metaArea), BorderLayout.EAST);

        // ===== Bas : export =====
        JPanel bottomPanel = new JPanel();

        typeCombo = new JComboBox<>(new String[]{"m3u8", "xspf", "jspf"});

        JButton exportSelectionButton =
                new JButton("Exporter la playlist sélectionnée");
        exportSelectionButton.addActionListener(e -> exporterPlaylistSelectionnee());

        JButton exportDefautButton =
                new JButton("Générer playlist par défaut");
        exportDefautButton.addActionListener(e -> exporterPlaylistParDefaut());

        bottomPanel.add(new JLabel("Type :"));
        bottomPanel.add(typeCombo);
        bottomPanel.add(exportSelectionButton);
        bottomPanel.add(exportDefautButton);

        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ======================================================
    // =================== MÉTHODES =========================
    // ======================================================

    private void ouvrirDossier() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            chargerMP3(chooser.getSelectedFile());
        }
    }

    private void chargerMP3(File dossier) {
        fichiersMP3.clear();
        tableModel.setRowCount(0);
        metaArea.setText("");

        ExplorateurRepertoire explorateur = new ExplorateurRepertoire();
        List<FichierMP3> fichiers = explorateur.explorerDossier(dossier.getAbsolutePath());

        for (FichierMP3 f : fichiers) {
            fichiersMP3.add(f);
            tableModel.addRow(new Object[]{
                    true,
                    new File(f.getChemin()).getName(),
                    f.getChemin()
            });
        }
    }

    private void afficherMetadonnees(int row) {
        if (row < 0 || row >= fichiersMP3.size()) return;

        FichierMP3 mp3 = fichiersMP3.get(row);
        MetaMP3 meta = mp3.extraireMetadonnees();

        metaArea.setText(
                "Fichier : " + mp3.getChemin() + "\n\n" +
                "Titre   : " + valeur(meta.getTitle()) + "\n" +
                "Artiste : " + valeur(meta.getArtist()) + "\n" +
                "Album   : " + valeur(meta.getAlbum()) + "\n" +
                "Année   : " + valeur(meta.getYear()) + "\n"
        );
    }

    private void exporterPlaylistSelectionnee() {
        List<FichierMP3> selection = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean inclure = (Boolean) tableModel.getValueAt(i, 0);
            if (inclure) selection.add(fichiersMP3.get(i));
        }

        if (selection.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun fichier sélectionné !");
            return;
        }

        sauvegarderPlaylist(selection, "PlaylistSelectionnee");
    }

    private void exporterPlaylistParDefaut() {
        if (fichiersMP3.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun fichier MP3 chargé !");
            return;
        }

        sauvegarderPlaylist(fichiersMP3, "PlaylistParDefaut");
    }

    private void sauvegarderPlaylist(List<FichierMP3> morceaux, String nom) {
        JFileChooser saver = new JFileChooser();
        saver.setDialogTitle("Choisir le fichier de sortie");

        if (saver.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String cheminSortie = saver.getSelectedFile().getAbsolutePath();
        String type = (String) typeCombo.getSelectedItem();

        Playlist playlist;
        switch (type) {
            case "m3u8": playlist = new PlaylistM3U8(nom); break;
            case "xspf": playlist = new PlaylistXSPF(nom); break;
            case "jspf": playlist = new PlaylistJSPF(nom); break;
            default:
                JOptionPane.showMessageDialog(this, "Type de playlist inconnu !");
                return;
        }

        for (FichierMP3 f : morceaux) {
            playlist.ajouterMorceau(f);
        }

        playlist.sauvegarder(cheminSortie);

        JOptionPane.showMessageDialog(this,
                "Playlist sauvegardée !\nNombre de morceaux : " + morceaux.size());
    }

    private String valeur(String s) {
        return (s == null || s.isEmpty()) ? "Inconnu" : s;
    }

    // ===== Main =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}

