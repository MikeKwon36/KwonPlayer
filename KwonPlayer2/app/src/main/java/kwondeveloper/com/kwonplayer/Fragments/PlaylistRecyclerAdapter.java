package kwondeveloper.com.kwonplayer.Fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kwondeveloper.com.kwonplayer.MediaObjects.KwonMediaObject;
import kwondeveloper.com.kwonplayer.MediaPlaybackService;
import kwondeveloper.com.kwonplayer.Playlists.CurrentPlaylist;
import kwondeveloper.com.kwonplayer.R;
import kwondeveloper.com.kwonplayer.SupportClasses.ItemTouchHelperAdapter;
import kwondeveloper.com.kwonplayer.SupportClasses.ItemTouchHelperViewHolder;

public class PlaylistRecyclerAdapter extends RecyclerView.Adapter<PlaylistRecyclerAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private ArrayList<KwonMediaObject> mList;
    private Context mContext;

    public PlaylistRecyclerAdapter (ArrayList<KwonMediaObject> array) {
        mList = array;
        Log.d("***PlaylistAdaptrCreate", "incoming parameter array size = " + array.size());
        Log.d("***PlaylistAdaptrCreate", "adapter's arraylist data source size set to = " + mList.size());
    }

    @Override
    public PlaylistRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.recycler_item_playlist, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PlaylistRecyclerAdapter.ViewHolder holder, int position) {
        holder.Artist.setText(mList.get(position).getmArtist());
        holder.Title.setText(mList.get(position).getmTitle());
        Picasso.with(mContext).load(mList.get(position).getmAlbumArt()).placeholder(R.drawable.kwondeveloper).fit().centerCrop().into(holder.albumArt);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onItemDismiss(int position) {
        //Remove swiped item from list and notify the RecyclerViewAdapter
        Log.d("***PlaylistAdapterSwipe", "old playlist size() = " + CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size());
        Log.d("***PlaylistAdapterSwipe", "item removed from index " + position + " with ID " + CurrentPlaylist.getMediaObject(position).getmID() + ")");
        Toast.makeText(mContext, CurrentPlaylist.getMediaObject(position).getmTitle() + " " + mContext.getString(R.string.PlaylistFragment_swipe_delete), Toast.LENGTH_SHORT).show();
        CurrentPlaylist.removeMediaObject(position);
        notifyItemRemoved(position);
        Log.d("***PlaylistAdapterSwipe", "new playlist size() = " + CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size());
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Log.d("***PlaylistAdapterDrag", "initial position = " + fromPosition);
        Log.d("***PlaylistAdapterDrag", "target position = " + toPosition);
        KwonMediaObject prev = CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().remove(fromPosition);
        CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().add(toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
        Log.d("***PlaylistAdapterDrag", "final position = " + CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().indexOf(prev));
    }

    //Custom ViewHolder inner class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ItemTouchHelperViewHolder {

        private TextView Artist,Title;
        private ImageView albumArt;

        public ViewHolder(View itemView) {
            super(itemView);
            Artist = (TextView)itemView.findViewById(R.id.PlaylistRecyclerCardArtist);
            Title = (TextView)itemView.findViewById(R.id.PlaylistRecyclerCardTitle);
            albumArt = (ImageView)itemView.findViewById(R.id.PlaylistRecyclerCardArt);
            itemView.setOnClickListener(this);
            mContext = itemView.getContext();
        }

        @Override
        public void onClick(View v) {
            //clicking on a song will play it and launch playerFragment
            Log.d("***PlaylistAdapterClick", "mList.size() = " + mList.size());
            Log.d("***PlaylistAdapterClick", "item selected at index " + getAdapterPosition() + " with ID " + mList.get(getAdapterPosition()).getmID() + ")");
            Intent intent = new Intent(v.getContext(),MediaPlaybackService.class);
            intent.putExtra(MediaPlaybackService.SELECTED_SONG_ID, mList.get(getAdapterPosition()).getmID());
            intent.setAction(MediaPlaybackService.PLAY);
            Log.d("***PlaylistAdapterClick", "intent sent with song ID = " + intent.getLongExtra(MediaPlaybackService.SELECTED_SONG_ID, -1));
            mContext.startService(intent);
            PlaylistFragment.onSongSelectedInCurrentPlaylist();
        }

        @Override
        public void onItemSelected() {
            itemView.setScaleX(1.05f);
            itemView.setScaleY(1.05f);
        }

        @Override
        public void onItemClear() {
            itemView.setScaleX(1f);
            itemView.setScaleY(1f);
        }
    }
}
