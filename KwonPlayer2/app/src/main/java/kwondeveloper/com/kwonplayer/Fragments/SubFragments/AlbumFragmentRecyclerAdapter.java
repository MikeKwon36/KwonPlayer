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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kwondeveloper.com.kwonplayer.MediaObjects.KwonMediaObject;
import kwondeveloper.com.kwonplayer.Playlists.AllSongsOnDevice;
import kwondeveloper.com.kwonplayer.Playlists.CurrentPlaylist;
import kwondeveloper.com.kwonplayer.R;

public class AlbumFragmentRecyclerAdapter extends RecyclerView.Adapter<AlbumFragmentRecyclerAdapter.ViewHolder> {
    public static ArrayList<Uri> mList;
    private Context mContext;
    private Uri ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart");
    private static ArrayList<KwonMediaObject> mSelectedAlbumArrayList;

    public AlbumFragmentRecyclerAdapter(ArrayList<Uri> array) {
        mList = array;
        mSelectedAlbumArrayList = new ArrayList<>();
        Log.d("***AlbumAdapterCreate", "parameter.size() = " + array.size());
        Log.d("***AlbumAdapterCreate", "mList.size() = " + mList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView albumArt;
        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            albumArt = (ImageView)itemView.findViewById(R.id.AlbumRecyclerCardArt);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //launch album dialog by pulling the album's id from the URI and filtering all device songs
            //for any song with the same id, to build the list of songs within the dialog
            mSelectedAlbumArrayList.clear();
            long albumID = Long.parseLong(mList.get(getAdapterPosition()).toString().substring(ALBUM_ART_URI.toString().length()+1));
            for (KwonMediaObject song:AllSongsOnDevice.getAllSongs()) {
                if(song.getmAlbumID() == albumID){
                    mSelectedAlbumArrayList.add(song);
                }
            }
            Log.d("***AlbumAdapterClick", "album created with ID = " + albumID);

            String[] array = new String[mSelectedAlbumArrayList.size()];
            for (int i = 0; i < mSelectedAlbumArrayList.size(); i++) {
                array[i]=mSelectedAlbumArrayList.get(i).getmTitle();
            }
            Log.d("***AlbumAdapterClick", "album's song size = " + mSelectedAlbumArrayList.size());

            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);

            //Dialog box's title is set to the first song's album field value
            final String dialogPlaylistTitle;
            if(!mSelectedAlbumArrayList.isEmpty() && mSelectedAlbumArrayList.get(0).getmAlbum() != null){
                dialogPlaylistTitle = mSelectedAlbumArrayList.get(0).getmAlbum();
            } else {
                dialogPlaylistTitle = mContext.getString(R.string.AlbumFragment_Dialog_UnknownAlbum);
            }
            dialog.setTitle(dialogPlaylistTitle);
            Log.d("***AlbumAdapterClick", "album title set to = " + dialogPlaylistTitle);

            dialog.setSingleChoiceItems(array,-1,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //add song to CurrentPlaylist
                    CurrentPlaylist.addMediaObject(mSelectedAlbumArrayList.get(which));
                    Toast.makeText(mContext, mSelectedAlbumArrayList.get(which).getmTitle() + " " + mContext.getString(R.string.AlbumFragment_Dialog_SongAdded), Toast.LENGTH_SHORT).show();
                    Log.d("***AlbumAdapterClick", mSelectedAlbumArrayList.get(which).getmTitle() + " added to current playlist");
                }
            });
            dialog.setPositiveButton(mContext.getString(R.string.AlbumFragment_Dialog_AddAll), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //add all songs to currentPlaylist
                    CurrentPlaylist.getPlaylist().addPlaylistToPlaylist(mSelectedAlbumArrayList);
                    Toast.makeText(mContext, dialogPlaylistTitle + " " + mContext.getString(R.string.AlbumFragment_Dialog_SongAdded), Toast.LENGTH_SHORT).show();
                    Log.d("***AlbumAdapterClick", "Album added to current playlist");
                }
            });
            dialog.setNegativeButton(mContext.getString(R.string.AlbumFragment_Dialog_Replace), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //replace all songs in currentPlaylist
                    CurrentPlaylist.replacePlaylist(mSelectedAlbumArrayList,dialogPlaylistTitle);
                    Toast.makeText(mContext, mContext.getString(R.string.AlbumFragment_Dialog_ReplacePlaylistUpdate), Toast.LENGTH_SHORT).show();
                    Log.d("***AlbumAdapterClick", "Current playlist replaced with album");
                }
            });
            dialog.create().show();
        }
    }

    @Override
    public AlbumFragmentRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.recycler_item_albumfrag, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AlbumFragmentRecyclerAdapter.ViewHolder holder, int position) {
        Picasso.with(mContext).load(mList.get(position)).placeholder(R.drawable.kwondeveloper).fit().centerCrop().into(holder.albumArt);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}