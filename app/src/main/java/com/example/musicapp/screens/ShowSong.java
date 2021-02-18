package com.example.musicapp.screens;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.musicapp.ChordsUtils;
import com.example.musicapp.R;
import com.example.musicapp.Utils;
import com.example.musicapp.Viewable;
import com.example.musicapp.boundaries.Artist;
import com.example.musicapp.boundaries.Song;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicInteger;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class ShowSong extends AppCompatActivity implements Viewable {

    private float mLastTouchX;
    private float mLastTouchY;
    private int mActivePointerId = INVALID_POINTER_ID;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private float scale = 2f;

    private TextView song_TXT_songName;
    private TextView song_TXT_artistName;
    private LinearLayout song_LAY_chords;
    private LinearLayout song_LAY_details;
    private ProgressBar song_PRG_loading;
    private ImageView song_ICN_like;
    private TextView song_TXT_likesCount;
    private TextView song_TXT_minus;
    private TextView song_TXT_plus;
    private TextView song_TXT_tones;
    private int tones = 0;
    private Song song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.setFullScreen(this);
        super.onCreate(savedInstanceState);
        findViews();
        initViews();
        mScaleDetector = new ScaleGestureDetector(this.song_TXT_songName.getContext(), new ScaleListener());
    }

    @Override
    public void findViews() {
        this.song_LAY_details = findViewById(R.id.song_LAY_details);
        this.song_LAY_chords = findViewById(R.id.song_LAY_chords);
        this.song_TXT_songName = findViewById(R.id.song_TXT_songName);
        this.song_TXT_artistName = findViewById(R.id.song_TXT_artistName);
        this.song_ICN_like = findViewById(R.id.song_ICN_like);
        this.song_TXT_likesCount = findViewById(R.id.song_TXT_likesCount);
        this.song_PRG_loading = findViewById(R.id.song_PRG_loading);
        this.song_TXT_minus = findViewById(R.id.song_TXT_minus);
        this.song_TXT_plus = findViewById(R.id.song_TXT_plus);
        this.song_TXT_tones = findViewById(R.id.song_TXT_tones);
    }

    @Override
    public void initViews() {

        this.song_LAY_details.setVisibility(View.INVISIBLE);
        String songId = getIntent().getStringExtra("song");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.document("songs/" + songId).get().addOnSuccessListener(songSnapshot -> {
            this.song = songSnapshot.toObject(Song.class);
            song_TXT_songName.setText(song.getName());
            FirebaseAuth fbUser = FirebaseAuth.getInstance();
            setLikeIcon(fbUser);
            this.song_ICN_like.setOnClickListener(v -> {
                song.toggleLike(fbUser.getUid());
                database.collection("songs").document(songId).update("likes", song.getLikes());
                setLikeIcon(fbUser);
            });
            song_LAY_details.setLayoutDirection(song.isRtl() ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
            if (this.song.getChords() != null && this.song.getChords().length() > 0) {
                updateChords();
            }

            String artistId = songSnapshot.getString("artistId");
            database.collection("artists").document(artistId).get().addOnSuccessListener(artistSnapshot -> {
                song.setArtist(artistSnapshot.toObject(Artist.class));
                song_TXT_artistName.setText(song.getArtist().getName());
                this.song_TXT_artistName.setOnClickListener(v -> {
                    Intent intent = new Intent(this, ShowArtist.class);
                    intent.putExtra("artistId", artistId);
                    this.startActivity(intent);
                });

                this.song_PRG_loading.setVisibility(View.INVISIBLE);
                this.song_LAY_details.setVisibility(View.VISIBLE);
            });
        });

        this.song_TXT_minus.setOnClickListener(v -> {
            this.tones--;
            updateChords();
        });
        this.song_TXT_plus.setOnClickListener(v -> {
            this.tones++;
            updateChords();
        });
    }

    private void setLikeIcon(@NotNull FirebaseAuth fbUser) {
        Drawable likeIcon = getDrawable(this.song.isUserLiked(fbUser.getUid()) ? R.drawable.ic_like_clicked : R.drawable.ic_like);
        this.song_ICN_like.setImageDrawable(likeIcon);
        this.song_TXT_likesCount.setText("" + this.song.getRank());

    }


    private void updateChords() {
        this.song_TXT_tones.setText("" + tones);
        this.song_LAY_chords.removeAllViewsInLayout();
        AtomicInteger index = new AtomicInteger();
        ChordsUtils.formatChordsString(this.song.getChords(), "_", tones).forEach(s -> {
            TextView textView = new TextView(this);
            textView.setTextSize(18);
            if (index.get() % 2 == 0) textView.setTextColor(Color.BLUE);
            textView.setText(s);
            Typeface typeface = ResourcesCompat.getFont(this, R.font.assistant);
            textView.setTypeface(typeface);
            song_LAY_chords.addView(textView);
            index.getAndIncrement();
        });
        this.song_LAY_chords.setOnLongClickListener(v -> {
            updateChordsFontSize(scale);
            scale = scale == 1f ? 2f : 1f;
            return true;
        });
    }

    private void updateChordsFontSize(float mScaleFactor) {
        Log.d("pttt", "updateChordsFontSize: " + mScaleFactor);
        for (int i = 0; i < song_LAY_chords.getChildCount(); i++) {
            TextView child = (TextView) (song_LAY_chords.getChildAt(i));
            child.setTextSize(24 * mScaleFactor);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();

                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = ev.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress()) {
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    //                    invalidate();
                }

                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }

            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }

    // Update Text Size by pinch and zoom
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(-3.0f, Math.min(mScaleFactor, 5.0f));
            Log.d("pttt", "onScale: " + mScaleFactor);

            updateChordsFontSize(mScaleFactor);
            return true;
        }
    }
}