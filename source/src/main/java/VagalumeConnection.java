import java.io.IOException;

import com.pengrad.telegrambot.model.Update;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class VagalumeConnection {

	public Request getArtistOrSong(Update update, String artistOrSongParam) throws IOException {
		HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.vagalume.com.br/search." + artistOrSongParam)
				.newBuilder();
		urlBuilder.addQueryParameter("q", update.message().text());
		urlBuilder.addQueryParameter("limit", "5");
		String url = urlBuilder.build().toString();
		return new Request.Builder().header("Authorization", "07d979c11da145225737d7773528cbb8").url(url).build();
	}

	public Request getLyrics(Update update, String artist, String song) throws IOException {
		HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.vagalume.com.br/search.php").newBuilder();
		urlBuilder.addQueryParameter("apikey", "07d979c11da145225737d7773528cbb8");
		urlBuilder.addQueryParameter("art", artist);
		urlBuilder.addQueryParameter("mus", song);
		String url = urlBuilder.build().toString();
		return new Request.Builder().header("Authorization", "07d979c11da145225737d7773528cbb8").url(url).build();
	}
}
