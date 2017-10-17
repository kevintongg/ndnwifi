package no.bouvet.p2pcommunication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.adapter.ChatListAdapter;
import no.bouvet.p2pcommunication.adapter.P2pCommunicationFragmentPagerAdapter;
import no.bouvet.p2pcommunication.listener.multicast.MulticastMessageReceivedListener;
import no.bouvet.p2pcommunication.listener.multicast.MulticastMessageSentListener;
import no.bouvet.p2pcommunication.multicast.MulticastMessage;
import no.bouvet.p2pcommunication.multicast.MulticastMessageReceivedHandler;
import no.bouvet.p2pcommunication.multicast.MulticastMessageReceiverService;
import no.bouvet.p2pcommunication.multicast.SendMulticastMessageAsyncTask;
import no.bouvet.p2pcommunication.util.UserInputHandler;

public class CommunicationFragment extends ListFragment implements MulticastMessageReceivedListener, MulticastMessageSentListener, UserInputHandler {

    public static final String TAG = CommunicationFragment.class.getSimpleName();
    private boolean viewsInjected;
    private Intent multicastReceiverServiceIntent;
    private ChatListAdapter chatListAdapter;

    @InjectView(R.id.user_input_edit_text) EditText userInputEditText;

    public static Fragment newInstance() {
        CommunicationFragment communicationFragment = new CommunicationFragment();
        communicationFragment.setArguments(getFragmentArguments());
        return communicationFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View communicationFragmentView = inflater.inflate(R.layout.communication_fragment, null);
        ButterKnife.inject(this, communicationFragmentView);
        viewsInjected = true;
        return communicationFragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chatListAdapter = new ChatListAdapter(getActivity(), R.layout.communication_fragment_list_row);
        setListAdapter(chatListAdapter);
    }

    @OnClick(R.id.send_button)
    public void sendMulticastMessage() {
        new SendMulticastMessageAsyncTask(this, this).execute();
    }

    public void startReceivingMulticastMessages() {
        if (!MulticastMessageReceiverService.isRunning) {
            multicastReceiverServiceIntent = createMulticastReceiverServiceIntent();
            getActivity().startService(multicastReceiverServiceIntent);
            Log.i(TAG, getString(R.string.multicast_receiver_service_started));
        }
    }

    public void stopReceivingMulticastMessages() {
        if (MulticastMessageReceiverService.isRunning) {
            getActivity().stopService(multicastReceiverServiceIntent);
            Log.i(TAG, getString(R.string.multicast_receiver_service_stopped));
        }
    }

    @Override
    public void onMulticastMessageReceived(MulticastMessage multicastMessage) {
        addMulticastMessageToChatList(multicastMessage);
    }

    @Override
    public void onCouldNotSendMessage() {
        MulticastMessage multicastMessage = new MulticastMessage(getString(R.string.message_not_multicasted), "", true);
        addMulticastMessageToChatList(multicastMessage);
    }

    @Override
    public String getMessageToBeSentFromUserInput() {
        return userInputEditText.getText().toString();
    }

    @Override
    public void clearUserInput() {
        userInputEditText.setText("");
    }

    public void reset() {
        if(viewsInjected) {
            chatListAdapter.clear();
            chatListAdapter.notifyDataSetChanged();
            stopReceivingMulticastMessages();
        }
    }

    private static Bundle getFragmentArguments() {
        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putString(P2pCommunicationFragmentPagerAdapter.FRAGMENT_TITLE, "MULTICAST CHAT");
        return fragmentArguments;
    }

    private Intent createMulticastReceiverServiceIntent() {
        Intent multicastReceiverServiceIntent = new Intent(getActivity(), MulticastMessageReceiverService.class);
        multicastReceiverServiceIntent.setAction(MulticastMessageReceiverService.ACTION_LISTEN_FOR_MULTICAST);
        MulticastMessageReceivedHandler multicastMessageReceivedHandler = new MulticastMessageReceivedHandler(this);
        multicastReceiverServiceIntent.putExtra(MulticastMessageReceiverService.EXTRA_HANDLER_MESSENGER, new Messenger(multicastMessageReceivedHandler));
        return multicastReceiverServiceIntent;
    }

    private void addMulticastMessageToChatList(MulticastMessage multicastMessage) {
        chatListAdapter.add(multicastMessage);
        chatListAdapter.notifyDataSetChanged();
        getListView().setSelection(chatListAdapter.getCount() - 1);
    }
}
