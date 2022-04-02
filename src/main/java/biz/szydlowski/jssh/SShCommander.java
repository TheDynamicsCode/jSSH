/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.szydlowski.jssh;

import java.io.*;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author dominik
 */
public class SShCommander {


    private boolean running=true;

    private final int sshBufferSize = 8192;
    private boolean writeLock = false;
    private List<String> commands;
    private int currentCommandId=0;
    private int timeWaitThread = 100;
    private int idleWait = 8000;
    private boolean echo=true;


    public void executeCommands(boolean echo, String hostname, String username, String password, String timeout, String _timeWaitThread, String _idleWait, List<String> commands) {
                this.commands = commands;
                this.echo=echo;
                try {
                   // System.out.println(username+"|"+password);
                     
                    JSch conn = new JSch();

                    if (echo) System.out.println("Connection to " + hostname);

                    Session session = conn.getSession(username, hostname, 22);
                    session.setPassword(password);
                    session.setConfig("StrictHostKeyChecking", "no");
                    session.setConfig("PreferredAuthentications",  "password, publickey,keyboard-interactive");
                
                    timeWaitThread = Integer.parseInt(_timeWaitThread);
                    idleWait = Integer.parseInt(_idleWait);
                    
                    session.setTimeout(Integer.parseInt(timeout));

                    if (echo) System.out.println("Connecting SSH to " + hostname + " - Please wait for few seconds... ");
                    session.connect();


                    ChannelShell channel = (ChannelShell)session.openChannel("shell");
                                       
                    channel.connect();  
                    
                    if (echo) System.out.println("Connected to " + hostname + "!");


                    //commands.add("logout");

                    try {
                        ( new Thread( new CommandReader(channel.getInputStream(), session, channel), "CommandReader" ) ).start();
                    } catch (IOException e1){

                    }


                    try {
                        ( new Thread( new CommandWriter(channel.getOutputStream() ), "CommandWriter" ) ).start();
                    } catch (IOException e2){

                    }     


                }
                catch (JSchException ex) {
                    System.out.println("Fatal error while executing command(s).");
                    System.out.println(ex.toString());           
                    running=false;
                } 
    }

     
   public  List<String> readFromFile(String filename, String separator) {
               
         BufferedReader br = null;
         List<String> _commands = new ArrayList<>();
         StringBuilder cmd = new StringBuilder();
                
        try {

                    String sCurrentLine;

                    br = new BufferedReader(new FileReader(filename));

                    while ((sCurrentLine = br.readLine()) != null) {
                           if (separator.equalsIgnoreCase("default")) {
                                _commands.add(sCurrentLine);                                   
                           } else {
                                cmd.append(sCurrentLine).append("\n");                                                        
                           }
                    }

        } catch (IOException e) {
              // e.printStackTrace(); 
               System.err.print("File Not Found: ");
               System.err.println(filename);
               System.exit(0);
        } finally {
                    try {
                            if (br != null)br.close();
                    } catch (IOException ex) {
                            System.exit(0);
                    }
        }
        if (!separator.equalsIgnoreCase("disable") && !separator.equalsIgnoreCase("default")) {
             for (String s : cmd.toString().split(separator)) _commands.add(s);
        }
        
        if (separator.equalsIgnoreCase("disable") ) _commands.add(cmd.toString());
        
        return  _commands;

  }
   
    public class CommandReader implements Runnable {
 
            InputStream in; 
            Session session;
            Channel channel;
            
            public CommandReader( InputStream in,  Session session, Channel channel ) {
              this.in = in;
              this.session=session;
              this.channel=channel;
            }

            @Override
            public void run() { 
                byte[] buffer = new byte[sshBufferSize];
                String line = "";
                long idle= System.currentTimeMillis();
                while (running){
                   try {
                       while (in.available() > 0) {
                                int i = in.read(buffer, 0, sshBufferSize);
                                if (i < 0) {
                                    break;
                                }
                                line = new String(buffer, 0, i);
                                System.out.println(line);                                
                                idle = System.currentTimeMillis();
                        }
                    } catch(Exception e){
                        System.out.println("Error while reading channel output: "+ e);                        
                    }
                               
                   // System.out.println("currentCommandId " + currentCommandId);
                  //  System.out.println("commands.size() " + commands.size());
                   // System.out.println("commands.size() " + commands.get(commands.size()-1));
                     
                    if (currentCommandId>0){
                        if (System.currentTimeMillis()-idle>100 && currentCommandId==commands.size() && (commands.get(commands.size()-1).contains("logout") || commands.get(commands.size()-1).contains("exit"))){
                            running=false; 

                            try {
                                channel.disconnect();
                                session.disconnect(); 
                            } catch (Exception e){

                            }
                        }
                    }
                  
                    if (System.currentTimeMillis()-idle>idleWait && currentCommandId==commands.size() ){
                        running=false; 
                        if (echo) System.out.println("Detected idle.... close");
                        System.out.println("Disconnected channel and session");
                
                        try {
                            if (channel.isClosed()) {
                                if (echo)  System.out.println("exit-status: " + channel.getExitStatus());
                            }
                            channel.disconnect();                            
                            session.disconnect(); 
                        } catch (Exception e){
                            
                        }
                          
                    }
                    
                    try {
                            Thread.sleep(timeWaitThread);
                    }  catch (InterruptedException e)    {
                             Thread.currentThread().interrupt(); // restore interrupted status
                    }
               }
            }
      } 
      
      public class CommandWriter implements Runnable {
 
            OutputStream outStream; 
            
            
            public CommandWriter( OutputStream outStream) {
              this.outStream = outStream;
            }

            @Override
            public void run() {
                
                while (running) {
                    if (currentCommandId==commands.size()){
                         writeLock=true;
                    } 
                    if (!writeLock){
                        writeLock=true;
                        if (echo) System.out.println("Execute " +  commands.get(currentCommandId));

                        try {

                             outStream.write((commands.get(currentCommandId)+"\n").getBytes());
                             outStream.flush();
                             
                             currentCommandId++;

                        } catch(Exception e){
                            System.out.print("Error while sending commands: "+ e);
                        } finally {
                            if (currentCommandId==commands.size()){
                                //running=false;
                                 writeLock=true;
                            } 
                            writeLock=false;
                        }
                    }

                    try {
                            Thread.sleep(timeWaitThread);
                    }  catch (InterruptedException e)    {
                                Thread.currentThread().interrupt(); // restore interrupted status
                    }
                    
                }
            }
      }
 
   

}