import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class XmlParser {
    public static void main(String args[]) {

        try {
            tsvToXmlConverter();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static void xmlToTsvConverter() throws Exception {
        File xmlSource = new File("res/strings.xml");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlSource);

        document.getDocumentElement().normalize();

        NodeList childNodes = document.getDocumentElement().getElementsByTagName("string");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            String name = childNodes.item(i).getAttributes().item(0).getTextContent();
            String value = childNodes.item(i).getFirstChild().getTextContent();
            map.put(name, value);
        }

        writeToCSV(map);

        System.out.println(map.toString());

    }

    private static void tsvToXmlConverter() throws Exception {
        File tsvSource = new File("res/source.tsv");
        ArrayList<String[]> Data = new ArrayList<>(); //initializing a new ArrayList out of String[]'s
        try (BufferedReader TSVReader = new BufferedReader(new FileReader(tsvSource))) {
            String line = null;
            while ((line = TSVReader.readLine()) != null) {
                String[] lineItems = line.split("\t"); //splitting the line and adding its items in String[]
                Data.add(lineItems); //adding the splitted line array to the ArrayList
            }
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
        writeToXml(Data);
    }

    private static void writeToXml(ArrayList<String[]> data) throws ParserConfigurationException, TransformerConfigurationException {
        DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder build = dFact.newDocumentBuilder();
        Document doc = build.newDocument();
        Element resources = doc.createElement("resources");
        doc.appendChild(resources);

        data.forEach(array -> {
            if (array.length >= 3) {
                Element string = doc.createElement("string");
                string.setAttribute("name", array[0]);
                string.appendChild(doc.createTextNode(array[2]));
                resources.appendChild(string);
            }
        });
        TransformerFactory tranFactory = TransformerFactory.newInstance();
        Transformer aTransformer = tranFactory.newTransformer();

        // format the XML nicely
        aTransformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

        aTransformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "4");
        aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");


        DOMSource source = new DOMSource(doc);
        try {
            FileWriter fos = new FileWriter("res/ros.xml");
            StreamResult result = new StreamResult(fos);
            aTransformer.transform(source, result);

        } catch (IOException | TransformerException e) {

            e.printStackTrace();
        }

    }

    private static void writeToCSV(LinkedHashMap<String, String> map) {
        String eol = System.getProperty("line.separator");

        try (Writer writer = new FileWriter("res/x.tsv")) {
            for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
                writer.append(stringStringEntry.getKey())
                        .append("\t")
                        .append(stringStringEntry.getValue())
                        .append(eol);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}