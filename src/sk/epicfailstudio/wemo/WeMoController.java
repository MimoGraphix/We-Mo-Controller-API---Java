package sk.epicfailstudio.wemo;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/*!
 * WeMo Switch Controller Api
 * version: 1.0 (08/08/2013)
 *
 * Copyright 2013 Mário Čechovič (MimoGraphix) - mimographix@gmail.com
 *
 */

public class WeMoController {

    /**
     * Host IP
     */
    private String host = null;

    /**
     * Port
     */
    private int port = 49153;

    /**
     *
     * @param ip
     */
    public WeMoController( String ip )
    {
        this.host = ip;
    }

    /**
     *
     * @param ip
     * @param port
     */
    public WeMoController( String ip, int port )
    {
        this.host = ip;
        this.port = port;
    }

    /**
     * Return actual Switch state (ON/OFF)
     * @return
     * @throws IOException
     */
    public boolean GetState()
            throws IOException
    {
        String response = this.runCommand( "GetBinaryState", "" );

        if( response.contains( "<BinaryState>1</BinaryState>" ) )
        {
            return true;
        }

        return false;
    }

    /**
     * Switch ON a WeMo Controller
     * @throws IOException
     */
    public void On()
            throws IOException
    {
        this.runCommand( "SetBinaryState", "<BinaryState>1</BinaryState>" );
    }

    /**
     * Switch OFF a WeMo Controller
     * @throws IOException
     */
    public void Off()
            throws IOException
    {
        this.runCommand( "SetBinaryState", "<BinaryState>0</BinaryState>" );
    }

    /**
     * Send XML command to WeMo Switch
     * @param function
     * @param value
     * @return
     * @throws IOException
     */
    private String runCommand( String function, String value)
            throws IOException
    {
        String urn = "urn:Belkin:service:basicevent:1";
        String path = "/upnp/control/basicevent1";
        String xmldata = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
                " <s:Body>\n" +
                "  <u:" + function + " xmlns:u=\"" + urn + "\">\n" +
                "   " + value + "\n" +
                "  </u:" + function + ">\n" +
                " </s:Body>\n" +
                "</s:Envelope>";


        InetAddress addr = InetAddress.getByName( this.host );
        Socket sock = new Socket(addr, this.port);

        BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(),"UTF-8"));

        String header = "POST " + path + " HTTP/1.1\r\n"
                        +"SOAPACTION: \"" + urn + "#" + function + "\"\r\n"
                        +"Host: " + this.host + ":" + this.port + "\r\n"
                        +"Content-Length: " + xmldata.length() + "\r\n"
                        +"Content-Type: text/xml; charset=\"utf-8\"\r\n\r\n";

        wr.write( header );
        wr.write(xmldata);
        wr.flush();

        BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        String line;
        String response = "";
        while((line = rd.readLine()) != null)
        {
            response += line + "\r\n";
        }

        return response;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
