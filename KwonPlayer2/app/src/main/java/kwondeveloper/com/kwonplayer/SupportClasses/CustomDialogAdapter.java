package kwondeveloper.com.kwonplayer.SupportClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import kwondeveloper.com.kwonplayer.R;


public class CustomDialogAdapter extends ArrayAdapter<String>{

    String[] mList;
    Context mContext;

    public CustomDialogAdapter(Context context, String[] array) {
        super(context, -1);
        mList = array;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_theme_list,parent,false);
        TextView text = (TextView) view.findViewById(R.id.dialoglistviewText);
        text.setText(mList[position]);
        return view;
    }

    @Override
    public int getCount() {
        return mList.length;
    }
}
