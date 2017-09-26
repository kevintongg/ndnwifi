package no.bouvet.p2pcommunication.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import no.bouvet.p2pcommunication.fragment.CommunicationFragment;
import no.bouvet.p2pcommunication.fragment.DiscoveryAndConnectionFragment;

public class P2pCommunicationFragmentPagerAdapter extends FragmentPagerAdapter {

    public static final String FRAGMENT_TITLE = "FRAGMENT_TITLE";
    private List<Fragment> fragmentList;

    public P2pCommunicationFragmentPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragmentList) {
        super(fragmentManager);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentList.get(position).getArguments().getString(FRAGMENT_TITLE);
    }

    public DiscoveryAndConnectionFragment getDiscoveryAndConnectionFragment() {
        return (DiscoveryAndConnectionFragment) getItem(0);
    }

    public CommunicationFragment getCommunicationFragment() {
        return (CommunicationFragment) getItem(1);
    }
}