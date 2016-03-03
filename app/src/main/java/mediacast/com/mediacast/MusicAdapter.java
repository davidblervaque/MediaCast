package mediacast.com.mediacast;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mediacast.com.mediacast.Models.Music;

/**
 * Created by David on 01/03/2016.
 */
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private ArrayList<Music> mMusics;
    private LayoutInflater mMusicInfo;
    private int mItemLayout;

    public MusicAdapter(Context context, ArrayList<Music> musics) {
        mMusics = musics;
        mMusicInfo = LayoutInflater.from(context);
    }

    @Override
    public MusicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MusicAdapter.ViewHolder holder, int position) {
        Music music = mMusics.get(position);
        holder.title.setText(music.getTitle());
        holder.artist.setText(music.getArtist());
        holder.itemView.setTag(music);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mMusics.size();
    }

    public void add(Music item)
    {
        mMusics.add(item);
        notifyItemInserted(mMusics.indexOf(item));
    }

    public void remove(Music item) {
        int position = mMusics.indexOf(item);
        mMusics.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView artist;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.music_title);
            artist = (TextView)itemView.findViewById(R.id.music_artist);
        }
    }
}
