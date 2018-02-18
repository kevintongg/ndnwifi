package no.bouvet.p2pcommunication.adapter;

import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import no.bouvet.p2pcommunication.R;

public class DiscoveryListAdapterViewHolder {

  @BindView(R.id.discovered_device_name_text_view)
  TextView deviceNameTextView;
  @BindView(R.id.discovered_device_status_text_view)
  TextView deviceStatusTextView;

  public DiscoveryListAdapterViewHolder(View view) {
    ButterKnife.bind(this, view);
  }

}
