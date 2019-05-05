package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import main.Menu.PlayThread;

import java.io.*;
import javax.sound.sampled.*;

public class Menu extends JFrame
{
	JPanel panel;
	JButton jbtStart;
	JButton jbtCredits;
	JButton jbtQuit;
	JButton jbtBack;
	JButton jbtMute;
	JLabel label;
	GridBagConstraints c;
	AudioFormat audioFormat;
	AudioInputStream audioInputStream;
	SourceDataLine sourceDataLine;
	boolean stopPlayback;
	boolean gameover;

    public Menu() //constructor
    {

            jbtStart = new JButton("Start Game");
            jbtCredits = new JButton("Credits");
            jbtMute = new JButton("Mute");
            jbtQuit = new JButton("Quit");
            jbtBack = new JButton("Back");

            //initialization
            gameover=false;
            panel = new JPanel(new GridBagLayout());
            c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            label = new JLabel("Gomoku");
            Font font=new Font("ariel",1,20);
            label.setFont(font);
            label.setForeground(Color.white);
            stopPlayback = false;
            playAudio("../music/main.wav");
            //adding the elements
            c.ipady = 100;
            c.weightx = 0.5;
            c.gridwidth = 1;
            c.gridx = 1;
            c.gridy = 0;
            panel.add(label,c);
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
            c.gridy = 3;
            panel.add(jbtMute,c);

            c.ipady = 50;
            c.weightx = 0.0;
            c.gridwidth = 1;
            c.gridx = 1;
            c.gridy = 4;
            panel.add(jbtQuit,c);



            //final details
            add(panel);
            setTitle("Main menu");
            setSize(400, 410);
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
            MuteListenerClass listener5 = new MuteListenerClass(this);
            jbtStart.addActionListener(listener1);
            jbtCredits.addActionListener(listener2);
            jbtQuit.addActionListener(listener3);
            jbtBack.addActionListener(listener4);
            jbtMute.addActionListener(listener5);





    }


    public void win() //winner players method
    {
    	gameover=true;
    	//stop the current music, and start the winners music
        stopPlayback = true;
        try {
			Thread.sleep(700); //its a must to play the proper music
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        stopPlayback = false;
    	playAudio("../music/win.wav");
    	
        //Appearance of the window
    	setVisible(true);
    	panel.removeAll();
    	repaint();

    	label=new JLabel();
    	label.setIcon(new ImageIcon("images/win.gif"));
    	c.ipady = 20;
        c.weightx = 0.5;
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 1;
    	panel.add(jbtBack,c);
    	c.ipady = 40;
        c.weightx = 0.5;
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 0;
    	panel.add(label,c);
    	setSize(400,420);





    }

    public void lose() //loser players method
    {
        gameover=true;
        //stop the current music, and start the losers music
        stopPlayback = true;
        try {
			Thread.sleep(700); //its a must to play the proper music
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        stopPlayback = false;
    	playAudio("../music/lost.wav");
    	
        
    	setVisible(true);
    	panel.removeAll();
    	repaint();

    	//Appearance of the window
    	label=new JLabel("Game over");
    	Font font=new Font("ariel",1,40);
    	label.setFont(font);
    	label.setForeground(Color.white);
    	label.setHorizontalAlignment(JLabel.CENTER);

    	c.ipady = 20;
        c.weightx = 0.5;
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 1;
    	panel.add(jbtBack,c);
    	c.ipady = 40;
        c.weightx = 0.5;
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 0;
    	panel.add(label,c);
    	setSize(400,420);




    }


    public void playAudio(String song) //audio player
    {

	    try
	    {
	      File soundFile =new File(song);
	      audioInputStream = AudioSystem.getAudioInputStream(soundFile);
	      audioFormat = audioInputStream.getFormat();


	      DataLine.Info dataLineInfo =new DataLine.Info( SourceDataLine.class, audioFormat);

	      sourceDataLine =(SourceDataLine)AudioSystem.getLine(dataLineInfo);


	      new PlayThread(song).start();
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	      System.exit(0);
	    }
	  }


    class PlayThread extends Thread //Inner class for the music
    {
    	  byte tempBuffer[] = new byte[10000];
    	  String song;
    	  PlayThread(String song){this.song=song;}
    	  public void run(){
    	    try{
    	      sourceDataLine.open(audioFormat);
    	      sourceDataLine.start();

    	      int cnt;
    	      //Keep looping until the input read method
    	      // returns -1 for empty stream or the
    	      // user clicks the Stop button causing
    	      // stopPlayback to switch from false to
    	      // true.
    	      while((cnt = audioInputStream.read(tempBuffer,0,tempBuffer.length)) != -1 && stopPlayback == false)
    	      {
    	        if(cnt > 0)
    	        {
    	          //Write data to the internal buffer of
    	          // the data line where it will be
    	          // delivered to the speaker.
    	          sourceDataLine.write(tempBuffer, 0, cnt);
    	        }
    	      }
    	      //Block and wait for internal buffer of the
    	      // data line to empty.
    	      sourceDataLine.drain();
    	      sourceDataLine.close();
    	      if (stopPlayback ==false && gameover==false) playAudio(song); //replay if the song is ended and the game is not over yet


    	    }catch (Exception e)
    	    {
    	      e.printStackTrace();
    	      System.exit(0);
    	    }
    	  }
    	}

}

class StartListenerClass implements ActionListener //Start button listener, starts the game
{
	Menu menu;
	StartListenerClass(Menu menu){this.menu=menu;}
	@Override

    public void actionPerformed(ActionEvent e)
	{
		menu.panel.removeAll();
		menu.panel.repaint();
        System.out.println("Start button clicked");
        menu.setVisible(false);

        new Thread(new Game()).start();

    }
}

class CreditsListenerClass implements ActionListener //Credits button listener, paints the credits page
{
	Menu menu;
	CreditsListenerClass(Menu menu){this.menu=menu;}
    @Override
    public void actionPerformed(ActionEvent e)
    {
        System.out.println("Credits button clicked");

    	//Appearance of the credits
        menu.panel.removeAll();
        menu.label = new JLabel("Belkacemi Nordin, Temlin Oliver, Kohlmann Daniel");
        Font font=new Font("ariel",1,20);

        menu.label.setFont(font);
        menu.label.setForeground(Color.white);
        menu.c.fill = GridBagConstraints.HORIZONTAL;

        menu.c.ipady = 20;
        menu.c.weightx = 0.5;
        menu.c.gridwidth = 1;
        menu.c.gridx = 1;
        menu.c.gridy = 1;
        menu.panel.add(menu.label,menu.c);
        menu.c.gridy = 2;
        menu.panel.add(menu.jbtBack,menu.c);



        menu.setTitle("Credits");

        menu.setSize(500,400);


    }
}
class QuitListenerClass implements ActionListener //Quit button listener, closes the program
{
	Menu menu;
	QuitListenerClass(Menu menu){this.menu=menu;}

    @Override
    public void actionPerformed(ActionEvent e)
    {
        System.out.println("Quit button clicked");
        menu.dispatchEvent(new WindowEvent(menu, WindowEvent.WINDOW_CLOSING));


    }
}

class MuteListenerClass implements ActionListener //Mute button listener, stops the current music/starts the main music
{
	Menu menu;
	MuteListenerClass(Menu menu){this.menu=menu;}

    @Override
    public void actionPerformed(ActionEvent e)
    {
        System.out.println("Mute button clicked");
        if(menu.stopPlayback==false) menu.stopPlayback=true;
        else {
        	menu.stopPlayback=false;
        	menu.playAudio("../music/main.wav");
       
        }


    }
}
class BackListenerClass implements ActionListener //Back Button listener, takes us back to the main menu
{
	Menu menu;
	BackListenerClass(Menu menu){this.menu=menu;}

    @Override
    public void actionPerformed(ActionEvent e)
    {
        System.out.println("Back button clicked");
        //outlook of the main menu
        menu.panel.removeAll();
        menu.c.fill = GridBagConstraints.HORIZONTAL;

        menu.label = new JLabel("Gomoku");
        Font font=new Font("ariel",1,20);
        menu.label.setFont(font);
        menu.label.setForeground(Color.white);

        menu.c.ipady = 100;
        menu.c.weightx = 0.5;
        menu.c.gridwidth = 1;
        menu.c.gridx = 1;
        menu.c.gridy = 0;
        menu.panel.add(menu.label,menu.c);
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
        menu.c.gridy = 3;
        menu.panel.add(menu.jbtMute,menu.c);

        menu. c.ipady = 50;
        menu. c.weightx = 0.0;
        menu. c.gridwidth = 1;
        menu.  c.gridx = 1;
        menu.  c.gridy = 4;
        menu.panel.add(menu.jbtQuit,menu.c);

        menu.setSize(400,410);
        //stops the end music, and starts the main theme
        if(menu.gameover==true)
        	{
        	 menu.stopPlayback = true;
             try {
     			Thread.sleep(700); //its a must to play the proper music
     		} catch (InterruptedException e1) {
     			// TODO Auto-generated catch block
     			e1.printStackTrace();
     		}
        	menu.stopPlayback = false;
        	
        	menu.playAudio("../music/main.wav");
        	menu.gameover=false;
        	}




    }
}
