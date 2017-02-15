package me.guyl.mas15puzzle.models.worlds;

import me.guyl.mas15puzzle.models.agents.Agent;
import me.guyl.mas15puzzle.models.agents.PuzzleAgent;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class Puzzle extends Observable {

    private Agent[][] grid;
    private int width, height;

    public Puzzle(int width, int height, Observer observer) {
        this.grid = new Agent[width][height];
        this.width = width;
        this.height = height;
        addObserver(observer);
    }

    public void start(){
        for(int i = 0; i < width * height; i++){
            int x = (i - (i%width)) / height, y = i%width;
            Agent a = this.grid[x][y];
            if(a != null)
                new Thread(a).start();
        }
    }


    public void stop() {
        for(int i = 0; i < width * height; i++){
            int x = (i - (i%width)) / height, y = i%width;
            Agent a = this.grid[x][y];
            if(a != null)
                (a).interruption();
        }
    }

    public Agent getAgentAtPosition(Point position) {
        return getAgentAtPosition(position.x, position.y);
    }

    public Agent getAgentAtPosition(int x, int y){
        if (x < 0 || x >= width ||
            y < 0 || y >= height)
            return null;
        return grid[x][y];
    }

    public synchronized boolean requestPosition(Agent agent, Point desired) {
        if (desired.x < 0 || desired.x >= width ||
            desired.y < 0 || desired.y >= height)
            return false;
        if(getAgentAtPosition(desired) != null)
            return false;
        if(manhattanDistance(agent.getPosition(), desired) != 1)
            return false;

        grid[agent.getPosition().x][agent.getPosition().y] = null;
        grid[desired.x][desired.y] = agent;

        this.setChanged();

        Point[] pts = new Point[2];
        pts[0] = agent.getPosition();
        pts[1] = desired;

        notifyObservers(pts);
        return true;
    }

    public static int manhattanDistance(Point a, Point b){
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    public Agent[][] getGrid() {
        return grid;
    }

    public void placeAgent(int i, PuzzleAgent agent) {
        int x = (i - (i%width)) / height, y = i%width;
        grid[x][y] = agent;
    }

    public synchronized boolean requestToken() {
        boolean token = true;
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                Agent a = grid[i][j];
                if(a != null && grid[i][j].isToken()){
                    token = false; break;
                }
            }
        }
        return token;
    }

}
