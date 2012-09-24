package server;

import java.io.*;

import javax.swing.JTextArea;

public class TextAreaOutputStream extends OutputStream {
	private JTextArea tArea;
	
	public TextAreaOutputStream(JTextArea jt)
	{
		tArea = jt;
	}

	@Override
	public void write(int b) throws IOException {
		this.tArea.append((char)b + "");
	}
}
