package com.zuojie.soundrecorder.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.zuojie.soundrecorder.R;
import com.zuojie.soundrecorder.bean.Audio;
import com.zuojie.soundrecorder.ui.DataView;
import com.zuojie.soundrecorder.ui.dialog.DeleteDialog;
import com.zuojie.soundrecorder.ui.dialog.PlayDialog;
import com.zuojie.soundrecorder.ui.dialog.RenameDialog;
import com.zuojie.soundrecorder.widget.CircleImageButton;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private ArrayList<Audio> data = new ArrayList<>();
    private int mBackground;
    private LayoutInflater inflater;
    private Context context;
    private DataView dataView;

    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private int animDuration = 150;

    public ListAdapter(Context context, ArrayList<Audio> data) {
        this.context = context;
        this.dataView = (DataView) context;
        if (data != null && data.size() > 0) {
            this.data.addAll(data);
        }
        this.inflater = LayoutInflater.from(context);
        TypedValue mTypedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
    }

    public void setData(final ArrayList<Audio> data) {
        this.data.clear();
        if (data != null && data.size() > 0) {
            this.data.addAll(data);
        }
        notifyDataSetChanged();
    }

    public ArrayList<Audio> getData() {
        return data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Audio audio = data.get(holder.getLayoutPosition());
        holder.item.setBackgroundResource(mBackground);
        holder.name.setText(audio.name);
        holder.time.setText(audio.date);
        holder.item.setOnClickListener(v -> doPlay(audio));
        holder.more.setOnClickListener(v -> showMoreMenu(v, audio));
    }

    private void showMoreMenu(View v, Audio audio) {
        v.setEnabled(false);
        v.animate().rotation(90).setInterpolator(interpolator).setDuration(animDuration).start();
        PopupMenu moreMenu = new PopupMenu(context, v);
        moreMenu.inflate(R.menu.menu_item_audio);
        moreMenu.setOnDismissListener(menu -> {
            v.setEnabled(true);
            v.animate().rotation(0).setInterpolator(interpolator).setDuration(animDuration).start();
        });
        moreMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_play:
                    doPlay(audio);
                    break;
                case R.id.action_delete:
                    doDelete(audio);
                    break;
                case R.id.action_rename:
                    doRename(audio);
                    break;
            }
            return true;
        });
        moreMenu.show();
    }

    private void doPlay(Audio audio) {
        PlayDialog dialog = new PlayDialog(context, audio);
        dialog.show();
    }

    private void doDelete(Audio audio) {
        DeleteDialog dialog = new DeleteDialog(context, audio, success -> {
            if (success) {
                int index = data.indexOf(audio);
                data.remove(audio);
                notifyItemRemoved(index);
            }
        });
        dialog.show();
    }

    private void doRename(Audio audio) {
        RenameDialog dialog = new RenameDialog(context, audio, success -> {
            if (success) {
                dataView.refreshData();
            }
        }, null, false);
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        CircleImageButton more;
        View item;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            time = view.findViewById(R.id.time);
            more = view.findViewById(R.id.more);
            item = view.findViewById(R.id.item);
        }
    }
}