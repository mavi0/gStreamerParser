package gstreamerparser;

import java.io.*;
import java.util.*;
import java.io.File;
import java.util.logging.Logger;

public class Driver
{
  public static final String CMD = "gst-launch-1.0 -vvv playbin uri='https://dash.akamaized.net/akamai/bbb_30fps/bbb_30fps.mpd' 2>&1 | tee >(ts '%Y-%m-%d %H_%M_%s' > raw.log)";

  public static void main(String[] args)
  {
    // // System.out.println(args);
    // //check args are there
    // if ( args.length < 2 || args.length > 2)
    // {
    //   System.err.println("Expected usage is: java -jar parser.jar <logToParse> <mpdFile> or\n ant -Dlogfile=<logToParse> -Dmpd=<mpdFile");
    //   System.exit(1);
    // }

    

      try {
        // System.out.println("Starting gstreamer and waiting.");
        // Process proc = Runtime.getRuntime().exec("bash /dash/test.sh");
        // Thread.sleep(1200000);
        System.out.println("Parsing gstreamer logfile");
        File f = new File("/dash/raw.log");
        File f2 = new File("/dash/testMPD/MultiRes.mpd");
        //check the files exist
        if (!f.exists() || !f2.exists())
        {
          System.err.println("files do not exist");
          System.exit(1);
        }
        //continue with a valid fileName
        //read the mpdfile and generate HashMap
        MPDRead r = new MPDRead("/dash/testMPD/MultiRes.mpd");
        //main class
        Parser p = new Parser("/dash/raw.log", r.getMPD());
        // System.out.println("Cleaning raw files");
        // f.delete();
        System.out.println("Complete!");
        // Thread.sleep(2400000);
       } catch (Exception e) {
         e.printStackTrace();
         System.exit(1);

    }
      
    

    
  }

}
