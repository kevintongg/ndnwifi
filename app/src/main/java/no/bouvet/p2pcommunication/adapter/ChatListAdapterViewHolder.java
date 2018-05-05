package no.bouvet.p2pcommunication.adapter;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import no.bouvet.p2pcommunication.R;

public class ChatListAdapterViewHolder {

  @BindView(R.id.message_received_layout)
  RelativeLayout messageReceivedLayout;
  @BindView(R.id.message_received_text_view)
  TextView messageReceivedTextView;
  @BindView(R.id.message_sent_layout)
  RelativeLayout messageSentLayout;
  @BindView(R.id.message_sent_text_view)
  TextView messageSentTextView;


  ChatListAdapterViewHolder(View view) {
    ButterKnife.bind(this, view);
  }
}
