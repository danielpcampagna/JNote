package dao;

//import java.io.BufferedInputStream;
import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//import android.app.backup.FileBackupHelper;

/**
 *
 * @author daniel
 */
public class JsonDao {
	
    private final static String DIR_DB = "";
    private final static String FORMAT_JSON = ".json";
    private final static String DEFAULT_SEP = ".";
    // Jogar para um arquivo de configuração
    private final static String CURRENT_VERSION = "1.0";
    private final static String DEFAULT_ROOT = "JNote";
    
    public final static Integer ATTRIBUTE_TYPE = 1;
    public final static Integer ARRAY_TYPE =    2;
    public final static Integer OBJECT_TYPE =   3;
    
    /**
     * Use esta função para acessar os valores de um dado caminho e uma dada chave.
     * @param caminho: Composição de chaves. Se vazio, root.
     * @param chave: Chave que terá seu valor acessado.
     * @param separador: Separador usado para escrever o caminho.
     * @return Retorna o conjunto dos valores (String) da chave e o caminho informado.
     * 
     * Exemplos:
     * 1) Suponha o Json:
     *  {
     *      nome: "João",
     *      idade:"16",
     *      favoritas: ["uva","maçã","banana"],
     *      educação:{
     *          ensino médio:{
     *              nome:   "colégio x",
     *              inicio: "2009",
     *              fim:    "2012"
     *          },
     *          graduação:{
     *              nome:   "faculdade Y",
     *              inicio: "2013",
     *              fim:    ""
     *          }
     *      }
     *  }
     * 
     * Para as seguintes chamadas, teremos:
     * acessar('JNote','');
     * => {
     *      'version': '1.0',
     *      'super': '',
     *      'label': "JNote",
     *      'type': 2,
     *      'value': [
     *          {
     *              'label': "nome",
     *              'type': 1,
     *              'value': "João"
     *          },{
     *              'label': "idade",
     *              'type': 1,
     *              'value': "16"
     *          },{
     *              'label': "favoritas",
     *              'type': 2,
     *              'value': null
     *          },{
     *              'label': "educação",
     *              'type': 3,
     *              'value': null
     *          }
     *      ]
     *  }
     * 
     *  acessar('JNote.favoritas', '');
     *  acessar('JNote', 'favoritas');
     *  =>  {
     *          'version': '1.0',
     *          'super':'JNote',
     *          'label': "favoritas",
     *          'type': 2,
     *          'value': ["uva","maçã","banana"]
     *      }
     * 
     *  acessar('JNote', 'educação');
     *  acessar('JNote.educacao', '');
     *  =>  {
     *          'version': '1.0',
     *          'super':'JNote',
     *          'label': "educação",
     *          'type': 3,
     *          'value': [
     *              {
     *                  'label': "ensino médio",
     *                  'type': 3,
     *                  'value': null
     *              },{
     *                  'label':"graduação",
     *                  'type': 3,
     *                  'value': null
     *              }
     *          ]
     *      }
     * 
     *  acessar('JNote.educação.ensino médio', '');
     *  acessar('JNote.educação', 'ensino médio');
     *  =>  {
     *          'version': '1.0',
     *          'super':'JNote.educacao',
     *          'label': "ensino médio",
     *          'type': 3,
     *          'value': [
     *              {
     *                  'labe': "nome",
     *                  'type': 1,
     *                  'value': "colégio X"
     *              },{
     *                  'label': "inicio",
     *                  'type': 1,
     *                  'value': "2009"
     *              },{
     *                  'label': "fim",
     *                  'type': 1,
     *                  'value': "2012"
     *              }
     *          ]
     *      }
     * 
     *  acessar('JNote.educação.ensino médio', 'nome');
     *  acessar('JNote.educação.ensino médio.nome', '');
     *  =>  {
     *          'version': '1.0',
     *          'super':'JNote.educação.ensino médio',
     *          'label': "nome",
     *          'type': 1,
     *          'value': "colégio x"
     *      }
     * @throws JSONException 
     * 
     * 
     */
    public Map<String, Object> acessar(String caminho, String chave) throws IOException, IllegalArgumentException, JSONException {
        Map<String, Object> result = newStruct();
        if(!chave.trim().equals("")){
            caminho = caminho + DEFAULT_SEP + chave;
        }
        JSONObject sourceJson = open(caminho);

        //chaves
        String version = "version";
        String _super = "super";
        String label = "label";
        String type = "type";
        String value = "value";

        // version
        result.put(version, getAttribute(version, sourceJson));
        // super
        result.put(_super, getAttribute(_super, sourceJson));
        // label
        result.put(label, getAttribute(label, sourceJson));
        // type
        result.put(type, getAttribute(type, sourceJson));
        // value
        if (result.get(type) == (Integer) ATTRIBUTE_TYPE) {
            result.put(value, getAttribute(value, sourceJson));
        } else {
            result.put(value, getValue(sourceJson));
        }
        return result;
    }

    // Private Methods
    private JSONObject open(String file) throws IOException, IllegalArgumentException, JSONException {
        String filePathString = DIR_DB + file + FORMAT_JSON;
        File f = new File(filePathString);
        if (f.exists() && !f.isDirectory()) {
        	
        	String jsonText = new Scanner(new File(file))
            .useDelimiter("\\A").next();
        	
            return new JSONObject(jsonText);
        } else {
            throw new IllegalArgumentException("Não existe a chave ou o caminho " + file);
        }

    }

    private static List<Object> getValue(JSONObject val) throws IllegalArgumentException, JSONException {
        List<Object> result = new ArrayList<Object>();
        String label = "label";
//        String type = "type";
        String value = "value";

        if (!isArray(val)) {
            throw new IllegalArgumentException("Não é uma lista");
        }
        
        JSONArray array = (JSONArray) val.get(value);
        for (int i = 0; i < array.length(); i++) {
            /*  there are three possibles cases:
                1) an absolute value
                2) an array element
                3) an object element
             */
            //in the third case
            if (isObject(array.get(i))) {
                
                JSONObject var = (JSONObject) array.get(i);
                result.add(createElementStruct((String)var.get(label), OBJECT_TYPE, null));
                // in the second case
            } else if (isArray(array.get(i))) {
                result.add(new ArrayList<Object>());
                // first case
            } else {
                result.add((String)array.get(i));
            }

        }
        return result;
    }

    private static String getAttribute(String key, JSONObject source) throws IllegalArgumentException, JSONException {
        String result = "";

        Object value = source.get(key);
        if (isArray(value) || isObject(value)) {
            throw new IllegalArgumentException("Não é um atributo");
        }

        return (String) result;
    }

    private static boolean isArray(Object val) {
        return val instanceof JSONArray;
    }

    private static boolean isObject(Object val) {
        return val instanceof JSONObject;
    }

    /**
     * Return the empty struct: => { 'version': '', 'super':'', 'label': '',
     * 'type': 1, 'value': null }
     *
     * @return
     */
    private static Map<String, Object> newStruct() {
        // version, super, label, type, value
        return JsonDao.createStruct(CURRENT_VERSION, DEFAULT_ROOT, "", ATTRIBUTE_TYPE, null);
    }

    /**
     * Return the struct filled:
     *
     * /**
     * Return the struct: => { 'version': '@version', 'super':'@_super',
     * 'label': '@label', 'type': @type, 'value': "@value" }
     *
     * @return * @param version
     * @param _super
     * @param label
     * @param type
     * @param value
     * @return
     */
    private static Map<String, Object> createStruct(String version, String _super, String label, Integer type, Object value) {
        Map<String, Object> result = createElementStruct(label, type, value);
        result.put("version", version);
        result.put("super", _super);

        return result;
    }

    private static Map<String, Object> createElementStruct(String label, Integer type, Object value) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("label", label);
        if (!isValidType(type)) {
            throw new IllegalArgumentException("type inválido");
        }
        result.put("type", type);
        result.put("value", value);

        return result;
    }

    private static boolean isValidType(Integer type) {
        return (type == ATTRIBUTE_TYPE || type == ARRAY_TYPE || type == OBJECT_TYPE);
    }
    
}
