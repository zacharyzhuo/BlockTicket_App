package com.example.asus.blockchain;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener{

    public Toolbar program_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Block券");

        program_toolbar = (Toolbar) findViewById(R.id.program_toolbar);

        // 與前端 View 重複
        //toolbar.setNavigationIcon(R.drawable.lo);//设置导航栏图标
        program_toolbar.setLogo(R.drawable.lo_bar);//设置app logo
        program_toolbar.setTitle("Block券");//设置主标f题
        //toolbar.setSubtitle("Subtitle");//设置子标题
        //toolbar.setTitleTextColor(...);
        //toolbar.setTitleTextAppearance(...);

        setSupportActionBar(program_toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);//使用logo
        getSupportActionBar().setDisplayShowTitleEnabled(true);//不顯示 title
        //getSupportActionBar().setTitle("Block券");

        //getSupportActionBar().setLogo(R.drawable.lo);//在程式裡指定 logo 的 ResourceID
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        int id = getIntent().getIntExtra("id", 0);
        if (id == 1) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,new Fragment_Home())
                    .addToBackStack(null)
                    .commit();
        }

        loadFragment(new Fragment_Home());
    }

    private boolean loadFragment(Fragment fragment){
        if (fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        switch (item.getItemId()){
            case R.id.home:
                fragment = new Fragment_Home();
                break;
            case R.id.apply:
                fragment = new Fragment_Apply();
                break;
            case R.id.ticket:
                fragment = new Fragment_ApplyStatus();
                break;
            case R.id.history:
                fragment = new Fragment_Records();
                break;
            case R.id.personal:
                fragment = new Fragment_PersonalPage();
                break;
        }
        return  loadFragment(fragment);
    }
}
