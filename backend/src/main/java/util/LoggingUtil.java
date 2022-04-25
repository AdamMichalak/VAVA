package util;

import java.io.File;
import java.io.IOException;
import java.rmi.server.ExportException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class LoggingUtil {

    public static boolean log_sql(String place, String sql, Exception e)
    {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse("./src/main/java/util/sql_logs.xml");
            Node root = doc.getFirstChild();
            Element log = doc.createElement("log");
            root.appendChild(log);
            Element place_e = doc.createElement("place");
            log.appendChild(place_e);
            place_e.appendChild(doc.createTextNode(place));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
            LocalDateTime now =  LocalDateTime.now();
            Element time = doc.createElement("time");
            log.appendChild(time);
            time.appendChild(doc.createTextNode(formatter.format(now)));
            Element sql_e = doc.createElement("sql");
            log.appendChild(sql_e);
            if(sql == null) sql = "--NO SQL AVAILABLE--";
            sql_e.appendChild(doc.createTextNode(sql));
            Element exception = doc.createElement("exception");
            log.appendChild(exception);
            exception.appendChild(doc.createTextNode(e.toString()));

            DOMSource source = new DOMSource(doc);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t =  tf.newTransformer();
            StreamResult result = new StreamResult("./src/main/java/util/sql_logs.xml");
            t.transform(source, result);

        } catch(TransformerException | ParserConfigurationException | IOException | SAXException exp) {
            System.out.println(exp);
            return false;
        }
        return true;
    }
}
