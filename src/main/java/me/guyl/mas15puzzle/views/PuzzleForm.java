package me.guyl.mas15puzzle.views;

import me.guyl.mas15puzzle.models.agents.Agent;
import me.guyl.mas15puzzle.models.agents.PuzzleAgent;
import me.guyl.mas15puzzle.models.worlds.Puzzle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;

/**
 * Created by Guyl Bastien on 18/01/2017.
 */
public class PuzzleForm implements ActionListener, Observer{
    private JPanel mainPanel;
    private JPanel puzzlePanel;
    private JPanel toolsPanel;
    private JTextField numberOfCasesInput;
    private JButton startButton;
    private JButton setButton;

    private static JFrame frame;

    public static void main(String[] args) {
        frame = new JFrame("Puzzle 15");
        PuzzleForm form = new PuzzleForm();

        form.startButton.setActionCommand("start");
        form.startButton.addActionListener(form);
        form.setButton.setActionCommand("set");
        form.setButton.addActionListener(form);;

        frame.setContentPane(form.mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JPanel getPuzzlePanel() {
        return puzzlePanel;
    }

    public JPanel getToolsPanel() {
        return toolsPanel;
    }

    public void actionPerformed(ActionEvent e) {
        String actioncmd = e.getActionCommand();
        if(actioncmd.equals("start"))
            onStart();
        if(actioncmd.equals("set"))
            onSet();
    }

    public void onStart(){
        AgentButton.puzzleWorld.start();
    }

    public void onSet(){
        getPuzzlePanel().removeAll();
        if(AgentButton.puzzleWorld != null)
            AgentButton.puzzleWorld.stop();
        AgentButton.puzzleWorld = new Puzzle(4, 4, this);

        ArrayList<JButton> buttons = new ArrayList<JButton>();

        //Making the puzzle

        int size = Integer.parseInt(numberOfCasesInput.getText());
        size = size > 15 ? 15 : size;
        getPuzzlePanel().setLayout(new GridLayout(4,4));

        for(int i = 0; i < size; i++)
            buttons.add(new JButton(" "));
        for(Integer i = size; i < 16; i++)
            buttons.add(new AgentButton(i.toString(), i));

        Collections.shuffle(buttons);

        int i = 0;
        for(JButton b : buttons){
            getPuzzlePanel().add(b);
            if(b.getClass() == AgentButton.class){
                AgentButton ab = (AgentButton)b;
                ab.setPosition(i);
                AgentButton.puzzleWorld.placeAgent(i, ab.getAgent());
            }
            i++;
        }

        frame.pack();
    }

    public void update(Observable o, Object arg) {
        //TODO: Dirty code here !
        Point[] pts = (Point[])arg;
        Point pos = pts[0], dest = pts[1];
        int ipos = 4*pos.x + pos.y, idest = 4*dest.x + dest.y;
        List<Component> compos = Arrays.asList(getPuzzlePanel().getComponents());
        Collections.swap(compos, ipos, idest);

        getPuzzlePanel().removeAll();
        for(Component c : compos){
            getPuzzlePanel().add(c);
        }

        getPuzzlePanel().revalidate();
        getPuzzlePanel().repaint();
    }
}