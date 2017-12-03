import java.io.IOException;
import java.text.Normalizer;
import java.util.List;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;

public class View implements Observer {

	TelegramBot bot = TelegramBotAdapter.build("407027788:AAGPBT8zc1yxTURQScb1xtE1LQ-MWo36L10");// inserir token do bot

	// Object that receives messages
	GetUpdatesResponse updatesResponse;
	// Object that send responses
	SendResponse sendResponse;
	// Object that manage chat actions like "typing action"
	BaseResponse baseResponse;

	int queuesIndex = 0;

	// Strategy Pattern -- connection View -> Controller
	ControllerSearch controllerSearch;

	boolean searchBehaviour = false;

	private Model model;

	public View(Model model) {
		this.model = model;
	}

	public void setControllerSearch(ControllerSearch controllerSearch) { // Strategy Pattern
		this.controllerSearch = controllerSearch;
	}

	public void receiveUsersMessages() {

		// infinity loop
		while (true) {

			// taking the Queue of Messages
			updatesResponse = bot.execute(new GetUpdates().limit(100).offset(queuesIndex));

			// Queue of messages
			List<Update> updates = updatesResponse.updates();

			// taking each message in the Queue
			for (Update update : updates) {

				// updating queue's index
				queuesIndex = update.updateId() + 1;

				if (this.searchBehaviour == true) {
					try {
						this.callController(update);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					selectSearchOption(update);
				}
			}
		}
	}

	public void callController(Update update) throws IOException {
		this.controllerSearch.search(update);
	}

	@Override
	public void update(long chatId, String userQuery) {
		sendResponse = bot.execute(new SendMessage(chatId, userQuery));
		this.searchBehaviour = false;
	}

	public void sendTypingMessage(Update update) {
		baseResponse = bot.execute(new SendChatAction(update.message().chat().id(), ChatAction.typing.name()));
	}

	private void selectSearchOption(Update update) {
		String searchOption;

		String userMsg = Normalizer.normalize(update.message().text(), Normalizer.Form.NFD);
		userMsg = userMsg.replaceAll("[^\\p{ASCII}]", "");

		if (userMsg.equalsIgnoreCase("artista")) {
			setControllerSearch(new ControllerSearchArtist(model, this));
			searchOption = "Qual o nome do artista?";
			this.searchBehaviour = true;
		} else if (userMsg.equalsIgnoreCase("musica")) {
			setControllerSearch(new ControllerSearchSong(model, this));
			searchOption = "Qual o nome da música?";
			this.searchBehaviour = true;
		} else if (userMsg.equalsIgnoreCase("letra")) {
			setControllerSearch(new ControllerSearchLyrics(model, this));
			searchOption = "Escreva 'nome do artista - nome da música'.";
			this.searchBehaviour = true;
		} else {
			searchOption = "Quer saber sobre o 'artista', a 'musica' ou a 'letra'?";
		}
		sendResponse = bot.execute(new SendMessage(update.message().chat().id(), searchOption));
	}

}