package me.guyl.mas15puzzle.models.agents;

import me.guyl.mas15puzzle.models.Message;

import java.awt.Point;
import java.util.List;

public abstract class Agent extends Thread {

    protected Point position, destination;
    protected boolean token = false;
    protected List<Message> messages;

    public Point getPosition() {
        return position;
    }

    public boolean isToken() {
        return token;
    }

    public void sendMessage(Message message){
        this.messages.add(message);
    }
}
