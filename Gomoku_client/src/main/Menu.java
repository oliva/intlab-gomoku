package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Menu extends JFrame 
{
	JPanel panel;
	JButton jbtStart;
	JButton jbtCredits;
	JButton jbtQuit;
	JButton jbtBack;
	JLabel credits;
	GridBagConstraints c;
	
    public Menu(int index) 
    {
        
            jbtStart = new JButton("Start Game");
            jbtCredits = new JButton("Credits");
            jbtQuit = new JButton("Quit");
            jbtBack = new JButton("Back");
            
           
            
            panel = new JPanel(new GridBagLayout());
            c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            credits = new JLabel("Gomoku");
            Font font=new Font("ariel",1,20);
            
            credits.setFont(font);
            credits.setForeground(Color.white);
            
            c.ipady = 100;      
            c.weightx = 0.5;
            c.gridwidth = 1;
            c.gridx = 1;
            c.gridy = 0;
            panel.add(credits,c);
            c.ipady = 50;      
            c.weightx = 0.0;
            c.gridwidth = 1;
            c.gridx = 1;
            c.gridy = 1;
            panel.add(jbtStart,c);
            c.ipady = 20;      
            c.weightx = 0.5;
            c.gridwidth = 1;
            c.gridx = 1;
            c.gridy = 2;
            panel.add(jbtCredits,c);
            
            c.ipady = 50;      
            c.weightx = 0.0;
            c.gridwidth = 1;
            c.gridx = 1;
            c.gridy = 3;
            panel.add(jbtQuit,c);
            
           
            
            
            add(panel);
            setTitle("Main menu");
            setSize(400, 360);
            setLocation(500, 500);
            Color color = new Color(64,64,64);
            panel.setBackground(color);
            setDefaultCloseOperation(Menu.EXIT_ON_CLOSE);
            setVisible(true);
            // Register listeners
            StartListenerClass listener1 = new StartListenerClass(this);
            CreditsListenerClass listener2 = new CreditsListenerClass(this);
            QuitListenerClass listener3 = new QuitListenerClass(this);
            BackListenerClass listener4 = new BackListenerClass(this);
            jbtStart.addActionListener(listener1);
            jbtCredits.addActionListener(listener2);
            jbtQuit.addActionListener(listener3);
            jbtBack.addActionListener(listener4);
                        
            
            
       

    }

}

class StartListenerClass implements ActionListener {
	Menu menu;
	StartListenerClass(Menu menu){this.menu=menu;}
	@Override
    
    public void actionPerformed(ActionEvent e) {
		menu.panel.removeAll();
		menu.panel.repaint();
        System.out.println("Start button clicked");
        menu.setVisible(false);

        new Thread(new Game()).start();
        
    }
}

class CreditsListenerClass implements ActionListener {
	Menu menu;
	CreditsListenerClass(Menu menu){this.menu=menu;}
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Credits button clicked");

       
        menu.panel.removeAll();
        menu.credits = new JLabel("Belkacemi Nordin, Temlin Olivér, Kohlmann Dániel");
        Font font=new Font("ariel",1,20);
        
        menu.credits.setFont(font);
        menu.credits.setForeground(Color.white);
        menu.c.fill = GridBagConstraints.HORIZONTAL;
        
        menu.c.ipady = 20;      
        menu.c.weightx = 0.5;
        menu.c.gridwidth = 1;
        menu.c.gridx = 1;
        menu.c.gridy = 1;
        //menu.jbtQuit.add(menu.credits,menu.c);
        menu.panel.add(menu.credits,menu.c);
        menu.c.gridy = 2;
        menu.panel.add(menu.jbtBack,menu.c);
        
        
        
        menu.setTitle("Credits");
       
        menu.setSize(500,400);
        
        
    }
}
class QuitListenerClass implements ActionListener {
	Menu menu;
	QuitListenerClass(Menu menu){this.menu=menu;}
	
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Quit button clicked");
        menu.dispatchEvent(new WindowEvent(menu, WindowEvent.WINDOW_CLOSING));
        
        
    }
}
class BackListenerClass implements ActionListener {
	Menu menu;
	BackListenerClass(Menu menu){this.menu=menu;}
	
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Back button clicked");
        menu.panel.removeAll();
        
        
        menu.c.fill = GridBagConstraints.HORIZONTAL;
        
        menu.credits = new JLabel("Gomoku");
        Font font=new Font("ariel",1,20);
        
        menu.credits.setFont(font);
        menu.credits.setForeground(Color.white);
        
        menu.c.ipady = 100;      
        menu.c.weightx = 0.5;
        menu.c.gridwidth = 1;
        menu.c.gridx = 1;
        menu.c.gridy = 0;
        menu.panel.add(menu.credits,menu.c);
        menu.c.ipady = 50;      
        menu.c.weightx = 0.0;
        menu. c.gridwidth = 1;
        menu. c.gridx = 1;
        menu. c.gridy = 1;
        menu. panel.add(menu.jbtStart,menu.c);
        menu.c.ipady = 20;      
        menu.c.weightx = 0.5;
        menu.c.gridwidth = 1;
        menu.c.gridx = 1;
        menu.c.gridy = 2;
        menu. panel.add(menu.jbtCredits,menu.c);
        
        menu. c.ipady = 50;      
        menu. c.weightx = 0.0;
        menu. c.gridwidth = 1;
        menu.  c.gridx = 1;
        menu.  c.gridy = 3;
        menu.panel.add(menu.jbtQuit,menu.c);

        menu.setSize(400,360);
        
         
        
        
        
    }
}