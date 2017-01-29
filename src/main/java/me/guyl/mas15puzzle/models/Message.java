package me.guyl.mas15puzzle.models;

import me.guyl.mas15puzzle.models.agents.Agent;

public class Message {
    private Agent emitter, receiver;
    private Perform perform;
    private Action action;

    public Message(Agent emitter, Agent receiver, Perform perform, Action action) {
        this.emitter = emitter;
        this.receiver = receiver;
        this.perform = perform;
        this.action = action;
    }

    public Agent getEmitter() {
        return emitter;
    }

    public Agent getReceiver() {
        return receiver;
    }

    public Perform getPerform() {
        return perform;
    }

    public Action getAction() {
        return action;
    }
}

