package gstreamerparser;

import java.io.*;
import java.util.*;

public class Driver
{
  public static final String CMD = "gst-launch-1.0 -vvv playbin uri='https://dash.akamaized.net/akamai/bbb_30fps/bbb_30fps.mpd' 2>&1 | tee >(ts '%d-%m-%y %H_%M_%S' > raw.log)";

  public static void main(String[] args)
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

    ArrayList<String> rawInput = new ArrayList<String>();

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


  }
}
