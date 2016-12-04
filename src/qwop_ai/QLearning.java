package qwop_ai;

import java.util.ArrayList;

public class QLearning {

    private ArrayList<String> _controls = null;
    private float _score;

    public QLearning(ArrayList<String> controls, float startingScore){

        this._controls = controls;
        this._score = startingScore;
    }

    
}
