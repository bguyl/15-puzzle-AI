package me.guyl.mas15puzzle.models.agents;

import me.guyl.mas15puzzle.models.Message;

import java.awt.Point;
import java.util.List;
import java.util.Observable;

public abstract class Agent extends Thread {

    protected Point position, destination;
    protected boolean token = false;
    protected List<Message> messages;
    protected boolean messagesAreModified = false;

    public Point getPosition() {
        return position;
    }
    public void setPosition(Point position) { this.position = position; }

    public boolean isToken() {
        return token;
    }

    public void sendMessage(Message message){
        while (messagesAreModified) {}
        messagesAreModified = true;
        this.messages.add(message);
        messagesAreModified = false;
    }

    public abstract void interruption();
}
