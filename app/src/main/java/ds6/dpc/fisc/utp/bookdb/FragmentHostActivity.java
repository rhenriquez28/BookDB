package ds6.dpc.fisc.utp.bookdb;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.NavigationView;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

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
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
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
                    }
                });


    }

    private void replaceFragment(android.support.v4.app.Fragment fragment) {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    private void DoBackUp(){
        final BookDatabase bookDatabase = Room.databaseBuilder(getApplicationContext(),
                BookDatabase.class, DATABASE_NAME)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File bdbackup = new File("BDBackup.txt");
                    if (bdbackup.exists()){
                        bdbackup.delete();
                    }
                    FileOutputStream fOut = null;
                    try {
                        fOut = openFileOutput(bdbackup.getPath(), Context.MODE_PRIVATE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                Cursor cursor = bookDatabase.bookDao().getAllCursors();
                String[] columnNames = cursor.getColumnNames();
                Log.d("columna io?", columnNames[0]);
                StringBuilder stringBuilder = new StringBuilder();
                OutputStreamWriter osw = new OutputStreamWriter(fOut);
                for (int i=0; i<columnNames.length; i++){
                    if (i == columnNames.length-1){
                        stringBuilder.append(columnNames[i] + "\n");
                    }else {
                        stringBuilder.append(columnNames[i] + " " + "|" + " ");
                    }
                }
                Log.d("Columnas", stringBuilder.toString());
                osw.write(stringBuilder.toString());
                stringBuilder = new StringBuilder();
                while (cursor.moveToNext()){
                    for (int i=0; i<4; i++){
                        stringBuilder.append(cursor.getString(i) + " " + "|" + " ");
                    }
                    stringBuilder.append(Integer.toString(cursor.getInt(4)) + " " + "|" + " ");
                    stringBuilder.append(cursor.getString(5) + "\n");
                    osw.write(stringBuilder.toString());
                }
                osw.flush();
                osw.close();
                cursor.close();
                bookDatabase.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //---display file saved message---
        Toast.makeText(getBaseContext(), "File saved successfully!", Toast.LENGTH_SHORT).show();
    }

    public void SeeBackUp(){
        InputStream is = null;
        try {
            is = openFileInput("BDBackup.txt");
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder = new StringBuilder();
        String str, backUpText;
        try {
            if (br.readLine() == null){
                Log.d("br vacio io?", "yes");
            }
            while ((str = br.readLine()) != null){
                builder.append(str + "\n");
            }
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        backUpText = builder.toString();
        Log.d("texto royal?", backUpText);
        BackUpFragment backUpFragment = BackUpFragment.newInstance(backUpText);
        replaceFragment(backUpFragment);
        getSupportActionBar().setTitle(R.string.backupFile);
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
        /*char[] CheckIsbn = fIsbn.toCharArray();
        if (CheckIsbn[0] != 'H' || CheckIsbn[1] != 'S' && fArea == "Historia"){

        }*/

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
    public void onFragmentInteraction(Uri uri) {

    }
}
