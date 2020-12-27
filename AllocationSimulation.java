package sample;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AllocationSimulation {

    @FXML
    private StackPane root;

    @FXML
    private AnchorPane rootAnchor;

    @FXML
    private TextField textField;

    @FXML
    private Label timeTotal;

    @FXML
    private Label iFragmentation;

    @FXML
    private Label waitList;

    @FXML
    private Label jobDoneList;

    @FXML
    private Label throughput;

    @FXML
    private Label queueLength;

    @FXML
    private Label waitTime;

    private boolean runSimulation = true;
    private boolean runAdd = true;

    private final int[] memoryDetails = ReadData.dataList.getBlock().clone();
    private final int[][] jobDetails = ReadData.dataList.getJobDetail().clone();
    private int timeCount = -1;
    private int po = 0;
    private int internalFragmentation = 0;
    private int jobDone = 0;
    private int unprocessedTime = 0;
    private int waitingTime = 0;

    private final double[][] segmentX = new double[ReadData.dataList.getJobListEnquire()][2];
    private final double[] jobSize = new double[ReadData.dataList.getTotalJobList()];
    private final double[] partitionWidth = new double[ReadData.dataList.getTotalBlockSize()];

    private final Rectangle[] jobBlock = new Rectangle[40];
    private final Rectangle[] memoryBlockEnquire = new Rectangle[ReadData.dataList.getJobListEnquire()];

    private final LinkedList<JobList> queue = new LinkedList<>();
    private final LinkedList<JobList> waitingListFixed = new LinkedList<>();
    private final LinkedList<JobList> listDynamic = new LinkedList<>();

    private static final DecimalFormat df = new DecimalFormat("#.###");

    @FXML
    public void calculate() {
        if (!queue.isEmpty())
            for (JobList jobList : queue) unprocessedTime += jobDetails[jobList.getJobIndex() - 1][2];

        double throughputCalc = (453 - unprocessedTime) / (double) timeCount;
        double averageWaitingTime = waitingTime / (double) 40;
        double averageQueueLength = waitingTime / (double) timeCount;
        double averageFragmentation = 0;

        throughput.setText(df.format(throughputCalc));
        waitTime.setText(df.format(averageWaitingTime));
        queueLength.setText(df.format(averageQueueLength));

        if (ReadData.dataList.getPartitionType().equals("Fixed Memory Partition")) {
            averageFragmentation = internalFragmentation / (double) ReadData.dataList.getJobListEnquire();
            iFragmentation.setText(df.format(averageFragmentation));
        }
    }

    @FXML
    public void back() {
        try {
            ReadData.readFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        makeFadeOut();
    }

    private void makeFadeOut() {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration((Duration.millis(1000)));
        fadeTransition.setNode(root);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);

        fadeTransition.setOnFinished((ActionEvent event) -> loadNextScene());

        fadeTransition.play();
    }

    private void loadNextScene() {
        try {
            Parent thirdView;
            thirdView = FXMLLoader.load(getClass().getResource("sceneTwo.fxml"));
            Scene newScene = new Scene(thirdView);
            Stage curStage = (Stage) root.getScene().getWindow();
            curStage.setScene(newScene);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initialize() {
        for (int i = 0; i < ReadData.dataList.getTotalJobList(); i++) {
            Rectangle job = new Rectangle();
            job.setHeight(50);

            if (ReadData.dataList.getPartitionType().equals("Fixed Memory Partition"))
                job.setWidth(650 * jobDetails[i][3] / ReadData.dataList.getTotalBlockSize());
            else
                job.setWidth((650 * jobDetails[i][3]) / ReadData.dataList.getDynamicMemory());

            job.setFill(Color.web("rgb(220,220,220)"));
            job.setStroke(Color.BLACK);
            job.setStrokeWidth(2);
            job.setStrokeType(StrokeType.INSIDE);

            jobBlock[i] = job;
            jobSize[i] = job.getWidth();
        }

        if (ReadData.dataList.getPartitionType().equals("Fixed Memory Partition")) {
            double newXPosition = 15;

            for (int i = 0; i < ReadData.dataList.getJobListEnquire(); i++) {
                Rectangle memory = new Rectangle();
                memory.setHeight(50);
                memory.setWidth(650 * memoryDetails[i] / ReadData.dataList.getTotalBlockSize());
                memory.setFill(Color.web("rgb(112,128,144)"));
                memory.setStroke(Color.BLACK);
                memory.setStrokeWidth(2);
                memory.setStrokeType(StrokeType.INSIDE);

                newXPosition += (memory.getWidth() / 2);
                addMemory(0, 0, newXPosition, memory);
                newXPosition += (memory.getWidth() / 2);
                segmentX[i][0] = newXPosition;
                partitionWidth[i] = newXPosition;
            }

            for (int i = 0; i < ReadData.dataList.getJobListEnquire(); i++)
                segmentX[i][1] = 0;

        } else if (ReadData.dataList.getPartitionType().equals("Dynamic Memory Partition")) {
            JobList job = new JobList();
            listDynamic.add(job);

            Rectangle memory = new Rectangle();
            memory.setHeight(50);
            memory.setWidth(650);
            memory.setFill(Color.web("rgb(112,128,144)"));
            memory.setStroke(Color.BLACK);
            memory.setStrokeWidth(2);
            memory.setStrokeType(StrokeType.INSIDE);
            addMemory(150, 0, 0, memory);
        }

        textField.setOnKeyPressed(event -> simulation());
    }

    public void addMemory(double startingXPosition, double startingYPosition, double newXPosition, Rectangle blocks) {
        Timeline timeline = new Timeline();
        int startX = 0;
        root.getChildren().add(blocks);
        blocks.translateXProperty().set(startingXPosition);
        blocks.translateYProperty().set(startingYPosition);

        if (ReadData.dataList.getPartitionType().equals("Fixed Memory Partition"))
            startX = 340;

        KeyValue keyValue = new KeyValue(blocks.translateXProperty(), -startX + newXPosition, Interpolator.EASE_IN);
        KeyValue keyValueTwo = new KeyValue(blocks.translateYProperty(), -140, Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(500), keyValue);
        KeyFrame keyFrame2 = new KeyFrame(Duration.millis(500), keyValueTwo);

        timeline.getKeyFrames().add(keyFrame);
        timeline.getKeyFrames().add(keyFrame2);
        timeline.play();
    }

    public void simulation() {
        runSimulation = true;
        if (!runAdd)
            return;

        while (runSimulation) {
            for (int i = 0; i < ReadData.dataList.getTotalJobList(); i++) {
                /* scene when job processing time ended */
                if (jobDetails[i][2] == 0) {
                    if (ReadData.dataList.getPartitionType().equals("Fixed Memory Partition")) {
                        for (int index = 0; index < ReadData.dataList.getJobListEnquire(); index++)
                            /* find the segment memory that contains the job will be deallocated */
                            if (jobDetails[i][0] == segmentX[index][1]) {
                                deallocateJob(memoryBlockEnquire[index]);
                                jobDetails[i][2]--;
                                jobDone++;
                                jobDoneList.setText(Integer.toString(jobDone));

                                memoryBlockEnquire[index] = null;
                                segmentX[index][1] = 0;

                                internalFragmentation += memoryDetails[index] - jobDetails[i][3];
                                iFragmentation.setText(Integer.toString(internalFragmentation));
                            }

                    } else if (ReadData.dataList.getPartitionType().equals("Dynamic Memory Partition"))
                        for (int index = 0; index < listDynamic.size(); index++)
                            /* find the segment memory that contains the job will be deallocated */
                            if (jobDetails[i][0] == listDynamic.get(index).getJobIndex()) {
                                deallocateJob(listDynamic.get(index).getJobSegment());
                                jobDetails[i][2]--;
                                jobDone++;
                                jobDoneList.setText(Integer.toString(jobDone));

                                listDynamic.get(index).setJobSegment();
                                listDynamic.get(index).setJobIndex(0);
                            }

                } else if ((jobDetails[i][2] > 0) && (jobDetails[i][1] == -2))
                    jobDetails[i][2]--;
            }

            /* reset the memory size of dynamic memory if job deallocates */
            if (listDynamic.size() > 1)
                for (int i = 0; i < listDynamic.size() - 1; i++)
                    if ((listDynamic.get(i).getJobSegment() == null) && (listDynamic.get(i + 1).getJobSegment() == null)) {
                        listDynamic.get(i).setEnd(listDynamic.get(i + 1).getEnd());
                        listDynamic.remove(i + 1);
                        i--;
                    }

            po = 0;

            /* check the waiting list of job to allocate them first rather than incoming jobs */
            if (!queue.isEmpty())
                for (int i = 0; i < queue.size(); i++) {
                    int jobIndex = queue.get(i).getJobIndex();
                    /* get the lowest accessible location for the memory */
                    int location = algorithmType(jobIndex - 1);

                    if (location != -1) {
                        jobIndex--;

                        /* flag job */
                        jobDetails[jobIndex][1] = -2;
                        jobDetails[jobIndex][2]--;

                        if  (ReadData.dataList.getPartitionType().equals("Fixed Memory Partition")) {
                            /* determine the size of Partition for the job */
                            if (location == 0)
                                po += (jobSize[jobIndex] / 2);
                            else
                                po += (partitionWidth[location - 1] + (jobSize[jobIndex] / 2) - 15);

                            moveJob(po, jobBlock[jobIndex]);
                            segmentX[location][1] = jobDetails[jobIndex][0];
                            memoryBlockEnquire[location] = jobBlock[jobIndex];

                        } else if (ReadData.dataList.getPartitionType().equals("Dynamic Memory Partition")) {
                            listDynamic.get(location).setJobSegment(jobBlock[jobIndex]);
                            listDynamic.get(location).setJobIndex(jobDetails[jobIndex][0]);

                            double initialSize = listDynamic.get(location).getSize();

                            JobList job = new JobList();
                            job.setEnd(listDynamic.get(location).getEnd());
                            listDynamic.get(location).setEnd(listDynamic.get(location).getStart() + jobDetails[jobIndex][3]);
                            job.setStart(listDynamic.get(location).getEnd());
                            listDynamic.add(location + 1, job);

                            po = (int)((650 * listDynamic.get(location).getStart() / ReadData.dataList.getDynamicMemory()) + (listDynamic.get(location).getJobSegment().getWidth()/2));
                            moveJob(po, jobBlock[jobIndex]);

                            internalFragmentation += initialSize - jobDetails[jobIndex][3];
                            iFragmentation.setText(Integer.toString(internalFragmentation));
                        }

                        queue.remove(i);
                        rootAnchor.getChildren().remove(waitingListFixed.get(i).getJobSegment());
                        waitingListFixed.remove(i);

                        if  (ReadData.dataList.getPartitionType().equals("Fixed Memory Partition"))
                            po = 0;

                        i--;

                        /* rearranging the job in waiting list */
                        Rectangle job;
                        double changeX;

                        for(int j = 0; j < waitingListFixed.size(); j++) {
                            job = waitingListFixed.get(j).getJobSegment();
                            changeX = 80;

                            for(int k = 0; k < j; k++)
                                changeX += 30;

                            job.setLayoutX(changeX);
                            job.setLayoutY(270);
                        }
                    }
                }

            /* reset the memory size of dynamic memory if job deallocates */
            if (listDynamic.size() > 1)
                for (int i = 0; i < listDynamic.size() - 1; i++)
                    if ((listDynamic.get(i).getJobSegment() == null) && (listDynamic.get(i + 1).getJobSegment() == null)) {
                        listDynamic.get(i).setEnd(listDynamic.get(i + 1).getEnd());
                        listDynamic.remove(i + 1);
                        i--;
                    }

            /* arrival of multiple jobs */
            for (int i = 0; i < ReadData.dataList.getTotalJobList(); i++) {
                po = 0;

                if (jobDetails[i][1] == 0) {
                    /* get the next accessible location */
                    int location = algorithmType(i);

                    /* memory is not fully occupied with jobs */
                    if (location != -1) {
                        /* flag job */
                        jobDetails[i][1] = -2;
                        jobDetails[i][2]--;

                        if (ReadData.dataList.getPartitionType().equals("Fixed Memory Partition")) {
                            if (location == 0)
                                po += (jobSize[i] / 2);
                            else
                                po += (partitionWidth[location - 1] + (jobSize[i] / 2) - 15);

                            moveJob(po, jobBlock[i]);
                            segmentX[location][1] = jobDetails[i][0];
                            memoryBlockEnquire[location] = jobBlock[i];

                        } else if (ReadData.dataList.getPartitionType().equals("Dynamic Memory Partition")) {
                            listDynamic.get(location).setJobSegment(jobBlock[i]);
                            listDynamic.get(location).setJobIndex(jobDetails[i][0]);
                            double initialSize = listDynamic.get(location).getSize();

                            /* job size can fit into the memory */
                            if (listDynamic.get(location).getEnd() < ReadData.dataList.getDynamicMemory()) {
                                JobList job = new JobList();
                                listDynamic.get(location).setEnd(listDynamic.get(location).getStart() + jobDetails[i][3]);
                                job.setStart(listDynamic.get(location).getEnd());
                                job.setEnd(listDynamic.get(location + 1).getStart());
                                listDynamic.add(location + 1, job);

                                if (jobDone > 0) {
                                    internalFragmentation += (initialSize - jobDetails[i][3]);
                                    iFragmentation.setText(Integer.toString(internalFragmentation));
                                }
                            } else {
                                /* job size is greater than the memory size */
                                listDynamic.get(location).setEnd(listDynamic.get(location).getStart() + jobDetails[i][3]);
                                JobList job = new JobList();
                                job.setStart(listDynamic.get(location).getEnd());
                                job.setEnd(ReadData.dataList.getDynamicMemory());
                                listDynamic.addLast(job);
                            }

                            po = (int)((650 * listDynamic.get(location).getStart() / ReadData.dataList.getDynamicMemory()) + (listDynamic.get(location).getJobSegment().getWidth() / 2));
                            moveJob(po, jobBlock[i]);
                        }
                    }

                    /* memory is fully occupied */
                    else {
                        jobDetails[i][1] = -1;

                        JobList job = new JobList();
                        job.setJobSegment(jobBlock[i]);
                        job.setEnd(jobSize[i]);
                        job.setJobIndex(i + 1);

                        /* add job to waiting list if memory is full or space if available space is not large enough */
                        queue.addLast(job);
                        waitingList();
                    }
                } else if (jobDetails[i][1] > 0)
                    jobDetails[i][1]--;
            }

            timeCount++;

            if (ReadData.dataList.getPartitionType().equals("Fixed Memory Partition"))
                for (int i = 0; i < ReadData.dataList.getJobListEnquire(); i++) {
                    if (memoryBlockEnquire[i] != null || (timeCount < 20)) {
                        runAdd = true;
                        break;
                    } else
                        runAdd = false;
                }
            else if (ReadData.dataList.getPartitionType().equals("Dynamic Memory Partition"))
                if(listDynamic.get(0).getSize() == ReadData.dataList.getDynamicMemory())
                    runAdd = false;

            if (!queue.isEmpty())
                waitingTime += queue.size();

            waitList.setText(Integer.toString(queue.size()));
            timeTotal.setText(Integer.toString(timeCount));

            if (!runAdd)
                break;
        }
    }

    public void deallocateJob(Rectangle job) {
        job.translateYProperty().set(-203);
        Timeline timeline = new Timeline();
        KeyValue kv2 = new KeyValue(job.translateYProperty(), -300, Interpolator.EASE_IN);
        KeyFrame kf2 = new KeyFrame(Duration.millis(500), kv2);

        timeline.getKeyFrames().add(kf2);
        timeline.setOnFinished(event -> root.getChildren().remove(job));
        timeline.play();

        runSimulation = false;
    }

    public void moveJob(double destination, Rectangle job) {
        root.getChildren().add(job);
        job.translateXProperty().set(0);
        job.translateYProperty().set(300);

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(job.translateXProperty(), -325 + destination, Interpolator.EASE_IN);
        KeyValue kv2 = new KeyValue(job.translateYProperty(), -140, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
        KeyFrame kf2 = new KeyFrame(Duration.millis(500), kv2);
        timeline.getKeyFrames().add(kf);
        timeline.getKeyFrames().add(kf2);
        timeline.play();
        runSimulation = false;
    }

    public int algorithmType(int jobIndex) {
        switch (ReadData.dataList.getAlgorithmType()) {
            case "First-Fit":
                if (ReadData.dataList.getPartitionType().equals("Fixed Memory Partition")) {
                    for (int i = 0; i < ReadData.dataList.getJobListEnquire(); i++)
                        /* if the segment of the memory is free and can fit the job, return the first free memory block index */
                        if ((segmentX[i][1] == 0) && (jobDetails[jobIndex][3] <= memoryDetails[i]))
                            return i;

                } else if (ReadData.dataList.getPartitionType().equals("Dynamic Memory Partition")) {
                    for (int i = 0; i < listDynamic.size(); i++)
                        /* if the segment of the memory previously allocated job, and return the segment if it fits the job */
                        if ((listDynamic.get(i).getJobSegment() == null) && (jobDetails[jobIndex][3] <= (listDynamic.get(i).getSize())))
                            return i;

                }

                return -1;
            case "Best-Fit": {
                int y = -1;

                if (ReadData.dataList.getPartitionType().equals("Fixed Memory Partition")) {
                    double p = 14000;

                    for (int i = 0; i < ReadData.dataList.getJobListEnquire(); i++)
                        /* if the memory segment is free and can fit the job, check if the internal fragmentation is minimized */
                        if ((segmentX[i][1] == 0) && (jobDetails[jobIndex][3] <= memoryDetails[i]))
                            /* mark the segment if the internal fragmentation is smaller than the previous fit */
                            if ((memoryDetails[i] - jobDetails[jobIndex][3]) < p) {
                                p = memoryDetails[i] - jobDetails[jobIndex][3];
                                y = i;
                            }

                } else if (ReadData.dataList.getPartitionType().equals("Dynamic Memory Partition")) {
                    double p = ReadData.dataList.getDynamicMemory();

                    for (int i = 0; i < listDynamic.size(); i++)
                        /* if the previous allocated memory space is free and large enough to fit the job, check if the fragmentation is minimized or maximized */
                        if ((listDynamic.get(i).getJobSegment() == null) && (jobDetails[jobIndex][3] <= (listDynamic.get(i).getSize())))
                            /* mark the segment if the fragmentation is smaller than the previous fit */
                            if (((listDynamic.get(i).getSize()) - jobDetails[jobIndex][3]) < p) {
                                p = listDynamic.get(i).getSize() - jobDetails[jobIndex][3];
                                y = i;
                            }
                }

                return y;
            }
            case "Worst-Fit": {
                int y = -1;

                if (ReadData.dataList.getPartitionType().equals("Fixed Memory Partition")) {
                    double p = -14000;

                    for (int i = 0; i < ReadData.dataList.getJobListEnquire(); i++)
                        if ((segmentX[i][1] == 0) && (jobDetails[jobIndex][3] <= memoryDetails[i]))
                            if ((memoryDetails[i] - jobDetails[jobIndex][3]) > p) {
                                p = memoryDetails[i] - jobDetails[jobIndex][3];
                                y = i;
                            }

                } else if (ReadData.dataList.getPartitionType().equals("Dynamic Memory Partition")) {
                    double p = -ReadData.dataList.getDynamicMemory();

                    for (int i = 0; i < listDynamic.size(); i++)
                        if ((listDynamic.get(i).getJobSegment() == null) && (jobDetails[jobIndex][3] <= (listDynamic.get(i).getSize())))
                            if (((listDynamic.get(i).getSize()) - jobDetails[jobIndex][3]) > p) {
                                p = (((listDynamic.get(i).getSize()) - jobDetails[jobIndex][3]));
                                y = i;
                            }

                }

                return y;
            }
        }

        return 10;
    }

    public void waitingList() {
        double xCoordinate = 80;
        Rectangle rectangle = new Rectangle();
        JobList obj = new JobList();

        rectangle.setHeight(50);
        rectangle.setWidth(30);
        rectangle.setFill(Color.web("rgb(220,220,220)"));
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(2);
        rectangle.setStrokeType(StrokeType.INSIDE);
        obj.setJobSegment(rectangle);

        waitingListFixed.addLast(obj);

        for(int i = 0; i < waitingListFixed.size() - 1; i++)
            xCoordinate += 30;

        rectangle.setLayoutX(xCoordinate);
        rectangle.setLayoutY(270);
        rootAnchor.getChildren().add(rectangle);
    }
}
