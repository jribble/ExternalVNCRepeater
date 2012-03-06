package com.novell.cm.ui.server.vnc;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.json.simple.parser.JSONParser;
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

   @Test
   public void testResultParsing ( ) throws Exception
   {
      String host = "4.3.2.1";
      byte[] hostbytes = {0x4, 0x3, 0x2, 0x1};
      int port = 5646;
      String jsonResponse = "{\"name\":\"name\",\"host\":\"" + host + "\",\"port\":" + port + "}";
      String cmasBaseUrl = "http://test/";
      JSONParser parser = new JSONParser();
      Client client = mock ( Client.class );
      ClientResponse clientResponse = mock ( ClientResponse.class );
      ChannelEvent e = mock ( ChannelEvent.class );
      Channel channel = mock ( Channel.class );
      InetSocketAddress remoteAddress = new InetSocketAddress ( InetAddress.getByAddress ( null, hostbytes ), port  );
      
      when ( e.getChannel ( ) ).thenReturn ( channel );
      when ( channel.getRemoteAddress ( ) ).thenReturn ( remoteAddress );
      
      WebResource webResource = mock ( WebResource.class );
      Builder builder = PowerMockito.mock ( Builder.class );
      
      when ( client.resource ( (String) any() ) ).thenReturn ( webResource );
      when ( webResource.accept ( (String) any() ) ).thenReturn ( builder );
      PowerMockito.when ( builder.get ( ClientResponse.class ) ).thenReturn ( clientResponse );
      when ( clientResponse.getStatus ( ) ).thenReturn ( 200 );
      when ( clientResponse.getEntity(String.class)).thenReturn ( jsonResponse );
      
      CMASRestResolver resolver = new CMASRestResolver ( cmasBaseUrl, parser, client );
      
      InetSocketAddress resultAddress = resolver.resolveTarget ( e );
      
      assertEquals ( host, resultAddress.getHostName ( ) );
      assertEquals ( port, resultAddress.getPort ( ) );
   }

   @Test
   public void testBaseUrlHandlingTrailingDelimiter ( ) throws Exception
   {
      String host = "4.3.2.1";
      byte[] hostbytes = {0x4, 0x3, 0x2, 0x1};
      int port = 5646;
      String jsonResponse = "{\"name\":\"name\",\"host\":\"" + host + "\",\"port\":" + port + "}";
      String cmasBaseUrl = "http://test/";
      JSONParser parser = new JSONParser();
      Client client = mock ( Client.class );
      ClientResponse clientResponse = mock ( ClientResponse.class );
      ChannelEvent e = mock ( ChannelEvent.class );
      Channel channel = mock ( Channel.class );
      InetSocketAddress remoteAddress = new InetSocketAddress ( InetAddress.getByAddress ( null, hostbytes ), port  );
      
      when ( e.getChannel ( ) ).thenReturn ( channel );
      when ( channel.getRemoteAddress ( ) ).thenReturn ( remoteAddress );
      
      WebResource webResource = mock ( WebResource.class );
      Builder builder = PowerMockito.mock ( Builder.class );
      
      when ( client.resource ( (String) any() ) ).thenReturn ( webResource );
      when ( webResource.accept ( (String) any() ) ).thenReturn ( builder );
      PowerMockito.when ( builder.get ( ClientResponse.class ) ).thenReturn ( clientResponse );
      when ( clientResponse.getStatus ( ) ).thenReturn ( 200 );
      when ( clientResponse.getEntity(String.class)).thenReturn ( jsonResponse );
      
      CMASRestResolver resolver = new CMASRestResolver ( cmasBaseUrl, parser, client );
      
      InetSocketAddress resultAddress = resolver.resolveTarget ( e );
      
      verify(client).resource ( cmasBaseUrl + "uirest/vnc/registration/" + host );
      
      assertEquals ( host, resultAddress.getHostName ( ) );
      assertEquals ( port, resultAddress.getPort ( ) );
   }

   @Test
   public void testBaseUrlHandlingNoTrailingDelimiter ( ) throws Exception
   {
      String host = "4.3.2.1";
      byte[] hostbytes = {0x4, 0x3, 0x2, 0x1};
      int port = 5646;
      String jsonResponse = "{\"name\":\"name\",\"host\":\"" + host + "\",\"port\":" + port + "}";
      String cmasBaseUrl = "http://test";
      JSONParser parser = new JSONParser();
      Client client = mock ( Client.class );
      ClientResponse clientResponse = mock ( ClientResponse.class );
      ChannelEvent e = mock ( ChannelEvent.class );
      Channel channel = mock ( Channel.class );
      InetSocketAddress remoteAddress = new InetSocketAddress ( InetAddress.getByAddress ( null, hostbytes ), port  );
      
      when ( e.getChannel ( ) ).thenReturn ( channel );
      when ( channel.getRemoteAddress ( ) ).thenReturn ( remoteAddress );
      
      WebResource webResource = mock ( WebResource.class );
      Builder builder = PowerMockito.mock ( Builder.class );
      
      when ( client.resource ( (String) any() ) ).thenReturn ( webResource );
      when ( webResource.accept ( (String) any() ) ).thenReturn ( builder );
      PowerMockito.when ( builder.get ( ClientResponse.class ) ).thenReturn ( clientResponse );
      when ( clientResponse.getStatus ( ) ).thenReturn ( 200 );
      when ( clientResponse.getEntity(String.class)).thenReturn ( jsonResponse );
      
      CMASRestResolver resolver = new CMASRestResolver ( cmasBaseUrl, parser, client );
      
      InetSocketAddress resultAddress = resolver.resolveTarget ( e );
      
      verify(client).resource ( cmasBaseUrl + "/uirest/vnc/registration/" + host );
      
      assertEquals ( host, resultAddress.getHostName ( ) );
      assertEquals ( port, resultAddress.getPort ( ) );
   }
}
