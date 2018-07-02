package gstreamerparser;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class MPDRead
{
  public MPDRead(String mpdFile)
  {
    try {
    	File fXmlFile = new File(mpdFile);
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    	Document doc = dBuilder.parse(fXmlFile);

    	//optional, but recommended
    	//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
    	doc.getDocumentElement().normalize();

    	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

    	NodeList nList = doc.getElementsByTagName("Representation");

    	System.out.println("----------------------------");

    	for (int temp = 0; temp < nList.getLength(); temp++) {
    	  Node nNode = nList.item(temp);
    		System.out.println("\nCurrent Element :" + nNode.getNodeName());

    		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

    			Element eElement = (Element) nNode;

    			System.out.println("" + eElement.getAttribute("id"));

		}
	}
    } catch (Exception e) {
	e.printStackTrace();
    }
  }
}
