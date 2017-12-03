import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import com.pengrad.telegrambot.model.Update;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Model implements Subject {

	private OkHttpClient client = new OkHttpClient();
	private VagalumeConnection vagalume = new VagalumeConnection();
	private List<Observer> observers = new LinkedList<Observer>();

	private static Model uniqueInstance;

	private Model() {
	}

	public static Model getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new Model();
		}
		return uniqueInstance;
	}

	@Override
	public void registerObserver(Observer observer) {
		observers.add(observer);
	}

	@Override
	public void notifyObservers(long chatId, String artistsData) {
		for (Observer observer : observers) {
			observer.update(chatId, artistsData);
		}
	}

	public void search(Update update, String searchParam) throws IOException {
		Request vagalumeRequest = this.vagalume.getArtistOrSong(update, searchParam);
		InputStream is = new URL(vagalumeRequest.url().toString()).openStream();
		String vagalumeResponse = formatVagalumeAnswer(is).getJSONObject("response").getJSONArray("docs")
				.getJSONObject(0).get("url").toString();
		if (!vagalumeResponse.isEmpty()) {
			this.notifyObservers(update.message().chat().id(), "http://www.vagalume.com.br" + vagalumeResponse);
		} else {
			this.notifyObservers(update.message().chat().id(), "Artista ou Música não encontrada!");
		}
	}

	public void search(Update update) throws IOException {
		String[] params = update.message().text().split("-");

		Request vagalumeRequest = this.vagalume.getLyrics(update, params[0].trim(), params[1].trim());
		InputStream is = new URL(vagalumeRequest.url().toString()).openStream();
		String vagalumeResponse = formatVagalumeAnswer(is).getJSONArray("mus").getJSONObject(0).get("text").toString();
		if (!vagalumeResponse.isEmpty()) {
			this.notifyObservers(update.message().chat().id(),
					"Letra para " + update.message().text() + ":\n\n" + vagalumeResponse);
		} else {
			this.notifyObservers(update.message().chat().id(), "Letra não encontrada!");
		}
	}

	private JSONObject formatVagalumeAnswer(InputStream is) throws IOException {
		StringBuilder sb;
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			sb = new StringBuilder();
			int cp;
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}
		} finally {
			is.close();
		}
		return new JSONObject(sb.toString());
	}

}
