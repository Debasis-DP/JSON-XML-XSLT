/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.io.StreamUtils;

import org.milyn.payload.StringResult;
import org.w3c.dom.Document;
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
public class Frame_Main {

    private static byte[] messageIn;// = readInputMessage();
    
    private final Smooks smooks;
    
    static Document document;
    
    protected Frame_Main() throws IOException, SAXException, SmooksException {
        smooks = new Smooks("smooks-config.xml");
    }
    protected String runSmooksTransform(ExecutionContext executionContext) throws IOException,SAXException, SmooksException{
        try{
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(new Locale("en","IN"));
            
            StringResult result = new StringResult();
            
            smooks.filterSource(executionContext, new StreamSource(new ByteArrayInputStream(messageIn)), result);
            Locale.setDefault(defaultLocale);
            return result.toString();
            
        } finally {
            smooks.close();
        }
        
    }

    
    public static String main(String src_file, String intermediate_file) throws IOException, SAXException, SmooksException {
       messageIn = readInputMessage(src_file);
       System.out.println("_______________________Original JSON file_____________________\n");
       System.out.println(new String(messageIn));
       System.out.println("______________________________________________________________\n");
       
       Frame_Main mainSmooks = new Frame_Main();
       ExecutionContext executionContext = mainSmooks.smooks.createExecutionContext();
       String out = new String(mainSmooks.runSmooksTransform(executionContext));
       String indented = format(out);
       System.out.println("______________________Intermediate XML______________________________\n");
       System.out.println(indented);
       System.out.println("_______________________________________________________________\n");
       
       try {
            BufferedWriter bufferedWriter_out = new BufferedWriter( new FileWriter (intermediate_file));
            bufferedWriter_out.write(indented);
            bufferedWriter_out.close();       
        } catch (IOException e) {
            e.printStackTrace();
        }
       return indented;
    }
    
    private static byte[] readInputMessage(String file_name) {
        try {
            return StreamUtils.readStream(new FileInputStream(file_name));
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
    
    public static String main1(String intermediate_file,String xsl_file) {
     
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        String finalString = new String();
        try {
            File stylesheet = new File(xsl_file);
            File datafile = new File(intermediate_file);
            
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
            finalString = sb.toString();
            
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
        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
        } catch (IOException ioe) {
            // I/O error
            ioe.printStackTrace();
        }
        return finalString;
    } // main
}