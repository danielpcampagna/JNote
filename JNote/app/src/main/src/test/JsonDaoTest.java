package test;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import dao.JsonDao;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author daniel
 */
public class JsonDaoTest {
    

    
    private Map<String, File> files;
    
    public static void main(String args[]){
        JsonDaoTest test = new JsonDaoTest();
        test.preload();
        try {
			test.runTests();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void preload(){
        String prefixe = "../db/test/";
        files = new HashMap<String, File>();
        files.put("JNote", new File(prefixe+"JNote.json"));
        files.put("JNote.educação.ensino médio", new File(prefixe+"JNote.educação.ensino médio.json"));
        files.put("JNote.educação", new File(prefixe+"JNote.educação.json"));
        files.put("JNote.educação .graduação", new File(prefixe+"JNote.educação .graduação.json"));
        files.put("JNote.favoritas", new File(prefixe+"JNote.favoritas.json"));
    }
    
    public void runTests() throws IllegalArgumentException, IOException, JSONException{
    	System.out.println("Test 01: acessar root");
        System.out.println(this.test01_acessar_root());
    }
    
    public boolean test01_acessar_root() throws IOException, IllegalArgumentException, JSONException{
        Map<String, Object> oracle = new HashMap<String, Object>();
        List<Object> oracleValue = new ArrayList<Object>();
        Map<String, Object> oracleValueNome = new HashMap<String, Object>();
        Map<String, Object> oracleValueIdade = new HashMap<String, Object>();
        Map<String, Object> oracleValueFavoritas = new HashMap<String, Object>();
        Map<String, Object> oracleValueEducacao = new HashMap<String, Object>();
        oracle.put("version", "1.0");
        oracle.put("super", "");
        oracle.put("label", "JNote");
        oracle.put("version", "1.0");
        oracle.put("type", 1);
        oracleValueNome.put("label", "nome");
        oracleValueNome.put("type", "1");
        oracleValueNome.put("value", "João");
        oracleValueIdade.put("label", "idade");
        oracleValueIdade.put("type", "1");
        oracleValueIdade.put("value", "16");
        oracleValueFavoritas.put("label", "favoritas");
        oracleValueFavoritas.put("type", "2");
        oracleValueFavoritas.put("value", null);
        oracleValueEducacao.put("label", "educação");
        oracleValueEducacao.put("type", "3");
        oracleValueEducacao.put("value", null);
        oracleValue.add(oracleValueNome);
        oracleValue.add(oracleValueIdade);
        oracleValue.add(oracleValueFavoritas);
        oracleValue.add(oracleValueEducacao);
        oracle.put("value", oracleValue);
        Map<String, Object> result_test = new HashMap<String, Object>();
        
        JsonDao jsonObject = new JsonDao();
        result_test = jsonObject.acessar("JNote", "");
        
        System.out.println(result_test.keySet());
        return true;
    }
    
}