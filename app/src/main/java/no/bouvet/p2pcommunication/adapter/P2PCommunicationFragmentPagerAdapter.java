package no.bouvet.p2pcommunication.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.List;
import no.bouvet.p2pcommunication.fragment.CommunicationFragment;
import no.bouvet.p2pcommunication.fragment.DiscoveryAndConnectionFragment;

public class P2PCommunicationFragmentPagerAdapter extends FragmentPagerAdapter {

  public static final String FRAGMENT_TITLE = "FRAGMENT_TITLE";
  private final List<Fragment> FRAGMENT_LIST;

  public P2PCommunicationFragmentPagerAdapter(FragmentManager fragmentManager,
      List<Fragment> FRAGMENT_LIST) {
    super(fragmentManager);
    this.FRAGMENT_LIST = FRAGMENT_LIST;
  }

  @Override
  public Fragment getItem(int i) {
    return FRAGMENT_LIST.get(i);
  }

  @Override
  public int getCount() {
    return FRAGMENT_LIST.size();
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return FRAGMENT_LIST.get(position).getArguments().getString(FRAGMENT_TITLE);
  }

  public DiscoveryAndConnectionFragment getDiscoveryAndConnectionFragment() {
    return (DiscoveryAndConnectionFragment) getItem(0);
  }

  public CommunicationFragment getCommunicationFragment() {
    return (CommunicationFragment) getItem(1);
  }
}