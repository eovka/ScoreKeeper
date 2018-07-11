package com.example.android.scorekeeper;

import android.arch.lifecycle.ViewModel;

public class ScoreViewModel extends ViewModel {
    private int scoreA;
    private int scoreB;
    private int redA;
    private int redB;
    private int yellowA;
    private int yellowB;

    public int getScoreA() {
        return scoreA;
    }
    public int getScoreB() {
        return scoreB;
    }
    public int getRedA() {
        return redA;
    }
    public int getRedB() {
        return redB;
    }
    public int getYellowA() {
        return yellowA;
    }
    public int getYellowB() {
        return yellowB;
    }

    public void setScoreA(int scoreA) {
        this.scoreA = scoreA;
    }
    public int setScoreB(int scoreB) {
        this.scoreB = scoreB;
        return this.scoreB;
    }
    public void setRedA(int redA) {
        this.redA = redA;
    }
    public void setRedB(int redB) {
        this.redB = redB;
    }
    public void setYellowA(int yellowA) {
        this.yellowA = yellowA;
    }
    public void setYellowB(int yellowB) {
        this.yellowB = yellowB;
    }
}
