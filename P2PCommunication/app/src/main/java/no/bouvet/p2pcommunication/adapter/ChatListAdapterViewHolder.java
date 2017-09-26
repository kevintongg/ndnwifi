package no.bouvet.p2pcommunication.adapter;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import no.bouvet.p2pcommunication.R;

public class ChatListAdapterViewHolder {

    @InjectView(R.id.message_received_layout) RelativeLayout messageReceivedLayout;
    @InjectView(R.id.message_received_text_view) TextView messageReceivedTextView;
    @InjectView(R.id.message_sent_layout) RelativeLayout messageSentLayout;
    @InjectView(R.id.message_sent_text_view) TextView messageSentTextView;

    public ChatListAdapterViewHolder(View view) {
        ButterKnife.inject(this, view);
    }
}
