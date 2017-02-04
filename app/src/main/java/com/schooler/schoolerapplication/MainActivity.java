package com.schooler.schoolerapplication;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import layout.MainFragment;
import layout.MyInfoFragment;

public class MainActivity extends NfcActivityWarrper {
    private static final String TAB_HOME = "홈";
    private static final String TAB_MYPAGE = "나의명함";
    private static final String TAB_CHATTING = "채팅";
    private RelativeLayout mainContent;

    private MainFragment mainFragment;
    private MyInfoFragment myPageFragment;

    private Fragment prevFragment;

    private View searchBar;
    private DetectScrollingScrollView mainScrollView;
    private FloatingActionButton fab;
    private TabLayout tabLayout;
    private View prevTab, bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mainFragment = MainFragment.newInstance();
        this.myPageFragment = MyInfoFragment.newInstance();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initialize();
        settingScrollView();
        setContent(mainFragment);
    }

    public void refreshMainContent() {
        FragmentTransaction mFragmentTransc = getFragmentManager().beginTransaction();
        this.mainFragment = MainFragment.newInstance();
        if(prevFragment!=null&&prevFragment.equals(mainFragment)){
            mFragmentTransc.remove(prevFragment);
            mFragmentTransc.add(mainContent.getId(), mainFragment);
            this.prevFragment = mainFragment;
        }
    }

    private void setContent(Fragment fragment) {
        if (prevFragment != null && prevFragment.equals(fragment)) return;
        FragmentTransaction mFragmentTransc = getFragmentManager().beginTransaction();
        if (prevFragment != null)
            mFragmentTransc.remove(prevFragment);
        mFragmentTransc.add(mainContent.getId(), fragment);
        mFragmentTransc.addToBackStack(null);
        mFragmentTransc.commit();
        prevFragment = fragment;
    }

    private void processTab(View view) {
        String title = ((TextView) view.findViewById(R.id.tv_title)).getText().toString();
        switch (title) {
            case TAB_HOME:
                setContent(mainFragment);
                showSearchBar();
                break;
            case TAB_MYPAGE:
                setContent(myPageFragment);
                hideSearchBar();
                break;
        }
    }

    private void initialize() {
        searchBar = findViewById(R.id.search_bar);
        mainContent = (RelativeLayout) findViewById(R.id.main_content);
        bottomNavigation = findViewById(R.id.bottom_nav);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorHeight(0);
        tabLayout.setTabTextColors(getResources().getColorStateList(R.color.icon));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                ((ImageView) view.findViewById(R.id.iv_icon)).setColorFilter(getResources().getColor(R.color.colorPrimary));
                ((TextView) view.findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.colorPrimary));
                if (prevTab != null) {
                    ((ImageView) prevTab.findViewById(R.id.iv_icon)).setColorFilter(getResources().getColor(R.color.icon));
                    ((TextView) prevTab.findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.icon));
                }
                prevTab = view;
                processTab(view);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTab(TAB_HOME, R.drawable.ic_home_black_24dp)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTab(TAB_MYPAGE, R.drawable.ic_account_card_details_black_24dp)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTab(TAB_CHATTING, R.drawable.ic_message_black_24dp)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTab("Tab 4", R.drawable.ic_more_vert_black_24dp)));
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void settingScrollView() {
        mainScrollView = (DetectScrollingScrollView) findViewById(R.id.main_scrollview);
        mainScrollView.setHandler(new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DetectScrollingScrollView.UP:
                        show();
                        break;
                    case DetectScrollingScrollView.DOWN:
                        if (msg.arg1 > searchBar.getHeight()) {
                            hide();
                        }
                        break;
                }
            }
        });
    }

    private void hide() {
        mainScrollView.setHide(true);
        bottomNavigation.animate().translationY(tabLayout.getHeight());
        fab.animate().translationY(tabLayout.getHeight());
        hideSearchBar();
    }

    private void hideSearchBar() {
        searchBar.animate().translationY(-(searchBar.getHeight() + getResources().getDimension(R.dimen.search_bar_top_margin)));
    }

    private void showSearchBar() {
        if (prevFragment.equals(mainFragment))
            searchBar.animate().translationY(0);
    }

    private void show() {
        mainScrollView.setHide(false);
        bottomNavigation.animate().translationY(0);
        fab.animate().translationY(0);
        showSearchBar();
    }

    private View createTab(String title, int iconId) {
        View tab = LayoutInflater.from(this).inflate(R.layout.tab, null);
        ((TextView) tab.findViewById(R.id.tv_title)).setText(title);
        ((ImageView) tab.findViewById(R.id.iv_icon)).setImageDrawable(getResources().getDrawable(iconId));
        return tab;
    }

}
