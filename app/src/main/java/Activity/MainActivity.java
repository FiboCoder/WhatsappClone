package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.Locale;

import Config.FirebaseConfig;
import Fragment.Contacts;
import Fragment.Conversations;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private MaterialSearchView svMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseConfig.getFirebaseAuth();
        svMain = findViewById(R.id.svMain);

        Toolbar toolbar = findViewById(R.id.tbMain);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);

        FragmentPagerItemAdapter fragmentPagerItemAdapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                .add("Conversas", Conversations.class)
                .add("Contatos", Contacts.class)
                .create()
        );
        ViewPager viewPager = findViewById(R.id.vpMain);
        viewPager.setAdapter(fragmentPagerItemAdapter);

        SmartTabLayout smartTabLayout = findViewById(R.id.stlMain);
        smartTabLayout.setViewPager(viewPager);

        svMain.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

                Conversations conversations = (Conversations) fragmentPagerItemAdapter.getPage(0);
                conversations.reloadConversations();

            }
        });

        svMain.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                switch (viewPager.getCurrentItem()){

                    case 0:
                        Conversations conversationsFragment = (Conversations) fragmentPagerItemAdapter.getPage(0);
                        if(newText != null && !newText.isEmpty()){

                            conversationsFragment.searchConversations(newText.toLowerCase());

                        }else{

                            conversationsFragment.reloadConversations();
                        }
                        break;

                    case 1:
                        Contacts contactsFragment = (Contacts) fragmentPagerItemAdapter.getPage(1);
                        if(newText != null && !newText.isEmpty()){

                            contactsFragment.searchContacts(newText.toLowerCase());

                        }else{

                            contactsFragment.reloadContacts();
                        }
                        break;
                }


                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.search);
        svMain.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.logout:
                logoutUser();
                finish();
                break;

            case R.id.configuration:
                openConfigurations();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logoutUser(){

        try {

            auth.signOut();
            openLoginScreen();

        }catch (Exception e){

            e.printStackTrace();
        }
    }

    public void openConfigurations(){

        Intent intent = new Intent(MainActivity.this, Configurations.class);
        startActivity(intent);
    }

    public void openLoginScreen(){

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}