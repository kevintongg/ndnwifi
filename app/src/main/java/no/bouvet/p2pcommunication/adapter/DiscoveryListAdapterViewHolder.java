package no.bouvet.p2pcommunication.adapter;

import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import no.bouvet.p2pcommunication.R;

public class DiscoveryListAdapterViewHolder {

    @InjectView(R.id.discovered_device_name_text_view) TextView deviceNameTextView;
    @InjectView(R.id.discovered_device_status_text_view) TextView deviceStatusTextView;

    public DiscoveryListAdapterViewHolder(View view) {
        ButterKnife.inject(this, view);
    }

}
