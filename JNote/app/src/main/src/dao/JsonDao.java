package dao;

//import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
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

import org.hamcrest.core.IsInstanceOf;
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
    private final static String DEFAULT_SEP = ".";
    // Jogar para um arquivo de configuração
    private final static String CURRENT_VERSION = "1.0";
    private final static String DEFAULT_ROOT = "JNote";
    
    public final static String ATTRIBUTE_TYPE = "1";
    public final static String ARRAY_TYPE =    "2";
    public final static String OBJECT_TYPE =   "3";
    
    private static String DIR_DB;
    
    public JsonDao(){
    	DIR_DB = Configuration.getInstance().currentDB();
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
     *      'type': "2",
     *      'value': [
     *          {
     *              'label': "nome",
     *              'type': "1",
     *              'value': "João"
     *          },{
     *              'label': "idade",
     *              'type': "1",
     *              'value': "16"
     *          },{
     *              'label': "favoritas",
     *              'type': "2",
     *              'value': null
     *          },{
     *              'label': "educação",
     *              'type': "3",
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
     *          'type': "2",
     *          'value': ["uva","maçã","banana"]
     *      }
     * 
     *  acessar('JNote', 'educação');
     *  acessar('JNote.educacao');
     *  =>  {
     *          'version': '1.0',
     *          'super':'JNote',
     *          'label': "educação",
     *          'type': "3",
     *          'value': [
     *              {
     *                  'label': "ensino médio",
     *                  'type': "3",
     *                  'value': null
     *              },{
     *                  'label':"graduação",
     *                  'type': "3",
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
     *          'type': "3",
     *          'value': [
     *              {
     *                  'labe': "nome",
     *                  'type': "1",
     *                  'value': "colégio X"
     *              },{
     *                  'label': "inicio",
     *                  'type': "1",
     *                  'value': "2009"
     *              },{
     *                  'label': "fim",
     *                  'type': "1",
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
     *          'type': "1",
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
        if (((String)result.get(type)).trim().equals(ATTRIBUTE_TYPE)) {
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
        String[] passosOrigem = caminhoOrigem.split("\\"+DEFAULT_SEP);
        String[] passosDestino = caminhoDestino.split("\\"+DEFAULT_SEP);
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
        	result = salvar(acessar(caminhoOrigem), force);
        	if(result && !copy){
        		result &= remover(caminhoDestino, force);
        	}
        }
    	return result;
    }
    public boolean salvar(Map<String, Object> val) throws IllegalArgumentException, IOException, JSONException {
    	if(!salvar(val, false)){
    		throw new IllegalArgumentException("Não foi possível salvar o elemento. ");
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
 
    	//remover
    	if(((String)estrutura.get(type)).trim().equals(ATTRIBUTE_TYPE)){
    		new File(caminho = (String)estrutura.get(_super)
    				+ DEFAULT_SEP
    				+ (String)estrutura.get(label)
    				+ DEFAULT_SEP).delete();
    	}else{
    		// apagar todos os elementos deste item
        	List<Map<String, Object>> filhos = (List)estrutura.get(value);
        	for(int i = 0; i < filhos.size(); i++){
        		String caminhoFilho = (String)estrutura.get(_super)
        				+ DEFAULT_SEP
        				+ (String)estrutura.get(label)
        				+ DEFAULT_SEP
        				+ filhos.get(i).get(label); 
        		result &= remover(caminhoFilho);
        		filhos.remove(i);
        	}
    	}
    	// apagar o elemento, que referencia esta estrutura, no pai
		for(int i = 0; i < ((List) estrutura_super.get(value)).size(); i++){
			if(((String)((Map<String, Object>)((List) estrutura_super.get(value)).get(i)).get(label)).trim().equalsIgnoreCase((String) estrutura.get(label))){
				((List) estrutura_super.get(value)).remove(i);
				result &= true;
			}
		}
    	
    	// salva o pai e o arquivo atual
		
    	result &= salvar(estrutura_super, force) && new File(DIR_DB+caminho+FORMAT_JSON).delete(); 
    	return result;
    }
    
    public boolean salvar(Map<String, Object> val, boolean force) throws IOException, JSONException{
    	boolean result = false;
    	
    	//chaves
        String version = "version";
        String _super = "super";
        String label = "label";
        String type = "type";
        String value = "value";
        
        String caminhoDestino = val.get(_super) + DEFAULT_SEP + val.get(label);
        
    	if(caminhoDestino == null
    			|| caminhoDestino.trim().equalsIgnoreCase("")
    			|| !caminhoDestino.trim().startsWith("JNote"))
    		throw new IOException("Caminho vazio ou inválido (não inicia com JNote)");
    	String[] passosDestino = caminhoDestino.split("\\"+DEFAULT_SEP);
    	String caminho = passosDestino[0]; // Todo caminho inicia com JNote
    	
    	// pega o úlimo caminho que existe
    	for(int i = 1; i < passosDestino.length && isExist(caminho); i++){
    		// concatena apenas se existir
    		if(isExist(caminho + DEFAULT_SEP + passosDestino[i])){
    			caminho = caminho + DEFAULT_SEP + passosDestino[i];
    		}else{
    			break;
    		}
    	}
    	
    	// se o caminho obtido for igual ao caminhoDestino
    	// entao já existe...
    	// senao devemos construir cada estrutura até que o caminhoDestino passe a existir.
    	if(caminho.trim().equalsIgnoreCase(caminhoDestino)){
    		if(!force){
    			return false;
    		}else{
    			return push(val, caminhoDestino);
    		}
    	}else{
        	int passo = caminho.split("\\"+DEFAULT_SEP).length-1;
        	while(!isExist(caminhoDestino) && passo+1 < passosDestino.length){
        		// construir um novo elemento para o value do caminho do passo
    			Map<String, Object> elemento_super = createElementStruct(passosDestino[passo+1], OBJECT_TYPE , null);
    			// adiciona o elemento no super
    			Map<String, Object> estrutura_super = acessar(caminho);
    			
    			// colocar com o tipo OBJETO
    			estrutura_super.put(type, OBJECT_TYPE);
    			
    			if(((List)estrutura_super.get(value)) == null){
    				estrutura_super.put(value, new ArrayList<Map<String, Object>>());
    			}
    			((List)estrutura_super.get(value)).add(elemento_super);
    			
        		// se o caminho != val.super (super do objeto que queremos salvar) 
        		// então temos que construir este element e dar mais um passo
        		// senão estamos acessando o nosso destino.
        		if(!(((String)val.get(_super)).trim().equalsIgnoreCase(caminho))){
        			// construir uma nova estrutura para o passo + 1
        			Map<String, Object> estrutura = createStruct(CURRENT_VERSION, caminho, passosDestino[passo+1], OBJECT_TYPE, null);
        			boolean val1 = push(estrutura_super, caminho);
        			boolean val2 = push(estrutura, caminho + DEFAULT_SEP + passosDestino[passo+1]);
        			//result &= push(estrutura_super, caminho) && push(estrutura, caminho + DEFAULT_SEP + passosDestino[passo]);
        			result &= val1 && val2;
        			
        		}else{
//        			// construir um novo elemento para o value do caminho do pai do nosso destino.
//        			Map<String, Object> elemento_super = createElementStruct(passosDestino[passo-1], (String)val.get(type), null);
//        			// caso seja um atributo, seu pai já conhece seu valor
//        			if(((String)val.get(type)).trim().equalsIgnoreCase(ATTRIBUTE_TYPE)){
//        				elemento_super.put(value, val.get(value));
//        			}
//        			val = createStruct(CURRENT_VERSION, caminhoDestino, (String)val.get(label), (String) val.get(type), val.get(value));
//        			
//        			result = push(val, caminhoDestino) && push(elemento_super, caminho);
        			
        			// altera o type da estrutura que está no pai.
        			estrutura_super.put(type, (String)val.get(type));
        			
        			// se o tipo de val não vou atributo,
        			// entao para cada objeto em val.get(value), criar uma estrutura e dar push
        			
        			if(!((String)val.get(type)).trim().equalsIgnoreCase(ATTRIBUTE_TYPE)){
        				for(HashMap<String, Object> v: (List<HashMap<String, Object>>)val.get(value)){	
	        				Map<String, Object> estrutura = createStruct(
	        						(String)val.get(version),
	        						(String)val.get(_super) + DEFAULT_SEP + (String)val.get(label),
	        						(String)v.get(label),
	        						(String)v.get(type),
	        						(String)v.get(value));
	        				result &= push(estrutura, (String)val.get(_super)
	        						+ DEFAULT_SEP
	        						+ (String)val.get(label)
	        						+ DEFAULT_SEP
	        						+ (String)v.get(label));
        				}
        			}
        			
        			result &= push(estrutura_super, caminho)
        					&& push(val, (String)val.get(_super) + DEFAULT_SEP + (String)val.get(label));
        			
        		}
        		passo++;
        		caminho = caminho + DEFAULT_SEP + passosDestino[passo];
    			
        	}
    	}
    	return result;
    }
    
    // Private Methods
    // 1) Métodos de acesso a arquivos
    private JSONObject open(String file) throws IOException, IllegalArgumentException, JSONException {
    	String filePathString = file + FORMAT_JSON;
    	File f = new File(filePathString);

    	if(!f.exists()){
    		filePathString = DIR_DB + file + FORMAT_JSON;
    		f = new File(filePathString);
    	}
    	
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
    	
    	String text = "{\n";
    	
    	if(isStruct(val)){
    		text += getJSFormatedAttribute(version, val)+",\n";
    		text += getJSFormatedAttribute(_super, val)+",\n";
    		//text += version + ": " + val.get(version)+",\n";
    		//text += _super+ ": " + val.get(_super)+",\n";
    	}
    	text += getJSFormatedAttribute(label, val)+",\n";
		text += getJSFormatedAttribute(type, val)+",\n";
		
		text += getValue(val)+'\n';
    	text += "}";
    	
    	PrintWriter writer = new PrintWriter(DIR_DB + file + FORMAT_JSON, "UTF-8");
    	writer.println(text);
    	writer.close();
    	
    	result = true;
    	return result;
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////
// 2) Métodos de checagem e verificação /////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isExist(String file){
    	String filePathString = DIR_DB + file + FORMAT_JSON;
        File f = new File(filePathString);
        return f.exists() && !f.isDirectory();
    }
    
    private static boolean isNull(Object val){
    	return val == null || val.toString().trim().equalsIgnoreCase("null");
    }
    
    private static boolean isArray(Object val) {
        return !isNull(val) && val instanceof JSONArray;
    }

    private static boolean isObject(Object val) throws IllegalArgumentException, JSONException {
    	return !isNull(val) && val instanceof JSONObject && !isArray(val);
    	
    }

    private static boolean isValidType(String type) {
        return (type.trim().equals(ATTRIBUTE_TYPE) || type.trim().equals(ARRAY_TYPE) || type.trim().equals(OBJECT_TYPE));
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
    public static Map<String, Object> newStruct() {
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
    public static Map<String, Object> createStruct(String version, String _super, String label, String type, Object value) {
        Map<String, Object> result = createElementStruct(label, type, value);
        result.put("version", version);
        result.put("super", _super);

        return result;
    }

    public static Map<String, Object> createElementStruct(String label, String type, Object value) {
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
    	if(((String)val.get(type)).trim().equals(ATTRIBUTE_TYPE)){
    		result += "\"" + (String) val.get(value) + "\"";
    	}else{
    		
    		if(val.get(value) == null){
    			result += "null";
    		}else{
    			result += '[';
	    		for(int i = 0; i < ((List) val.get(value)).size();i++){
	    		
	    			result += "{\n";
	    			result += "\t"+getJSFormatedAttribute(label, (Map)((List) val.get(value)).get(i))+",\n";
	    			result += "\t"+getJSFormatedAttribute(type, (Map)((List) val.get(value)).get(i))+",\n";
	    			if(((Map)((List) val.get(value)).get(i)).get(type) == ATTRIBUTE_TYPE){
	    				result += "\t"+getJSFormatedAttribute(value, (Map)((List) val.get(value)).get(i))+",\n";
	    			}else{
	    				result += "\t"+getJSFormatedAttribute(value, null)+",\n";
	    			}
	    			
	    			result += "}";
	    			if(i < ((List) val.get(value)).size() - 1){
	    				result += ",";
	    			}
	    		}
	    		result += "]";
    		}
    	}
    	return result;
    }
    /**
     * Usado apenas para os casos em que o Type é Object ou Array
     * @param val
     * @return
     * @throws IllegalArgumentException
     * @throws JSONException
     */
    private static Object getValue(JSONObject val) throws IllegalArgumentException, JSONException {
        List<Object> result = new ArrayList<Object>();
        String label = "label";
        String type = "type";
        String value = "value";
        
        if (!val.isNull(value) && !isObject(val.get(value)) && !isArray(val.get(value))) {
            throw new IllegalArgumentException(val.toString() + " Não é uma lista ou um objeto");
        }
        if(!val.isNull(value)){
	        JSONArray array = (JSONArray) val.get(value);
	        
	        for (int i = 0; i < array.length(); i++) {
	        	JSONObject var = (JSONObject) array.get(i);
	        	String labelVar = getAttribute(label, var);
	        	String typeVar = getAttribute(type, var);
	        	String valueVar;
	            /*  there are three possibles cases:
	                1) an absolute value
	                2) an array element
	                3) an object element
	             */
	            //in the third and second case
	            if (isArray(array.get(i)) || isObject(array.get(i))) {
	            	valueVar = null;
	                // first case
	            } else {
	            	valueVar = getAttribute(value, var);
	            }
	            result.add(createElementStruct(labelVar, typeVar, valueVar));
	        }
        }
        return result;
    }
    
    private static String getJSFormatedAttribute(String key, Map<String, Object> source){
    	Object value;
    	if(source == null){
    		value = null;
    	}else{
    		value = source.get(key);
    	}
    	return "\'" + key + "\'" + ": \"" + value + "\"";
    }
    
    private static String getAttribute(String key, JSONObject source) throws IllegalArgumentException, JSONException {
        String result = "";
        
        Object value = source.get(key);
        if (isArray(value) || isObject(value)) {
            throw new IllegalArgumentException("Não é um atributo");
        }

        if(value == null || source.isNull(key)){
        	value = "null";
        }
        return result = (String) value;
    }
    
    
}
