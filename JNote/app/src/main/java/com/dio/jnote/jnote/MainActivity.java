package com.dio.jnote.jnote;

import dao.ObjectList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ObjectList> list = new ArrayList<>(); //CONTEUDO DO JSON
    private ObjectList auxObjectList;
    private ArrayList<String> auxStringList;
    private Stack<ObjectList> parentStack = new Stack<>();
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
        if(!list.isEmpty()) {
            auxStringList = new ArrayList<>();
            for (int i = 0; i < list.size() ; i++) {
                auxStringList.add(list.get(i).name);
            }
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, auxStringList);
        }
        else
            adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, new String[]{"//SEM CONTEÚDO//"});
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(viewContent(this));
    }

    private AdapterView.OnItemClickListener viewContent(final Context context) {
        return(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position, long id){
                if(list.get(position).type == 3){
                    Intent i = new Intent(context,ViewActivity.class);
                    String[] str = new String[2];
                    str[0]=list.get(position).name;
                    str[1]=list.get(position).value;
                    i.putExtra("NAME_VALUE",str);
                    startActivity(i);
                }else{
                    Intent i = new Intent(context,Main2Activity.class);
                    auxObjectList = list.get(position);
                    parentStack.push(auxObjectList);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode==1) {
            String[] res = data.getStringArrayExtra("VALUE");
            switch (res[2]) {
                case "Dado simples":
                    res[2] = "3";
                    break;
                case "Dado composto":
                    res[2] = "2";
                    break;
                case "Lista":
                    res[2] = "1";
                    break;
            }
            ObjectList auxObj = new ObjectList(res[0], res[1], Integer.parseInt(res[2]));
            list.add(auxObj);
            auxStringList = new ArrayList<>();
            for (int i = 0; i < list.size() ; i++) {
                auxStringList.add(list.get(i).name);
            }
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, auxStringList);
            lv.setAdapter(adapter);
        } else if(resultCode == 2){

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
