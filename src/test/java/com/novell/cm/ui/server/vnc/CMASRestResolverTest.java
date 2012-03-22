package com.novell.cm.ui.server.vnc;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({InetSocketAddress.class,WebResource.Builder.class})
public class CMASRestResolverTest
{
   String host = null;
   byte[] hostbytes;
   int port;
   String jsonResponse = null;
   String cmasBaseUrl = null;
   JSONParser parser = null;
   Client client = null;
   ClientResponse clientResponse = null;
   ChannelEvent e = null;
   Channel channel = null;
   InetSocketAddress remoteAddress = null;
   
   WebResource webResource = null;
   Builder builder = null;
   
   @Before
   public void setup ( ) throws Exception
   {
      host = "4.3.2.1";
      hostbytes = new byte[] {0x4, 0x3, 0x2, 0x1};
      port = 5646;
      jsonResponse = "{\"name\":\"name\",\"host\":\"" + host + "\",\"port\":" + port + "}";
      cmasBaseUrl = "http://test/";
      parser = new JSONParser();
      client = mock ( Client.class );
      clientResponse = mock ( ClientResponse.class );
      e = mock ( ChannelEvent.class );
      channel = mock ( Channel.class );
      remoteAddress = new InetSocketAddress ( InetAddress.getByAddress ( null, hostbytes ), port  );
      
      when ( e.getChannel ( ) ).thenReturn ( channel );
      when ( channel.getRemoteAddress ( ) ).thenReturn ( remoteAddress );
      
      webResource = mock ( WebResource.class );
      builder = PowerMockito.mock ( Builder.class );
      
      when ( client.resource ( (String) any() ) ).thenReturn ( webResource );
      when ( webResource.accept ( (String) any() ) ).thenReturn ( builder );
      PowerMockito.when ( builder.post ( ClientResponse.class ) ).thenReturn ( clientResponse );
      when ( clientResponse.getStatus ( ) ).thenReturn ( 200 );
      when ( clientResponse.getEntity(String.class)).thenReturn ( jsonResponse );
      
   }

   @Test
   public void testResultParsing ( ) throws Exception
   {      
      CMASRestResolver resolver = new CMASRestResolver ( cmasBaseUrl, parser, client );
      
      InetSocketAddress resultAddress = resolver.resolveTarget ( e.getChannel ( ) );
      
      assertEquals ( host, resultAddress.getHostName ( ) );
      assertEquals ( port, resultAddress.getPort ( ) );
   }

   @Test
   public void testBaseUrlHandlingTrailingDelimiter ( ) throws Exception
   {                      
      cmasBaseUrl = "http://test/";
      CMASRestResolver resolver = new CMASRestResolver ( cmasBaseUrl, parser, client );
      
      InetSocketAddress resultAddress = resolver.resolveTarget ( e.getChannel ( ) );
      
      verify(client).resource ( cmasBaseUrl + "uirest/vnc/registration/" + host );
      
      assertEquals ( host, resultAddress.getHostName ( ) );
      assertEquals ( port, resultAddress.getPort ( ) );
   }

   @Test
   public void testBaseUrlHandlingNoTrailingDelimiter ( ) throws Exception
   {                      
      cmasBaseUrl = "http://test";
      CMASRestResolver resolver = new CMASRestResolver ( cmasBaseUrl, parser, client );
      
      InetSocketAddress resultAddress = resolver.resolveTarget ( e.getChannel ( ) );
      
      verify(client).resource ( cmasBaseUrl + "/uirest/vnc/registration/" + host );
      
      assertEquals ( host, resultAddress.getHostName ( ) );
      assertEquals ( port, resultAddress.getPort ( ) );
   }
}
