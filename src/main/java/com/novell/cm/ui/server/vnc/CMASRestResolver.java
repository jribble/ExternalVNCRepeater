package com.novell.cm.ui.server.vnc;

import java.net.InetSocketAddress;

import org.jboss.netty.channel.Channel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.netiq.websockify.IProxyTargetResolver;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class CMASRestResolver implements IProxyTargetResolver
{
   
   private String cmasBaseUrl;
   private JSONParser parser;
   private Client client;
   
   public CMASRestResolver ( String cmasBaseUrl )
   {
      this ( cmasBaseUrl, new JSONParser(), Client.create() );
   }
   
   public CMASRestResolver ( String cmasBaseUrl, JSONParser parser, Client client )
   {
      this.cmasBaseUrl = cmasBaseUrl;
      this.parser = parser;
      this.client = client;
   }

   public InetSocketAddress resolveTarget( Channel channel )
   {
      // make rest call to cmas to get registration      
      String remoteIp = ((InetSocketAddress)channel.getRemoteAddress ( )).getAddress ( ).getHostAddress ( );
 
      WebResource webResource = client.resource(cmasBaseUrl + (cmasBaseUrl.endsWith ( "/" ) ? "" : "/") + "uirest/vnc/registration/" + remoteIp );
 
      ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
 
      if (response.getStatus() != 200) {
         return null;
      }
 
      String output = response.getEntity(String.class);
      
      String host = null;
      int port = -1;
      
      JSONObject jsonObj;
      try
      {
         jsonObj = (JSONObject) parser.parse( output );
         if ( jsonObj != null && jsonObj.containsKey( "host" ) )
         {
            host = jsonObj.get( "host" ).toString ( );
         }
         if ( jsonObj != null && jsonObj.containsKey ( "port" ))
         {
            port = Integer.parseInt ( jsonObj.get ( "port" ).toString ( ) );
         }
      }
      catch ( ParseException e1 )
      {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }
      
      // if we got something, return it as the connection target
      if ( host != null && port != -1 )
      {
         return new InetSocketAddress ( host, port );
      }
      
      // otherwise return null
      return null;
   }

}
