package gstreamerparser;

import java.io.*;
import java.util.*;
import java.time.*;
import com.google.gson.Gson;
import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.*;

public class Parser {
  private ArrayList<String> rawInput;
  private ArrayList<Resolution> resolutionChanges;
  private final HashMap<String, int[]> MPD;
  private final int RAW_SIZE;
  // private ArrayList<Double> bufferMeanValues;

  /**
   * Main parser class. Outputs the resluts in JSON format
   * @param filename log file to parse
   * @param mpdMap hashmap of MPD values. Format should be <String, int[]>. String = ID, int[0] = width, int[1] = height, int[2] = bandwidth.
   */
  public Parser(String filename, HashMap<String, int[]> mpdMap) {
    rawInput = new ArrayList<String>();
    resolutionChanges = new ArrayList<Resolution>();
    this.MPD = mpdMap;

    //read the file line by line into ArrayList
    try {
      File file = new File(filename);
      BufferedReader br = new BufferedReader(new FileReader(file));

      String st;
      while ((st = br.readLine()) != null) {
        rawInput.add(st);
        // System.out.println(st);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    RAW_SIZE = (rawInput.size() - 1);
    //get the start time of the file, and start parsing.
    Double startTime = parseTime(0, rawInput);
    parse(0, startTime);

  }

  // 12-06-18 16:30:1528817458 Setting pipeline to PLAYING ...

  //iterate over each line in the raw log file, until PREROLLED is reached. Return the line number of the next line.
  private int prerolledLine(int startLine) {
    for (int i = startLine; i < rawInput.size(); i++)
      if (rawInput.get(i).contains("PREROLLED"))
        return i + 1;
    return startLine;
  }

  private void parse(int startLine, double startTime) {
    double bufferingTime = 0L;
    double playtime = 0L;
    // double[] resolutionTimes = new double[8]; // 0 = 108p, 1 = 270p, 2 = 360p, 3 = 432p, 4 = 576p, 5 = 720p, 6 = 1080, 7 = 2160p
    double[] resolutionTimes = new double[this.MPD.size()];
    int bufferCount = 0;
    boolean prerolled = false;

    //iterate over each line in the raw log file, until PREROLLED is reached. Return the line number of the next line.
    for (int i = prerolledLine(startLine); i < RAW_SIZE; i++) {
      // System.out.println(i);
      // System.out.println("THEMPD: " + MPD.size());

      if (rawInput.get(i).contains("PAUSED")) {
        System.out.println("PAUSED");
        startTime = parseTime(i, rawInput);
        //get releveant resolution object, set its endTime.
        resolutionTimes = resolutionChanges.get(resolutionChanges.size() - 1).setEndTime(startTime, resolutionTimes);
        // System.out.printf("Res Time PAUSED %f\n", resolutionChanges.get(resolutionChanges.size() - 1).getDuration());
      }

      if (rawInput.get(i).contains("PLAYING")) {
        System.out.println("PLAY");
        double buffer = parseTime(i, rawInput) - startTime;
        bufferingTime += buffer;
        bufferCount++;
        // bufferMeanValues.add(buffer);
      }

      //new resolution change. If a previous event exists, set its end time. Make a new resolution event and andd it to the list
      if (rawInput.get(i).contains("/GstPlayBin:playbin0/GstInputSelector:inputselector0.GstPad:src:")) {
        System.out.println("RES CHANGE");
        if (resolutionChanges.size() > 0)
        {
          System.out.println(resolutionChanges.size());
          resolutionTimes = resolutionChanges.get(resolutionChanges.size() - 1).setEndTime(parseTime(i, rawInput),  resolutionTimes);
          // System.out.println(resolutionChanges.size().setEndTime(parseTime(i, rawInput), resolutionTimes));

        }

        //new resolution Parsed

        resolutionChanges.add(new Resolution(rawInput.get(i), new HashMap<String, int[]>(MPD), parseTime(i, rawInput)));
      }

    }

    System.out.printf("Number of buffering events = %d\n", bufferCount);
    System.out.printf("Total buffering time = %f\n", bufferingTime);
    System.out.printf("Mean time buffering = %f\n", bufferingTime / bufferCount);

    for (int i = 0; i < resolutionChanges.size(); i++) {
      playtime += resolutionChanges.get(i).getDuration();
      System.out.printf("Playtime (RES) = %f\n", playtime);
      System.out.println(resolutionChanges.get(i).getHeight());
    }

    // for (int i = 0; i < bufferMeanValues.size(); i++)
    // System.out.printf("BUFFERING VALS = %f\n", bufferMeanValues.get(i));

    System.out.printf("Playtime (RES) = %f\n", playtime);
    System.out.printf("Mean time between buffering - %f\n", playtime / bufferCount);
    System.out.println("Number of change of resolution events = " + resolutionChanges.size());
    System.out.println("RES TIMES" + Arrays.toString(resolutionTimes));

    Output out = new Output();
    out.bufferingEventsCount = bufferCount;
    out.totalBufferingTime = bufferingTime;
    out.meanTimeBuffering = (bufferingTime / bufferCount);
    out.totalPlaytime = playtime;
    out.meanTimeBetweenBuffering = (playtime / bufferCount);
    out.resolutionChangeCount = resolutionChanges.size();
    out.resolutionTimes = resolutionTimes;
    out.client_id = System.getenv("CLIENT_ID");

    Gson gson = new Gson();
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    try {
      //archive the result
      File file = new File("/log/" + timestamp + ".json");
      FileWriter fileWriter = new FileWriter(file);
      fileWriter.write(gson.toJson(out));
      fileWriter.flush();
      fileWriter.close();
      //overwrite file to save
      File fileB = new File("/share/qoe.json");
      FileWriter fileWriterB = new FileWriter(fileB);
      fileWriterB.write(gson.toJson(out));
      fileWriterB.flush();
      fileWriterB.close();

      // PrintWriter outFile = new PrintWriter();
      // outFile.println(gson.toJson(out));
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  //cheat
  public double parseTime(int lineNumber, ArrayList<String> rawInput) {
    String rawTime = findTimestamp(lineNumber, rawInput);
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

  /**
   * recursivley search for a timestamp. If this line has no timestamp at the start, go to the next until a timestamp is found.
   * @param  lineNumber line to search for a timestamp
   * @param  rawInput   input arrayList
   * @return            the raw time as a string
   */
  // private static String findTimestampOLD(int lineNumber, ArrayList<String> rawInput) {
  //   //skip lines which are too short
  //   try {
  //     if (rawInput.get(lineNumber).length() < 27) {
  //       System.out.println("TOO SMALL " + rawInput.get(lineNumber).length() + " | " + rawInput.get(lineNumber));
  //       System.out.println("Line: " + lineNumber);
  //       lineNumber = lineNumber + 1;
  //       System.out.println("Line1: " + lineNumber);
  //       return findTimestamp(lineNumber, rawInput);
  //     }
  //   } catch (Exception e) {
  //     System.out.println(rawInput.get(lineNumber).length() + " | " + rawInput.get(lineNumber));
  //   }
  //   String rawTime = "";
  //   //generate the regex
  //   Pattern timeRegex = Pattern.compile(
  //       "\\d\\d\\d\\d\\p{Punct}\\d\\d\\p{Punct}\\d\\d\\s\\d\\d\\p{Punct}\\d\\d\\p{Punct}\\d\\d\\p{Punct}\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d");
  //   //get the raw timestamp from substring
  //   rawTime = rawInput.get(lineNumber).substring(0, 26);
  //   Matcher matcher = timeRegex.matcher(rawTime);
  //   //try and get a timestamp
  //   if (matcher.matches())
  //     return rawTime;
  //   return findTimestamp(lineNumber + 1, rawInput);
  // }

  private String findTimestamp(int lineNumber, ArrayList<String> rawInput) {
    String rawTime = "";
    for (; lineNumber < RAW_SIZE; lineNumber++)
      if (rawInput.get(lineNumber).length() > 26) {
        Pattern timeRegex = Pattern.compile(
            "\\d\\d\\d\\d\\p{Punct}\\d\\d\\p{Punct}\\d\\d\\s\\d\\d\\p{Punct}\\d\\d\\p{Punct}\\d\\d\\p{Punct}\\d\\d\\d\\d\\d\\d");
        rawTime = rawInput.get(lineNumber).substring(0, 26);
        Matcher matcher = timeRegex.matcher(rawTime);
        //try and get a timestamp
        if (matcher.matches())
          return rawTime;
          // System.out.println(rawTime);
      }
      System.out.println("THIS: " + lineNumber + " MAX: " + RAW_SIZE);
      System.out.println("PREV: " + rawInput.get(lineNumber - 1) + " THIS: " + rawInput.get(lineNumber - 1));
      return null;
  }
  // {
  //   System.out.println("TOO SMALL " + rawInput.get(lineNumber).length() + " | " + rawInput.get(lineNumber));
  //   System.out.println("Line: " + lineNumber);
  //   lineNumber = lineNumber + 1;
  //   System.out.println( "Line1: " + lineNumber);
  //   return findTimestamp(lineNumber, rawInput);
  // }

  //generate the regex
  //get the raw timestamp from substring

  private class Output {
    public int bufferingEventsCount;
    public double totalBufferingTime;
    public double meanTimeBuffering;
    public double totalPlaytime;
    public double meanTimeBetweenBuffering;
    public int resolutionChangeCount;
    public double[] resolutionTimes;
    public String client_id;
  }
}
