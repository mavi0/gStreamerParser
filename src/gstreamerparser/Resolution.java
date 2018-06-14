package gstreamerparser;

public class Resolution {
  private int width = 0;
  private int height = 0;
  private double startTime = 0;
  private double endTime = 0;

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
    this.endTime = endTime;
  }

  public double getDuration()
  {
    return this.endTime - this.startTime;
  }
}
