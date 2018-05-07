package kwondeveloper.com.kwonplayer.Fragments.SubFragments;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
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

import kwondeveloper.com.kwonplayer.MediaObjects.ArtistFile;
import kwondeveloper.com.kwonplayer.MediaObjects.GenreFile;
import kwondeveloper.com.kwonplayer.MediaObjects.KwonMediaObject;
import kwondeveloper.com.kwonplayer.Playlists.AllSongsOnDevice;
import kwondeveloper.com.kwonplayer.Playlists.CurrentPlaylist;
import kwondeveloper.com.kwonplayer.R;

public class GenreFragmentRecyclerAdapter extends RecyclerView.Adapter<GenreFragmentRecyclerAdapter.ViewHolder> {
    public static ArrayList<GenreFile> mList;
    private Context mContext;
    private static ArrayList<KwonMediaObject> mSelectedGenreArrayList;
    private Uri ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart");

    public GenreFragmentRecyclerAdapter(ArrayList<GenreFile> array) {
        mList = array;
        mSelectedGenreArrayList = new ArrayList<>();
        Log.d("***GenreAdapterCreate", "parameter.size() = " + array.size());
        Log.d("***GenreAdapterCreate", "mList.size() = " + mList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView genre;

        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            genre = (TextView)itemView.findViewById(R.id.GenreRecyclerCardText);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //launch album dialog
            mSelectedGenreArrayList.clear();
            Log.d("***GenreAdapterClick", "song collection created for = " + mList.get(getAdapterPosition()).getGenreName());
            Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", mList.get(getAdapterPosition()).getGenreID());
            String[] projection = new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ARTIST_ID,MediaStore.Audio.Media.ALBUM_ID};
            Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);

            if (cursor == null || !cursor.moveToFirst()) {
                // query failed OR no media on the device
            } else {
                while (cursor.moveToNext()) {
                    KwonMediaObject songData = new KwonMediaObject();
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    long artistId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                    long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                    Uri albumArtUri = ContentUris.withAppendedId(ALBUM_ART_URI, albumId);
                    songData.setmID(id);
                    songData.setmTitle(title);
                    songData.setmAlbum(album);
                    songData.setmArtist(artist);
                    songData.setmDuration(duration);
                    songData.setmArtistID(artistId);
                    songData.setmAlbumID(albumId);
                    songData.setmAlbumArt(albumArtUri);
                    mSelectedGenreArrayList.add(songData);
                }
                cursor.close();
            }
            Log.d("***GenreAdapterClick", "collection song size = " + mSelectedGenreArrayList.size());
            String[] array = new String[mSelectedGenreArrayList.size()];
            for (int i = 0; i < mSelectedGenreArrayList.size(); i++) {
                array[i]=mSelectedGenreArrayList.get(i).getmTitle();
            }
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            final String dialogPlaylistTitle;
            if(mList.get(0).getGenreName()!=null){
                dialogPlaylistTitle = mList.get(getAdapterPosition()).getGenreName();
            } else {
                dialogPlaylistTitle = mContext.getString(R.string.GenreFragment_Dialog_UnknownGenre);
            }
            Log.d("***GenreAdapterClick", "album title set to = " + dialogPlaylistTitle);
            dialog.setTitle(dialogPlaylistTitle);
            dialog.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //add song to CurrentPlaylist
                    CurrentPlaylist.addMediaObject(mSelectedGenreArrayList.get(which));
                    Toast.makeText(mContext, mSelectedGenreArrayList.get(which).getmTitle() + " " + mContext.getString(R.string.GenreFragment_Dialog_SongAdded), Toast.LENGTH_SHORT).show();
                    Log.d("***GenreAdapterClick", mSelectedGenreArrayList.get(which).getmTitle() + " added to current playlist");
                }
            });
            dialog.setPositiveButton(mContext.getString(R.string.GenreFragment_Dialog_AddAll), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //add all songs to currentPlaylist
                    CurrentPlaylist.getPlaylist().addPlaylistToPlaylist(mSelectedGenreArrayList);
                    Toast.makeText(mContext, dialogPlaylistTitle + " " + mContext.getString(R.string.GenreFragment_Dialog_SongAdded), Toast.LENGTH_SHORT).show();
                    Log.d("***GenreAdapterClick", "All songs added to current playlist");
                }
            });
            dialog.setNegativeButton(mContext.getString(R.string.GenreFragment_Dialog_Replace), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //replace all songs in currentPlaylist
                    CurrentPlaylist.replacePlaylist(mSelectedGenreArrayList,dialogPlaylistTitle);
                    Toast.makeText(mContext, mContext.getString(R.string.GenreFragment_Dialog_ReplacePlaylistUpdate), Toast.LENGTH_SHORT).show();
                    Log.d("***GenreAdapterClick", "Current playlist replaced");
                }
            });
            dialog.create();
            dialog.show();
        }
    }

    @Override
    public GenreFragmentRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.recycler_item_genrefrag, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GenreFragmentRecyclerAdapter.ViewHolder holder, int position) {
        holder.genre.setText(mList.get(position).getGenreName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}