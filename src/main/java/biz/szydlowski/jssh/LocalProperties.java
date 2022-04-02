/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.szydlowski.jssh;

import biz.szydlowski.utils.OSValidator;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author Dominik
 */
public class LocalProperties {
 
    Properties prop = null;
       
     /** Konstruktor pobierający parametry z pliku konfiguracyjnego "config.xml"
     */
     public  LocalProperties (){
         
               
     }
    
     
             
    public void loadLocalProps(String local) {
              
        if (OSValidator.isUnix()){
              String absolutePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
                   absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
              local = absolutePath + "/" + local;
        }
           
       
        try (InputStream input = new FileInputStream(local)) {

            prop = new Properties();

            // load a properties file
            prop.load(input);

        } catch (IOException ex) {
           // ex.printStackTrace();
            prop = new Properties();
        }
               

    }
    
    
   public Set<Object> getAllKeys(){
        Set<Object> keys = prop.keySet();
        return keys;
    }
     
    public String getPropertyValue(String key){
        return this.prop.getProperty(key);
    }
    
    public boolean isLoaded(){
        if (prop==null) return false;
        else return true;
    }


  
 

}