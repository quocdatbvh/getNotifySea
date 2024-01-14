package com.notifySeabank;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private Activity activity;
    private List<NotificationInfo> listNotification;
    private NotificationClickListener listener;

    public NotificationAdapter(Context context,
                               Activity activity,
                               List<NotificationInfo> listNotification,
                               NotificationClickListener listener) {
        this.context = context;
        this.activity = activity;
        this.listNotification = new ArrayList<>();
        this.listNotification.addAll(listNotification);
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NotificationInfo notificationInfo = listNotification.get(position);
        NotificationViewHolder viewHolder = (NotificationViewHolder) holder;

        viewHolder.ivAppIcon.setImageDrawable(Utils.getAppIconFromPackageName(context, notificationInfo.getPackageName()));
        viewHolder.tvAppName.setText(Utils.getAppNameFromPackageName(context, notificationInfo.getPackageName()));
        viewHolder.tvTitle.setText(notificationInfo.getTitle());
        String text = notificationInfo.getText();
        // Thông báo mail có 2 dòng tiêu đề và nội dung nên phải xử lý xóa dấu xuống dòng
        if (notificationInfo.getPackageName().equals(Constants.GMAIL_PACKAGE_NAME)) {
            text = text.replace("\n", " ");
        }
        viewHolder.tvText.setText(text);

        viewHolder.tvPostTime.setText(Utils.getFormattedPostTime(context, notificationInfo.getPostTime()));
        viewHolder.clNotification.setOnClickListener(v -> listener.onClickNotification(notificationInfo, position));
    }

    @Override
    public int getItemCount() {
        return listNotification.size();
    }

    public void addItem(NotificationInfo newNotification) {
        listNotification.add(0, newNotification);
        notifyItemInserted(0);
        listener.onNewNotificationAdded();
    }

    public void removeItem(int selectedItemPosition) {
        listNotification.remove(selectedItemPosition);
        notifyItemRemoved(selectedItemPosition);
    }

    public void removeAllItem() {
        listNotification.clear();
        notifyDataSetChanged();
    }

    public void setItems(List<NotificationInfo> listNotification) {
        // get the current size
        int currentSize = this.listNotification.size();
        // remove the current items
        this.listNotification.clear();
        // add all the new items
        this.listNotification.addAll(listNotification);
        // tell the recyclerview that all the old items are gone
        notifyItemRangeRemoved(0, currentSize);
        // tell the recyclerview how many items we added
        notifyItemRangeInserted(0, listNotification.size());
        notifyDataSetChanged();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_app_icon)
        ImageView ivAppIcon;
        @BindView(R.id.tv_app_name)
        TextView tvAppName;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_text)
        TextView tvText;
        @BindView(R.id.tv_post_time)
        TextView tvPostTime;
        @BindView(R.id.cl_notification)
        ConstraintLayout clNotification;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface NotificationClickListener {
        void onClickNotification(NotificationInfo notificationInfo, int itemPosition);
        void onNewNotificationAdded();
    }
}
