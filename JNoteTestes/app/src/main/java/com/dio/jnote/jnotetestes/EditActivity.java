package com.dio.jnote.jnotetestes;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {

    private Spinner sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        String[] content = new String[]{"Dado simples","Dado composto","Lista"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,content);
        sp = (Spinner) findViewById(R.id.spinner);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(redefineLayout(this));

        Button bt = (Button) findViewById(R.id.button);
        bt.setOnClickListener(addData());
    }

    private View.OnClickListener addData() {
        return(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent();
        String[] str;
        if (sp.getSelectedItem().toString().equals("Dado simples")) {
            EditText edt = (EditText) findViewById(R.id.editText);
            EditText edt2 = (EditText) findViewById(R.id.editText2);
            if ((!edt.getText().toString().equals("")) && (!edt2.getText().toString().equals(""))) {
                str = new String[]{edt.getText().toString(), edt2.getText().toString()};
                i.putExtra("VALUE", str);
                setResult(1, i);
            }else
                setResult(0);
        }
        else {
            EditText edt = (EditText) findViewById(R.id.editText);
            if (!edt.getText().toString().equals("")) {
                str = new String[]{edt.getText().toString(), " "};
                i.putExtra("VALUE", str);
                setResult(1, i);
            }else
                setResult(0);
        }
        super.onBackPressed();
    }

    private AdapterView.OnItemSelectedListener redefineLayout(final Context context) {
        return(new AdapterView.OnItemSelectedListener(){
            private boolean added=false;
            @Override
            public void onItemSelected(AdapterView<?> av, View v, int position, long id){
                if(position==0){
                    RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
                    LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    rl.addView(li.inflate(R.layout.alt_txtv_edit,rl,false),4);
                    rl.addView(li.inflate(R.layout.alt_edtxt_edit,rl,false),5);
                    added=true;
                }else{
                    if(added){
                        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
                        rl.removeViewAt(5);
                        rl.removeViewAt(4);
                        added=false;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> av){
            }
        });
    }
}
