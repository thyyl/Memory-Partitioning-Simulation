package sample;

import javafx.scene.shape.Rectangle;

public class JobList {

    public double start;
    public double end;
    public int jobIndex;
    public Rectangle jobSegment;

    JobList() {
        start = 0;
        end = ReadData.dataList.getDynamicMemory();
        jobIndex = 0;
        jobSegment = null;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public int getJobIndex() {
        return jobIndex;
    }

    public void setJobIndex(int jobIndex) {
        this.jobIndex = jobIndex;
    }

    public Rectangle getJobSegment() {
        return jobSegment;
    }

    public void setJobSegment(Rectangle jobSegment) {
        this.jobSegment = jobSegment;
    }

    public void setJobSegment() {
        this.jobSegment = null;
    }

    public double getSize(){
        return (end - start);
    }
}
