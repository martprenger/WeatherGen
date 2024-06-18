package generator;

import java.nio.ByteBuffer;

public interface IClient {
	int getId();

	ByteBuffer getWriteBuffer();
}