package com.novell.cm.ui.server.vnc;


import java.io.PrintStream;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.netiq.websockify.WebsockifyServer;
import com.netiq.websockify.WebsockifyServer.SSLSetting;



public class ExternalVNCRepeater
{   
   @Option(name="--help",usage="show this help message and quit")
   private boolean showHelp = false;
   
   @Option( name = "--enable-ssl", usage = "enable SSL" )
   private boolean       enableSSL  = false;

   @Option( name = "--ssl-only", usage = "disallow non-encrypted connections" )
   private boolean       requireSSL = false;
   
   @Option(name="--keystore",usage="path to a java keystore file. Required for SSL.")
   private String keystore = null;
   
   @Option(name="--keystore-password",usage="password to the java keystore file. Required for SSL.")
   private String keystorePassword = null;

   @Argument( index = 0, metaVar = "source_port", usage = "(required) local port the websockify server will listen on", required = true )
   private int           sourcePort;

   @Argument( index = 1, metaVar = "cmas_base_url", usage = "(required) the base URL of the CMAS server", required = true )
   private String        cmasBaseUrl;

   private CmdLineParser parser;


   public ExternalVNCRepeater()
   {
      parser = new CmdLineParser ( this );
   }


   public void printUsage( PrintStream out )
   {
      out.println ( "Usage:" );
      out.println ( " java -jar external-vnc-repeater.jar [options] source_port cmas_base_url" );
      out.println ( );
      out.println ( "Options:" );
      parser.printUsage ( out );
      out.println ( );
      out.println ( "Example:" );
      out.println ( " java -jar external-vnc-repeater.jar 5900 https://cloud.acmecloud.demo" );
   }
   
   public static void main(String[] args) throws Exception {
     new ExternalVNCRepeater().doMain(args);
   }
   
   public void doMain(String[] args) throws Exception
   {
      parser.setUsageWidth ( 80 );

      try
      {
         parser.parseArgument ( args );
      }
      catch ( CmdLineException e )
      {
         System.err.println ( e.getMessage ( ) );
         printUsage ( System.err );
         return;
      }

      if ( showHelp )
      {
         printUsage ( System.out );
         return;
      }
      
      SSLSetting sslSetting = SSLSetting.OFF;
      if ( requireSSL ) sslSetting = SSLSetting.REQUIRED;
      else if ( enableSSL ) sslSetting = SSLSetting.ON;

      
      if ( sslSetting != SSLSetting.OFF ) {
          if (keystore == null || keystore.isEmpty()) {
              System.out.println("No keystore specified.");
          printUsage(System.err);
              System.exit(1);
          }

          if (keystorePassword == null || keystorePassword.isEmpty()) {
              System.out.println("No keystore password specified.");
          printUsage(System.err);
              System.exit(1);
          }
      }

      System.out.println ( "Proxying *:" + sourcePort + " to workloads defined by CMAS at " + cmasBaseUrl + " ..." );
      if(sslSetting != SSLSetting.OFF) System.out.println("SSL is " + (sslSetting == SSLSetting.REQUIRED ? "required." : "enabled."));

      WebsockifyServer ws = new WebsockifyServer ( );
      ws.connect ( sourcePort, new CMASRestResolver ( cmasBaseUrl ), sslSetting, keystore, keystorePassword, null );

   }
}
