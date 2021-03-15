package nz.kabanov.learning.concurrency.idioms.escape;

import javax.swing.*;

public class PatternAntiEscape {

    private PatternAntiEscape() {
    }

    private void doStuff() {
    }

    public static PatternAntiEscape newActionFor(JButton button) {
        PatternAntiEscape instance = new PatternAntiEscape();
        button.addActionListener(e -> instance.doStuff());
        return instance;
    }
}
