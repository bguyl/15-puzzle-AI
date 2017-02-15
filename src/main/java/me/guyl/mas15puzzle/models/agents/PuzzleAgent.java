package me.guyl.mas15puzzle.models.agents;

import me.guyl.mas15puzzle.models.Action;
import me.guyl.mas15puzzle.models.Message;
import me.guyl.mas15puzzle.models.Perform;
import me.guyl.mas15puzzle.models.worlds.Puzzle;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PuzzleAgent extends Agent {

    private boolean running;
    private Puzzle world;
    private List<Message> complains;
    private Point waitingPosition;
    private Point requestedPosition;
    private Agent waitedAgent;
    static int count = 0;
    static Random rand = new Random();
    int id;

    public PuzzleAgent(Point position, Point destination, Puzzle world) {
        this.position = position;
        this.destination = destination;
        this.world = world;
        this.running = true;
        this.messages = new ArrayList<Message>();
        this.complains = new ArrayList<Message>();
        this.id = ++count;
    }

    @Override
    public void run(){
        while (running){
            messageHandler();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(token){
                move(getNextPositions());
                token = false;
            } else {
                token = world.requestToken();
            }
        }
    }

    private boolean moveForward(List<Point> positions){
        boolean succeed = false;
        List<Point> nextPositions = new ArrayList<Point>(positions);

        while (!nextPositions.isEmpty()) {
            Point current = nextPositions.remove(rand.nextInt(nextPositions.size()));
            if (world.requestPosition(this, current)) {
                position = current;
                if (!complains.isEmpty()) {
                    for (Message m : complains) {
                        Agent emitter = m.getEmitter();
                        emitter.sendMessage(new Message(this, emitter, Perform.RESPONSE, Action.OK));
                    }
                    complains = new ArrayList<Message>();
                }
                succeed = true;
                break;
            }
        }

        return succeed;
    }

    private void sendOK(){
        for(Message m : complains){
            Agent emitter = m.getEmitter();
            emitter.sendMessage(new Message(this, emitter, Perform.RESPONSE, Action.OK));
        }
        complains = new ArrayList<Message>();
    }

    private void sendFree(){

    }

    private boolean avoidance(){
        boolean succeed = false;
        for(int i = -1; i <= 1; i += 2){
            Point xi = new Point(position.x + i, position.y), yi = new Point(position.x, position.y + i);
            if(world.requestPosition(this, xi)){
                position = xi;
                sendOK();
                succeed = true;
                break;
            }
            else if(world.requestPosition(this, yi)){
                position = yi;
                sendOK();
                succeed = true;
                break;
            }
        }
        return succeed;
    }

    private void askForHelp(List<Point> nextPositions){
        Point p = nextPositions.get(rand.nextInt(nextPositions.size()));
        Agent neighbor = world.getAgentAtPosition(p);
        neighbor.sendMessage(new Message(this, neighbor, Perform.REQUEST, Action.FREE));
        waitingPosition = p;
    }

    protected void move(List<Point> nextPositions){
        ArrayList<Point> moves = new ArrayList<Point>();
        for (int i = -1; i <= 1; i += 2) {
            Point xi = new Point(position.x + i, position.y), yi = new Point(position.x, position.y + i);
            moves.add(xi);
            moves.add(yi);
        }

        if(waitingPosition != null){
            if(world.requestPosition(this, waitingPosition)){
                position = waitingPosition;
                return;
            }
            waitingPosition = null;
        }

        if(complains.isEmpty() && destination.equals(position)){
            return;
        }

        // If we can, we pick a chosen position
        if(!nextPositions.isEmpty()){
            if(!moveForward(nextPositions))
                askForHelp(nextPositions);
            return;
        }

        ArrayList<Point> neighs = new ArrayList<Point>();
        // If we at the destination, we try to move
        if(!complains.isEmpty()){
            if(!avoidance()){
                for(Point p : moves){
                    Agent a = world.getAgentAtPosition(p);
                    if(a != null){
                        neighs.add(p);
                    }
                }
                askForHelp(neighs);
            }
            return;
        }

        //Try a random move
        if(!destination.equals(position)) {
            Point current = new Point();
            while (!moves.isEmpty()) {
                current = moves.remove(rand.nextInt(moves.size()));
                if (world.requestPosition(this, current)) {
                    position = current;
                    return;
                }
            }
            List<Point> lp = new ArrayList<Point>();
            lp.add(current);
            askForHelp(lp);
        }
    }

    private List<Point> getNextPositions(){
        int minDist = Puzzle.manhattanDistance(position, destination);
        List<Point> potentialPoint = new ArrayList<Point>();

        for(int i = -1; i <= 1; i += 2){
            Point xi = new Point(position.x + i, position.y), yi = new Point(position.x, position.y + i);
            if(Puzzle.manhattanDistance(xi, destination) <= minDist)
                potentialPoint.add(xi);
            if(Puzzle.manhattanDistance(yi, destination) <= minDist)
                potentialPoint.add(yi);
        }

        for(int i = 0; i < potentialPoint.size(); i++){
            if(world.getAgentAtPosition(potentialPoint.get(i)) != null){
                potentialPoint.remove(i);
            }
        }
        //TODO: WARNING - Hypothesis: Manhattan distance never give an out of bound position. This is false
        return potentialPoint;
    }

    private void messageHandler(){
        for(int i = 0; i < messages.size(); i++){
            while (messagesAreModified) {}
            messagesAreModified = true;
            Message message = messages.remove(i);
            messagesAreModified = false;
            if(message == null || message.getReceiver() != this || message.getEmitter() == this){
                continue;
            }
            switch (message.getPerform()){
                case REQUEST: onRequest(message); break;
                case RESPONSE: onResponse(message); break;
            }
        }
    }

    private void onRequest(Message message){
        switch (message.getAction()){
            case FREE: onFree(message);
        }
    }

    private void onResponse(Message message){
        switch (message.getAction()){
            case OK: onOK(message);
            case KO: onKO(message);
        }
    }

    private void onFree(Message message){
        complains.add(message);
        System.out.println("Free received !");
    }

    private void onOK(Message message){
        if(message.getEmitter() != waitedAgent)
            return;
        waitingPosition = requestedPosition;
        requestedPosition = null;
    }

    private void onKO(Message message){
        if(message.getEmitter() != waitedAgent)
            return;
        requestedPosition = null;
    }

    public void interruption() {
        token = false;
        running = false;
    }
}
