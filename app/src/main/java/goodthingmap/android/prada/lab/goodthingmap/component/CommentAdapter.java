package goodthingmap.android.prada.lab.goodthingmap.component;

import android.content.Context;
import android.prada.lab.goodthingmap.model.VendorComment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import goodthingmap.android.prada.lab.goodthingmap.R;

/**
 * Created by prada on 2014/7/5.
 */
public class CommentAdapter extends ArrayAdapter<VendorComment> {

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
        VendorComment comment = getItem(i);
        ((TextView)ViewHolder.get(view, R.id.item_comment)).setText(comment.getComment());
        ImageView iv = ViewHolder.get(view, R.id.item_comment_icon);

        return view;
    }

}
