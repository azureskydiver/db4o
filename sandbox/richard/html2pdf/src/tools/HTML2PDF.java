package tools;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.w3c.dom.*;

public class HTML2PDF {
    
    static HashMap<String, String> options = new HashMap<String, String>();
    
    static {
        options.put("-ref", "");
        options.put("-conv", "");
        options.put("-content", "");
    }

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String option = args[i];
            if (option.startsWith("-")) {
                System.out.println("value: " + options.get(option.toLowerCase()));
                if ("".equals(options.get(option.toLowerCase())) 
                        && (i < args.length - 1)) {
                    options.put(option, args[i+1]);
                }
            }
        }
        
        String ref = options.get("-ref");
        String conv = options.get("-conv");
        String content = options.get("-content");
        System.out.println("ref: " + ref);
        System.out.println(conv);
        System.out.println(content);
        
        if (ref.length() == 0
                || conv.length() == 0
                || content.length() == 0) {
            System.err.println("Please specify the arguments!");
            System.err.println("java tools.HTML2PDF -ref <referencePath> \n " +
            		"-conv <conversionPath> \n " +
            		"-content <contentPath>");
            System.exit(1);
        }
        
        HTML2PDF task = new HTML2PDF();
        task.setContentFile(content);
        task.setConversionPath(conv);
        task.setReferencePath(ref);
        task.execute();
        System.out.println("Success!");
    }

    private String referencePath;

    private String conversionPath;

    private String contentFile;

    private final String expression = "html/body/div/ul//a";

    public void setReferencePath(String path) {
        referencePath = path;
    }

    public void setConversionPath(String path) {
        conversionPath = path;
    }

    public void setContentFile(String file) {
        contentFile = file;
    }

    public void execute() {
        File conversionDir = new File(conversionPath);
        if (!conversionDir.mkdirs() && !conversionDir.exists()) {
            throw new RuntimeException("Cannot create dir: " + conversionPath);
        }

        File config = new File(conversionDir, "config.js");
        try {
            PrintWriter writer = new PrintWriter(config);

            writer.println("var rootDir = \"" + referencePath + "/html/\";");
            writer.println("var tempPDF = \"" + conversionPath
                    + "/tempDoc.pdf\";");
            writer.println("var targetPDF = \"" + conversionPath
                    + "/reference.pdf\";");
            writer.println();
            writer.println("combineFiles = app.trustedFunction(function()");
            writer.println("{");
            writer.println("\tapp.beginPriv();");
            writer.println("\tfirstDoc = app.openDoc({");
            writer.println("\t\tcPath: rootDir + \"cover.pdf\",");
            writer.println("\t});");
            writer.println();

            writer.println("\tfiles = new Array(");
            String[] htmlFiles = getPageFiles(contentFile);
            for (int i = 0; i < htmlFiles.length; i++) {
                writer.print("\t\t\"" + htmlFiles[i] + "\"");
                if (i < htmlFiles.length - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }
            writer.println("\t);");

            writer.println();
            writer.println("\tfor (var i = 0; i < files.length; i++)");
            writer.println("\t{");
            writer.println("\t\tnewDoc = app.openDoc({");
            writer.println("\t\toDoc: firstDoc,");
            writer.println("\t\tcPath: files[i],");
            writer.println("\t\tbUseConv: true,");
            writer.println("\t});");
            writer.println();
            writer.println("\tnewDoc.saveAs({");
            writer.println("\t\tcPath: tempPDF,");
            writer.println("\t});");
            writer.println("\tnewDoc.closeDoc(true);");
            writer.println("\tfirstDoc.insertPages({");
            writer.println("\t\tnPage: firstDoc.numPages - 1,");
            writer.println("\t\tcPath: tempPDF,");
            writer.println("\t\t});");
            writer.println("\t}");
            writer.println("\tfirstDoc.saveAs({cPath: targetPDF,});");
            writer.println("\tfirstDoc.closeDoc(true);");
            writer.println("\tapp.endPriv();");
            writer.println("});");
            writer.close();

            File html2pdf = new File(conversionDir, "html2pdf.js");
            writer = new PrintWriter(html2pdf);
            writer.println("combineFiles();");
            writer
                    .println("console.println(\"Reference.pdf is created successfully!\");");
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    String[] getPageFiles(String contentFile) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document document = builder.parse(new File(contentFile));

            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xpath.evaluate(expression, document,
                    XPathConstants.NODESET);
            String[] pages = new String[nodeList.getLength()];
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String href = node.getAttributes().getNamedItem("href")
                        .getTextContent();
                pages[i] = href;
            }
            return pages;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
