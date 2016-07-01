import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author daniel
 */
public class JsonDao {
    private final static String DIR_DB = "";
    private final static String FORMAT = ".json";
    private final static String DEFAULT_SEP = ".";
    
    
    public List<String> acessar(String caminho, String chave) throws IOException, ParseException, IllegalArgumentException{
        return acessar(caminho, chave, DEFAULT_SEP);
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
     * Para as seguintes chamadas, teremos:
     *   acessar("", "nome",".");
     * => ["João"]
     * 
     *   acessar("", "favoritas", ".");
     * => ["uva", "maçã", "banana"]
     * 
     *   acessar("", "educação", ".");
     * => ["ensino médio", "graduação"]
     * 
     *   acessar("educação","ensino médio", ".");
     * => ["nome", "inicio", "fim"]
     * 
     *   acessar("educação.ensido médio", "nome", ".");
     * => ["colégio x"]
     * 
     * 
     */
    public List<String> acessar(String caminho, String chave, String separador) throws IOException, ParseException, IllegalArgumentException{
        List<String> result = new ArrayList<String>();
        
        JSONObject json = open(caminho);
        
        JSONArray  interventionJsonArray;
        JSONObject interventionObject;
        
        Object root = json.get(chave);
        if (root instanceof JSONArray) {
            // It's an array
            result = (List<String>) root;
        }
        else if (root instanceof JSONObject) {
            // It's an object
            result = (List<String>) ((JSONObject) root).keySet();
        }
        else {
            // It's something else, like a string or number
            result.add(root.toString());
        }
        
        return result;
    }
    
    // Private Methods
    private JSONObject open(String file) throws IOException, ParseException, IllegalArgumentException{
        String filePathString = DIR_DB+file+".json";
        File f = new File(filePathString);
        if(f.exists() && !f.isDirectory()) { 
            return (JSONObject) new JSONParser().parse(new FileReader(filePathString));
        }else{
            throw new IllegalArgumentException("Não existe a chave ou o caminho "+file);
        }
        
    }
}
