package five;

import javax.swing.*;
import java.awt.*;

public class GUI {

    JFrame frame;
    GridBagLayout lay;

    JLabel txt;
    JButton but;

    public GUI() {
        frame = new JFrame("Counter Test");
        frame.setLayout(lay = new GridBagLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(Color.WHITE);

        lay.setConstraints(frame.add(txt = new JLabel("0",SwingConstants.CENTER))/* */, new GridBagConstraints(0,0,1,1,1,1,10, 1, new Insets(10,10,/*  */0,10), 100,50));
        lay.setConstraints(frame.add(but = new JButton("Add"))/*                    */, new GridBagConstraints(0,1,1,1,1,1,10, 1, new Insets(10,10,/* */10,10), 100,50));

        txt.setFont(new Font("Exotc350 Bd BT", Font.BOLD, 30));
        but.setFont(txt.getFont());

        frame.pack();
    }

}
