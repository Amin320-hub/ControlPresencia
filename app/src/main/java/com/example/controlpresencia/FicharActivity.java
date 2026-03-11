package com.example.controlpresencia;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FicharActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fichar);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        String token = getIntent().getStringExtra("TOKEN");
        String rol = getIntent().getStringExtra("ROL");

        viewPager.setAdapter(new ViewPagerAdapter(this, token, rol));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Fichar"); break;
                case 1: tab.setText("Registros"); break;
                case 2: tab.setText("Perfil"); break;
            }
        }).attach();
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        private final String token;
        private final String rol;

        public ViewPagerAdapter(@NonNull AppCompatActivity activity, String token, String rol) {
            super(activity);
            this.token = token;
            this.rol = rol;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return FichajeFragment.newInstance(token, rol);
                case 1: return  RegistrosFragment.newInstance(token);
                case 2: return PerfilFragment.newInstance(token, rol);
                default: return FichajeFragment.newInstance(token, rol);
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
