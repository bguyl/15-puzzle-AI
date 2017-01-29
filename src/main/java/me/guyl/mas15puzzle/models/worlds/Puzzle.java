package me.guyl.mas15puzzle.models.worlds;

import me.guyl.mas15puzzle.models.agents.Agent;

import java.awt.*;
import java.util.List;
import java.util.Observable;

public class Puzzle extends Observable {

    private List<Agent> grid;
    private int width, height;

    public Puzzle(List<Agent> grid, int width, int height) {
        this.grid = grid;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Agent getAgentAtPosition(Point position) {
        return getAgentAtPosition(position.x, position.y);
    }

    public Agent getAgentAtPosition(int x, int y){
        if (x < 0 || x >= width ||
            y < 0 || y >= height)
            return null;
        return grid.get(x * width + y + 1);
    }

    public synchronized boolean requestPosition(Agent agent, Point desired) {
        if (desired.x < 0 || desired.x >= width ||
            desired.y < 0 || desired.y >= height)
            return false;
        if(getAgentAtPosition(desired) != null)
            return false;
        if(manhattanDistance(agent.getPosition(), desired) != 1)
            return false;

        grid.set(desired.x * width + desired.y + 1, agent);

        this.setChanged();
        notifyObservers();
        return true;
    }

    public static int manhattanDistance(Point a, Point b){
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

}
