package com.dio.jnote.jnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Igor on 07/07/2016.
 */
public class Main2Activity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBackPressed();
    }

    @Override
    public void onBackPressed(){
        Intent i = getIntent();
        Intent i2 = new Intent(this,MainActivity.class);
        i2.putExtra("WAY",i.getStringArrayExtra("WAY"));
        //setResult(2,i);
        startActivity(i2);
        super.onBackPressed();
    }
}
