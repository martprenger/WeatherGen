package timing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AccurateTimer
implements Runnable
{
	private Thread thread;
	private long futureTime;
	private ActionListener listener;

	public AccurateTimer(long futureTime, ActionListener listener) {
		this.listener = listener;
		this.futureTime = futureTime;
		this.thread = new Thread(this);
		this.thread.setDaemon(true);
		this.thread.start();
	}

	public long getTime() {
		return this.futureTime;
	}

	public void stop() {
		this.thread.interrupt();
	}

	public void run() {
		while (System.currentTimeMillis() < this.futureTime) {
			try {
				Thread.sleep(this.futureTime - System.currentTimeMillis());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} 
		} 

		if (!Thread.currentThread().isInterrupted()) {
			notifyListener();
		}
	}

	private void notifyListener() {
		this.listener.actionPerformed(new ActionEvent(this, 0, null));
	}
}
