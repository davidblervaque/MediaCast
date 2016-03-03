package mediacast.com.mediacast.Models;

/**
 * Created by David on 29/02/2016.
 */
public class Music {

    private long id;
    private String title;
    private String artist;

    public Music(long songId, String title, String artist) {
        id = songId;
        this.title = title;
        this.artist = artist;
    }

    public long getId() { return this.id; }
    public String getTitle() {return this.title;}
    public String getArtist() {return this.artist;}

}
