package com.example.musicapp.screens;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;
import com.example.musicapp.Utils;
import com.example.musicapp.Viewable;
import com.example.musicapp.boundaries.Artist;
import com.example.musicapp.boundaries.Playlist;
import com.example.musicapp.boundaries.Song;
import com.example.musicapp.boundaries.User;
import com.example.musicapp.views.PlaylistListAdapter;
import com.example.musicapp.views.SongsListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements Viewable {
    private static final String TAG = "pttt";

    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private ListView main_LST_topSongs;
    private ListView main_LST_likedSongs;
    private TextView main_TXT_greeting;
    private User connectedUser;
    private List<Artist> artistsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.setFullScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initViews();
        getData();

    }

    @Override
    public void findViews() {
        this.main_LST_topSongs = findViewById(R.id.main_LST_topSongs);
        this.main_LST_likedSongs = findViewById(R.id.main_LST_likedSongs);
        this.main_TXT_greeting = findViewById(R.id.main_TXT_greeting);
    }

    @Override
    public void initViews() {
        main_TXT_greeting.setText("");
        createDummyData();
    }

    private void getData() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.connectedUser = new User().setUid(firebaseUser.getUid());

        database.collection("artists").get().addOnSuccessListener(artistsDocuments -> {
            this.artistsList = artistsDocuments.getDocuments().stream().map(x -> x.toObject(Artist.class)).collect(Collectors.toList());

            setUserUI(this.connectedUser);
        });
    }

    private void setUserUI(User connectedUser) {
        database.collection("users").document(connectedUser.getUid()).get().addOnSuccessListener(userDocument -> {
            connectedUser.setName(userDocument.getString("name"));
            setLikedSongsList(userDocument);
            setPlaylists(userDocument);
            this.main_TXT_greeting.setText(getGreetByDayTime() + ", " + this.connectedUser.getName());
        });
    }

    private void setLikedSongsList(DocumentSnapshot userDocument) {
        List<String> likedSongsIDs = (List<String>) userDocument.get("likedSongs");
        database.collection("songs").whereIn(FieldPath.documentId(), likedSongsIDs).get().addOnSuccessListener(likedSongs -> {
            likedSongs.forEach(likedSongRef ->
            {
                Song likedSong = getSong(likedSongRef);
                this.connectedUser.getLikedSongs().add(likedSong);
            });
            initLikedSongsList(this.connectedUser.getLikedSongs());

        });
    }

    private void setPlaylists(DocumentSnapshot userDocument) {
        List<String> playlistsID = (List<String>) userDocument.get("playlists");
        playlistsID.forEach(playlistID -> {
            database.collection("playlists").document(playlistID).get().addOnSuccessListener(playlistRef -> {
                Map<String, Object> playlistMap = playlistRef.getData();
                Playlist playlist = new Playlist().setName(playlistMap.get("name").toString());
                playlist.setId(playlistRef.getId());
                List<String> playlistSongs = (List<String>) playlistMap.get("songs");
                database.collection("songs").whereIn(FieldPath.documentId(), playlistSongs).get().addOnSuccessListener(playlistSongsRefs -> {
                    playlistSongsRefs.forEach(playlistSongRef -> {
                        playlist.getSongs().add(getSong(playlistSongRef));
                    });
                    this.connectedUser.getPlaylists().add(playlist);

                    this.main_LST_topSongs.setAdapter(new PlaylistListAdapter(this, this.connectedUser.getPlaylists()));
                });
            });
        });
    }

    private Song getSong(QueryDocumentSnapshot likedSongRef) {
        Song likedSong = likedSongRef.toObject(Song.class);
        likedSong.setArtist(artistsList.stream().filter(artist -> artist.getId().equals(likedSong.getArtistId())).findFirst().get());
        return likedSong;
    }


//    private void setUserSongsFromReferencesList(List<DocumentReference> refs, List<Song> songsList, Callable Next) {
//        refs.forEach(ref -> ref.get().addOnSuccessListener(command -> {
//            Song song = command.toObject(Song.class);
//            DocumentReference artistDocument = command.getDocumentReference("artist");
//            database.document(artistDocument.getPath()).get().addOnSuccessListener(documentSnapshot -> {
//                song.setArtist(documentSnapshot.toObject(Artist.class));
//                songsList.add(song);
//                try {
//                    Next.call();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//        }));
//    }


    private String getGreetByDayTime() {

        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)
        if (hour <= 5 || hour >= 20)
            return getString(R.string.good_night);
        else if (hour >= 16)
            return getString(R.string.good_afternoon);
        else
            return getString(R.string.hello);
    }

    private void initLikedSongsList(List<Song> songs) {
        SongsListAdapter adapter = new SongsListAdapter(this, songs);
        this.main_LST_likedSongs.setAdapter(adapter);
    }


    private void createDummyData() {
        createDummySongsAndArtists();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void createDummySongsAndArtists() {
//        final HashMap<String, Artist> artists = getArtistHashMap();
//        for (Map.Entry<String, Artist> artistEntry : artists.entrySet()) {
//            saveArtistInDB(artistEntry);
//        }
//        this.connectedUser.setLikedSongs(artists.get("queen").getSongs());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.connectedUser = new User().setUid(user.getUid()).setName("Niv Sorek");
        saveLikedSongsInDB(this.connectedUser);


    }

    private HashMap<String, Artist> getArtistHashMap() {
        final HashMap<String, Artist> artists = new HashMap<>();

        Artist queen = new Artist("Queen", "https://cdn.britannica.com/" +
                "38/200938-050-E22981D1/Freddie-Mercury-Live-Aid-Queen-Wembley-Stadium-July-13-1985.jpg");
        Artist ofer = new Artist("עופר לוי", "https://images.maariv.co.il/image/upload/f_auto,fl_lossy/c_fill,g_faces:center,h_792,w_900/477232");
        Artist beatles = new Artist("The Beatles", "https://upload.wikimedia.org/wikipedia/commons/d/df/The_Fabs.JPG");

        addSongToArtist(ofer, new Song("בצמאוני", true));
        addSongToArtist(ofer, new Song("מעיין הנעורים", true));
        addSongToArtist(ofer, new Song("רוח ים", true));

        addSongToArtist(queen, new Song("I Want To Break Free", false));
        addSongToArtist(queen, new Song("Radio Ga Ga", false));
        addSongToArtist(queen, new Song("Bohemian Rhapsody", false));

        addSongToArtist(beatles, new Song("Help!", true));
        addSongToArtist(beatles, new Song("Because", false));
        addSongToArtist(beatles, new Song("HeyJude", false));

        artists.put(queen.getId(), queen);
        artists.put(ofer.getId(), ofer);
        artists.put(beatles.getId(), beatles);
        return artists;
    }

    private void saveArtistInDB(Map.Entry<String, Artist> artistEntry) {
        database.collection("artists").document(artistEntry.getKey()).set(artistEntry.getValue().toMap()).addOnSuccessListener(aVoid -> {
            DocumentReference artistDocument = database.collection("artists").document(artistEntry.getKey());
            for (Song songEntry : artistEntry.getValue().getSongs()) {
                saveSongInDB(artistDocument, songEntry);
            }
        });
    }

    private void saveSongInDB(DocumentReference artistDocument, Song songEntry) {
        DocumentReference songDocument = database.collection("songs").document(songEntry.getId());

        songDocument.set(songEntry).addOnSuccessListener(aVoid1 -> songDocument.update("artist", artistDocument).addOnSuccessListener(aVoid2 -> artistDocument.update("songs", FieldValue.arrayUnion(songDocument))));
    }

    private void saveLikedSongsInDB(User user) {


        DocumentReference userDocument = database.collection("users").document(connectedUser.getUid());

        this.connectedUser.getLikedSongs().forEach((song) -> {
            DocumentReference songDocument = database.collection("songs").document(song.getId());
            userDocument.set(connectedUser.toMap()).addOnSuccessListener(command -> userDocument.update("likedSongs", FieldValue.arrayUnion(songDocument)));
        });
    }


    private void addSongToArtist(Artist artist, Song song) {
        song.setArtist(artist);
        artist.addSong(song);
    }
}