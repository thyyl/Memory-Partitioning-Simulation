package sample;

import javafx.scene.control.Label;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadData extends JFrame {

    public static Label jobFilePath = new Label(), memoryFilePath = new Label();
    public static int[][] jobBlock;
    public static int[] memoryBlockSize;

    public static void setMemoryFilePath() {
        JFileChooser fileChooser = new JFileChooser((FileSystemView.getFileSystemView().getHomeDirectory()));
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter restrict = new FileNameExtensionFilter("Only .txt files","txt");
        fileChooser.addChoosableFileFilter(restrict);
        int flag = fileChooser.showOpenDialog(null);

        if ((flag == JFileChooser.APPROVE_OPTION))
            memoryFilePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
        else
            memoryFilePath.setText("Action aborted");
    }

    public static void setJobFilePath() {
        JFileChooser fileChooser = new JFileChooser((FileSystemView.getFileSystemView().getHomeDirectory()));
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter restrict = new FileNameExtensionFilter("Only .txt files","txt");
        fileChooser.addChoosableFileFilter(restrict);
        int flag = fileChooser.showOpenDialog(null);

        if ((flag == JFileChooser.APPROVE_OPTION))
            jobFilePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
        else
            jobFilePath.setText("Action aborted");
    }

    public static void readFile() {
        try (Scanner scanner = new Scanner(new File(jobFilePath.getText()))) {
            int totalJob = scanner.nextInt();
            jobBlock = new int[totalJob][4];

            for (int count = 0; scanner.hasNext(); count++) {
                jobBlock[count][0] = scanner.nextInt();
                jobBlock[count][1] = scanner.nextInt();
                jobBlock[count][2] = scanner.nextInt();
                jobBlock[count][3] = scanner.nextInt();
            }

            ReadData.dataList.setTotalJobList(totalJob);
            ReadData.dataList.setJobDetail(jobBlock);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try (Scanner scanner = new Scanner(new File(memoryFilePath.getText()))) {
            int totalMemory = scanner.nextInt();
            memoryBlockSize = new int[totalMemory];

            for (int count = 0; scanner.hasNext(); count++)
                memoryBlockSize[count] = scanner.nextInt();

            ReadData.dataList.setTotalMemoryBlock(totalMemory);
            ReadData.dataList.setBlock(memoryBlockSize);

        } catch (FileNotFoundException e) { e.printStackTrace(); }
    }

    public static class dataList {

        public static int totalJobList;
        public static int jobListEnquire;
        public static int totalMemoryBlock;
        public static int[] block;
        public static int[][] jobDetail;
        public static int dynamicMemory;
        public static String allocationType;
        public static String algorithmType;

        public static int getTotalJobList() {
            return totalJobList;
        }

        public static void setTotalJobList(int totalJobList) {
            dataList.totalJobList = totalJobList;
        }

        public static int getJobListEnquire() {
            return jobListEnquire;
        }

        public static void setJobListEnquire(int jobListEnquire) {
            dataList.jobListEnquire = jobListEnquire;
        }

        public static void setTotalMemoryBlock(int totalMemory) {
            dataList.totalMemoryBlock = totalMemory;
        }

        public static int[] getBlock() {
            return block;
        }

        public static void setBlock(int[] block) {
            dataList.block = block;
        }

        public static int getTotalBlockSize() {
            int totalBlockSize = 0;
            for (int i = 0; i < jobListEnquire; i++)
                totalBlockSize += block[i];

            return totalBlockSize;
        }

        public static int getDynamicMemory() {
            return dynamicMemory;
        }

        public static void setDynamicMemory(int dynamicMemory) {
            dataList.dynamicMemory = dynamicMemory;
        }

        public static int[][] getJobDetail() {
            return jobDetail;
        }

        public static void setJobDetail(int[][] jobDetail) {
            dataList.jobDetail = jobDetail;
        }

        public static String getPartitionType() { return allocationType; }

        public static void setAllocationType(String allocationType) {
            dataList.allocationType = allocationType;
        }

        public static String getAlgorithmType() {
            return algorithmType;
        }

        public static void setAlgorithmType(String algorithmType) {
            dataList.algorithmType = algorithmType;
        }
    }
}
