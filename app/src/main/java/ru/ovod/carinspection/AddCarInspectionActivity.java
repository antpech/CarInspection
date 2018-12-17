package ru.ovod.carinspection;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ru.ovod.carinspection.Network.NetworkCall;
import ru.ovod.carinspection.adapters.AddCarInspectionAdapter;
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
        viewDate = findViewById(R.id.viewDate);
        viewModel = findViewById(R.id.viewModel);
        viewVIN = findViewById(R.id.viewVIN);
        btnSync = findViewById(R.id.btnSync);

        sysHelper.setProgressBar((ProgressBar) findViewById(R.id.progressBar));
        sysHelper.getProgressBar().setVisibility(ProgressBar.INVISIBLE);

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
                       File file = new File(photo.getPath());
                       boolean deleted = file.delete();
                       if (deleted) {
                           deleted = sysHelper.getDbhelper().delPhoto(photo.get_photoid());
                           if (deleted) {
                               adapter.del(position);
                           }
                       }
                    }
                }
        );
        recyclerView.setAdapter(adapter);

        setControls();
        new RefreshList().execute();
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
                    item.setTitle(R.string.action_del_photo);
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
            takePhoto2();
            //sysHelper.getPhotoHelper().takePhoto(this, PREFIX, inspection.getNumber());
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
        int number = Integer.parseInt(editNumber.getText().toString());
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
        }

        if (inspection.getOrderid() > 0) {
            (new NetworkCall()).fileUpload(adapter, inspection, sysHelper);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 110) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePhoto2();
                //sysHelper.getPhotoHelper().takePhoto(this, PREFIX, inspection.getNumber());
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
            (new RefreshList()).execute();

            // запишем информацию о фото в базу
            /*
            Photo photo = new Photo(0
                    ,sysHelper.getPhotoHelper().getmCurrentPhotoPath()
                    ,sysHelper.getPhotoHelper().getmCurrentPhotoName(), 0, inspection.get_inspectionid());
            photo = sysHelper.getDbhelper().insPhoto(photo);
            adapter.add(photo);
            */
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

}

