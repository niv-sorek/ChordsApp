package com.example.musicapp.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;
import com.example.musicapp.boundaries.Instrument;
import com.example.musicapp.boundaries.User;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupScreen extends AppCompatActivity {

    private Button signup_BTN_OK;
    private TextInputLayout signup_INP_name;
    private ChipGroup signup_CHP_instruments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        findViews();
        initViews();
    }

    private void findViews() {
        this.signup_CHP_instruments = findViewById(R.id.signup_CHP_instruments);
        this.signup_BTN_OK = findViewById(R.id.signup_BTN_OK);
        this.signup_INP_name = findViewById(R.id.signup_INP_name);

    }

    private void initViews() {
        for (int i = 0; i < Instrument.InstrumentsDrawables.length; i++) {
            Chip chip = new Chip(this);
            chip.setId(i);
            chip.setCheckable(true);
            chip.setText(getString(Instrument.InstrumentsNames[i]));
            chip.setChipIcon(getDrawable(Instrument.InstrumentsDrawables[i]));

            this.signup_CHP_instruments.addView(chip);
        }
        this.signup_CHP_instruments.setOnCheckedChangeListener((group, checkedId) ->
                Log.d("pttt", "initViews: "+checkedId));
        this.signup_BTN_OK.setOnClickListener(v -> {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            User connectedUser = new User().setUid(firebaseUser.getUid())
                    .setName(this.signup_INP_name.getEditText().getText().toString());
            connectedUser.getInstruments().addAll(this.signup_CHP_instruments.getCheckedChipIds());
            Intent intent = new Intent(SignupScreen.this, MainActivity.class);
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection("users").document(connectedUser.getUid()).set(connectedUser);
            startActivity(intent);
            finish();
        });


    }

}