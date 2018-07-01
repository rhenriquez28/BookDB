package ds6.dpc.fisc.utp.bookdb;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import ds6.dpc.fisc.utp.bookdb.database.BookDatabase;
import ds6.dpc.fisc.utp.bookdb.fragments.AddFragment;
import ds6.dpc.fisc.utp.bookdb.fragments.BackUpFragment;
import ds6.dpc.fisc.utp.bookdb.fragments.BookListFragment;
import ds6.dpc.fisc.utp.bookdb.fragments.DetailFragment;
import ds6.dpc.fisc.utp.bookdb.fragments.SearchFragment;

public class FragmentHostActivity extends AppCompatActivity implements
        BookListFragment.OnFragmentInteractionListener, AddFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener, DetailFragment.OnFragmentInteractionListener,
        BackUpFragment.OnFragmentInteractionListener{

    private static final String DATABASE_NAME = "book_db";
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragmenthost);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        requestPremissions();
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        getSupportActionBar().setTitle(R.string.listBooks);
        toolbar.setTitleTextColor(Color.WHITE);
        android.support.v4.app.FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction1.add(R.id.fragmentContainer, BookListFragment.newInstance());
        fragmentTransaction1.commit();

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // close drawer when item is tapped
                    drawerLayout.closeDrawers();

                    switch (menuItem.getItemId()) {

                        case R.id.seeAll:
                            BookListFragment bookListFragment = BookListFragment.newInstance();
                            replaceFragment(bookListFragment);
                            getSupportActionBar().setTitle(R.string.listBooks);
                            break;
                        case R.id.add:
                            AddFragment addFragment = AddFragment.newAddInstance();
                            replaceFragment(addFragment);
                            getSupportActionBar().setTitle(R.string.add);
                            break;
                        case R.id.search:
                            SearchFragment searchFragment = SearchFragment.newInstance();
                            replaceFragment(searchFragment);
                            getSupportActionBar().setTitle(R.string.search);
                            break;
                        case R.id.export:
                            DoBackUp();
                            break;
                        case R.id.seeExport:
                            SeeBackUp();
                            break;
                    }
                    return true;
                });


    }

    private void requestPremissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    private void replaceFragment(android.support.v4.app.Fragment fragment) {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    private boolean writeToFile(String data) {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir,"BDBackup.txt");
        if(file.exists() && file.isFile()) {
            file.delete();
        }
        try (FileWriter fileWriter = new FileWriter(file,true)) {
            fileWriter.write(data);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void DoBackUp(){
        final Toast toast = Toast.makeText(getBaseContext(),"message",Toast.LENGTH_SHORT);
        new Thread(() -> {
            if(writeToFile(makeBackupString())) {
                toast.setText("File saved successfully!");
            } else {
                toast.setText("File saving failed!");
            }
            toast.show();
        }).start();
    }

    public String makeBackupString() {
        final BookDatabase bookDatabase = Room.databaseBuilder(
                getApplicationContext(),
                BookDatabase.class, DATABASE_NAME
        ).build();
        Cursor cursor = bookDatabase.bookDao().getAllCursors();
        String[] columnNames = cursor.getColumnNames();

        String backupStr = "";
        backupStr += Arrays.stream(columnNames)
                .reduce((accum,str) -> accum + " | " + str)
                .get() + " | \n";
        while (cursor.moveToNext()){
            for (int i = 0; i < columnNames.length; i++) {
                backupStr += cursor.getString(i) + " | ";
            }
            backupStr += "\n";
        }
        return backupStr;
    }

    public void SeeBackUp(){
        InputStream is;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"BDBackup.txt");
            is = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            int c;
            String backUpText;
                while ((c = br.read()) > -1 ){
                    builder.append((char)c);
                }
                is.close();
                br.close();

            backUpText = builder.toString();
            Log.d("texto royal?", backUpText);
            BackUpFragment backUpFragment = BackUpFragment.newInstance(backUpText);
            replaceFragment(backUpFragment);
            getSupportActionBar().setTitle(R.string.backupFile);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("FILE","File not found");
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAdd() {
        //Ocultar el keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        BookListFragment bookListFragment = BookListFragment.newInstance();
        replaceFragment(bookListFragment);
        getSupportActionBar().setTitle(R.string.listBooks);

    }

    @Override
    public void onUpdate(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        BookListFragment bookListFragment = BookListFragment.newInstance();
        replaceFragment(bookListFragment);
        getSupportActionBar().setTitle(R.string.listBooks);
    }

    @Override
    public void onSearch(String title, String author, String isbn, String area, int year, String editorial) {
        String[] book = new String[6];
        book[0] = title;
        book[1] = author;
        book[2] = isbn;
        book[3] = area;
        book[4] = Integer.toString(year);
        book[5] = editorial;
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        DetailFragment detailFragment = DetailFragment.newInstance(book);
        replaceFragment(detailFragment);
        getSupportActionBar().setTitle(R.string.detailedBook);
    }

    @Override
    public void onDetailUpdate(String title, String author, String isbn,
                               String area, int year, String editorial, boolean isUpdate) {
        AddFragment updateFragment = AddFragment.newUpdateInstance(title,
                author, isbn, area, year, editorial, isUpdate);
        replaceFragment(updateFragment);
    }

    @Override
    public void onDelete() {
        BookListFragment bookListFragment = BookListFragment.newInstance();
        replaceFragment(bookListFragment);
        getSupportActionBar().setTitle(R.string.listBooks);
    }

    @Override
    public void onBookSelection(String title, String author, String isbn, String area, int year, String editorial) {
        String[] book = new String[6];
        book[0] = title;
        book[1] = author;
        book[2] = isbn;
        book[3] = area;
        book[4] = Integer.toString(year);
        book[5] = editorial;
        DetailFragment detailFragment = DetailFragment.newInstance(book);
        replaceFragment(detailFragment);
        getSupportActionBar().setTitle(R.string.detailedBook);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
