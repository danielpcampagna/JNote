package com.dio.jnote.jnote;

import android.Manifest;
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

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dao.JsonDao;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> auxStringList;
    //private Stack<String> parentStack = new Stack<>();
    private JsonDao dao;
    private Map<String, Object> content;
    private ListView lv;
    private ArrayAdapter<String> adapter;
    private List<Object> values;
    private int positionValue;

    private void setAuxStringList (){
        auxStringList = new ArrayList<>();
        for (int i = 0; i < values.size() ; i++) {
            auxStringList.add(((Map<String,Object>) values.get(i)).get("label").toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[]permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(PermissionUtils.validate(this,0,permissions)) {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            dao = new JsonDao();

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, EditActivity.class);
                    startActivityForResult(i, 1);
                }
            });

            lv = (ListView) findViewById(R.id.listView);

            Intent i = getIntent();
            String[] aux = i.getStringArrayExtra("WAY");
            if(aux!=null){
                try {
                    content = dao.acessar(aux[0]);
                    values = (List<Object>) content.get("value");
                    setAuxStringList();
                    if(values.size()!=0)
                        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, auxStringList);
                    else
                        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"//SEM CONTEÚDO//"});
                }catch (Exception e){
                    adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{e.toString()});//"//SEM CONTEÚDO//"});
                }
            } else{
                try {
                    content = dao.acessar("JNote");
                    values = (List<Object>) content.get("value");
                    setAuxStringList();
                    if(values.size()!=0)
                        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, auxStringList);
                    else
                        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"//SEM CONTEÚDO//"});
                }catch(Exception e){
                    adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{e.toString()});//"//SEM CONTEÚDO//"});
                }
            }

            lv.setAdapter(adapter);
            lv.setOnItemClickListener(viewContent(this));
            registerForContextMenu(lv);
        }else
            finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listView) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            if(!lv.getItemAtPosition(acmi.position).toString().equals("//SEM CONTEÚDO//")) {
                positionValue = acmi.position;
                menu.setHeaderTitle(lv.getItemAtPosition(acmi.position).toString());
                menu.add(0, v.getId(), 0, "Editar");
                menu.add(0, v.getId(), 0, "Remover");
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Map<String,Object> auxValue = (Map<String,Object>) values.get(positionValue);
        if(item.getTitle()=="Editar"){
            Intent i = new Intent(MainActivity.this,EditActivity.class);
            String [] values = {auxValue.get("label").toString(), auxValue.get("type").toString() ,auxValue.get("value").toString()};
            i.putExtra("VALUESVECTOR", values);
            startActivityForResult(i,1);
        }
        else if(item.getTitle()=="Remover") {
            try {
                if(content.get("label").toString().equals("JNote"))
                    dao.remover("JNote."+auxValue.get("label").toString(),true);
                else
                    dao.remover(content.get("super").toString()+"."+auxValue.get("label").toString(),true);
            } catch (Exception e) {
                System.out.println(e);
            }
            setAuxStringList();
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, auxStringList);
            lv.setAdapter(adapter);
        }
        else {return false;}
        return true;
    }

    private AdapterView.OnItemClickListener viewContent(final Context context) {
        return(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position, long id){
                if(!lv.getItemAtPosition(position).toString().equals("//SEM CONTEÚDO//")) {
                    if (Integer.parseInt(((Map<String, Object>) values.get(position)).get("type").toString()) == 1) {
                        Intent i = new Intent(context, ViewActivity.class);
                        String[] str = new String[2];
                        str[0] = ((Map<String, Object>) values.get(position)).get("label").toString();
                        str[1] = ((Map<String, Object>) values.get(position)).get("value").toString();
                        i.putExtra("NAME_VALUE", str);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(context, Main2Activity.class);
                        String[] way = new String[2];
                        if (content.get("label").toString().equals("JNote")) {
                            try {
                                way[0] = "JNote." + ((Map<String, Object>) values.get(position)).get("label").toString();
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        } else {
                            try {
                                way[0] = (((Map<String, Object>) values.get(position)).get("super").toString() + "." + ((Map<String, Object>) values.get(position)).get("label").toString());
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        }
                        way[1] = (((Map<String, Object>) values.get(position)).get("type").toString());
                        i.putExtra("WAY", way);
                        //startActivityForResult(i, 2);
                        startActivity(i);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode==1) {
            String[] res = data.getStringArrayExtra("VALUE");
            int edit = data.getIntExtra("EDIT",0);
            switch (res[2]) {
                case "Dado simples":
                    res[2] = "1";
                    break;
                case "Dado composto": //LISTA DE STRINGS
                    res[2] = "2";
                    break;
                case "Lista": //LISTA DE OBJETOS
                    res[2] = "3";
                    break;
            }
            Map<String, Object> val;
            if(edit==1){
                val = dao.createStruct("1.0",content.get("super").toString(),res[0],res[2],values.get(positionValue));
            }else{
                val = dao.createStruct("1.0",content.get("super").toString(),res[0],res[2],res[1]);
            }
            try {
                if(content.get("label").equals("JNote"))
                    dao.salvar(val,content.get("label").toString()+"."+res[0],true);
                else
                    dao.salvar(val,content.get("super").toString()+"."+res[0],true);
            } catch (Exception e) {
                System.out.println(e);
            }
            setAuxStringList();
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, auxStringList);
            lv.setAdapter(adapter);

        }/* else if(resultCode == 2){
            String[]res = data.getStringArrayExtra("WAY");
            try {
                content = dao.acessar(res[0]);
            } catch (Exception e) {
                System.out.println(e);
            }
            values = (List<Object>) content.get("value");
            setAuxStringList();
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, auxStringList);
            lv.setAdapter(adapter);
        }*/
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        if(!content.get("label").toString().equals("JNote")) {
            try {
                content = dao.acessar(content.get("super").toString());
            } catch (Exception e) {
                System.out.println(e);
            }
            setAuxStringList();
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, auxStringList);
            lv.setAdapter(adapter);
        }else
            finish();
        super.onBackPressed();
    }

}
