package ui;

import app.Application;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import util.UIUtil;

class AboutDialog
extends JDialog
{
	private static final long serialVersionUID = 1L;
	private static AboutDialog instance;
	private static AboutDialog getInstance() {
		if (instance == null) {
			instance = new AboutDialog(Application.getInstance().getGeneratorGui().getFrame());
		}
		return instance;
	}

	private AboutDialog(Frame owner) {
		super(owner, "About UNWDMI Global Surface Weather Data Generator", true);
		setVisible(false);
		JLabel lbl = new JLabel(new ImageIcon(Application.getInstance().getLocalFile("logo.png").getAbsolutePath()));
		JPanel p = new JPanel();
		Border b1 = new BevelBorder(0);
		Border b2 = new EmptyBorder(5, 5, 5, 5);
		lbl.setBorder(new CompoundBorder(b1, b2));
		p.add(lbl);
		getContentPane().add(p, "West");
		String message = 
				"IWA Global Surface Weather Data Generator\nVersie: 2.1\n\nCopyright © 2009 Hanzehogeschool Groningen";
		JTextArea txt = new JTextArea(message);
		txt.setBorder(new EmptyBorder(5, 10, 5, 10));
		txt.setFont(new Font("Helvetica", 1, 12));
		txt.setEditable(false);
		txt.setBackground(getBackground());
		p = new JPanel();
		p.setLayout(new BoxLayout(p, 1));
		p.add(txt);
		message = "Gemaakt door: Sjors van Oers";
		txt = new JTextArea(message);
		txt.setBorder(new EmptyBorder(5, 10, 5, 10));
		txt.setFont(new Font("Arial", 0, 12));
		txt.setEditable(false);
		txt.setBackground(getBackground());
		p.add(txt);
		getContentPane().add(p, "Center");
		JButton btOK = new JButton("OK");
		ActionListener lst = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				AboutDialog.this.setVisible(false);
			}
		};
		btOK.addActionListener(lst);
		p = new JPanel();
		p.add(btOK);
		getContentPane().add(p, "South");
		pack();
		UIUtil.center(this);
		setResizable(true);
		setVisible(false);     
		setDefaultCloseOperation(0);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				AboutDialog.this.setVisible(false);
			}
		});
	}

	public static void showDialog() {
		getInstance().setVisible(true);
	}
}
