package util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

public class UIUtil
{
	public static void center(Window window) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int)(screen.getWidth() / 2.0D - (window.getWidth() / 2));
		int y = (int)(screen.getHeight() / 2.0D - (window.getHeight() / 2));
		window.setLocation(x, y);
	}
}
