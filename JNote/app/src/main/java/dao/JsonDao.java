package dao;

//import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
//import java.io.FileInputStream;
//import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import junit.runner.Version;

import org.json.*;

import config.Configuration;
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
	
    
    private final static String FORMAT_JSON = ".json";
    private final static String DEFAULT_SEP = "build/intermediates/exploded-aar/com.android.support/support-vector-drawable/23.4.0/res";
    // Jogar para um arquivo de configuração
    private final static String CURRENT_VERSION = "1.0";
    private final static String DEFAULT_ROOT = "JNote";
    
    public final static Integer ATTRIBUTE_TYPE = 1;
    public final static Integer ARRAY_TYPE =    2;
    public final static Integer OBJECT_TYPE =   3;
    
    private static String DIR_DB;
    
    public JsonDao(){
    	DIR_DB = Configuration.getInstance().currentDB();
        System.out.println(DIR_DB);
    }
    
    public Map<String, Object> acessar(String caminho, String chave) throws IOException, IllegalArgumentException, JSONException {
    	
        if(!chave.trim().equals("")){
            caminho = caminho + DEFAULT_SEP + chave;
        }
        return acessar(caminho);
    }
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
     * acessar('JNote');
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
     *  acessar('JNote.favoritas');
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
     *  acessar('JNote.educacao');
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
     *  acessar('JNote.educação.ensino médio');
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
     *  acessar('JNote.educação.ensino médio.nome');
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
    public Map<String, Object> acessar(String caminho) throws IOException, IllegalArgumentException, JSONException {
    	Map<String, Object> result = newStruct();
    	
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

    public boolean copiar(String caminhoOrigem, String caminhoDestino) throws IllegalArgumentException, IOException, JSONException{
    	if(!mover(caminhoOrigem, caminhoDestino, false, true)){
    		throw new IllegalArgumentException("Não é possível copiar o elemento "+caminhoOrigem+" para "+caminhoDestino);
    	}
		return true;	
    }
    public boolean copiar(String caminhoOrigem, String caminhoDestino, boolean force) throws IOException, JSONException{
    	return (!mover(caminhoOrigem, caminhoDestino, force, true));	
    }
    
    public boolean mover(String caminhoOrigem, String caminhoDestino) throws IllegalArgumentException, IOException, JSONException{
    	if(!mover(caminhoOrigem, caminhoDestino, false, false)){
    		throw new IllegalArgumentException("Não é possível mover o elemento "+caminhoOrigem+" para "+caminhoDestino);
    	}
		return true;
    }
    public boolean mover(String caminhoOrigem, String caminhoDestino, boolean force, boolean copy) throws IOException, JSONException{
    	boolean result = false;
    	
    	Map<String, Object> origem = acessar(caminhoOrigem);
    	Map<String, Object> destino;
    	/////////// Tratando o Destino ////////////////////////////
	  	// 1) Checar se já existe
	  	if(isExist(caminhoDestino)){
	  		return false;
	  	}
    	
    	//chaves
        String version = "version";
        String _super = "super";
        String label = "label";
        String type = "type";
        String value = "value";
    	
        ////////// Buscar a maior semelhança entre os dois caminhos (origem e destino)
        // através de semelhança sintática
        String[] passosOrigem = caminhoOrigem.split(DEFAULT_SEP);
        String[] passosDestino = caminhoDestino.split(DEFAULT_SEP);
        String caminhoComum = "";
        for(int i = 0; i < passosOrigem.length && i < passosDestino.length; i++){
        	if(passosOrigem[i].trim().equalsIgnoreCase(passosDestino[i].trim())){
        		caminhoComum = caminhoComum + DEFAULT_SEP + passosOrigem[i];
        	}
        }
        // Caso o caminhoComum for igual ao caminhoDestino
        // então não faz sentido mover. => true
        // senão devemos construir cada elemento até que o caminhoDestino passe a existir.
        if(caminhoComum.trim().equalsIgnoreCase(caminhoDestino)){
        	return true;
        }else{
        	result = salvar(acessar(caminhoOrigem), caminhoDestino, force);
        	if(result && !copy){
        		result &= remover(caminhoDestino, force);
        	}
        }
    	return result;
    }
    public boolean salvar(Map<String, Object> val, String caminhoDestino) throws IllegalArgumentException, IOException, JSONException {
    	if(!salvar(val, caminhoDestino, false)){
    		throw new IllegalArgumentException("Não foi possível salvar o elemento "+caminhoDestino);
    	}
    	return true;
    	
    }
    
    
    public boolean remover(String caminho) throws IllegalArgumentException, IOException, JSONException{
    	if(!remover(caminho, false)){
    		throw new IllegalArgumentException("Não foi possível remover o elemento: "+caminho);
    	}
    	return true;
    }
    /**
     * Remove o todos elementos da lista dos pais que tenham o mesmo label do elemento a ser removido
     * @param caminho
     * @param force
     * @return
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws JSONException
     */
    public boolean remover(String caminho, boolean force) throws IllegalArgumentException, IOException, JSONException{
    	boolean result = false;
    	
    	//chaves
        String version = "version";
        String _super = "super";
        String label = "label";
        String type = "type";
        String value = "value";
    	
    	Map<String, Object> estrutura = acessar(caminho);
    	Map<String, Object> estrutura_super = acessar((String)estrutura.get(_super));
    	
    	//remover do pai
    	if(estrutura.get(type) == ATTRIBUTE_TYPE){
    		estrutura_super.put(value, "");
    	}else{
    		for(int i = 0; i < ((List) estrutura_super.get(value)).size(); i++){
    			if(((String)((Map<String, Object>)((List) estrutura_super.get(value)).get(i)).get(label)).trim().equalsIgnoreCase((String) estrutura.get(label))){
    				((List) estrutura_super.get(value)).remove(i);
    				result = true;
    			}
    		}
    	}
    	result &= salvar(estrutura_super, (String)estrutura.get(_super), force) && new File(caminho).delete(); 
    	return result;
    }
    
    public boolean salvar(Map<String, Object> val, String caminhoDestino, boolean force) throws IOException, JSONException{
    	boolean result = false;
    	
    	//chaves
        String version = "version";
        String _super = "super";
        String label = "label";
        String type = "type";
        String value = "value";
    	
    	String[] passosDestino = caminhoDestino.split(DEFAULT_SEP);
    	String caminho = passosDestino[0];
    	for(int i = 0; i < passosDestino.length && isExist(caminho); i++){
    		caminho = caminho + DEFAULT_SEP + passosDestino[i];
    	}
    	// se o caminho obtido for igual ao caminhoDestino
    	// entao já existe...
    	// senao devemos construir cada estrutura até que o caminhoDestino passe a existir.
    	if(caminho.trim().equalsIgnoreCase(caminhoDestino)){
    		// caso
    		if(!force){
    			return false;
    		}else{
    			return push(val, caminhoDestino);
    		}
    	}else{
        	int passo = caminho.split(DEFAULT_SEP).length;
        	while(!isExist(caminhoDestino)){
        		// se o passo < passoDestino - 1
        		// então temos que construir este element e dar mais um passo
        		// senão estamos acessando o pai do passo destino, devemos...
        		if(passo < passosDestino.length - 1){
        			// construir um novo elemento para o value do caminho do passo
        			Map<String, Object> elemento_super = createElementStruct(passosDestino[passo-1], OBJECT_TYPE , null);
        			// construir uma nova estrutura para o passo + 1
        			Map<String, Object> estrutura = createStruct(CURRENT_VERSION, caminho, passosDestino[passo], OBJECT_TYPE, null);
        			push(elemento_super, caminho);
        			
        		}else{
        			// construir um novo elemento para o value do caminho do pai do nosso destino.
        			Map<String, Object> elemento_super = createElementStruct(passosDestino[passo-1], ((Integer)val.get(type)), null);
        			// caso seja um atributo, seu pai já conhece seu valor
        			if(((Integer)val.get(type)) == ATTRIBUTE_TYPE){
        				elemento_super.put(value, val.get(value));
        			}
        			val = createStruct(CURRENT_VERSION, caminhoDestino, (String)val.get(label), (Integer) val.get(type), val.get(value));
        			
        			result = push(val, caminhoDestino) && push(elemento_super, caminho); 
        			
        		}
        		caminho = caminho + DEFAULT_SEP + passosDestino[passo];
    			passo++;
        	}
    	}
    	return result;
    }
    
    // Private Methods
    // 1) Métodos de acesso a arquivos
    private JSONObject open(String file) throws IOException, IllegalArgumentException, JSONException {
    	String filePathString = file+ FORMAT_JSON;
    	File f = new File(filePathString);

    	if(!f.exists()){
    		filePathString = DIR_DB + file + FORMAT_JSON;
    		f = new File(filePathString);
    	}
        System.out.println(filePathString);
        if (f.exists() && !f.isDirectory()) {
        	
        	String jsonText = new Scanner(f)
            .useDelimiter("\\A").next();

            return new JSONObject(jsonText);
        } else {
            throw new IllegalArgumentException("Não existe a chave ou o caminho " + file);
        }

    }
    
    // checar se esse é necessário diferenciar se é uma estrutura ou se é um elemento
    private boolean push(Map<String, Object> val, String file) throws IOException{
    	boolean result = false;
    	
    	//chaves
        String version = "version";
        String _super = "super";
        String label = "label";
        String type = "type";
        String value = "value";
    	
    	String text = "{";
    	if(isStruct(val)){
    		text += version + ": " + val.get(value);
    		text += _super+ ": " + val.get(_super);
    	}
    	text += getJSFormatedAttribute(label, val);
		text += getJSFormatedAttribute(type, val);
		text += getValue(val);
    	text = "}";
    	
    	File f = new File(DIR_DB + file + FORMAT_JSON);
    	f.createNewFile();
    	FileWriter fw = new FileWriter(f);
    	fw.write(text);
    	fw.close();
    	
    	return result;
    }
    
    // 2) Métodos de checagem e verificação
    private boolean isExist(String file){
    	String filePathString = DIR_DB + file + FORMAT_JSON;
        File f = new File(filePathString);
        return f.exists() && !f.isDirectory();
    }
    
    private static boolean isArray(Object val) {
        return val instanceof JSONArray;
    }

    private static boolean isObject(Object val) {
        return val instanceof JSONObject;
    }

    private static boolean isValidType(Integer type) {
        return (type == ATTRIBUTE_TYPE || type == ARRAY_TYPE || type == OBJECT_TYPE);
    }
    
    private static boolean isStruct(Map<String, Object> val){
    	//chaves
        String version = "version";
        String _super = "super";
        String label = "label";
        String type = "type";
        String value = "value";
        
    	return val.containsKey(version) 
    			&& val.containsKey(_super) 
    			&& val.containsKey(label)
    			&& val.containsKey(type)
    			&& val.containsKey(value);
    }
    
    private static boolean isElement(Map<String, Object> val){
    	//chaves
        String label = "label";
        String type = "type";
        String value = "value";
        
    	return !isStruct(val)  
    			&& val.containsKey(label)
    			&& val.containsKey(type)
    			&& val.containsKey(value);
    }
    
    // 4) Métodos de criação de estrutura
    /**
     * Return the empty struct:
     * <pre> 
     * => {
     * 	'version': CURRENT_VERSION,
     * 	'super':DEFAULT_ROOT,
     * 	'label': '',
     * 	'type': ATTRIBUTE_TYPE,
     * 	'value': null
     *}
     *</pre>
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
    
    // 3) Métodos de obtenção de valores da estrutura
    private static String getValue(Map<String, Object> val){
    	String label = "label";
    	String type = "type";
        String value = "value";
        
        String result = "\""+value + "\": ";
    	if((Integer) val.get(type) == ATTRIBUTE_TYPE){
    		result += "\"" + (String) val.get(value) + "\"";
    	}else{
    		result += "[";
    		for(int i = 0; i < ((List) val.get(value)).size();i++){
    			result += "{";
    			result += getJSFormatedAttribute(label, val);
    			result += getJSFormatedAttribute(type, val);
    			result += getJSFormatedAttribute(value, null);
    			result += "}";
    			if(i < ((List) val.get(value)).size() - 1){
    				result += ",";
    			}
    		}
    		result += "]";
    	}
    	return result;
    }
    private static List<Object> getValue(JSONObject val) throws IllegalArgumentException, JSONException {
        List<Object> result = new ArrayList<Object>();
        String label = "label";
        String type = "type";
        String value = "value";

        if (!isArray(val.get(value))) {
            throw new IllegalArgumentException(val.toString() + "Não é uma lista");
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
    
    private static String getJSFormatedAttribute(String key, Map<String, Object> source){
    	return key + ": \"" + source.get(key) + "\"";
    }
    private static String getAttribute(String key, JSONObject source) throws IllegalArgumentException, JSONException {
        String result = "";

        Object value = source.get(key);
        if (isArray(value) || isObject(value)) {
            throw new IllegalArgumentException("Não é um atributo");
        }

        return (String) result;
    }
}
