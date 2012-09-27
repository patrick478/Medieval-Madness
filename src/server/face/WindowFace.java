package server.face;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import javax.swing.*;

import server.Server;

public class WindowFace extends ServerFace implements Runnable, WindowListener {
	Server parentServer;
	
	JFrame frame;
	JPanel data;
	JTextArea console;
	PrintStream os;
	JTextField input;
	JButton submit = new JButton();
	
	JLabel atpsLabel = new JLabel("..loading..");
	
	@Override
	public void setup(Server server) {
		this.parentServer = server;
		frame = new JFrame("Medieval Madness Server");
		frame.addWindowListener(this);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setResizable(false);
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(815, 447));
		
		data = new JPanel();
		data.add(atpsLabel);
		data.setPreferredSize(new Dimension(200, 400));
		panel.add(data);
		
	
		console = new JTextArea();
		console.setEditable(false);
		console.setPreferredSize(new Dimension(600, 400));
		panel.add(console);
		
		input = new JTextField();
		input.setPreferredSize(new Dimension(700, 32));
		panel.add(input);
		
		submit = new JButton("Execute");
		submit.setPreferredSize(new Dimension(100, 32));
		panel.add(submit);
		
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
		
		os = new PrintStream(new TextAreaOutputStream(console));
	}

	@Override
	public void close() {
		frame.setVisible(false);
		frame.dispose();
	}

	@Override
	public PrintStream getOut() {
		return os;
	}

	@Override
	public InputStream getIn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run() {
		while(true)
		{
			String output = "<html><b>Server Variables</b><br />";
			for(String str : this.parentServer.serverData.keySet())
			{
				output += String.format("<br /><i>%s</i>=%s", str, this.parentServer.serverData.get(str));
			}
			output += "</html>";
			this.atpsLabel.setText(output);
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		this.parentServer.HandleCommandLine("quit");
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
