package soundengine.copy;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;


/**	<titleabbrev>SimpleAudioPlayer</titleabbrev>
	<title>Playing an audio file (easy)</title>

	<formalpara><title>Purpose</title>
	<para>Plays a single audio file.</para></formalpara>

	<formalpara><title>Usage</title>
	<cmdsynopsis>
	<command>java SimpleAudioPlayer</command>
	<replaceable class="parameter">audiofile</replaceable>
	</cmdsynopsis>
	</formalpara>

	<formalpara><title>Parameters</title>
	<variablelist>
	<varlistentry>
	<term><option><replaceable class="parameter">audiofile</replaceable></option></term>
	<listitem><para>the name of the
	audio file that should be played</para></listitem>
	</varlistentry>
	</variablelist>
	</formalpara>


 */
public class SimpleAudioPlayer extends Thread
{
	private static final int	EXTERNAL_BUFFER_SIZE = 128000;



	public static void play(final String filename){
		new Thread(new Runnable(){
			@Override
			public void run(){
		//set filename to File
		File soundFile = new File(filename);
		while(true){
		/*
		  We have to read in the sound file.
		 */
		AudioInputStream	audioInputStream = null;
		try
		{
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		//Get audio format from file
		AudioFormat	audioFormat = audioInputStream.getFormat();

		//Perform magic to make music happen
		SourceDataLine	line = null;
		DataLine.Info	info = new DataLine.Info(SourceDataLine.class,
				audioFormat);
		try
		{
			line = (SourceDataLine) AudioSystem.getLine(info);

			/*
			  The line is there, but it is not yet ready to
			  receive audio data. We have to open the line.
			 */
			line.open(audioFormat);
		}
		catch (LineUnavailableException e1)
		{
			e1.printStackTrace();
			System.exit(1);
		}
		catch (Exception e2)
		{
			e2.printStackTrace();
			System.exit(1);
		}
	
			//start the line playing
			line.start();

			/*
		  Ok, finally the line is prepared. Now comes the real
		  job: we have to write data to the line. We do this
		  in a loop. First, we read data from the
		  AudioInputStream to a buffer. Then, we write from
		  this buffer to the Line. This is done until the end
		  of the file is reached, which is detected by a
		  return value of -1 from the read method of the
		  AudioInputStream.
			 */
			int	nBytesRead = 0;
			byte[]	abData = new byte[EXTERNAL_BUFFER_SIZE];
			while (nBytesRead != -1)
			{
				try
				{
					nBytesRead = audioInputStream.read(abData, 0, abData.length);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				if (nBytesRead >= 0)
				{
					int	nBytesWritten = line.write(abData, 0, nBytesRead);
				}
			}
		


			// Wait until all data are played.

			line.drain();
			line.close();
		

		//System.exit(0);
		}
			}
		}).start();
	}


	private static void printUsageAndExit()
	{
		out("SimpleAudioPlayer: usage:");
		out("\tjava SimpleAudioPlayer <soundfile>");
		System.exit(1);
	}


	private static void out(String strMessage)
	{
		System.out.println(strMessage);
	}
}
