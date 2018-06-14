package gstreamerparser;

import java.io.*;
import java.util.*;
import java.time.*;

public class Parser
{
  private ArrayList<String> rawInput;
  private ArrayList<Resolution> resolutionChanges;
  private ArrayList<Double> bufferMeanValues;
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
    resolutionChanges = new ArrayList<Resolution>();
    bufferMeanValues = new ArrayList<Double>();

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

    // for (int i = 0; i < rawInput.size(); i++)
    //   System.out.println(rawInput.get(i));

    // System.out.println(rawInput.get(1));

    Double startTime = parseTime(rawInput.get(0));
    // String tst = "12-06-18 16:30:1528817458 Setting pipeline to PLAYING ...";
    // String tst2 = tst.substring(46, 53);
    // String p = "PLAYING";
    // System.out.println(tst2 + tst2.equals(p));

    parse(0, startTime);
    // System.out.println(rawInput.get(570));
    // System.out.println(rawInput.get(231).substring(47, 54));

  }
// 12-06-18 16:30:1528817458 Setting pipeline to PLAYING ...
  private void parse(int startLine, double startTime)
  {
    double bufferingTime = 0L;
    double playtime = 0L;

    for (int i = startLine + 1; i < rawInput.size(); i++)
    {
      // System.out.println(i);
      if (rawInput.get(i).contains("PAUSED"))
      {
        startTime = parseTime(rawInput.get(i - 1));
        resolutionChanges.get(resolutionChanges.size() - 1).setEndTime(startTime);
        System.out.printf("Res Time PAUSED %f\n", resolutionChanges.get(resolutionChanges.size() - 1).getDuration());
      }

      if (rawInput.get(i).contains("PLAYING"))
      {
        double buffer = parseTime(rawInput.get(i + 1)) - startTime;
        bufferingTime += buffer;
        bufferMeanValues.add(buffer);
      }

      if (rawInput.get(i).contains("/GstPlayBin:playbin0/GstInputSelector:inputselector0.GstPad:src:"))
      {
        if (resolutionChanges.size() > 0)
          resolutionChanges.get(resolutionChanges.size() - 1).setEndTime(parseTime(rawInput.get(i - 1)));
        resolutionChanges.add(new Resolution(rawInput.get(i)));
      }

    }

    System.out.println("Total buffering time = " + bufferingTime);

    for (int i = 0; i < resolutionChanges.size(); i++)
      playtime += resolutionChanges.get(i).getDuration();

    for (int i = 0; i < bufferMeanValues.size(); i++)
      System.out.printf("BUFFERING VALS = %f\n", bufferMeanValues.get(i));

    System.out.printf("Playtime = %f\n", playtime);

  }

  public static double parseTime(String rawInput)
  {
    String rawTime = rawInput.substring(0, 26);
    String localDateTimeStr = rawTime.replace(" ", "T");
    localDateTimeStr = localDateTimeStr.substring(0, 20);
    LocalDateTime localDateTime = LocalDateTime.parse(localDateTimeStr);
    ZoneId zoneId = ZoneId.systemDefault();
    double epoch = localDateTime.atZone(zoneId).toEpochSecond();
    rawTime = "0" + rawTime.substring(19, 26);
    epoch = epoch + Double.parseDouble(rawTime);
    //
    return epoch;
  }
}
