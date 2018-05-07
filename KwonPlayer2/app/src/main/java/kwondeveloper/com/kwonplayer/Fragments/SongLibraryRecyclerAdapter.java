package kwondeveloper.com.kwonplayer.Fragments;

import android.content.Context;
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
import kwondeveloper.com.kwonplayer.Playlists.CurrentPlaylist;
import kwondeveloper.com.kwonplayer.R;

public class SongLibraryRecyclerAdapter extends RecyclerView.Adapter<SongLibraryRecyclerAdapter.ViewHolder> {
    private ArrayList<KwonMediaObject> mList;
    private Context mContext;

    public SongLibraryRecyclerAdapter(ArrayList<KwonMediaObject> array) {
        mList = array;
        Log.d("***LibraryAdapterCreate", "parameter.size() = " + array.size());
        Log.d("***LibraryAdapterCreate", "mList.size() = " + mList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView Artist,Title;
        private ImageView albumArt;

        public ViewHolder(View itemView) {
            super(itemView);
            Artist = (TextView)itemView.findViewById(R.id.SongLibraryRecyclerCardArtist);
            Title = (TextView)itemView.findViewById(R.id.SongLibraryRecyclerCardTitle);
            albumArt = (ImageView)itemView.findViewById(R.id.SongLibraryRecyclerCardArt);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Add song to current playlist
            Log.d("***LibraryAdapterClick", "old playlist size = " + CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size());
            Log.d("***LibraryAdapterClick", "mList.size() = " + mList.size());
            Log.d("***LibraryAdapterClick", "mList.get(" + getLayoutPosition() + ").getmID(" + mList.get(getAdapterPosition()).getmID() + ")");
            CurrentPlaylist.addMediaObject(mList.get(getAdapterPosition()));
            PlaylistFragment.mPlaylistAdapter.notifyDataSetChanged();
            Toast.makeText(mContext, mList.get(getAdapterPosition()).getmTitle() + " " + v.getContext().getString(R.string.Recycler_Song_added_to_current_playlist) + " " + CurrentPlaylist.getPlaylistTitle(), Toast.LENGTH_SHORT).show();
            Log.d("***LibraryAdapterClick", "new playlist size = " + CurrentPlaylist.getPlaylist().getAllSongsInPlaylist().size());
        }
    }

    @Override
    public SongLibraryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.recycler_item_song_library, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SongLibraryRecyclerAdapter.ViewHolder holder, int position) {
        holder.Artist.setText(mList.get(position).getmArtist());
        holder.Title.setText(mList.get(position).getmTitle());
        Picasso.with(mContext).load(mList.get(position).getmAlbumArt()).placeholder(R.drawable.kwondeveloper).fit().centerCrop().into(holder.albumArt);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
