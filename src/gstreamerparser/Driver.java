package gstreamerparser;

import java.io.*;
import java.util.*;
import java.io.File;

public class Driver
{
  public static final String CMD = "gst-launch-1.0 -vvv playbin uri='https://dash.akamaized.net/akamai/bbb_30fps/bbb_30fps.mpd' 2>&1 | tee >(ts '%Y-%m-%d %H_%M_%s' > raw.log)";

  public static void main(String[] args)
  {
    System.out.println(args);
    //check args are there
    if ( args.length < 2 || args.length > 2)
    {
      System.err.println("Expected usage is: java -jar parser.jar <logToParse> <mpdFile> or\n ant -Dlogfile=<logToParse> -Dmpd=<mpdFile");
      System.exit(1);
    }

    File f = new File(args[0]);
    File f2 = new File(args[1]);
    //check the file exists
    if (!f.exists() || !f2.exists())
    {
      System.err.println("File '" + args[0] + "' or '" + args[1] + "' does not exist.");
      System.err.println("Expected usage is: java -jar parser.jar <logToParse> <mpdFile> or\n ant -Dlogfile=<logToParse> -Dmpd=<mpdFile");
      System.exit(1);
    }

    MPDRead r = new MPDRead(args[1]);
    //continue with a valid fileName
    Parser p = new Parser(args[0]);
  }

}
