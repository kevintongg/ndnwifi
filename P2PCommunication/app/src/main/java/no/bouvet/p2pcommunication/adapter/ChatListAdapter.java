package no.bouvet.p2pcommunication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import no.bouvet.p2pcommunication.multicast.MulticastMessage;

public class ChatListAdapter extends ArrayAdapter<MulticastMessage> {

    private Context context;
    private int layoutResourceId;

    public ChatListAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = ensureConvertView(convertView);
        ChatListAdapterViewHolder chatListAdapterViewHolder = ensureChatListAdapterViewHolder(convertView);

        MulticastMessage multicastMessage = getItem(position);
        if (multicastMessage.isSentByMe()) {
            chatListAdapterViewHolder.messageReceivedLayout.setVisibility(View.GONE);
            chatListAdapterViewHolder.messageSentLayout.setVisibility(View.VISIBLE);
            chatListAdapterViewHolder.messageSentTextView.setText(multicastMessage.getText());
        } else {
            chatListAdapterViewHolder.messageReceivedLayout.setVisibility(View.VISIBLE);
            chatListAdapterViewHolder.messageSentLayout.setVisibility(View.GONE);
            chatListAdapterViewHolder.messageReceivedTextView.setText(multicastMessage.getSenderIpAddress() + ":\n" + multicastMessage.getText());
        }

        return convertView;
    }

    private LayoutInflater getLayoutInflaterService() {
        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private View ensureConvertView(View convertView) {
        if (convertView == null) {
            convertView = getLayoutInflaterService().inflate(layoutResourceId, null);
        }
        return convertView;
    }

    private ChatListAdapterViewHolder ensureChatListAdapterViewHolder(View convertView) {
        if (convertView.getTag() == null) {
            convertView.setTag(new ChatListAdapterViewHolder(convertView));
        }
        return (ChatListAdapterViewHolder) convertView.getTag();
    }
}
