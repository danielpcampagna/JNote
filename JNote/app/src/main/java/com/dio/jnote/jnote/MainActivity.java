package com.dio.jnote.jnote;

import dao.ObjectList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ObjectList> list = new ArrayList<>(); //CONTEUDO DO JSON
    private ArrayList<ObjectList> auxObjectList = new ArrayList<>();
    private ArrayList<String> auxStringList;
    private Stack<ObjectList> parentStack = new Stack<>();
    private ListView lv;
    private ArrayAdapter<String> adapter;
    private int positionObj = -1;

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
        registerForContextMenu(lv);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listView) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            positionObj = acmi.position;
            menu.setHeaderTitle(lv.getItemAtPosition(acmi.position).toString());
            menu.add(0, v.getId(), 0, "Editar");
            menu.add(0, v.getId(), 0, "Remover");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle()=="Editar"){
            ObjectList obj = list.get(positionObj);
            Intent i = new Intent(MainActivity.this,EditActivity.class);
            String [] values = {obj.name, String.valueOf(obj.type) ,obj.value};
            i.putExtra("VALUESVECTOR", values);
            startActivityForResult(i,1);
        }
        else if(item.getTitle()=="Remover") {
            list.remove(positionObj);
            auxStringList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                auxStringList.add(list.get(i).name);
            }
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, auxStringList);
            lv.setAdapter(adapter);
        }
        else {return false;}
        return true;
    }

    private AdapterView.OnItemClickListener viewContent(final Context context) {
        System.out.println("entrou aki");
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
                    if(list.get(position).childList.isEmpty()) {
                        auxObjectList = new ArrayList<>();
                    }else{
                        auxObjectList = list.get(position).childList;
                    }
                    parentStack.push(list.get(position));
                    startActivityForResult(i,2);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode==1) {
            String[] res = data.getStringArrayExtra("VALUE");
            boolean edit = data.getBooleanExtra("EDIT", true);
            if(!edit) {
                positionObj = -1;
            }
            switch (res[2]) {
                case "Dado simples":
                    res[2] = "1";
                    break;
                case "Dado composto":
                    res[2] = "2";
                    break;
                case "Lista":
                    res[2] = "3";
                    break;
            }
            ObjectList auxObj = new ObjectList(res[0], res[1], Integer.parseInt(res[2]));
            if(parentStack.empty()){
                if(positionObj != -1){
                    list.remove(positionObj);
                    list.add(positionObj, auxObj);
                } else {
                    list.add(auxObj);
                }
            }else{
                if(positionObj != -1){
                    auxObjectList.remove(positionObj);
                    auxObjectList.add(positionObj, auxObj);
                } else {
                    auxObjectList.add(auxObj);
                }
            }
            auxStringList = new ArrayList<>();
            if(auxObjectList.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    auxStringList.add(list.get(i).name);
                }
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, auxStringList);
            }else{
                for (int i = 0; i < auxObjectList.size(); i++) {
                    auxStringList.add(auxObjectList.get(i).name);
                }
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, auxStringList);
            }
            lv.setAdapter(adapter);
        } else if(resultCode == 2){
            if(auxObjectList.isEmpty())
                adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, new String[]{"//SEM CONTEÚDO//"});
            else {
                for (int i = 0; i < auxObjectList.size(); i++) {
                    auxStringList.add(auxObjectList.get(i).name);
                }
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, auxStringList);
            }
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

    @Override
    public void onBackPressed(){
        if(parentStack.size()==1){
                auxStringList=new ArrayList<>();
                for (int i = 0; i < list.size() ; i++) {
                    auxStringList.add(list.get(i).name);
                }
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, auxStringList);
                lv.setAdapter(adapter);
            }else{
                auxStringList=new ArrayList<>();
                ArrayList<ObjectList> auxChildList = parentStack.pop().childList;
                for (int i = 0; i < auxChildList.size() ; i++) {
                    auxStringList.add(auxChildList.get(i).name);
                }
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, auxStringList);
                lv.setAdapter(adapter);
        }
    }

}
