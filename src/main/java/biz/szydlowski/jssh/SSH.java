/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.szydlowski.jssh;

/**
 *
 * @author Intel-i3
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.List;
import java.util.Set;
/**
 *
 * @author World
 */
public class SSH  {

           
        public static void main(String[] args){

              CommandLine _CommandLine = new CommandLine();

              Properties cmdProps = _CommandLine.handleCmdLine(args);

              if (args.length<4) _CommandLine.printUsage();
              
              LocalProperties LPr = new LocalProperties();
              
              if (cmdProps.getProperty("local").equalsIgnoreCase("default")){
                  LPr.loadLocalProps("local.props");
                  if ( LPr.isLoaded()){
                         System.out.println("Load default properties file.");
                  }
              } else {
                    LPr.loadLocalProps(cmdProps.getProperty("local"));
                   if ( LPr.isLoaded()){
                        System.out.println("Load properties file " +cmdProps.getProperty("local"));
                  }
              }
               String tmp = ""; 
               tmp = cmdProps.getProperty("user");
               Set<Object> keys =  LPr.getAllKeys();
               for(Object k:keys){
                    String key = (String)k;
                    //System.out.println(key+"->"+LPr.getPropertyValue(key));                   
                    tmp=tmp.replaceAll(key, LPr.getPropertyValue(key));
                   
               }
               
               cmdProps.setProperty("user", tmp);  
               
               tmp = cmdProps.getProperty("password");
               for(Object k:keys){
                    String key = (String)k;
                    //System.out.println(key+"->"+LPr.getPropertyValue(key));                   
                    tmp=tmp.replaceAll(key, LPr.getPropertyValue(key));                 
               }
              
               cmdProps.setProperty("password", tmp);
          
            //   System.out.println(cmdProps.getProperty("password")); 
              
              if (cmdProps.getProperty("sets").equalsIgnoreCase("false")) {
                  _CommandLine.printUsage();
              }

              SShCommander _SShCommander = new SShCommander();
              List<String> commands_prepare = new ArrayList<>();
              
              if (!cmdProps.getProperty("input").equalsIgnoreCase("default")){          
                  commands_prepare = _SShCommander.readFromFile(cmdProps.getProperty("input"), cmdProps.getProperty("separator"));
              } else {
                  String separator=";";
                  if (cmdProps.getProperty("separator").equalsIgnoreCase("default")){
                     separator=";"; 
                  } else separator= cmdProps.getProperty("separator");
                 
                  String cmds [] = cmdProps.getProperty("cmd").split(separator);
                  commands_prepare.addAll(Arrays.asList(cmds));
              }
             // System.out.println( cmdProps.getProperty("cmd"));
             
             for (int i=0; i<commands_prepare.size();i++){
                 tmp = commands_prepare.get(i);
                 for(Object k:keys){
                        String key = (String)k;
                     //   System.out.println(key+"->"+LPr.getPropertyValue(key));
                        tmp = tmp.replaceAll(key, LPr.getPropertyValue(key));
                       // System.out.println(tmp);
                 }
                 commands_prepare.set(i, tmp);
             }  
             
            /* List<String> commands_exec = new ArrayList<>();
             for (int i=0; i<commands_prepare.size();i++){
                 commands_exec.add(commands_prepare.get(i));
                 if (commands_prepare.get(i).startsWith("sudo su"))  commands_exec.add(cmdProps.getProperty("password"));
                 
             }*/
            
            // System.out.println(cmdProps.getProperty("password"));
                         
             _CommandLine.printSimpleUsage();
            _SShCommander.executeCommands(Boolean.getBoolean(cmdProps.getProperty("echo")), cmdProps.getProperty("hostname"), cmdProps.getProperty("user"), cmdProps.getProperty("password"), 
                    cmdProps.getProperty("timeout"),cmdProps.getProperty("waittime"), cmdProps.getProperty("idletime"), commands_prepare);


        }
        
     

}