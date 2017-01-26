import jdk.nashorn.internal.runtime.ECMAException;

import javax.swing.text.NumberFormatter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by mhoro on 1/24/2017.
 * @noinspection Since15
 */
public class Lab1 {
    public static void main(String[] args) throws Exception {
        if(args.length != 3){
            System.out.println("Please input train tune and test files");
            System.exit(0);
        }
        String pathTrain = args[0];
        String pathTune = args[1];
        String pathTest = args[2];
        Data train =  parseDataset(pathTrain);
        Data tune = parseDataset(pathTune);
        Data test = parseDataset(pathTest);



        double[] w = new double[train.x[0].length];
        double[] bestW = null;
        double bestTune = -1;
        int bestEpoch = -1;
        /*
        for(int i = 0; i < w.length; i ++){
            w[i] = Math.random() * 0.1 - 0.05;
        }*/
        double learningRate = 0.1;
        for(int epoch = 0; epoch < 5000; epoch ++){
            shuffle(train.x,train.y);
            for(int i = 0; i < train.x.length; i ++){
                int[] x = train.x[i];
                int y = train.y[i];
                double o = score(x,w);
                double diff = y- o;
                for(int j = 0; j < w.length; j ++){
                    w[j] += learningRate * diff * x[j];
                }


            }
            if((epoch + 1)%50 == 0) {
                double tuneAccuracy = accuracy(tune, w, false);
                if (tuneAccuracy > bestTune) {
                    bestTune = tuneAccuracy;
                    bestW = w;
                    bestEpoch = epoch;
                }
            }
        }
        printAccuracy(bestEpoch,train, tune, test,bestW);
    }

    static double bestTune = -1;
    static double testOfBestTune = -1;
    static int bestTuneEpoch = 0;
    static NumberFormat formatter = new DecimalFormat("#0.0");


    public static void printAccuracy(int epoch, Data train, Data tune, Data test, double[] w){
        double trainAcc = accuracy(train,w,false);
        double tuneAcc = accuracy(tune,w,false);
        double testAcc = accuracy(test, w, true);
        if(tuneAcc > bestTune){
            bestTune = tuneAcc;
            testOfBestTune = testAcc;
            bestTuneEpoch = epoch;
        }
        System.out.println("\nEpoch "+ (epoch)+": train = " + formatter.format(trainAcc) + "% tune = "
                +formatter.format(tuneAcc) + "% test = " + formatter.format(testAcc)+"%" );
    }

    public static void printW(double[] w, Data data){
        for(int i = 0; i< w.length -1; i ++ ){
            System.out.printf("Wgt = %s %s\n", formatter.format(w[i]), data.featureNames[i]);
        }
        System.out.printf("Threshold = %s\n", formatter.format(w[w.length-1]));

    }

    private static void printData(Data data){
        String[] classNames = new String[]{"lowToMid","midToHigh"};
        String[] featureNames = new String[]{"T","F"};
        for(int i = 0; i < data.x.length; i ++){
            System.out.print("trainEx"+(i+1));
            System.out.print(" " +classNames[data.y[i]]);
            for(int j = 0; j < 20; j ++){
                System.out.print(" "+featureNames[data.x[i][j]]);
            }
            System.out.println();
        }
    }

    private static  double accuracy(Data data, double[] w, boolean printPredictions){
        int correct = 0;
        if(printPredictions){
            System.out.println("Test Set Predictions:");
        }
        for(int i = 0; i < data.x.length; i ++){
            int[] x = data.x[i];
            int y = data.y[i];
            int pred = score(x,w);
            if(printPredictions)
                System.out.println(data.classNames[pred]);
            if(pred == y)
                correct ++;
        }
        return 100*((double)correct) / data.x.length;
    }


    public static int score(int[] a, double[] w ){
        double score = dot(a,w);
        if(score > 0)
            return 1;
        else
            return 0;
    }

    public static void shuffle(int[][] xArr, int[] yArr){
        for(int i =0; i < xArr.length; i++){
            int[] tempX = xArr[i];
            int tempY = yArr[i];
            int j = (int)(Math.random() * (xArr.length - i));
            xArr[i] = xArr[i + j];
            xArr[i + j] = tempX;
            yArr[i] = yArr[i+j];
            yArr[i+j] = tempY;
        }
    }

    public static double dot(int[] a, double[] w){
        double sum = 0;
        for(int i = 0; i < a.length; i ++){
            sum += a[i]* w[i];
        }
        return sum;
    }

    public static Data parseDataset(String path) throws Exception {
        Scanner scanner = new Scanner(new FileReader(path));
        int numFeatures = 0;
        while(scanner.hasNextLine()){
            String[] line = getLine(scanner);
            if(line.length == 1){
                numFeatures = Integer.parseInt(line[0]);
                break;
            }
            else if(line.length > 0){
                throw new Exception("Invalid number of numbers on line for num features");
            }
        }
        int featuresRead = 0;
        String[] featureNames = new String[numFeatures];
        String[][] featureValues = new String[numFeatures][2];
        while(scanner.hasNextLine()){
            String[] line = getLine(scanner);
            if(line.length == 4) {
                featureNames[featuresRead] = line[0];
                String dash = line[1];
                if(!dash.equals("-"))
                    throw new Exception("Parse error. No dash!");

                featureValues[featuresRead][0] = line[2];
                featureValues[featuresRead][1] = line[3];
                featuresRead ++;
                if(featuresRead == numFeatures) {
                    break;
                }
            }
            else if(line.length != 0){
                throw new Exception("Incorrect num of words for feature names");
            }
        }
        String[] classNames = new String[2];
        int tempClassIndex = 0;
        while(scanner.hasNextLine()){
            String[] line = getLine(scanner);

            if(line.length == 1){
                classNames[tempClassIndex] = line[0];
                tempClassIndex ++;
                if(tempClassIndex > 1){
                    break;
                }
            }
            else if(line.length != 0){
                throw new Exception("Invalid number of words for class names");
            }
        }
        int numExamples = 0;
        while(scanner.hasNextLine()){
            String[] line = getLine(scanner);
            if(line.length == 1){
                numExamples = Integer.parseInt(line[0]);
                break;
            }
            else if(line.length != 0)
                throw new Exception("Wrong number of words for num examples");
        }
        int[][] x = new int[numExamples][numFeatures+1];
        int[] y = new int[numExamples];
        for(int i = 0; i < numExamples; i ++){
            String[] line = null;
            while(scanner.hasNextLine()){
                line = getLine(scanner);
                if(line.length ==0)
                    continue;
                else if(line.length == 2 + numFeatures)
                    break;
                else{
                    throw new Exception("Incorrect number of features for example");
                }
            }
            String[] split = line;
            y[i] = split[1].equals(classNames[0]) ? 0 : 1;
            for(int j = 0; j < numFeatures; j ++){
                String val = split[j + 2];
                if(val.equals(featureValues[j][0]))
                    x[i][j] = 0;
                else if(val.equals(featureValues[j][1]))
                    x[i][j] = 1;
                else
                    throw new Exception("Failed parsing, incorrect feature value");
            }
            x[i][numFeatures] = -1;
        }
        Data data = new Data();
        data.x = x;
        data.y = y;
        data.featureNames = featureNames;
        data.featureValues = featureValues;
        data.classNames = classNames;
        return data;
    }

    public static String[] getLine(Scanner scanner){
        if(!scanner.hasNextLine())
            return new String[0];
        else{
            Scanner lineReader = new Scanner(scanner.nextLine());
            List<String> wordList = new ArrayList<>();
            while(lineReader.hasNext()){
                String word = lineReader.next();
                if(word.startsWith("//"))
                    break;
                wordList.add(word.split("//")[0]);
            }
            return wordList.toArray(new String[wordList.size()]);
        }
    }

    public static class Data{
        public int[][] x;
        public int[] y;
        public String[] featureNames;
        public String[][] featureValues;
        public String[] classNames;
    }
}
