package br.com.cozinheirodelivery.pricemyprint.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;



import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 03/01/2017.
 */

public class IntroAppActivity2 extends AppIntro {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            //Adicionando os slides sem fragment.
            addSlide(AppIntroFragment.newInstance(getString(R.string.intro_title_4), getString(R.string.intro_message_4), R.drawable.shot_config, 0xff33b5e5));
            addSlide(AppIntroFragment.newInstance(getString(R.string.intro_title_5), getString(R.string.intro_message_5), R.drawable.shot_material, 0xff33b5e5));
            addSlide(AppIntroFragment.newInstance(getString(R.string.intro_title_6), getString(R.string.intro_message_6), R.drawable.shot_import, 0xff33b5e5));
            showSkipButton(true);
            setProgressButtonEnabled(true);
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
        this.finish();
    }

}
