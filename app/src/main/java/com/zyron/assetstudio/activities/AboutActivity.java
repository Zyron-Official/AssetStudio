package com.zyron.assetstudio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.provider.Settings;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;
import com.zyron.assetstudio.databinding.ActivityAboutBinding;
import com.zyron.assetstudio.R;

public class AboutActivity extends AppCompatActivity {
    private ActivityAboutBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        setTitle("About");
        binding.toolbar.setNavigationOnClickListener((v) -> onBackPressed());
        
        MaterialButton xIcon = findViewById(R.id.xIcon);
        xIcon.setOnClickListener(view -> openXProfile());
        
        MaterialButton emailIcon = findViewById(R.id.emailIcon);
        emailIcon.setOnClickListener(view -> sendEmail());
        
        MaterialButton notificationIcon = findViewById(R.id.notificationIcon);
        notificationIcon.setOnClickListener(view -> openNotificationSettings());
    }
    
    private void openXProfile() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://x.com/assetstudiox")); // Replace with your Twitter URL
        startActivity(intent);
    }
    
    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:artsphereofficial@gmail.com")); // Replace with your email address
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject of the email");
        intent.putExtra(Intent.EXTRA_TEXT, "Body of the email");
        startActivity(intent.createChooser(intent, "Send Email"));
        
    }
    
    private void openNotificationSettings() {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(intent);
    }
}