package util;

import java.nio.ByteBuffer;

public class ByteUtil
{
	public static void writeAsString(ByteBuffer buffer, float number, int maxSigFigs, int precision, boolean padding) {
		long num = Math.round(number * Math.pow(10.0D, precision));
		long divider = Math.round(Math.pow(10.0D, (maxSigFigs - 1)));
		int signFactor = (num < 0L) ? -1 : 1;
		num *= signFactor;
		num %= divider * 10L;
		if (signFactor == -1) {
			buffer.put((byte)45);
		}
		boolean writing = false;
		for (int i = 0; i < maxSigFigs; i++) {
			byte digit = (byte)(int)(num / divider);
			if (i == maxSigFigs - precision - 1 || padding) {
				writing = true;
			}
			if (digit != 0 || writing) {
				buffer.put((byte)(digit + 48));
				writing = true;
			} 
			num -= digit * divider;
			divider /= 10L;
			if (i == maxSigFigs - precision - 1 && precision > 0) {
				buffer.put((byte)46);
			}
		} 
	}

	public static float getShortAsFloat(ByteBuffer buffer, int precision) {
		return buffer.getShort() / (float)Math.pow(10.0D, precision);
	}
}

