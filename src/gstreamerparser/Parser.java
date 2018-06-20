package gstreamerparser;

import java.io.*;
import java.util.*;
import java.time.*;
import com.google.gson;

public class Parser
{
  private ArrayList<String> rawInput;
  private ArrayList<Resolution> resolutionChanges;
  // private ArrayList<Double> bufferMeanValues;
  public Parser(String filename)
  {
    rawInput = new ArrayList<String>();
    resolutionChanges = new ArrayList<Resolution>();

    try
    {
      File file = new File(filename);
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

    Double startTime = parseTime(rawInput.get(0));
    parse(0, startTime);

  }
// 12-06-18 16:30:1528817458 Setting pipeline to PLAYING ...

  private void parse(int startLine, double startTime)
  {
    double bufferingTime = 0L;
    double playtime = 0L;
    double[] resolutionTimes = new double[8]; // 0 = 108p, 1 = 270p, 2 = 360p, 3 = 432p, 4 = 576p, 5 = 720p, 6 = 1080, 7 = 2160p
    int bufferCount = 0;

    for (int i = startLine + 1; i < rawInput.size(); i++)
    {
      // System.out.println(i);
      if (rawInput.get(i).contains("PAUSED"))
      {
        startTime = parseTime(rawInput.get(i - 1));

        resolutionTimes = resolutionChanges.get(resolutionChanges.size() - 1).setEndTime(startTime, resolutionTimes);
        // System.out.printf("Res Time PAUSED %f\n", resolutionChanges.get(resolutionChanges.size() - 1).getDuration());
      }

      if (rawInput.get(i).contains("PLAYING"))
      {
        double buffer = parseTime(rawInput.get(i + 1)) - startTime;
        bufferingTime += buffer;
        bufferCount++;
        // bufferMeanValues.add(buffer);
      }

      if (rawInput.get(i).contains("/GstPlayBin:playbin0/GstInputSelector:inputselector0.GstPad:src:"))
      {
        if (resolutionChanges.size() > 0)
          resolutionTimes = resolutionChanges.get(resolutionChanges.size() - 1).setEndTime(parseTime(rawInput.get(i - 1)), resolutionTimes);
        resolutionChanges.add(new Resolution(rawInput.get(i)));
      }

    }

    System.out.printf("Number of buffering events = %d\n", bufferCount);
    System.out.printf("Total buffering time = %f\n", bufferingTime);
    System.out.printf("Mean time buffering = %f\n", bufferingTime/bufferCount);


    for (int i = 0; i < resolutionChanges.size(); i++)
    {
      playtime += resolutionChanges.get(i).getDuration();
      System.out.printf("Playtime (RES) = %f\n", playtime);
      System.out.println(resolutionChanges.get(i).getHeight());
    }

    // for (int i = 0; i < bufferMeanValues.size(); i++)
    // System.out.printf("BUFFERING VALS = %f\n", bufferMeanValues.get(i));

    System.out.printf("Playtime (RES) = %f\n", playtime);
    System.out.printf("Mean time between buffering - %f\n", playtime/bufferCount);
    System.out.println("Number of change of resolution events = " + resolutionChanges.size());
    System.out.printf("%f", resolutionTimes[7]);

    Output out = new Output();
    out.bufferingEventsCount = bufferCount;
    out.totalBufferingTime = bufferingTime;
    out.meanTimeBuffering = (bufferingTime/bufferCount);
    out.totalPlaytime = playtime;
    out.meanTimeBetweenBuffering = (playtime/bufferCount);
    out.resolutionChangeCount = resolutionChanges.size();
    out.resolutionTimes = resolutionTimes;

    Gson gson = new Gson();

    System.out.println(gson.toJson(out));

  }

  //cheat
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

  private class Output
  {
    public int bufferingEventsCount;
    public double totalBufferingTime;
    public double meanTimeBuffering;
    public double totalPlaytime;
    public double meanTimeBetweenBuffering;
    public int resolutionChangeCount;
    public double[] resolutionTimes;

  }
}
