import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class GUI extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextArea metaArea;
    private JComboBox<String> typeCombo;
    private JButton exportButton;
    private List<FichierMP3> fichiersMP3 = new ArrayList<>();

    public GUI(){
        super("Créateur de playlist");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLayout(new BorderLayout());

        // ===== Panneau supérieur : Sélection dossier =====
        JButton selectFolder = new JButton("Sélectionner un dossier");
        selectFolder.addActionListener(e -> ouvrirDossier());
        add(selectFolder, BorderLayout.NORTH);

        // ===== Centre : Table des fichiers =====
        String[] colonnes = {"Inclure", "Nom de fichier", "Chemin"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 0; // Seule la checkbox est éditable
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> afficherMetadonnees(table.getSelectedRow()));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== Droite : Zone métadonnées =====
        metaArea = new JTextArea(10, 20);
        metaArea.setEditable(false);
        add(new JScrollPane(metaArea), BorderLayout.EAST);

        // ===== Bas : Type playlist + Export =====
        JPanel bottomPanel = new JPanel();
        typeCombo = new JComboBox<>(new String[]{"m3u8", "xspf", "jspf"});
        exportButton = new JButton("Exporter la playlist");
        exportButton.addActionListener(e -> exporterPlaylist());
        bottomPanel.add(new JLabel("Type de playlist :"));
        bottomPanel.add(typeCombo);
        bottomPanel.add(exportButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // ===== Méthodes =====

    private void ouvrirDossier() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File dossier = chooser.getSelectedFile();
            chargerMP3(dossier);
        }
    }

    private void chargerMP3(File dossier) {
        fichiersMP3.clear();
        tableModel.setRowCount(0);

        ExplorateurRepertoire explorateur = new ExplorateurRepertoire();
        List<FichierMP3> fichiers = explorateur.explorerDossier(dossier.getAbsolutePath());

        for (FichierMP3 f : fichiers) {
            fichiersMP3.add(f);
            tableModel.addRow(new Object[]{true, f.getChemin(), f.getChemin()});
        }
    }

    private void afficherMetadonnees(int row) {
        if (row < 0 || row >= fichiersMP3.size()) return;

        FichierMP3 mp3 = fichiersMP3.get(row);
        MetaMP3 meta = mp3.extraireMetadonnees();

        StringBuilder sb = new StringBuilder();
        sb.append("Fichier : ").append(mp3.getChemin()).append("\n");
        sb.append("Titre   : ").append(valeur(meta.getTitle())).append("\n");
        sb.append("Artiste : ").append(valeur(meta.getArtist())).append("\n");
        sb.append("Album   : ").append(valeur(meta.getAlbum())).append("\n");
        sb.append("Année   : ").append(valeur(meta.getYear())).append("\n");

        metaArea.setText(sb.toString());
    }

    private void exporterPlaylist() {
        List<FichierMP3> selection = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            boolean inclure = (Boolean) tableModel.getValueAt(i, 0);
            if (inclure) selection.add(fichiersMP3.get(i));
        }

        if (selection.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun fichier sélectionné !");
            return;
        }

        JFileChooser saver = new JFileChooser();
        saver.setDialogTitle("Choisir le fichier de sortie");
        int result = saver.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        String cheminSortie = saver.getSelectedFile().getAbsolutePath();
        String type = (String) typeCombo.getSelectedItem();

        Playlist playlist;
        switch (type.toLowerCase()) {
            case "m3u8": playlist = new PlaylistM3U8("MaPlaylist"); break;
            case "xspf": playlist = new PlaylistXSPF("MaPlaylist"); break;
            case "jspf": playlist = new PlaylistJSPF("MaPlaylist"); break;
            default: JOptionPane.showMessageDialog(this, "Type de playlist inconnu !"); return;
        }

        for (FichierMP3 f : selection) {
            playlist.ajouterMorceau(f);
        }

        playlist.sauvegarder(cheminSortie);
        JOptionPane.showMessageDialog(this, "Playlist sauvegardée : " + cheminSortie);
    }

    private String valeur(String s) {
        return (s == null || s.isEmpty()) ? "Inconnu" : s;
    }

    // ===== Main =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI());
    }
}
