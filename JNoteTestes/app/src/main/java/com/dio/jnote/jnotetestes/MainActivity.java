package com.dio.jnote.jnotetestes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> names = new ArrayList<>(); //CONTEUDO DO JSON
    private ArrayList<String> content = new ArrayList<>();
    private ListView lv;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,EditActivity.class);
                startActivityForResult(i,1);
            }
        });

        lv = (ListView)findViewById(R.id.listView);
        if(!names.isEmpty())
            adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, names);
        else
            adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, new String[]{"//SEM CONTEÚDO//"});
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(viewContent(this));
    }

    private AdapterView.OnItemClickListener viewContent(final Context context) {
        return(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position, long id){
                if(!content.get(position).equals(" ")){
                    Intent i = new Intent(context,ViewActivity.class);
                    String[] str = new String[2];
                    str[0]=names.get(position);
                    str[1]=content.get(position);
                    i.putExtra("NAME_VALUE",str);
                    startActivity(i);
                }else{
                    //CASO DE OBJETO COMPOSTO OU LISTA
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode==1) {
            String[] res = data.getStringArrayExtra("VALUE");
            if (names.isEmpty())
                names.add(0, res[0]);
            else
                names.add(names.size(), res[0]);
            if (content.isEmpty())
                content.add(0, res[1]);
            else
                content.add(content.size(), res[1]);
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
            lv.setAdapter(adapter);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
