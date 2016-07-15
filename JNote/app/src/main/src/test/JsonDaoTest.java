package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import config.Configuration;

import dao.JsonDao;

public class JsonDaoTest {

	private Map<String, File> files;
	private static final String DIR_DB_TEST = "../db/test/";
	
	@Before
	public void setUp() throws Exception {
		Configuration.getInstance(Configuration.TEST);
		
        files = new HashMap<String, File>();
        files.put("JNote", new File(DIR_DB_TEST + "JNote.json"));
        files.put("JNote.educação.ensino médio", new File(DIR_DB_TEST + "JNote.educação.ensino médio.json"));
        files.put("JNote.educação", new File(DIR_DB_TEST + "JNote.educação.json"));
        files.put("JNote.educação .graduação", new File(DIR_DB_TEST + "JNote.educação .graduação.json"));
        files.put("JNote.favoritas", new File(DIR_DB_TEST + "JNote.favoritas.json"));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test01_acessar_root() throws IllegalArgumentException, IOException, JSONException {
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
        oracle.put("type", "3");
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
        
        System.out.println("super: "+result_test.get("super"));
        System.out.println("super: "+oracle.get("super"));
        
        System.out.println("value: "+result_test.get("value"));
        System.out.println("value: "+oracle.get("value"));
        
        System.out.println("label: "+result_test.get("label"));
        System.out.println("label: "+oracle.get("label"));
        
        System.out.println("type: "+result_test.get("type"));
        System.out.println("type: "+oracle.get("type"));
        
        System.out.println("version: "+result_test.get("version"));
        System.out.println("version: "+oracle.get("version"));
	}

}
