package config;

import android.os.Environment;

import java.net.URL;

public class Configuration {

	public static final int DEVELOPMENT = 1;
	public static final int TEST 		= 2;
	public static final int PRODUCTION 	= 3;
	public static final int DEFAULT 	= PRODUCTION;
	
	//public final static String PREFIXE_DB 		= Configuration.class.getProtectionDomain().getCodeSource().getLocation().getFile()+"db/";
	public final static String PREFIXE_DB 		= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
	public final static String DEVELOPMENT_DB 	= PREFIXE_DB + "development/";
	public final static String TEST_DB 			= PREFIXE_DB + "test/";
	public final static String PRODUCTION_DB 	= PREFIXE_DB + "/production/";
//	private static String DEVELOPMENT_DB 	= PREFIXE_DB + "development/";
//	private static String TEST_DB 			= PREFIXE_DB + "test/";
//	private static String PRODUCTION_DB 	= PREFIXE_DB + "production/";
	
	private static Configuration instance;
	
	private int enviroment;
	private String dbUse;
	
	public static Configuration getInstance(){
		if(instance == null){
			instance = new Configuration(DEFAULT);
		}
		return instance;
	}
	
	public static Configuration getInstance(int enviroment){
		if(instance == null){
			instance = new Configuration(enviroment);
		}
		return instance;
	}
	
	private Configuration(int enviroment){
		this.setEnviroment(enviroment);
		this.setDbUse(enviroment);
	}
	
	private void setEnviroment(int val)throws IllegalArgumentException{
		if(!(val == DEVELOPMENT ||
				val == TEST ||
				val == PRODUCTION)){
			throw new IllegalArgumentException(this.getClass().toString() + ".enviroment inválido");
		}
		this.enviroment = val;
	}
	
	private void setDbUse(int env){
		if(env == DEVELOPMENT){
			this.setDbUse(DEVELOPMENT_DB);
		}else if(env == TEST){
			this.setDbUse(TEST_DB);
		}else if(env == PRODUCTION){
			this.setDbUse(PRODUCTION_DB);
		}else{
			throw new IllegalArgumentException(this.getClass().toString() + ".environment inválido!");
		}
	}
	
	private void setDbUse(String val){
		if(val == null || val.trim().equalsIgnoreCase("")){
			throw new IllegalArgumentException(this.getClass().toString() + ".dbUse inválido!");
		}
		this.dbUse = val;
	}
	
	public int currentEnv(){
		return this.enviroment;
	}
	
	public String currentDB(){
		return this.dbUse;
	}

}
