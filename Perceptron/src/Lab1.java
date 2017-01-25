import javax.swing.text.NumberFormatter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
        for(int i = 0; i < w.length; i ++){
            w[i] = Math.random() * 0.1 - 0.05;
        }
        double learningRate = 0.1;
        for(int epoch = 0; epoch < 1000; epoch ++){
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
            if((epoch + 1)%50 == 0)
                printAccuracy(epoch +1,train,tune,test,w);
        }
        System.out.println(String.format("The tune set was highest (%s%% accuracy) at Epoch %d. Test set = %s%% here",
                formatter.format(bestTune),bestTuneEpoch,formatter.format(testOfBestTune)));
        printW(w,train);
    }

    static double bestTune = -1;
    static double testOfBestTune = -1;
    static int bestTuneEpoch = 0;
    static NumberFormat formatter = new DecimalFormat("#0.0");


    public static void printAccuracy(int epoch, Data train, Data tune, Data test, double[] w){
        double trainAcc = accuracy(train,w);
        double tuneAcc = accuracy(tune,w);
        double testAcc = accuracy(test, w);
        if(tuneAcc > bestTune){
            bestTune = tuneAcc;
            testOfBestTune = testAcc;
            bestTuneEpoch = epoch;
        }
        System.out.println("Epoch "+ epoch+": train = " + formatter.format(trainAcc) + "% tune = "
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

    private static  double accuracy(Data data, double[] w){
        int correct = 0;
        for(int i = 0; i < data.x.length; i ++){
            int[] x = data.x[i];
            int y = data.y[i];
            if(score(x,w) == y)
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
        while(scanner.hasNext()){
            String word = scanner.next();
            if(!word.contains("//")) {
                numFeatures = Integer.parseInt(word);
                scanner.nextLine();
                break;
            }
            scanner.nextLine();
        }
        int featuresRead = 0;
        String[] featureNames = new String[numFeatures];
        String[][] featureValues = new String[numFeatures][2];
        while(scanner.hasNext()){
            String word = scanner.next();
            if(!word.contains("//")) {
                featureNames[featuresRead] = word;
                String dash = scanner.next();
                if(!dash.equals("-"))
                    throw new Exception("Parse error. No dash!");

                featureValues[featuresRead][0] = scanner.next();
                featureValues[featuresRead][1] = scanner.next();
                featuresRead ++;
                if(featuresRead == numFeatures) {
                    scanner.nextLine();
                    break;
                }
            }
            scanner.nextLine();

        }
        String[] classNames = new String[2];
        int tempClassIndex = 0;
        while(scanner.hasNext()){
            String word = scanner.next();
            if(!word.contains("//")){
                classNames[tempClassIndex] = word;
                tempClassIndex ++;
                if(tempClassIndex > 1){
                    scanner.nextLine();
                    break;
                }
            }
            scanner.nextLine();
        }
        int numExamples = 0;
        while(scanner.hasNext()){
            String word = scanner.next();
            if(!word.contains("//")){
                numExamples = Integer.parseInt(word);
                scanner.nextLine();
                break;
            }
            scanner.nextLine();
        }
        int[][] x = new int[numExamples][numFeatures+1];
        int[] y = new int[numExamples];
        for(int i = 0; i < numExamples; i ++){
            String line = "";
            while(scanner.hasNextLine()){
                line = scanner.nextLine();
                if(!line.contains("//") && line.length() > 0) {
                    break;
                }
            }
            String[] split = line.split("\\s+");
            y[i] = split[1].equals(classNames[0]) ? 0 : 1;
            for(int j = 0; j < numFeatures; j ++){
                String val = split[j + 2];
                if(val.equals(featureValues[j][0]))
                    x[i][j] = -1;
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

    public static class Data{
        public int[][] x;
        public int[] y;
        public String[] featureNames;
        public String[][] featureValues;
        public String[] classNames;
    }
}
