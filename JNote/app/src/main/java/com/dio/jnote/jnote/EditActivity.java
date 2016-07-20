package com.dio.jnote.jnote;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

public class EditActivity extends AppCompatActivity {

    private Spinner sp;
    private String[] values;
    private EditText edt;
    private EditText edt2;
    private boolean addClicked =false;
    private int edit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        String[] content = new String[]{"Dado simples","Dado composto","Lista"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,content);

        sp = (Spinner) findViewById(R.id.spinner);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(redefineLayout(this));

        edt = (EditText) findViewById(R.id.editText);
        edt2 = (EditText) findViewById(R.id.editText2);

        Button bt = (Button) findViewById(R.id.button);
        bt.setOnClickListener(addData());

        Intent i = getIntent();
        values = i.getStringArrayExtra("VALUESVECTOR");
        if(values!=null) {
            edit = 1;
            edt.setText(values[0]);
            if (values[1] == "1") {
                edt2.setText(values[2]);
            }
            sp.setSelection(Integer.parseInt(values[1]) - 1);
        }

    }

    private View.OnClickListener addData() {
        return(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addClicked =true;
                onBackPressed();
            }
        });
    }

    @Override //REMOVER ESTA FUNÇÃO.
    public void onBackPressed(){
        Intent i = new Intent();
        String[] str = new String[]{};
        //EditText edtaux = edt;
        //EditText edtaux2 = edt;

        if (sp.getSelectedItem().toString().equals("Dado simples")) {
            if ((!edt.getText().toString().equals("")) && (!edt2.getText().toString().equals(""))) {
                str = new String[]{edt.getText().toString(), edt2.getText().toString(), sp.getSelectedItem().toString()};
                i.putExtra("VALUE", str);
                i.putExtra("EDIT" , edit);
                setResult(1, i);
            }else
                setResult(0);
        }
        else {
            if (!edt.getText().toString().equals("")) {
                str = new String[]{edt.getText().toString(), " " , sp.getSelectedItem().toString()};
                i.putExtra("VALUE", str);
                i.putExtra("EDIT" , edit);
                setResult(1, i);
            }else
                setResult(0);
        }
        if(!addClicked){
            i = new Intent();
            setResult(0);
            this.finish();
        }
        super.onBackPressed();
    }

    private AdapterView.OnItemSelectedListener redefineLayout(final Context context) {
        return(new AdapterView.OnItemSelectedListener(){
            private boolean added=false;
            @Override
            public void onItemSelected(AdapterView<?> av, View v, int position, long id){
                if(position==0 || (values != null && values[1].equals("1"))) {
                    RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
                    LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    rl.addView(li.inflate(R.layout.alt_txtv_edit, rl, false), 4);
                    rl.addView(li.inflate(R.layout.alt_edtxt_edit, rl, false), 5);
                    added = true;
                    edt2 = (EditText) findViewById(R.id.editText2);
                    if(values != null){
                        edt2.setText(values[2]);
                    }
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
