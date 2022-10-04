// Group22_InClass08
// MainActivity.java
// Ken Stanley & Stephanie Karp

package edu.charlotte.group22_inclass08;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.charlotte.group22_inclass08.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new ContactsFragment())
                .commit();
    }
}