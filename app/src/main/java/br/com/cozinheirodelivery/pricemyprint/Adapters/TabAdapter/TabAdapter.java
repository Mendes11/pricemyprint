package br.com.cozinheirodelivery.pricemyprint.Adapters.TabAdapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Fragments.ConfigurationsListFragment;
import br.com.cozinheirodelivery.pricemyprint.Fragments.MainFragment;
import br.com.cozinheirodelivery.pricemyprint.Fragments.MaterialFragment;
import br.com.cozinheirodelivery.pricemyprint.Fragments.PrintsFragment;
import br.com.cozinheirodelivery.pricemyprint.Objects.Piece;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;

/**
 * Created by Mendes on 23/12/2016.
 */

public class TabAdapter extends FragmentStatePagerAdapter {
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    List<Fragment> fragmentList;
    List<String> titleList;
    Activity activity;
    PrintsFragment printsFragment;
    ConfigurationsListFragment configurationsListFragment;
    MaterialFragment materialFragment;
    Project oProject;
    Boolean isQuickPrice;
    public TabAdapter(Activity activity, FragmentManager fm, Project oProject, List<String> titleList,Boolean isQuickPrice) {
        super(fm);
        this.oProject = oProject;
        this.titleList = titleList;
        this.activity = activity;
        this.isQuickPrice = isQuickPrice;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
    @Override
    public Fragment getItem(int position)
    {
        if(isQuickPrice) {
            switch (position) {
                case 0:
                    if (oProject.getChildList() == null) {
                        oProject.setChildList(new ArrayList<Piece>());
                    }
                    printsFragment = PrintsFragment.newInstance(oProject);
                    return printsFragment;
                case 1:
                    configurationsListFragment = ConfigurationsListFragment.newInstance(oProject);
                    return configurationsListFragment;
                case 2:
                    materialFragment = MaterialFragment.newInstance(oProject);
                    return materialFragment;
                default:
                    return null;
            }
        }else{
            switch (position) {
                case 0:
                    configurationsListFragment = ConfigurationsListFragment.newInstance(oProject);
                    return configurationsListFragment;
                case 1:
                    materialFragment = MaterialFragment.newInstance(oProject);
                    return materialFragment;
                default:
                    return null;
            }
        }

    }

    @Override
    public int getCount() {
        return titleList.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

}
