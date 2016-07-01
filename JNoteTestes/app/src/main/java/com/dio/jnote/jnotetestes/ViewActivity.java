package com.dio.jnote.jnotetestes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Intent i = getIntent();
        String[] str = i.getStringArrayExtra("NAME_VALUE");
        String name = str[0];
        String content = str[1];

        setTitle(name);
        TextView txtV = (TextView)findViewById(R.id.textView5);
        txtV.setText(content);
    }
}
