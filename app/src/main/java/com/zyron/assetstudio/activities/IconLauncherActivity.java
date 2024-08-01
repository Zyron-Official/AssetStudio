package com.zyron.assetstudio.activities;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.zyron.assetstudio.databinding.ActivityIconLauncherBinding;
import com.zyron.assetstudio.R;
import android.view.View;
import android.graphics.Color;

import com.google.android.material.slider.Slider;
import android.content.res.TypedArray;
import androidx.core.content.ContextCompat;

import com.zyron.assetstudio.fragments.ClipartIconFragment;
import com.zyron.assetstudio.fragments.TextIconFragment;
import com.zyron.assetstudio.fragments.ImageIconFragment;

public class IconLauncherActivity extends AppCompatActivity {

  private ActivityIconLauncherBinding binding;
  private FragmentManager fragmentManager;
  private Slider iconPadding;
  private Handler handler = new Handler();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityIconLauncherBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    setSupportActionBar(binding.toolbar);
    setTitle("Launcher Icon");
    binding.toolbar.setNavigationOnClickListener((v) -> onBackPressed());
    fragmentManager = getSupportFragmentManager();
    MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggleButton);
    loadHeavyData();

    toggleGroup.check(R.id.clipartIcon);
    Fragment fragment = new ClipartIconFragment();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.replace(R.id.fragmentContainer, fragment);
    transaction.commit();

    toggleGroup.addOnButtonCheckedListener(
        (group, checkedId, isChecked) -> {
          if (isChecked) {
            Fragment selectedFragment = null;
            if (checkedId == R.id.clipartIcon) {
              selectedFragment = new ClipartIconFragment();
            } else if (checkedId == R.id.textIcon) {
              selectedFragment = new TextIconFragment();
            } else if (checkedId == R.id.imageIcon) {
              selectedFragment = new ImageIconFragment();
            }

            if (selectedFragment != null) {

              FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
              fragmentTransaction.setCustomAnimations(
                  R.anim.enter_animation,
                  R.anim.exit_animation,
                  R.anim.enter_animation,
                  R.anim.exit_animation);
              fragmentTransaction.replace(R.id.fragmentContainer, selectedFragment);
              fragmentTransaction.commit();
            }
          }
        });
        
    }
    
    private void loadHeavyData() {
    // Simulate heavy work in a background thread
    handler.postDelayed(() -> {
        try {
            // Simulate heavy processing
            Thread.sleep(0000); // Replace with actual heavy work simulation

            // Ensure UI updates are only performed if activity is not finishing or destroyed
            runOnUiThread(() -> {
                if (!isFinishing() && !isDestroyed()) {
                    // Update UI components here
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }, 0); // Delay of 0 ensures it runs immediately on the next message loop iteration
}

}
