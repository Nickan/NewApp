package com.nickan.newapp;

import com.nickan.newapp.util.IdCreator;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment implements ActionBar.TabListener {
	private static String TAG = "MainFragment";
	
	private FragPagerAdapter fragPagerAdapter;
	
	private View view;
	private ViewPager viewPager;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.main_fragment_layout, root, false);
		
		fragPagerAdapter = new FragPagerAdapter(getActivity().getSupportFragmentManager());
		
		viewPager = (ViewPager) view.findViewById(R.id.pager);
		viewPager.setId(IdCreator.getId());
		viewPager.setAdapter(fragPagerAdapter);
		
		final ActionBar actionBar = getActivity().getActionBar();
		actionBar.show();
		actionBar.removeAllTabs();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
				Log.e(TAG, "onPageSelected() " + position);
			}
		});
		
		for (int index = 0; index < fragPagerAdapter.getCount(); ++index) {
			actionBar.addTab(actionBar.newTab()
					.setText(fragPagerAdapter.getPageTitle(index))
					.setTabListener(this)
					);
		}
		
		return view;
	}
	
	public void onBackPressed() {
	//	ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
	//	viewPager.setEnabled(true);
	//	viewPager.setVisibility(ViewPager.VISIBLE);
		final ActionBar actionBar = getActivity().getActionBar();
		actionBar.show();
	}
	
	public void onPostSelected() {
		final ActionBar actionBar = getActivity().getActionBar();
		actionBar.hide();
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (viewPager != null) {
			viewPager.setCurrentItem(tab.getPosition());
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	
	
}
