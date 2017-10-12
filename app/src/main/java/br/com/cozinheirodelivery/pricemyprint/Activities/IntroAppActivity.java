package br.com.cozinheirodelivery.pricemyprint.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;


import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 03/01/2017.
 */

public class IntroAppActivity extends AppIntro {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        //Adicionando os slides sem fragment.
        try {
            super.onCreate(savedInstanceState);
            addSlide(AppIntroFragment.newInstance(getString(R.string.intro_title_1), getString(R.string.intro_message_1), R.mipmap.ic_launcher, 0xff33b5e5));
            addSlide(AppIntroFragment.newInstance(getString(R.string.intro_title_2), getString(R.string.intro_message_2), R.drawable.shot_main_frag, 0xff33b5e5));
            addSlide(AppIntroFragment.newInstance(getString(R.string.intro_title_3), getString(R.string.intro_message_3), R.drawable.shot_project_components, 0xff33b5e5));
            showSkipButton(true);
            setProgressButtonEnabled(true);
            setDoneText("NEXT");
        }catch (OutOfMemoryError err){
            Toast.makeText(this, R.string.intro_error, Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        this.finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent i = new Intent(IntroAppActivity.this,IntroAppActivity2.class);
        startActivity(i);
        this.finish();
    }
}
