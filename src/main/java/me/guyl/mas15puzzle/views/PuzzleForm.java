package me.guyl.mas15puzzle.views;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Guyl Bastien on 18/01/2017.
 */
public class PuzzleForm {
    private JPanel mainPanel;
    private JPanel puzzlePanel;
    private JPanel toolsPanel;
    private JTextField numberOfCasesInput;
    private JButton applyButton;
    private JButton resetButton;
    private JButton startButton;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Puzzle 15");
        PuzzleForm form = new PuzzleForm();

        ArrayList<Button> buttons = new ArrayList<Button>();

        //Making the puzzle
        form.getPuzzlePanel().setLayout(new GridLayout(4,4));
        for(Integer i = 0; i < 15; i++)
            buttons.add(new Button(i.toString()));
        buttons.add(new Button(" "));

        Collections.shuffle(buttons);

        for(Button b : buttons){
            form.getPuzzlePanel().add(b);
        }

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
}