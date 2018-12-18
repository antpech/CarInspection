package ru.ovod.carinspection;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import ru.ovod.carinspection.adapters.CarInspectionAdapter;
import ru.ovod.carinspection.helpers.SysHelper;
import ru.ovod.carinspection.pojo.Inspection;


public class CarInspectionsActivity extends AppCompatActivity {
    private SysHelper sysHelper;
    private CarInspectionAdapter adapter;

    //Создание формы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carinspections);

        sysHelper = SysHelper.getInstance(this);

        adapter = new CarInspectionAdapter(CarInspectionsActivity.this);
        final ListView items = (ListView) findViewById(R.id.items);
        items.setAdapter(adapter);
        items.setClickable(true);
        items.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Inspection item = (Inspection) parent.getItemAtPosition(position);
                        startAddCarInspectionActivity(item.get_inspectionid());
                    }
                }
        );

        new RefreshList().execute();
    }

/* Функции МЕНЮ */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_carinspections, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    // Determines if Action bar item was selected. If true then do corresponding action.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //handle presses on the action bar items
        switch (item.getItemId()) {

            case R.id.action_add_carinspection:
                startAddCarInspectionActivity(0);
                return true;
            case R.id.action_del_carinspection:
                if (item.getTitle() != "Готово") {
                    item.setTitle("Готово");
                    adapter.showDelBtn(true);
                } else {
                    item.setTitle(R.string.action_edit);
                    adapter.showDelBtn(false);
                }
        }

        return super.onOptionsItemSelected(item);
    }

    private void startAddCarInspectionActivity(int id){
        Intent intent = new Intent(this, AddCarInspectionActivity.class);
        intent.putExtra("inspectionID", id);
        startActivity(intent);
    }


    // Фунция получает список Inspections из базу
    private class RefreshList extends AsyncTask<String, Void, Void> {
        ArrayList<Inspection> inspectionList;

        @Override
        protected Void doInBackground(String... arg0) {
            inspectionList = sysHelper.getDbhelper().getInspectionList();
            return null;
        }

        @Override
        protected void onPostExecute(final Void unused){
            for (Inspection item: inspectionList) {
                adapter.add(item);
            }
        }
    }

    public boolean delInspection(Inspection item){
        return sysHelper.getDbhelper().delInspection(item);
    }


}
