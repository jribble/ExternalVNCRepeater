package com.novell.cm.ui.server.vnc;

import com.netiq.websockify.Websockify;

public class ExternalVNCRepeater
{
   public static void main(String[] args) throws Exception {
       // Validate command line options.
       if (args.length != 2 && args.length != 3) {
           System.err.println(
                   "Usage: " + Websockify.class.getSimpleName() +
                   " <local port> <CMAS base URL> [encrypt]");
           return;
       }
       
       if (args.length == 3) {
           String keyStoreFilePath = System.getProperty("keystore.file.path");
           if (keyStoreFilePath == null || keyStoreFilePath.isEmpty()) {
               System.out.println("ERROR: System property keystore.file.path not set. Exiting now!");
               System.exit(1);
           }

           String keyStoreFilePassword = System.getProperty("keystore.file.password");
           if (keyStoreFilePassword == null || keyStoreFilePassword.isEmpty()) {
               System.out.println("ERROR: System property keystore.file.password not set. Exiting now!");
               System.exit(1);
           }
       }

       // Parse command line options.
       int localPort = Integer.parseInt(args[0]);
       String cmasBaseUrl = args[1];
       boolean useSSL = args.length < 3 ? false : true;

       System.out.println(
               "Websockify Proxying *:" + localPort + " to workloads defined by CMAS at " +
               cmasBaseUrl + " ...");
       if(useSSL) System.out.println("Websocket communications are SSL encrypted.");

       Websockify ws = new Websockify ( );
       ws.connect ( localPort, new CMASRestResolver ( cmasBaseUrl ), useSSL, false );
       
   }
}
