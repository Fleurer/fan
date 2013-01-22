package com.googolmo.shmily.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.view.MenuItem;
import com.googolmo.shmily.BaseActivity;
import com.googolmo.shmily.MainActivity;
import com.googolmo.shmily.R;
import com.googolmo.shmily.fragment.StatusFragment;

/**
 * User: googolmo
 * Date: 12-10-21
 * Time: 下午12:32
 */
public class StatusActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_base);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = Fragment.instantiate(this, StatusFragment.class.getName(), getIntent().getExtras());
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.container, fragment);
        ft.commitAllowingStateLoss();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.setClass(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
