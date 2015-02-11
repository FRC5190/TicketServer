package net.viperfish.ticketClient;

import java.util.List;

public interface Window {
	void show();

	void hide();

	void updateDisplay(List<Display> displayUpdates);
}
