package qwop_ai;

import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


// Learning matrix is expressed as an arraylist of arrays of the form:
// { [ distance, ctrl0, ctrl1, ctrl2, ... ctrl9 ]
//   ...
//   ...
//   ...
// }
// Where the values of the ctrl(n) entries are the "score" of taking that action
// at the distance.
// For the controls, the index mapping is as follows:
// ctrl 0 -> no-op or ""
// ctrl 1 -> q
// ctrl 2 -> w
// ctrl 3 -> o
// ctrl 4 -> p
// ctrl 5 -> qo
// ctrl 6 -> qp
// ctrl 7 -> wo
// ctrl 8 -> wp
// .

public class QLearning {

    private final String[] CONTROLS = {"", "q", "w", "o", "p", "qo", "qp", "wo", "wp"};

    private final double LEARNING_RATE = 0.5;
    private final double DISCOUNT_FACTOR = 0.5;
    private final double TIME_DEDUCTION = 0.2;

    private double _score = 0;

    private HashMap<String, ArrayList<Double>> _matrix;

    public QLearning(){
        _matrix = new HashMap<String, ArrayList<Double>>();
    }

    public String getDecision(double distance){
        ArrayList<Double> choices = _matrix.get(String.valueOf(distance));
        if (choices == null){
            _matrix.put(String.valueOf(distance), newAL());
            choices = newAL();
        }
        int max_index = 0;
        double max = choices.get(0);
        for (int i=0; i<9; i++){
            if(choices.get(i) > max){
                max = choices.get(i);
                max_index = i;
            }
        }
        return CONTROLS[max_index];

    }

    public void recordOutcome(double distance, double outcome, String actionTaken){
        List<Double> choices = _matrix.get(String.valueOf(distance));
        if (choices == null){
            _matrix.put(String.valueOf(distance), newAL());
            choices = newAL();
        }
        int actionIndex = ArrayUtils.indexOf(CONTROLS, actionTaken);
        if(actionIndex <0 || actionIndex >8){ return; }

        double previousQ = choices.get(actionIndex);
        System.out.println(qLearningEq(previousQ, outcome));

        choices.set(actionIndex, qLearningEq(previousQ, outcome));
    }

    private double qLearningEq(double oldValue, double newValue){
        double learned_value = (newValue - oldValue) - TIME_DEDUCTION ;
        return oldValue + (LEARNING_RATE * learned_value);
    }

    private void readMatrixFile(){

        //_matrix.clear();

        String csvFile = "matrix.txt";
        BufferedReader br = null;
        String line = "";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                _matrix.put(values[0],
                        stringArrTodoubleArr(Arrays.copyOfRange(values, 1, 9)));

                System.out.println(_matrix.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private ArrayList<Double> stringArrTodoubleArr(String[] arr){
        ArrayList<Double> result = new ArrayList<Double>();
        for (int i=0; i<9; i++){
            result.add(Double.valueOf(arr[i]));
        }

        return result;
    }

    private ArrayList<Double> newAL(){
        ArrayList<Double> list = new ArrayList<Double>(10);
        while(list.size() < 10) list.add(0.0);
        return list;
    }


    public void saveMatrixFile(){
        TreeSet<String> keys = new TreeSet<String>();
        keys.addAll(_matrix.keySet());

        for (String key : keys){
            System.out.println(key + _matrix.get(key).toString());
        }
    }

}
