/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.szydlowski.jssh;

import java.util.Properties;

/**
 *
 * @author Dominik
 */
public class CommandLine {
    
  String version = "1.11 b20201109"; 
   
  public Properties handleCmdLine(String[] args) {
      
    
     Properties cmdProps = new Properties();
     
     cmdProps.put("user", "user");
     cmdProps.put("password", "pswd");
     cmdProps.put("hostname", "host");
     cmdProps.put("input", "default_input_filename");   
     cmdProps.put("cmd", "cmd"); 
     cmdProps.put("output", "output_filename");
     cmdProps.put("timeout", "5000");
     cmdProps.put("waittime", "1000");
     cmdProps.put("idletime", "8000");
     cmdProps.put("sets", "false");
     cmdProps.put("separator", "default");
     cmdProps.put("local", "default"); 
     cmdProps.put("debug", "disable");
     cmdProps.put("echo", "false");
      
     int sets=0;
      
     for (int i = 0; i < args.length; i++) {
       String arg = args[i];
       if (arg.regionMatches(0, "-", 0, 1))  {
        try
         {
           switch (arg.charAt(1))
           { 
               
           case 'd':
             i++;
             cmdProps.setProperty("debug", "enable");
             System.out.println("Debug enabled...");
             break;  
           
           case 'e':
             i++;
             cmdProps.setProperty("echo", "true");
             System.out.println("ECHO enabled...");
             break; 
             
           case 'u':
             i++;
             cmdProps.setProperty("user", args[i]);
             sets++;
             break;  
           
           case 'w':
             i++;
             cmdProps.setProperty("waittime", args[i]);
             break;
           
           case 't':
             i++;
             cmdProps.setProperty("timeout", args[i]);
             break;
           
           case 'c':
             i++;
             cmdProps.setProperty("cmd", args[i]); 
             cmdProps.put("input", "default");
             sets++;
             break;
               
          case 'p':
              i++;
             cmdProps.setProperty("password", args[i]);
             sets++;
             break;
       
          case 'h':
              i++;
             cmdProps.setProperty("hostname", args[i]);
             sets++;
             break;
              
         case 'i':
             i++;
             cmdProps.setProperty("input", args[i]);
             cmdProps.put("cmd", "default");
             sets++;
             break;  
         
         case 'o':
             i++;
             cmdProps.setProperty("output", args[i]);
             break; 
         
         case 's':
             i++;
             cmdProps.setProperty("separator", args[i]);
             break; 
         
         case 'r':
             i++;
             cmdProps.setProperty("idletime", args[i]);
             break;  
         
         case 'l':
             i++;
             cmdProps.setProperty("local", args[i]);
             break; 
   
  
           default:
              printUsage();
           }
         }
         catch (ArrayIndexOutOfBoundsException ae)
         {
           printUsage();
         }
       }
     }
     if (sets==4) cmdProps.put("sets", "true");
     return cmdProps;
 }

  protected void printUsage()
   {
     System.out.println("jSSH by Dominik Szydlowski - support@szydlowski.biz ver " + version);
     System.out.println("jSSH -u User -p Password -h hostname [-c execute_command | -i input_filename] -o output_filename -t timeout -w waittime -r idletime -s separator");
     System.exit(0);
  } 
  
  protected void printSimpleUsage()
   {
     System.out.println("jSSH by Dominik Szydlowski - support@szydlowski.biz ver " + version);
  }

}