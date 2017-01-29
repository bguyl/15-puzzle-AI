package me.guyl.mas15puzzle.models.agents;

import me.guyl.mas15puzzle.models.Action;
import me.guyl.mas15puzzle.models.Message;
import me.guyl.mas15puzzle.models.Perform;
import me.guyl.mas15puzzle.models.worlds.Puzzle;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class PuzzleAgent extends Agent {

    private boolean running;
    private Puzzle world;
    private List<Message> complains;
    private Point waitingPosition;
    private Point requestedPosition;
    private Agent waitedAgent;
    private static List<Agent> agents;

    public PuzzleAgent(Point position, Point destination, Puzzle world) {
        this.position = position;
        this.destination = destination;
        this.world = world;
        this.running = true;
        this.messages = new ArrayList<Message>();
        this.complains = new ArrayList<Message>();
    }

    @Override
    public void run(){
        while (running){
            messageHandler();

            if(token){
                move(getNextPositions());
                token = false;
            }
        }
    }

    private void move(List<Point> nextPositions){
        if(waitingPosition != null){
            if(world.requestPosition(this, waitingPosition))
                return;
            waitingPosition = null;
        }

        // If we can, we pick a chosen position
        if(!nextPositions.isEmpty()){
            int i = 0;
            while (!world.requestPosition(this, nextPositions.get(i)))
                i++;
            if(!complains.isEmpty()){
                for(Message m : complains){
                    Agent emitter = m.getEmitter();
                    emitter.sendMessage(new Message(this, emitter, Perform.RESPONSE, Action.OK));
                }
            }
            complains = new ArrayList<Message>();
            return;
        }

        // If not, we inform the other that we can't perform their requests
        if (!complains.isEmpty()){
            for(Message m : complains){
                Agent emitter = m.getEmitter();
                emitter.sendMessage(new Message(this, emitter, Perform.RESPONSE, Action.KO));
            }
            complains = new ArrayList<Message>();
        }


        // We try to contact someone else
        int minDist = Puzzle.manhattanDistance(position, destination);
        List<Point> potentialPoint = new ArrayList<Point>();

        for(int i = -1; i <= 1; i += 2){
            Point xi = new Point(position.x + i, position.y), yi = new Point(position.x, position.y + i);
            Agent receiver = null;
            if(Puzzle.manhattanDistance(xi, destination) <= minDist) {
                receiver = world.getAgentAtPosition(xi);
                if(receiver != null) {
                    receiver.sendMessage(new Message(this, receiver, Perform.REQUEST, Action.FREE));
                    requestedPosition = xi;
                    waitedAgent = receiver;
                    break;
                }
            }
            if(Puzzle.manhattanDistance(yi, destination) <= minDist){
                receiver = world.getAgentAtPosition(yi);
                if(receiver != null) {
                    receiver.sendMessage(new Message(this, receiver, Perform.REQUEST, Action.FREE));
                    requestedPosition = yi;
                    waitedAgent = receiver;
                    break;
                }
            }
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
        //WARNING - Hypothesis: Manhattan distance never give and out of bound position. This is false
        return potentialPoint;
    }

    private void messageHandler(){
        for(int i = 0; i < messages.size(); i++){
            Message message = messages.remove(i);
            if(message.getReceiver() != this || message.getEmitter() == this){
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

}
