/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.server.config;

/**
 * @author Arriety
 */
public final class ConfigDB {

    public static final int DATABASE_DYNAMIC = 0;
    public static final int DATABASE_STATIC = 1;
    public static final int DATABASE_ENTITY = 2;
    
    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    // Database dynamic
    public static final String DB_DYNAMIC_HOST = "localhost";
    public static final int DB_PORT = 3306;
    public static final String DB_DYNAMIC_NAME = "nro_dynamic";
    public static final String DB_DYNAMIC_USER = "root";
    public static final String DB_DYNAMIC_PASSWORD = "";
    public static final String DB_DYNAMIC_URL = "jdbc:mysql://" + DB_DYNAMIC_HOST + ":" + DB_PORT + "/" + DB_DYNAMIC_NAME;

    // Database static
    public static final String DB_STATIC_HOST = "localhost";
    public static final int DB_STATIC_PORT = 3306;
    public static final String DB_STATIC_NAME = "nro_static";
    public static final String DB_STATIC_USER = "root";
    public static final String DB_STATIC_PASSWORD = "";
    public static final String DB_STATIC_URL = "jdbc:mysql://" + DB_STATIC_HOST + ":" + DB_STATIC_PORT + "/" + DB_STATIC_NAME;

    // Database entity
    public static final String DB_ENTITY_HOST = "localhost";
    public static final int DB_ENTITY_PORT = 3306;
    public static final String DB_ENTITY_NAME = "nro_entity";
    public static final String DB_ENTITY_USER = "root";
    public static final String DB_ENTITY_PASSWORD = "";
    public static final String DB_ENTITY_URL = "jdbc:mysql://" + DB_ENTITY_HOST + ":" + DB_ENTITY_PORT + "/" + DB_ENTITY_NAME;

}
