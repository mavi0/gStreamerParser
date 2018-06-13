package gstreamerparser;

import java.io.*;
import java.util.*;

public class Driver
{
  public static final String CMD = "gst-launch-1.0 -vvv playbin uri='https://dash.akamaized.net/akamai/bbb_30fps/bbb_30fps.mpd' 2>&1 | tee >(ts '%Y-%m-%d %H_%M_%s' > raw.log)";

  public static void main(String[] args)
  {
    Parser p = new Parser();
  }

}
