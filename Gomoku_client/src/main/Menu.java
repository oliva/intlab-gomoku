package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
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
	JLabel credits;
	GridBagConstraints c;
	AudioFormat audioFormat;
	AudioInputStream audioInputStream;
	SourceDataLine sourceDataLine;
	boolean stopPlayback = false;
	
	
    public Menu() 
    {
        
            jbtStart = new JButton("Start Game");
            jbtCredits = new JButton("Credits");
            jbtMute = new JButton("Mute");
            jbtQuit = new JButton("Quit");
            jbtBack = new JButton("Back");
            
           
            
            panel = new JPanel(new GridBagLayout());
            c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            credits = new JLabel("Gomoku");
            Font font=new Font("ariel",1,20);
            playAudio("music/main.wav");
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
            c.gridy = 3;
            panel.add(jbtMute,c);
            
            c.ipady = 50;      
            c.weightx = 0.0;
            c.gridwidth = 1;
            c.gridx = 1;
            c.gridy = 4;
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
            MuteListenerClass listener5 = new MuteListenerClass(this);
            jbtStart.addActionListener(listener1);
            jbtCredits.addActionListener(listener2);
            jbtQuit.addActionListener(listener3);
            jbtBack.addActionListener(listener4);
            jbtMute.addActionListener(listener5);
                        
            
            
       

    }
    
   
    
    public void playAudio(String song) {
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
	
    
    class PlayThread extends Thread //Inner class for music
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
    	      if (stopPlayback ==false) playAudio(song); //replay if the song is ended
    	     
    	      
    	    }catch (Exception e) {
    	      e.printStackTrace();
    	      System.exit(0);
    	    }
    	  }
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
        menu.credits = new JLabel("Belkacemi Nordin, Temlin Oliver, Kohlmann Daniel");
        Font font=new Font("ariel",1,20);
        
        menu.credits.setFont(font);
        menu.credits.setForeground(Color.white);
        menu.c.fill = GridBagConstraints.HORIZONTAL;
        
        menu.c.ipady = 20;      
        menu.c.weightx = 0.5;
        menu.c.gridwidth = 1;
        menu.c.gridx = 1;
        menu.c.gridy = 1;
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

class MuteListenerClass implements ActionListener {
	Menu menu;
	MuteListenerClass(Menu menu){this.menu=menu;}
	
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Mute button clicked");
        if(menu.stopPlayback==false) menu.stopPlayback=true;
        else {
        	 menu.stopPlayback=false;
        	menu.playAudio("music/main.wav");
       
        }
        
        
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
        menu.c.gridy = 3;
        menu.panel.add(menu.jbtMute,menu.c);
        
        menu. c.ipady = 50;      
        menu. c.weightx = 0.0;
        menu. c.gridwidth = 1;
        menu.  c.gridx = 1;
        menu.  c.gridy = 4;
        menu.panel.add(menu.jbtQuit,menu.c);

        menu.setSize(400,360);
        
         
        
        
        
    }
}