package ru.ovod.carinspection;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ru.ovod.carinspection.Network.NetworkCall;
import ru.ovod.carinspection.adapters.AddCarInspectionAdapter;
import ru.ovod.carinspection.helpers.DataSet;
import ru.ovod.carinspection.helpers.SysHelper;
import ru.ovod.carinspection.pojo.Inspection;
import ru.ovod.carinspection.pojo.Order;
import ru.ovod.carinspection.pojo.Photo;



public class AddCarInspectionActivity extends AppCompatActivity {
    private SysHelper sysHelper;
    private AddCarInspectionAdapter adapter;
    private Inspection inspection;

    private EditText editNumber; //поле Edit с номером ЗН. Инициализируется OnCreate.
    private TextView viewDate;
    private TextView viewModel;
    private TextView viewVIN;
    private RecyclerView.LayoutManager layoutManager;
    private Button btnSync;
    private Button btnTakePhoto;


    /*для фото*/
    static String PREFIX = "Order";
    static final int REQUEST_TAKE_PHOTO = 3333;
    static final int REQUEST_ORDERID = 3477;

    //Создание формы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcarinspection);

        sysHelper = SysHelper.getInstance(this);

        editNumber = findViewById(R.id.editNumber);
        editNumber.setOnEditorActionListener(new DoneOnEditorActionListener());
        viewDate = findViewById(R.id.viewDate);
        viewModel = findViewById(R.id.viewModel);
        viewVIN = findViewById(R.id.viewVIN);
        btnSync = findViewById(R.id.btnSync);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);

        sysHelper.setProgressBar((ProgressBar) findViewById(R.id.progressBar));
        sysHelper.getProgressBar().setVisibility(ProgressBar.INVISIBLE);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNumberIsSet()) {
                    if (saveInspection()) {
                        takePhoto((Activity) sysHelper.getApplicationContext());
                    }
                }
            }
        });

        //событие на клик
        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncData();
            }
        });

        //Получить данные из БД по inspectionID
        Intent intent = getIntent();
        int inspectionID = intent.getIntExtra("inspectionID", 0);
        inspection = sysHelper.getDbhelper().getInspection(inspectionID);

        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.imageGallery);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AddCarInspectionAdapter();
        adapter.setOnClickListener(
                new AddCarInspectionAdapter.DetailsAdapterListener() {
                    @Override
                    public void fabOnClick(View v, int position) {
                       Photo photo = adapter.getItem(position);
                       boolean deleted = sysHelper.getDbhelper().delPhoto(photo);
                       if (deleted) {
                           adapter.del(position);
                       }
                    }

                    @Override
                    public void imgOnClick(Uri uri){
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "image/jpeg");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    }
                }
        );
        recyclerView.setAdapter(adapter);

        setControls();
        new RefreshList().execute();
    }

    public AddCarInspectionAdapter getAdapter() {
        return adapter;
    }

    public Button getBtnSync() {
        return btnSync;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_addcarinspection, menu);
        return true;

    }

    // Determines if Action bar item was selected. If true then do corresponding action.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                saveInspection();

                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_takephoto:
                if (checkNumberIsSet()) {
                    if (saveInspection()) {
                        takePhoto(this);
                    }
                }

                return true;

            case R.id.action_searchorder:
                if (checkNumberIsSet()) {
                    if (saveInspection()) {
                        searchOrder(this);
                    }
                }

                return true;

            case R.id.action_del_photo:
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

    private void takePhoto(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
        } else {
            //takePhoto2();
            sysHelper.getPhotoHelper().takePhoto(this, PREFIX, inspection.getNumber());
        }

    }

    private void takePhoto2() {
        Intent cameraIntent = new Intent(AddCarInspectionActivity.this, CameraCaptureActivity.class);
        cameraIntent.putExtra("Inspection", inspection);
        startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
    }

    private void searchOrder(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE}, 120);
        } else {
            searchOrder2();
        }

    }

    private void searchOrder2() {
        if (!sysHelper.isOnline()){
            sysHelper.showToAst(getString(R.string.WifiIsOffline));
        } else {
            Intent questionIntent = new Intent(AddCarInspectionActivity.this, OrderSearchActivity.class);
            questionIntent.putExtra("inspectionID", inspection.get_inspectionid());
            startActivityForResult(questionIntent, REQUEST_ORDERID);
        }
        return;
    }

    private void setControls() {
        if (inspection != null) {
            editNumber.setText(String.valueOf(inspection.getNumber()));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");
            if (inspection.getDate().getTime() > 0) { viewDate.setText(dateFormat.format(inspection.getDate())); } else viewDate.setText("");
            viewModel.setText(inspection.getModel());
            viewVIN.setText(inspection.getVin());
        }

        return;
    }

    private class RefreshList extends AsyncTask<String, Void, Void> {
        ArrayList<Photo> photoList;

        @Override
        protected Void doInBackground(String... arg0) {
            if (inspection != null) {
                if (inspection.get_inspectionid() > 0) {
                    photoList = sysHelper.getDbhelper().getPhotoList(inspection.get_inspectionid());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void unused){
            if (photoList != null) {
                adapter.setGalleryList(photoList);
            }
        }
    }

    private boolean saveInspection() {
        boolean result = true;
        int number;
        try {
            number = Integer.parseInt(editNumber.getText().toString());
        } catch(Exception e) {
            number = 0;
            result = false;
        }


        if (number > 0) {
            if (inspection == null) {
                inspection = sysHelper.getDbhelper().getInspectionByNumber(number);
                if (inspection == null) {
                    //Если номера не было, создадим новую запись
                    inspection = new Inspection(0, number, 0, 0, null, null, null);
                    inspection = sysHelper.getDbhelper().insInspection(inspection);
                } else {
                    //если номер найден, обновим экранные данные по осмотру
                    (new RefreshList()).execute();
                }
            }
            else {
                //Если сущность существует, проверим изменился ли номер
                if (number != inspection.getNumber()) {
                    //если номер изменился, проверим на факт синхронизации
                    if (inspection.getIssync() > 0) {
                        sysHelper.showToAst(getString(R.string.ErrorNumberChange));
                        result = false;
                    } else {
                        Inspection inspection2;
                        inspection2 = sysHelper.getDbhelper().getInspectionByNumber(number);
                        if (inspection2 == null) {
                            inspection.setNumber(number);
                            if (inspection.getOrderid() > 0) {
                                inspection.setOrderid(0);
                                inspection.setDate(null);
                                inspection.setModel(null);
                                inspection.setVin(null);
                            }
                            inspection = sysHelper.getDbhelper().updInspection(inspection);
                        } else {
                            inspection = inspection2;
                            (new RefreshList()).execute();
                        }
                    }
                }
                //если не изменился, то ничего не делаем
            }
            setControls();
        }

        return result;
    }

    private boolean checkNumberIsSet() {
        boolean result = false;
        int number;
        try {
            number = Integer.parseInt(editNumber.getText().toString());
        } catch (Exception e) {
            number = 0;
        }
        if (number <= 0) {
            sysHelper.showToAst(getString(R.string.SetOrderNumber));
            result = false;
        } else {
            result = true;
        }

        return result;
    }


    private void syncData() {
        if (inspection == null) {
            saveInspection();
        }

         if (inspection.getNumber() <= 0) {
            sysHelper.showToAst(getString(R.string.SetOrderNumber));
            return;
        }

        if (inspection.getOrderid() <= 0) {
            searchOrder(this);
        } else {
                btnSync.setEnabled(false);
                (new NetworkCall()).fileUpload(this, inspection, sysHelper);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 110) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //takePhoto2();
                sysHelper.getPhotoHelper().takePhoto(this, PREFIX, inspection.getNumber());
            }
        }

        if (requestCode == 120) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                searchOrder2();
            }
        }
    }


    // определим функцию получени резултатов (обращение к другим формам или активностя)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // обработаем получение фото
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //(new RefreshList()).execute();

            // запишем информацию о фото в базу

            Photo photo = new Photo(0
                    ,sysHelper.getPhotoHelper().getmCurrentPhotoPath()
                    ,sysHelper.getPhotoHelper().getmCurrentPhotoName(), 0, inspection.get_inspectionid());
            photo = sysHelper.getDbhelper().insPhoto(photo);
            adapter.add(photo);
        }

        // обработаем получение OrderID
        if (requestCode == REQUEST_ORDERID && resultCode == RESULT_OK)
        {
            Order order;
            order = (Order)data.getParcelableExtra ("Order");
            if (order != null) {
                inspection = sysHelper.getDbhelper().updInspectionOrder(order);
                setControls();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ((GridLayoutManager) layoutManager).setSpanCount(4);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            ((GridLayoutManager) layoutManager).setSpanCount(2);
        }
    }

    private class DoneOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                (new searchOrder()).execute();

                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        }
    }

    private class searchOrder extends AsyncTask<String, Void, Void> {
        Order order = null;

        @Override
        protected Void doInBackground(String... arg0) {
            if (checkNumberIsSet()) {
                if (saveInspection()) {
                    if (inspection.getOrderid() <= 0) {
                        if (sysHelper.isOnline()) {
                            DataSet dataset = new DataSet();
                            String sql = "select orderid, number, date, vin, model from TechnicalCentre.dbo.V_ActualOrderForOrderPhotos with(NoLock) ";
                            if (inspection != null) {
                                sql += "where number = " + String.valueOf(inspection.getNumber());
                            }
                            sql += " order by date ";
                            dataset.GetJSONFromWEB(sql);
                            if (dataset.RecordCount() > 0) {
                                for (int i = 0; i < dataset.RecordCount(); i++) {
                                    dataset.GetRowByNumber(i);
                                    order = new Order(
                                            dataset.FieldByName_AsInteger("orderid"),
                                            dataset.FieldByName_AsInteger("number"),
                                            dataset.FieldByName_AsDate("date"),
                                            dataset.FieldByName_AsString("model"),
                                            dataset.FieldByName_AsString("vin")
                                    );
                                    order.setInspectionID(inspection.get_inspectionid());
                                }
                            }
                        }
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Void unused){
            if (order != null) {
                inspection = sysHelper.getDbhelper().updInspectionOrder(order);
                setControls();
            }
        }
    }

}

