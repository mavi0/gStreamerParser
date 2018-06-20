package gstreamerparser;

public class Resolution {
  private int width = 0;
  private int height = 0;
  private double startTime = 0;
  private double endTime = 0;
  private boolean mutable = true;

  public Resolution(String rawLine)
  {
    this.height = Integer.parseInt(rawLine.substring(rawLine.indexOf("height=(int)") + 12, rawLine.indexOf(", interlace-mode=(string)")));
    this.width = Integer.parseInt(rawLine.substring(rawLine.indexOf("width=(int)") + 11, rawLine.indexOf(", height=(int)")));
    this.startTime = Parser.parseTime(rawLine);
  }

  public int getWidth()
  {
    return this.width;
  }

  public int getHeight()
  {
    return this.height;
  }

  public double[] setEndTime(double endTime, double[] resolutionChanges)
  {
    if (mutable)
    {
      this.endTime = endTime;
      this.mutable = false;
      resolutionChanges = addEvent(resolutionChanges);
      // System.out.println(" Width: " + width + " Height: " + height);

    }
    return resolutionChanges;
  }

  public double getDuration()
  {
    System.out.printf("START: %f ", startTime);
    System.out.printf(" END: %f ", endTime);

    if (this.endTime > this.startTime)
      return this.endTime - this.startTime;
    else
      return 0L;
  }

  public boolean isMutable()
  {
    return this.mutable;
  }

  private double[] addEvent(double[] resolutionChanges)
  {
    switch (height)
    {
      case 180:
        resolutionChanges[0] += getDuration();
      case 270:
        resolutionChanges[1] += getDuration();
      case 360:
        resolutionChanges[2] += getDuration();
      case 432:
        resolutionChanges[3] += getDuration();
      case 576:
        resolutionChanges[4] += getDuration();
      case 720:
        resolutionChanges[5] += getDuration();
      case 1080:
        resolutionChanges[6] += getDuration();
      case 2160:
        resolutionChanges[7] += getDuration();
      default:
        break;
    }
    return resolutionChanges;
  }

}
