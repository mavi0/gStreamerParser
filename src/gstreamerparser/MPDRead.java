package gstreamerparser;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.*;

public class MPDRead {
  private HashMap<String, int[]> mpdMap = new HashMap<>();

  /**
   * Class to parse an MPD XML file.
   * Retrieve the bandwidth/resolutions that the stream offers for use in parsing.
   * @param mpdFile MPD file to parse.
   */
  public MPDRead(String mpdFile) {
    try {
      File fXmlFile = new File(mpdFile);
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(fXmlFile);

      //optional, but recommended
      //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
      doc.getDocumentElement().normalize();

      // System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

      NodeList nList = doc.getElementsByTagName("Representation");

      // System.out.println("----------------------------");

      for (int temp = 0; temp < nList.getLength(); temp++) {
        Node nNode = nList.item(temp);
        // System.out.println("\nCurrent Element :" + nNode.getNodeName());
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
          Element eElement = (Element) nNode;
          // Get the element ID of the Representation, use that as the HashMap ID.
          // Add Representation width, height, bandwidth to an array and add that array to the main hashmap
          // System.out.println(eElement.getAttribute("id"));
          // System.out.println(eElement.getAttribute("width"));
          // System.out.println(eElement.getAttribute("height"));
          // System.out.println(eElement.getAttribute("bandwidth"));
          if (eElement.getAttribute(mimeType).contains("video")) {
            int[] resArray = new int[3];
            resArray[0] = Integer.parseInt(eElement.getAttribute("width"));
            resArray[1] = Integer.parseInt(eElement.getAttribute("height"));
            resArray[2] = Integer.parseInt(eElement.getAttribute("bandwidth"));
            mpdMap.put(eElement.getAttribute("id"), resArray);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Get hashmap, contains MPD representation details
   * @return String = representation ID, int[0] = width, int[1] = height, int[2] = bandwidth
   */
  public HashMap<String, int[]> getMPD() {
    return mpdMap;
  }
}
