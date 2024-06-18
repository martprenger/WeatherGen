package util;

import app.Application;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log
{
	private static final String filename = "logfile.log";
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public static final ILogOutput DEBUG = new ILogOutput()
	{
		public synchronized void println(String msg)
		{
			if (!Application.getInstance().getSettings().loggingEnabled()) {
				return;
			}
			String format = "%s - %s\n";
			try {
				FileWriter writer = new FileWriter(Application.getInstance().getLocalFile("logfile.log"), true);
				writer.write(String.format(format, new Object[] { "Log.access$0()", msg }));
				writer.close();
			} catch (FileNotFoundException fileNotFoundException) {
			} catch (IOException iOException) {}
		}

		public synchronized void printf(String format, Object... args) {
			println(String.format(format, args));
		}
	};

	public static final ILogOutput ERROR = new ILogOutput()
	{
		public synchronized void println(String msg)
		{
			if (!Application.getInstance().getSettings().loggingEnabled()) {
				return;
			}
			String format = "%s - %s\n";
			try {
				FileWriter writer = new FileWriter(Application.getInstance().getLocalFile("logfile.log"), true);
				writer.write(String.format(format, new Object[] { "Log.access$0()", msg }));
				writer.close();
			} catch (FileNotFoundException fileNotFoundException) {
			} catch (IOException iOException) {}
		}

		public synchronized void printf(String format, Object... args) {
			println(String.format(format, args));
		}
	};

	private static String getDateTime() {
		return DATE_FORMATTER.format(new Date());
	}
}
