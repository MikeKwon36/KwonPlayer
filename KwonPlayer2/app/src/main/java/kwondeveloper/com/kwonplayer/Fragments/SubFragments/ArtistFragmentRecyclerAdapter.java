package kwondeveloper.com.kwonplayer.Fragments.SubFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import kwondeveloper.com.kwonplayer.MediaObjects.ArtistFile;
import kwondeveloper.com.kwonplayer.MediaObjects.KwonMediaObject;
import kwondeveloper.com.kwonplayer.Playlists.AllSongsOnDevice;
import kwondeveloper.com.kwonplayer.Playlists.CurrentPlaylist;
import kwondeveloper.com.kwonplayer.R;

public class ArtistFragmentRecyclerAdapter extends RecyclerView.Adapter<ArtistFragmentRecyclerAdapter.ViewHolder> {
    public static ArrayList<ArtistFile> mList;
    private Context mContext;
    private static ArrayList<KwonMediaObject> mSelectedArtistArrayList;

    public ArtistFragmentRecyclerAdapter(ArrayList<ArtistFile> array) {
        mList = array;
        mSelectedArtistArrayList = new ArrayList<>();
        Log.d("***ArtistAdapterCreate", "parameter.size() = " + array.size());
        Log.d("***ArtistAdapterCreate", "mList.size() = " + mList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView artist;
        private ImageView albumArt;

        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            albumArt = (ImageView)itemView.findViewById(R.id.ArtistRecyclerCardArt);
            artist = (TextView)itemView.findViewById(R.id.ArtistRecyclerName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //launch album dialog
            mSelectedArtistArrayList.clear();
            Log.d("***ArtistAdapterClick", "artist collection created for = " + mList.get(getAdapterPosition()).getmName());
            for (KwonMediaObject song: AllSongsOnDevice.getAllSongs()) {
                if(song.getmArtistID() == mList.get(getAdapterPosition()).getmID()){
                    mSelectedArtistArrayList.add(song);
                }
            }
            Log.d("***ArtistAdapterClick", "collection song size = " + mSelectedArtistArrayList.size());
            String[] array = new String[mSelectedArtistArrayList.size()];
            for (int i = 0; i < mSelectedArtistArrayList.size(); i++) {
                array[i]=mSelectedArtistArrayList.get(i).getmTitle();
            }
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            final String dialogPlaylistTitle;
            if(!mSelectedArtistArrayList.isEmpty() && mSelectedArtistArrayList.get(0).getmArtist()!=null){
                dialogPlaylistTitle = mSelectedArtistArrayList.get(0).getmArtist();
            } else {
                dialogPlaylistTitle = mContext.getString(R.string.ArtistFragment_Dialog_UnknownArtist);
            }
            Log.d("***ArtistAdapterClick", "album title set to = " + dialogPlaylistTitle);
            dialog.setTitle(dialogPlaylistTitle);
            dialog.setSingleChoiceItems(array,-1,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //add song to CurrentPlaylist
                    CurrentPlaylist.addMediaObject(mSelectedArtistArrayList.get(which));
                    Toast.makeText(mContext, mSelectedArtistArrayList.get(which).getmTitle() + " " + mContext.getString(R.string.ArtistFragment_Dialog_SongAdded), Toast.LENGTH_SHORT).show();
                    Log.d("***ArtistAdapterClick", mSelectedArtistArrayList.get(which).getmTitle() + " added to current playlist");
                }
            });
            dialog.setPositiveButton(mContext.getString(R.string.ArtistFragment_Dialog_AddAll), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //add all songs to currentPlaylist
                    CurrentPlaylist.getPlaylist().addPlaylistToPlaylist(mSelectedArtistArrayList);
                    Toast.makeText(mContext, dialogPlaylistTitle + " " + mContext.getString(R.string.ArtistFragment_Dialog_SongAdded), Toast.LENGTH_SHORT).show();
                    Log.d("***ArtistAdapterClick", "All songs added to current playlist");
                }
            });
            dialog.setNegativeButton(mContext.getString(R.string.ArtistFragment_Dialog_Replace), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //replace all songs in currentPlaylist
                    CurrentPlaylist.replacePlaylist(mSelectedArtistArrayList,dialogPlaylistTitle);
                    Toast.makeText(mContext, mContext.getString(R.string.ArtistFragment_Dialog_ReplacePlaylistUpdate), Toast.LENGTH_SHORT).show();
                    Log.d("***ArtistAdapterClick", "Current playlist replaced");
                }
            });
            dialog.create();
            dialog.show();
        }
    }

    @Override
    public ArtistFragmentRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.recycler_item_artistfrag, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ArtistFragmentRecyclerAdapter.ViewHolder holder, int position) {
        holder.artist.setText(mList.get(position).getmName());
        Uri albumArtPath = Uri.parse("");
        for (KwonMediaObject song:AllSongsOnDevice.getAllSongs()) {
            if(song.getmArtistID()==mList.get(position).getmID()){
                albumArtPath = song.getmAlbumArt();
                break;
            }
        }
        Picasso.with(mContext).load(albumArtPath).placeholder(R.drawable.kwondeveloper).fit().into(holder.albumArt);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}