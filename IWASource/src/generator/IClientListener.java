package generator;

public interface IClientListener {
	void onConnected(IClient paramIClient);

	void onWriteComplete(IClient paramIClient);

	void onError(IClient paramIClient);

	void onDisconnected(IClient paramIClient);
}