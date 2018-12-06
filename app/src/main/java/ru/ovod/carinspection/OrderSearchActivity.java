package ru.ovod.carinspection;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;

import ru.ovod.carinspection.adapters.OrderSearchAdapter;
import ru.ovod.carinspection.helpers.DataSet;
import ru.ovod.carinspection.helpers.SysHelper;
import ru.ovod.carinspection.pojo.Inspection;
import ru.ovod.carinspection.pojo.Order;

public class OrderSearchActivity extends AppCompatActivity {
        private SysHelper sysHelper;
        private OrderSearchAdapter adapter;
        private Inspection inspection;
        private int inspectionID;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordersearch);

        sysHelper = SysHelper.getInstance(this);
        Intent intent = getIntent();
        inspectionID = intent.getIntExtra("inspectionID", 0);
        inspection = sysHelper.getDbhelper().getInspection(inspectionID);

        adapter = new OrderSearchAdapter(OrderSearchActivity.this);
        final ListView items = (ListView) findViewById(R.id.items);
        items.setAdapter(adapter);
        items.setClickable(true);
        items.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Order order = (Order) parent.getItemAtPosition(position);
                        ReturnOrder(order);
                    }
                }
        );

        (new RefreshList()).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ReturnOrder(null);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void ReturnOrder(Order order) {
        Intent answerIntent = new Intent();
        if (order != null) {
            answerIntent.putExtra("Order", order);
            setResult(RESULT_OK, answerIntent);
        } else {
            setResult(RESULT_CANCELED, answerIntent);
        }
        finish();
    }

    private class RefreshList extends AsyncTask<String, Void, Void> {
        ArrayList<Order> orderList;

        @Override
        protected Void doInBackground(String... arg0) {
            Order order;
            orderList = new ArrayList<Order>();

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
                    order.setInspectionID(inspectionID);
                    orderList.add(order);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void unused){
            for (Order item: orderList) {
                adapter.add(item);
            }
        }
    }
}
