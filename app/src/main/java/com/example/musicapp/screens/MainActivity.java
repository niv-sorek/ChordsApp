package com.example.musicapp.screens;

import android.app.AlertDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.ListItem;
import com.example.musicapp.R;
import com.example.musicapp.Utils;
import com.example.musicapp.Viewable;
import com.example.musicapp.boundaries.Artist;
import com.example.musicapp.boundaries.Instrument;
import com.example.musicapp.boundaries.Playlist;
import com.example.musicapp.boundaries.Song;
import com.example.musicapp.boundaries.User;
import com.example.musicapp.views.PlaylistListAdapter;
import com.example.musicapp.views.SongsListAdapter;
import com.google.android.material.textfield.TextInputLayout;
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
import java.util.Optional;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements Viewable {
    private static final String TAG = "pttt";
    public static List<Artist> artistsList = new ArrayList<>();
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private ListView main_LST_topSongs;
    private ListView main_LST_likedSongs;
    private TextView main_TXT_greeting;
    private ImageView main_IMG_addPlaylist;
    private TextInputLayout main_INP_search;
    private User connectedUser;
    private LinearLayout main_LAY_greeting;
    private String newPlaylistName = "";
    private AutoCompleteTextView editText;

    public static Song getSong(QueryDocumentSnapshot likedSongRef) {
        Song likedSong = likedSongRef.toObject(Song.class);
        Optional<Artist> first = artistsList.stream().filter(artist -> artist.getId().equals(likedSong.getArtistId())).findFirst();
        first.ifPresent(likedSong::setArtist);
        return likedSong;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.setFullScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        getData();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        database.collection("users").document(this.connectedUser.getUid()).get().addOnSuccessListener(this::updateUserUI);
    }

    @Override
    public void findViews() {
        this.main_INP_search = findViewById(R.id.main_INP_search);
        this.main_LST_topSongs = findViewById(R.id.main_LST_topSongs);
        this.main_LST_likedSongs = findViewById(R.id.main_LST_likedSongs);
        this.main_TXT_greeting = findViewById(R.id.main_TXT_greeting);
        this.main_LAY_greeting = findViewById(R.id.main_LAY_greeting);
        this.main_IMG_addPlaylist = findViewById(R.id.main_IMG_addPlaylist);
    }

    @Override
    public void initViews() {
        main_TXT_greeting.setText("");


        this.editText = (AutoCompleteTextView) this.main_INP_search.getEditText();

        this.editText.setOnItemClickListener((parent, view, position, id) -> {
            Song selectedSong = (Song) ((ListItem) parent.getAdapter().getItem(position)).getData();
            Intent intent = new Intent(MainActivity.this, ShowSong.class);
            intent.putExtra("song", selectedSong.getId());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);

        });

        // createDummyData();
    }

    private void getData() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.connectedUser = new User().setUid(firebaseUser.getUid());

        database.collection("artists").get().addOnSuccessListener(artistsDocuments -> {
            artistsList = artistsDocuments.getDocuments().stream().map(x -> x.toObject(Artist.class)).collect(Collectors.toList());
            setUserUI();
        });


        this.main_IMG_addPlaylist.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.new_playlist);

            // Set up the input
            final EditText input = new EditText(this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                newPlaylistName = input.getText().toString();
                Playlist p = new Playlist().setName(newPlaylistName).setCreatorId(connectedUser.getUid());
                String id = database.collection("playlists").document().getId();
                database.collection("playlists").document(id).set(p);
                database.collection("users").document(connectedUser.getUid()).update("playlists", FieldValue.arrayUnion(id));
                updateUserUI();
            });
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

            builder.show();
        });

    }

    private void updateUserUI() {
        database.collection("users").document(connectedUser.getUid()).get().addOnSuccessListener(v-> updateUserUI(v));
    }

    private void setUserUI() {
        database.collection("songs").get().addOnSuccessListener(v -> {
            List<ListItem> collect = v.getDocuments().stream().map(x -> {
                Song song = getSong((QueryDocumentSnapshot) x);
                return new ListItem(song.getName() + " - " + song.getArtist().getName(), song);
            }).collect(Collectors.toList());
            this.editText.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, collect));
        });


        database.collection("users").document(this.connectedUser.getUid()).get().addOnSuccessListener(userDocument -> {

            ArrayList<Number> numbers = (ArrayList<Number>) userDocument.get("instruments");
            ArrayList<Integer> instruments = (ArrayList<Integer>) numbers.stream().map(Number::intValue).collect(Collectors.toList());
            this.connectedUser.setInstruments(instruments);
            this.connectedUser.setName(userDocument.getString("name"));
            this.main_TXT_greeting.setText(getString(R.string.greeting, getGreetByDayTime(), this.connectedUser.getName()));
            showInstrumentsIcons();


        });
    }

    private void updateUserUI(DocumentSnapshot userDocument) {
        setLikedSongsList();
        setPlaylists(userDocument);
    }

    private void showInstrumentsIcons() {

        for (int i = 0; i < this.connectedUser.getInstruments().size(); i++) {
            ImageView imageView = new ImageView(this);
            Log.d(TAG, "setUserUI: " + i);
            int instrumentIndex = this.connectedUser.getInstruments().get(i);
            imageView.setImageResource(Instrument.InstrumentsDrawables[instrumentIndex]);
            imageView.setContentDescription(getString(Instrument.InstrumentsNames[instrumentIndex]));
            main_LAY_greeting.addView(imageView, 70, 70);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            lp.setMarginEnd(25);
            imageView.setLayoutParams(lp);

        }
    }

    private void setLikedSongsList() {

        database.collection("songs").whereArrayContains("likes", this.connectedUser.getUid()).get().addOnSuccessListener(v -> {
            this.connectedUser.setLikedSongs(v.getDocuments().stream().map(x->getSong((QueryDocumentSnapshot) x)).collect(Collectors.toList()));
            initLikedSongsList(this.connectedUser.getLikedSongs());
        });
    }

    private void setPlaylists(DocumentSnapshot userDocument) {
        List<String> playlistsID = (List<String>) userDocument.get("playlists");
        if (playlistsID != null && playlistsID.size() > 0) {
            this.connectedUser.getPlaylists().clear();
            playlistsID.forEach(playlistID -> {
                try {
                    database.collection("playlists").document(playlistID).get().addOnSuccessListener(playlistRef -> {
                        Map<String, Object> playlistMap = playlistRef.getData();
                        Playlist playlist = new Playlist().setName(playlistMap.get("name").toString());
                        playlist.setId(playlistRef.getId());
                        //noinspection unchecked
                        List<String> playlistSongs = (List<String>) playlistMap.get("songs");

                        this.connectedUser.getPlaylists().add(playlist);
                        assert playlistSongs != null;
                        if (playlistSongs.size() > 0) {
                            database.collection("songs").whereIn(FieldPath.documentId(), playlistSongs).get().addOnSuccessListener(playlistSongsRefs -> {
                                playlist.setSongs(playlistSongsRefs.getDocuments().stream().map(x->getSong((QueryDocumentSnapshot) x)).collect(Collectors.toList()));
                                this.main_LST_topSongs.setAdapter(new PlaylistListAdapter(this, this.connectedUser.getPlaylists()));
                            });
                        }
                        this.main_LST_topSongs.setAdapter(new PlaylistListAdapter(this, this.connectedUser.getPlaylists()));

                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private String getGreetByDayTime() {

        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)
        if (hour <= 5 || hour >= 20) return getString(R.string.good_night);
        else if (hour >= 16) return getString(R.string.good_afternoon);
        else return getString(R.string.hello);
    }

    private void initLikedSongsList(List<Song> songs) {
        SongsListAdapter adapter = new SongsListAdapter(this, songs);
        this.main_LST_likedSongs.setAdapter(adapter);
    }


    private void createDummyData() {
        createDummySongsAndArtists();
    }

    private void createDummySongsAndArtists() {

        //        this.connectedUser = new User().setUid(user.getUid()).setName("Niv Sorek");
        saveLikedSongsInDB();
    }

    private HashMap<String, Artist> getArtistHashMap() {
        final HashMap<String, Artist> artists = new HashMap<>();

        Artist queen = new Artist("Queen", "https://cdn.britannica.com/" + "38/200938-050-E22981D1/Freddie-Mercury-Live-Aid-Queen-Wembley-Stadium-July-13-1985.jpg");
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

    private void saveLikedSongsInDB() {


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