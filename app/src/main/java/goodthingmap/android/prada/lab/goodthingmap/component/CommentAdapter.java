package goodthingmap.android.prada.lab.goodthingmap.component;

import android.content.Context;
import android.prada.lab.goodthingmap.model.UserMessage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import goodthingmap.android.prada.lab.goodthingmap.R;

/**
 * Created by prada on 2014/7/5.
 */
public class CommentAdapter extends ArrayAdapter<UserMessage> {

    private final LayoutInflater mInflater;

    public CommentAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.item_comment, null);
        } else {
            view = convertView;
        }
        UserMessage comment = getItem(i);
        ((TextView)view.findViewById(R.id.list_seq_id)).setText("#" + (i + 1));
        ((TextView)view.findViewById(R.id.list_comment)).setText(comment.getMessage());
        ((TextView)view.findViewById(R.id.list_time)).setText(String.valueOf(comment.getTime()));
        return view;
    }

}
