package gstreamerparser;

import java.io.*;
import java.util.*;
import java.time.*;

public class Parser
{
  private ArrayList<String> rawInput;
  public Parser()
  {
    // // stream the file, generate the file to be parsed
    // try{
    //   Process p = Runtime.getRuntime().exec(new String[]{"bash","-c", CMD});
    //   p.waitFor();
    // }
    // catch (Exception e)
    // {
    //   e.printStackTrace();
    // }

    rawInput = new ArrayList<String>();

    try
    {
      File file = new File("raw.log");
      BufferedReader br = new BufferedReader(new FileReader(file));

      String st;
      while ((st = br.readLine()) != null)
      {
          rawInput.add(st);
          // System.out.println(st);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    // System.out.println(rawInput.get(1));

    Double startTime = parseTime(rawInput.get(0));
    // bufferTime(0);
    System.out.println(rawInput.get(570));
    // System.out.println(rawInput.get(231).substring(47, 54));

  }
// 12-06-18 16:30:1528817458 Setting pipeline to PLAYING ...
  // private void bufferTime(int startLine)
  // {
  //   for (int i = startLine; i <= rawInput.size(); i++)
  //   {
  //     if (rawInput.get(i).substring(47, 54).equals("PLAYING"))
  //     {
  //       System.out.println("Found you ;)");
  //       break;
  //     }
  //   }
  //
  // }

  private double parseTime(String rawInput)
  {
    String rawTime = rawInput.substring(0, 30);
    String localDateTimeStr = rawTime.replace(" ", "T");
    localDateTimeStr = localDateTimeStr.substring(0, 20);
    LocalDateTime localDateTime = LocalDateTime.parse(localDateTimeStr);
    ZoneId zoneId = ZoneId.systemDefault();
    double epoch = localDateTime.atZone(zoneId).toEpochSecond();
    rawTime = "0" + rawTime.substring(19, 30);
    epoch = epoch + Double.parseDouble(rawTime);
    // System.out.printf("dexp: %f\n", epoch);
    return epoch;
  }
}
