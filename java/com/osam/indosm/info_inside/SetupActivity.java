package com.osam.indosm.info_inside;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SetupActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_layout);

        final SharedPreferences pref = getSharedPreferences("isSetup", Activity.MODE_PRIVATE);
        boolean isSetup = pref.getBoolean("isSetup", false);
        if (isSetup == true) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            //is Setuped
            Button regist_bt = findViewById(R.id.regist_bt);
            final TextView rank_tx = findViewById(R.id.rank_tx);
            final TextView name_tx = findViewById(R.id.name_tx);

            regist_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("rank", rank_tx.getText().toString());
                    editor.putString("name", name_tx.getText().toString());
                    editor.putBoolean("isSetup", true);
                    editor.commit();
                    startActivity(new Intent(SetupActivity.this, MainActivity.class));
                    finish();
                }
            });
        }

    }
}