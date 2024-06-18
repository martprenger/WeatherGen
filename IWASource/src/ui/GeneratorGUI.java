package ui;
import app.Application;
import generator.IGenerator;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import timing.AccurateTimer;
import util.Log;
import util.UIUtil;

public class GeneratorGUI
extends WindowAdapter
implements ActionListener
{
	private static final int WIDTH = 360;
	private static final int HEIGHT = 330;
	private JFrame frame;
	private IGenerator generator;
	private boolean started;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem aboutMenuItem;
	private JMenuItem preferencesMenuItem;
	private JMenuItem exitMenuItem;
	private JLabel sliderLabel;
	private SpeedAdjuster slider;
	private JButton startButton;
	private JButton stopButton;
	private JTextField peakTempField;
	private JTextField missingDataField;
	private JTextField activeClustersField;
	private JTextField disabledClustersField;
	private JTextField errorClustersField;
	private JTextField clusterSendSpeedField;
	private SettingsDialog settingsDialog;
	private AccurateTimer statsTimer;
	private int timerInterval;
	private long lastPeakTempCount;
	private long lastMissingValueCount;
	private long lastWrittenCount;

	public GeneratorGUI(IGenerator generator) {
		this.generator = generator;
		this.started = false;
		setNativeLookAndFeel();
		this.frame = new JFrame("IWA Global Surface Weather Data Generator");
		this.frame.setVisible(false);
		this.settingsDialog = new SettingsDialog(this.frame);
		JPanel content = new JPanel();
		FlowLayout layout = new FlowLayout();
		content.setLayout(new BoxLayout(content, 3));
		layout.setAlignment(0);
		this.frame.setContentPane(content);
		this.menuBar = new JMenuBar();
		this.frame.setJMenuBar(this.menuBar);
		this.fileMenu = new JMenu("File");
		this.fileMenu.setMnemonic(70);
		this.menuBar.add(this.fileMenu);
		this.preferencesMenuItem = new JMenuItem("Preferences...");
		this.fileMenu.add(this.preferencesMenuItem);
		this.preferencesMenuItem.addActionListener(this);
		this.aboutMenuItem = new JMenuItem("About");
		this.aboutMenuItem.setMnemonic(65);
		this.aboutMenuItem.addActionListener(this);
		this.fileMenu.add(this.aboutMenuItem);
		this.exitMenuItem = new JMenuItem("Exit");
		this.exitMenuItem.setMnemonic(88);
		this.fileMenu.add(this.exitMenuItem);
		this.exitMenuItem.addActionListener(this);
		JPanel controlPanel = new JPanel(new FlowLayout());
		controlPanel.setToolTipText("<html>The generator speed indicates the number of clusters that are set to connect to the server.<br>The slider and spinner control the number of clusters trying to connect to the server.<br><i>When the generator is started and this value is altered,<br>the generator will disconnect or try to connect a number of clusters to match this value.");
		controlPanel.setBorder(BorderFactory.createTitledBorder("Generator speed"));
		this.sliderLabel = new JLabel("No. Clusters:");
		controlPanel.add(this.sliderLabel);
		this.slider = new SpeedAdjuster();
		controlPanel.add(this.slider);
		this.slider.addActionListener(this);
		JPanel buttonPanel = new JPanel(new FlowLayout());
		this.startButton = new JButton("Start");
		buttonPanel.add(this.startButton);
		this.startButton.addActionListener(this);
		this.stopButton = new JButton("Stop");
		buttonPanel.add(this.stopButton);
		this.stopButton.addActionListener(this);
		this.stopButton.setEnabled(false);
		content.add(controlPanel);
		content.add(buttonPanel);
		JPanel statusPanel = new JPanel();
		GridLayout statusLayout = new GridLayout(6, 2);
		statusPanel.setLayout(statusLayout);
		statusPanel.setBorder(BorderFactory.createTitledBorder("Generator status"));
		this.peakTempField = new JTextField();
		this.peakTempField.setToolTipText("<html>The number of peak temperatures/second.<br>This value indicates the rate at which the generator is sending peak temperatures.<br><i>A peak temperature is defined as a temperature measurement which<br>is at least 20% higher than the previous temperature measurement.");
		this.peakTempField.setText("0");
		JLabel peakTempLabel = new JLabel("Peak temperatures per sec:");
		peakTempLabel.setToolTipText("<html>The number of peak temperatures/second.<br>This value indicates the rate at which the generator is sending peak temperatures.<br><i>A peak temperature is defined as a temperature measurement which<br>is at least 20% higher than the previous temperature measurement.");
		statusPanel.add(peakTempLabel);
		statusPanel.add(this.peakTempField);
		this.missingDataField = new JTextField();
		this.missingDataField.setToolTipText("<html>The number of missing values/second.<br>This value indicates the rate at which the generator is sending measurements with a missing value.<br><i>A missing value is defined as an empty XML node value.");
		this.missingDataField.setText("0");
		JLabel missingDataLabel = new JLabel("Missing values per sec:");
		missingDataLabel.setToolTipText("<html>The number of missing values/second.<br>This value indicates the rate at which the generator is sending measurements with a missing value.<br><i>A missing value is defined as an empty XML node value.");
		statusPanel.add(missingDataLabel);
		statusPanel.add(this.missingDataField);
		this.clusterSendSpeedField = new JTextField();
		this.clusterSendSpeedField.setToolTipText("<html>The sending speed in clusters/second.<br>This value indicates the rate at which the generator is sending its data.<br><i>This value is increased when a cluster has finished writing, regardless of any error that may have occured.");
		this.clusterSendSpeedField.setText("0");
		JLabel clusterSendSpeedLabel = new JLabel("Clusters per sec:");
		clusterSendSpeedLabel.setToolTipText("<html>The sending speed in clusters/second.<br>This value indicates the rate at which the generator is sending its data.<br><i>This value is increased when a cluster has finished writing, regardless of any error that may have occured.");
		statusPanel.add(clusterSendSpeedLabel);
		statusPanel.add(this.clusterSendSpeedField);
		this.activeClustersField = new JTextField();
		this.activeClustersField.setToolTipText("<html>The number of clusters that are marked as active.<br><i>A cluster is marked active when it is connected to a server.");
		this.activeClustersField.setText("0");
		JLabel activeClustersLabel = new JLabel("Active clusters:");
		activeClustersLabel.setToolTipText("<html>The number of clusters that are marked as active.<br><i>A cluster is marked active when it is connected to a server.");
		statusPanel.add(activeClustersLabel);
		statusPanel.add(this.activeClustersField);
		this.disabledClustersField = new JTextField();
		this.disabledClustersField.setToolTipText("<html>The number of clusters that are marked as disabled.<br><i>A cluster is marked disabled when it is not connected to a server.");
		this.disabledClustersField.setText("800");
		JLabel disabledClustersLabel = new JLabel("Disabled clusters:");
		disabledClustersLabel.setToolTipText("<html>The number of clusters that are marked as disabled.<br><i>A cluster is marked disabled when it is not connected to a server.");
		statusPanel.add(disabledClustersLabel);
		statusPanel.add(this.disabledClustersField);
		this.errorClustersField = new JTextField();
		this.errorClustersField.setToolTipText("<html>The number of clusters that had an error.<br><i>A cluster is marked having an error when an error occures during the time it is connected.<br>The error status will be reset during the next succesful connection attempt.");
		this.errorClustersField.setText("0");
		JLabel errorClustersLabel = new JLabel("Error clusters:");
		errorClustersLabel.setToolTipText("<html>The number of clusters that had an error.<br><i>A cluster is marked having an error when an error occures during the time it is connected.<br>The error status will be reset during the next succesful connection attempt.");
		statusPanel.add(errorClustersLabel);
		statusPanel.add(this.errorClustersField);
		content.add(statusPanel);
		this.frame.setSize(360, 330);
		this.frame.setResizable(true);
		this.frame.setVisible(true);
		this.frame.addWindowListener(this);
		UIUtil.center(this.frame);
		this.lastWrittenCount = 0L;
		this.lastMissingValueCount = 0L;
		this.lastPeakTempCount = 0L;
		this.timerInterval = Application.getInstance().getSettings().getStatsUpdateInterval();
		this.statsTimer = new AccurateTimer(System.currentTimeMillis() + this.timerInterval, this);
	}

	public void windowClosing(WindowEvent e) {
		Application.getInstance().exit();
	}

	public void exit() {
		this.frame.dispose();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.statsTimer) {
			updateStats();
		}
		if (e.getSource() == this.slider && 
				this.started) {
			this.generator.setActiveClusters(this.slider.getValue());
		}
		if (e.getSource() == this.exitMenuItem) {
			exit();
		}
		if (e.getSource() == this.preferencesMenuItem) {
			this.settingsDialog.showDialog();
		}
		if (e.getSource() == this.aboutMenuItem) {
			AboutDialog.showDialog();
		}
		if (e.getSource() == this.startButton) {
			this.generator.setActiveClusters(this.slider.getValue());
			this.generator.start();
			this.started = true;
			this.startButton.setEnabled(false);
			this.stopButton.setEnabled(true);
		} 
		if (e.getSource() == this.stopButton) {
			this.generator.stop();
			this.started = false;
			this.startButton.setEnabled(true);
			this.stopButton.setEnabled(false);
		} 
	}

	public void updateStats() {
		double duration = this.timerInterval / 1000.0D;
		long peakTempCount = this.generator.getPeakTempCount();
		long missingValueCount = this.generator.getMissingValueCount();
		long writtenCount = this.generator.getWrittenClusters();
		double dPeakTemps = (peakTempCount - this.lastPeakTempCount);
		double dMissingValues = (missingValueCount - this.lastMissingValueCount);
		double dWritten = (writtenCount - this.lastWrittenCount);

		this.lastPeakTempCount = peakTempCount;
		this.lastMissingValueCount = missingValueCount;
		this.lastWrittenCount = writtenCount;
		int peakTempSpeed = (int)Math.round(dPeakTemps / duration);
		int missingValueSpeed = (int)Math.round(dMissingValues / duration);
		int writtenSpeed = (int)Math.round(dWritten / duration);
		this.peakTempField.setText(Integer.toString(peakTempSpeed));
		this.missingDataField.setText(Integer.toString(missingValueSpeed));
		this.activeClustersField.setText(Integer.toString(this.generator.getActiveClusterCount()));
		this.disabledClustersField.setText(Integer.toString(this.generator.getDisabledClusterCount()));
		this.errorClustersField.setText(Integer.toString(this.generator.getErrorClusterCount()));
		this.clusterSendSpeedField.setText(Integer.toString(writtenSpeed));
		this.timerInterval = Application.getInstance().getSettings().getStatsUpdateInterval();
		this.statsTimer = new AccurateTimer(this.statsTimer.getTime() + this.timerInterval, this);
	}

	private void setNativeLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			Log.ERROR.println("Error setting native LAF: " + e);
		} 
	}

	public JFrame getFrame() {
		return this.frame;
	}
}