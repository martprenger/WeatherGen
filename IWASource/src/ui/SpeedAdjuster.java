package ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SpeedAdjuster
extends JPanel
implements ChangeListener
{
	private static final long serialVersionUID = 1L;
	private JSlider slider;
	private JSpinner spinner;
	private ArrayList<ActionListener> listenerList = new ArrayList<ActionListener>();
	private boolean notifyEnabled = true;
	private int value = 800;
	public SpeedAdjuster() {
		FlowLayout layout = new FlowLayout();
		setLayout(layout);
		this.slider = new JSlider(0, 800, this.value);
		this.slider.setMajorTickSpacing(100);
		this.slider.setPaintTicks(true);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(1), new JLabel("1"));
		labelTable.put(new Integer(200), new JLabel("200"));
		labelTable.put(new Integer(400), new JLabel("400"));
		labelTable.put(new Integer(600), new JLabel("600"));
		labelTable.put(new Integer(800), new JLabel("800"));
		this.slider.setLabelTable(labelTable);
		this.slider.setPaintLabels(true);
		add(this.slider);
		this.slider.addChangeListener(this);
		SpinnerNumberModel spinnerModel = new SpinnerNumberModel(this.value, 1, 800, 1);
		this.spinner = new JSpinner(spinnerModel);
		this.spinner.setPreferredSize(new Dimension((int)this.spinner.getPreferredSize().getWidth() + 1, (int)this.spinner.getPreferredSize().getHeight() + 3));
		add(this.spinner);
		this.spinner.addChangeListener(this);
	}

	public JSlider getSlider() {
		return this.slider;
	}

	public int getValue() {
		return this.slider.getValue();
	}

	private void setValue(int value) {
		boolean notify = this.notifyEnabled;
		this.notifyEnabled = false;
		if (value == 0) {
			value = 1;
		}
		this.value = value;
		update();
		this.notifyEnabled = notify;
	}

	private void update() {
		this.slider.setValue(this.value);
		this.spinner.setValue(Integer.valueOf(this.value));
		repaint();
	}

	public void addActionListener(ActionListener l) {
		this.listenerList.add(l);
	}

	private void notifyListeners() {
		if (this.notifyEnabled) {
			for (ActionListener l : this.listenerList) {
				l.actionPerformed(new ActionEvent(this, 0, null, 0));
			}
		}
	}


	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == this.slider) {
			if (!this.slider.getValueIsAdjusting()) {
				setValue(this.slider.getValue());
				notifyListeners();
			} 
			setValue(this.slider.getValue());
		} 
		if (e.getSource() == this.spinner) {
			setValue(((Integer)this.spinner.getValue()).intValue());
			notifyListeners();
		} 
	}
}
