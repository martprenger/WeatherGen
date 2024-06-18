package net.sourceforge.zmanim;

public class ZmanimException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public ZmanimException() {
		super("An unknow Zmanim problem occurred");
	}

	public ZmanimException(String message) {
		super(message);
	}
}
