package com.digi.diary;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.digi.diary.adapter.Notesadapter;
import com.digi.diary.db.tables.NotesTable;
import com.digi.diary.model.NotesModel;
import com.digi.diary.utils.AlertDialogHelper;
import com.digi.diary.utils.RecyclerItemClickListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener{
    private RecyclerView mRecyclerView;
    private ArrayList<NotesModel> mNotesModels;
    private NotesTable mNotesTable;
    private Notesadapter mNotesAdapter;
    private android.view.ActionMode mActionMode;
    private ArrayList<NotesModel> multiselect_list=new ArrayList<>();
    boolean isMultiSelect = false;
    Menu context_menu;
    AlertDialogHelper alertDialogHelper;
    NotesTable notesTable;
    TextView mNoRecordFound;

    InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        String themeName = pref.getString("theme", "Theme1");
        if (themeName.equals("Theme1")) {
            setTheme(R.style.AppTheme);
        }
        else if (themeName.equals("Theme2")) {
            Toast.makeText(this, "set theme", Toast.LENGTH_SHORT).show();
            setTheme(R.style.AppTheme1);
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        //ads
        AdView mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
       // mInterstitialAd = new InterstitialAd(this);
       // mInterstitialAd.setAdUnitId("ca-app-pub-7134639728280028/9037177194");
       // mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");


//        AdRequest adRequest = new AdRequest.Builder()
//
//                // Add a test device to show Test Ads
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .build();
//        mInterstitialAd.loadAd(adRequest);
//
//        // Prepare an Interstitial Ad Listener
//        mInterstitialAd.setAdListener(new AdListener() {
//            public void onAdLoaded() {
//                // Call displayInterstitial() function
//                displayInterstitial();
//            }
//        });
        //ads
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WriteNoteActivity.class);
                startActivity(intent);
            }
        });
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect)
                    multi_select(position);
//                else
//                    Toast.makeText(getApplicationContext(), "Details Page", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<NotesModel>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    }
                }

                multi_select(position);

            }
        }));
    }
    public void displayInterstitial() {
        // If Ads are loaded, show Interstitial else show nothing.
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AsyncData().execute();

    }
class AsyncData extends AsyncTask<Void,Void,Void>
{

    @Override
    protected Void doInBackground(Void... voids) {
        initData();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mNotesAdapter = new Notesadapter(MainActivity.this);

        if(mNotesModels!=null&&mNotesModels.size()>0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNotesAdapter.setList(mNotesModels,multiselect_list);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setAdapter(mNotesAdapter);
            mNoRecordFound.setVisibility(View.GONE);
        }
        else
        {
            mRecyclerView.setVisibility(View.GONE);
            mNoRecordFound.setVisibility(View.VISIBLE);
        }
        super.onPostExecute(aVoid);
    }
}

    private void initView() {
        alertDialogHelper =new AlertDialogHelper(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        mNoRecordFound=(TextView) findViewById(R.id.nothing_found);
        mNotesModels=new ArrayList<>();
        mNotesTable=new NotesTable((BaseApplication) getApplicationContext());
         notesTable = new NotesTable((BaseApplication) getApplicationContext());

    }
    private void initData()
    {
        mNotesModels=mNotesTable.getAllNotesData();


    }
    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(mNotesModels.get(position)))
                multiselect_list.remove(mNotesModels.get(position));
            else
                multiselect_list.add(mNotesModels.get(position));

            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(this);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                mNotesAdapter.getFilter().filter(s.toString());
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
//                                              Toast.makeText(getActivity(),"heloo",Toast.LENGTH_LONG).show();
                                          }
                                      }
        );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            startActivityForResult(new Intent(this,
//                    ThemePreferenceActivity.class), 1);
//
//        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == ThemePreferenceActivity.RESULT_CODE_THEME_UPDATED) {
                finish();
                startActivity(getIntent());
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void refreshAdapter()
    {
        mNotesAdapter.selected_usersList=multiselect_list;
        mNotesAdapter.mNotesModels=mNotesModels;
        mNotesAdapter.notifyDataSetChanged();
    }

    //
    private android.view.ActionMode.Callback mActionModeCallback = new android.view.ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("","Delete Contact","DELETE","CANCEL",1,false);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<NotesModel>();
            refreshAdapter();
        }
    };

    // AlertDialog Callback Functions

    @Override
    public void onPositiveClick(int from) {
        if(from==1)
        {
            if(multiselect_list.size()>0)
            {
                for(int i=0;i<multiselect_list.size();i++) {
                    notesTable.deleteRecord(multiselect_list.get(i).getId());
                    mNotesModels.remove(multiselect_list.get(i));
                }

                mNotesAdapter.notifyDataSetChanged();

                if (mActionMode != null) {
                    mActionMode.finish();
                }
                Toast.makeText(getApplicationContext(), "Delete Click", Toast.LENGTH_SHORT).show();
            }
        }
        else if(from==2)
        {
            if (mActionMode != null) {
                mActionMode.finish();
            }
NotesModel notesModel=new NotesModel();
            notesModel.setmContents("jsds");
            notesModel.setmTitle("hey");
            mNotesModels.add(notesModel);
            mNotesAdapter.notifyDataSetChanged();

//            NotesModel mSample = new SampleModel("Name"+user_list.size(),"Designation"+user_list.size());
//            user_list.add(mSample);
//            multiSelectAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }
}
