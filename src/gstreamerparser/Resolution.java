package gstreamerparser;

import java.util.*;

public class Resolution {
  private int width = 0;
  private int height = 0;
  private double startTime = 0;
  private double endTime = 0;
  private boolean mutable = true;
  private final HashMap<String, int[]> MPD;

  public Resolution(String rawLine, HashMap<String, int[]> mpdMap, double startTime) {
    this.MPD = mpdMap;
    System.out.println("MPD: " + this.MPD.size());
    this.height = Integer.parseInt(
        rawLine.substring(rawLine.indexOf("height=(int)") + 12, rawLine.indexOf(", interlace-mode=(string)")));
    this.width = Integer
        .parseInt(rawLine.substring(rawLine.indexOf("width=(int)") + 11, rawLine.indexOf(", height=(int)")));
    this.startTime = startTime;
    searchMapWidth(1920);
  }

  public int getWidth() {
    return this.width;
  }

  public int getHeight() {
    return this.height;
  }

  public double[] setEndTime(double endTime, double[] resolutionChanges) {
    if (mutable) {
      this.endTime = endTime;
      this.mutable = false;
      resolutionChanges = addEvent(resolutionChanges);
      System.out.println(" Width: " + width + " Height: " + height);

    }
    return resolutionChanges;
  }

  public double getDuration() {
    System.out.printf("START: %f ", startTime);
    System.out.printf(" END: %f ", endTime);

    if (this.endTime > this.startTime)
      return this.endTime - this.startTime;
    else
      return 0L;
  }

  public boolean isMutable() {
    return this.mutable;
  }

  private boolean searchMap(int size, int arrayPos) {
    HashMap<String, int[]> mMap = new HashMap<String, int[]>(MPD);
    Iterator it = mMap.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry) it.next();
      int[] val = (int[])pair.getValue();
      System.out.println(pair.getKey() + " = " + val[0]);
      it.remove(); // avoids a ConcurrentModificationException
    }
    System.out.println("MAP " + MPD.size());
    return true;
  }

  private boolean searchMapWidth(int width) {
    return searchMap(width, 0);
  }

  private boolean searchMapHeight(int height) {
    return searchMap(height, 1);
  }

  private double[] addEvent(double[] resolutionChanges) {
    HashMap<String, int[]> mMap = new HashMap<String, int[]>(MPD);
    Iterator it = mMap.entrySet().iterator();
    int i = 0;
    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry) it.next();
      // System.out.println(pair.getKey() + " = " + pair.getValue());
      int[] val = (int[])pair.getValue();
      if (this.width == val[0])
      {
        resolutionChanges[i] += getDuration();
        System.out.println("DURATION" + getDuration() + "  " + i);
        System.out.println("THIS WIDTH " + this.width + "TEST WIDTH" + val[0]);
      }
      i++;
      it.remove(); // avoids a ConcurrentModificationException
    }
    //
    // switch (height) {
    // case 180:
    //   resolutionChanges[0] += getDuration();
    // case 270:
    //   resolutionChanges[1] += getDuration();
    // case 360:
    //   resolutionChanges[2] += getDuration();
    // case 432:
    //   resolutionChanges[3] += getDuration();
    // case 576:
    //   resolutionChanges[4] += getDuration();
    // case 720:
    //   resolutionChanges[5] += getDuration();
    // case 1080:
    //   resolutionChanges[6] += getDuration();
    // case 2160:
    //   resolutionChanges[7] += getDuration();
    // default:
    //   break;
    // }
    return resolutionChanges;
  }

}
