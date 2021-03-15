package nz.kabanov.learning.concurrency.idioms.escape;

import javax.swing.*;

public class ImplicitEscape {

    public ImplicitEscape(JButton button) {
        // implicit 'this' escape
        button.addActionListener(e -> doStuff());
    }

    private void doStuff() {
    }
}
