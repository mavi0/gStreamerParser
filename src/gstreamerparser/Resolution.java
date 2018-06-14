package gstreamerparser;

public class Resolution {
  private int width = 0;
  private int height = 0;
  private double startTime = 0;
  private double endTime = 0;
  private boolean mutable = true;

  public Resolution(String rawLine)
  {
    this.width = Integer.parseInt(rawLine.substring(rawLine.indexOf("height=(int)") + 12, rawLine.indexOf(", interlace-mode=(string)")));
    this.height = Integer.parseInt(rawLine.substring(rawLine.indexOf("width=(int)") + 11, rawLine.indexOf(", height=(int)")));
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

  public void setEndTime(double endTime)
  {
    if (mutable)
    {
      this.endTime = endTime;
      // System.out.printf("START: %f ", startTime);
      // System.out.printf(" END: %f ", endTime);
      // System.out.println(" Width: " + width + " Height: " + height);
    }

    this.mutable = false;
  }

  public double getDuration()
  {
    return this.endTime - this.startTime;
  }

  public boolean isMutable()
  {
    return this.mutable;
  }

}
