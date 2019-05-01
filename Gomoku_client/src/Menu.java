import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Menu extends JFrame {
    public Menu(int index) {
        if (index == 1) {
            JButton jbtStart = new JButton("Start");
            JButton jbtCredits = new JButton("Credits");

            JPanel panel = new JPanel();
            panel.add(jbtStart);
            panel.add(jbtCredits);

            // ImageIcon icon = new ImageIcon("gomba.png");
            // JLabel thumb = new JLabel();
            // thumb.setIcon(icon);
            // setContentPane(thumb);
            // panel.add(thumb);

            add(panel);
            setTitle("Main menu");
            setSize(500, 500);
            setLocation(500, 500);
            setDefaultCloseOperation(Menu.EXIT_ON_CLOSE);
            setVisible(true);
            // Register listeners
            StartListenerClass listener1 = new StartListenerClass();
            CreditsListenerClass listener2 = new CreditsListenerClass();
            jbtStart.addActionListener(listener1);
            jbtCredits.addActionListener(listener2);
        } else {
            JLabel credits = new JLabel("mi");
            JPanel panel = new JPanel();
            panel.add(credits);
            add(panel);
            setTitle("Credits");
            setSize(500, 500);
            setLocation(500, 500);
            setDefaultCloseOperation(Menu.EXIT_ON_CLOSE);

            setVisible(true);

        }

    }

}

class StartListenerClass implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Start button clicked");

    }
}

class CreditsListenerClass implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Credits button clicked");

        Menu credits = new Menu(2);
    }
}