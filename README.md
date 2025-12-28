# Gestionnaire de métadonnées et playlists MP3

## Membres du projet
- Khoudja Laiza – Groupe B5
- Djermoune Thanina – Groupe B5

## Description
Application Java permettant :
- d’analyser les **métadonnées** de fichiers MP3 (titre, artiste, album, année, durée)
- de parcourir un dossier contenant des fichiers MP3
- de gérer une sélection de morceaux
- de générer des **playlists** aux formats **M3U8**, **XSPF** et **JSPF**

L’application propose :
- une interface **en ligne de commande (CLI)**
- une interface **graphique (GUI)**

## Structure du projet
- `src/` : code source Java
- `lib/` : bibliothèques externes (mp3agic)
- `doc/` : documentation Javadoc générée
- `README.md` : informations générales du projet

## Prérequis
- Java **JDK 17 ou supérieur**
- Bibliothèque **mp3agic** (présente dans le dossier `lib/`)

## Compilation et exécution

### Compilation
```bash
javac -classpath "lib/*" src/*.java

