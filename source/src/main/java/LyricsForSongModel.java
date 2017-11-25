import java.util.LinkedList;
import java.util.List;

import com.pengrad.telegrambot.model.Update;

public class LyricsForSongModel implements Subject{
	
	private List<Observer> observers = new LinkedList<Observer>();
	
	private List<Song> song = new LinkedList<Song>();
	private List<Artist> artist = new LinkedList<Artist>();
	
	private static LyricsForSongModel uniqueInstance;
	
	private LyricsForSongModel(){}
	
	public static LyricsForSongModel getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new LyricsForSongModel();
		}
		return uniqueInstance;
	}
	
	public void registerObserver(Observer observer){
		observers.add(observer);
	}
	
	public void notifyObservers(long chatId, String songsData){
		for(Observer observer:observers){
			observer.update(chatId, songsData);
		}
	}

	public List<Artist> getArtist() {
		return artist;
	}

	public void setArtist(List<Artist> artist) {
		this.artist = artist;
	}
	
	public List<Song> getSong() {
		return song;
	}
	
	public void setSong(List<Song> song) {
		this.song = song;
	}
}