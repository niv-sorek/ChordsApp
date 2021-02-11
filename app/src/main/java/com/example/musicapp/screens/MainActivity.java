package com.example.musicapp.screens;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;
import com.example.musicapp.Utils;
import com.example.musicapp.Viewable;
import com.example.musicapp.models.Artist;
import com.example.musicapp.models.Song;
import com.example.musicapp.models.User;
import com.example.musicapp.views.SongsListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Viewable {
    private static final String TAG = "pttt";
    private final ArrayList<Song> songs = new ArrayList<>();
    private final HashMap<String, Artist> artists = new HashMap<>();
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private ListView main_LST_topSongs;
    private ListView main_LST_likedSongs;
    private TextView main_TXT_greeting;
    private User connectedUser;

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
        // createDummyData();
    }

    private void getData() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.connectedUser = new User().setUid(firebaseUser.getUid());
        database.collection("users").document(firebaseUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                    Map<String, Object> data = document.getData();
                    connectedUser.setName(data.get("name").toString());
                    List<DocumentReference> refs = (List<DocumentReference>) data.get("likedSongs");
                    setUserSongsFromReferencesList(connectedUser, refs);
                    main_TXT_greeting.setText(getGreetByDayTime() + ", " + connectedUser.getName());

                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void setUserSongsFromReferencesList(User connectedUser, List<DocumentReference> refs) {
        refs.forEach(ref -> ref.get().addOnSuccessListener(command -> {
            Song song = command.toObject(Song.class);
            DocumentReference artistDocument = command.getDocumentReference("artist");
            database.document(artistDocument.getPath()).get().addOnSuccessListener(documentSnapshot -> {
                song.setArtist(documentSnapshot.toObject(Artist.class));
                this.connectedUser.getLikedSongs().add(song);
                initLikedSongsList();
            });

        }));
    }

    private void getSongsById(User user, HashMap<String, String> likedSongsIDS) {
        likedSongsIDS.entrySet().forEach(x -> database.collection("artists").whereArrayContains("songs", x.getValue()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                    Artist artist = snapshot.toObject(Artist.class);
                    Log.d(TAG, "getSongsById: " + artist);
                }
            }
        }));
    }

    private Song getSong(DocumentSnapshot document) {
        Song song = document.toObject(Song.class);

        return new Song();
    }


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

    private void initLikedSongsList() {
        SongsListAdapter adapter = new SongsListAdapter(this, this.connectedUser.getLikedSongs(), null);
        this.main_LST_likedSongs.setAdapter(adapter);
    }


    private void createDummyData() {
        createDummySongsAndArtists();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void createDummySongsAndArtists() {

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.connectedUser = new User().
                setUid(user.getUid()).setName("Niv Sorek");

        for (Map.Entry<String, Artist> artistEntry : artists.entrySet()) {

            database.collection("artists").document(artistEntry.getKey()).set(artistEntry.getValue().toMap()).addOnSuccessListener(aVoid -> {
                DocumentReference artistDocument = database.collection("artists").document(artistEntry.getKey());
                for (Song songEntry : artistEntry.getValue().getSongs()) {
                    DocumentReference songDocument = database.collection("songs").document(songEntry.getId());

                    songDocument.set(songEntry).addOnSuccessListener(aVoid1 -> songDocument.update("artist", artistDocument).addOnSuccessListener(aVoid2 -> artistDocument.update("songs", FieldValue.arrayUnion(songDocument))));
                }
            });
        }

        setLikedSongs(this.connectedUser, queen.getSongs());
    }

    private void setLikedSongs(User user, List<Song> songs) {

        user.setLikedSongs(songs);
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