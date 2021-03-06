import java.io.IOException;

import com.pengrad.telegrambot.model.Update;

public class ControllerSearchArtist implements ControllerSearch {

	private Model model;
	private View view;

	public ControllerSearchArtist(Model model, View view) {
		this.model = model; // connection Controller -> Model
		this.view = view; // connection Controller -> View
	}

	@Override
	public void search(Update update) throws IOException {
		view.sendTypingMessage(update);
		model.search(update, "art");
	}

}
