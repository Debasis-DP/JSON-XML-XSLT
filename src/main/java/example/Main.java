/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import java.util.Locale;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.io.StreamUtils;

import org.milyn.payload.StringResult;
import org.xml.sax.InputSource;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;

// For write operation
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

import java.io.BufferedWriter;
import javax.xml.transform.*;

/**
 *
 * @author Debasis
 */
public class Main {

    private static byte[] messageIn = readInputMessage();
    
    private final Smooks smooks;
    
    static Document document;
    
    protected Main() throws IOException, SAXException, SmooksException {
        smooks = new Smooks("smooks-config.xml");
    }
    protected String runSmooksTransform(ExecutionContext executionContext) throws IOException,SAXException, SmooksException{
        try{
            //Locale defaultLocale = Locale.getDefault();
            //Locale.setDefault(new Locale("en","IN"));
            
            StringResult result = new StringResult();
            
            smooks.filterSource(executionContext, new StreamSource(new ByteArrayInputStream(messageIn)), result);
            //Locale.setDefault(defaultLocale);
            return result.toString();
            
        } finally {
            smooks.close();
        }
        
    }

    
    public static void main(String[] args) throws IOException, SAXException, SmooksException {
       System.out.println("_______________________Original JSON file_____________________\n");
       System.out.println(new String(messageIn));
       System.out.println("______________________________________________________________\n");
       
       String out_file = "intermediateXML.xml";
       
       Main mainSmooks = new Main();
       ExecutionContext executionContext = mainSmooks.smooks.createExecutionContext();
       String out = mainSmooks.runSmooksTransform(executionContext);
       String indented = format(out);
       System.out.println("______________________Intermediate XML______________________________\n");
       System.out.println(indented);
       System.out.println("_______________________________________________________________\n");
       
       try {
            BufferedWriter bufferedWriter_out = new BufferedWriter( new FileWriter (out_file));
            bufferedWriter_out.write(indented);
            bufferedWriter_out.close();       
        } catch (IOException e) {
            e.printStackTrace();
        }
       Main.main1();
    }
    
    private static byte[] readInputMessage() {
        try {
            return StreamUtils.readStream(new FileInputStream("input-message.jsn"));
        } catch (IOException e) {
            e.printStackTrace();
            return "<no-message />".getBytes();
        }
    }
    
    public String runSmooksTransform() throws IOException, SAXException {
        ExecutionContext executionContext = smooks.createExecutionContext();
        return runSmooksTransform(executionContext);
    }
    
    private static Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String format(String unformattedXml) {
        try {
            Document document = parseXmlFile(unformattedXml);
 
            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);
 
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
 
    }
    public static void main1() {
     
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        
        try {
            File stylesheet = new File("article.xsl");
            File datafile = new File("intermediateXML.xml");
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(datafile);
            
            // Use a Transformer for output
            TransformerFactory tFactory = TransformerFactory.newInstance();
            StreamSource stylesource = new StreamSource(stylesheet);
            Transformer transformer = tFactory.newTransformer(stylesource);
            
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
            
            DOMSource source = new DOMSource(document);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            StringBuffer sb = writer.getBuffer(); 
            String finalString = sb.toString();
            
            System.out.println("===================Final XML======================");
            System.out.println(finalString);
            System.out.println("=====================================================");
            
                    
        } catch (TransformerConfigurationException tce) {
            // Error generated by the parser
            System.out.println("\n** Transformer Factory error");
            System.out.println("   " + tce.getMessage());

            // Use the contained exception, if any
            Throwable x = tce;

            if (tce.getException() != null) {
                x = tce.getException();
            }

            x.printStackTrace();
        } catch (TransformerException te) {
            // Error generated by the parser
            System.out.println("\n** Transformation error");
            System.out.println("   " + te.getMessage());

            // Use the contained exception, if any
            Throwable x = te;

            if (te.getException() != null) {
                x = te.getException();
            }

            x.printStackTrace();
        } catch (SAXException sxe) {
            // Error generated by this application
            // (or a parser-initialization error)
            Exception x = sxe;

            if (sxe.getException() != null) {
                x = sxe.getException();
            }

            x.printStackTrace();
        } catch (ParserConfigurationException | IOException e) {
            // Parser with specified options can't be built
            e.printStackTrace();
        } 
    } // main
}