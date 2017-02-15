package me.guyl.mas15puzzle.views;

import javax.swing.*;
import me.guyl.mas15puzzle.models.agents.*;
import me.guyl.mas15puzzle.models.worlds.Puzzle;

import java.awt.*;
import java.util.ArrayList;

public class AgentButton extends JButton {

    PuzzleAgent agent;
    static Puzzle puzzleWorld;

    public AgentButton(String s, int position) {
        super(s);
        int x = (position - (position%4)) / 4;
        Point destination = new Point(x, position % 4);
        agent = new PuzzleAgent(destination, destination, puzzleWorld);
    }

    public AgentButton(String s, Agent agent) {
        super(s);
        //TODO: Dirty code here ! Upcast need to be removed
        this.agent = (PuzzleAgent)agent;
    }

    public void setPosition(int position){
        int x = (position - (position%4)) / 4;
        agent.setPosition(new Point(x, position%4));
    }

    public PuzzleAgent getAgent() {
        return agent;
    }
}

