package ru.ovod.carinspection.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import ru.ovod.carinspection.pojo.Inspection;
import ru.ovod.carinspection.pojo.Order;
import ru.ovod.carinspection.pojo.Photo;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 9; // версия
    private static final String DB_Name = "OvodOrders";  // имя локаьной базы данных
    private static final String TAGDB = "DATABASE_OPERATION";

    public static final String INSPECTION = "inspection";  // таблица актов осмотра
    public static final String INSPECTION_ID = "_inspectionid";  // id
    public static final String INSPECTION_NUMBER = "number";  // номер ЗН
    public static final String INSPECTION_ORDERID = "orderid";
    public static final String INSPECTION_ISSYNC = "issync";  // пометка, что синхронизировано
    public static final String INSPECTION_DATE = "date";  // дата ЗН
    public static final String INSPECTION_MODEL = "model";
    public static final String INSPECTION_VIN = "vin";

    public static final String PHOTO = "photo";  // таблица актов осмотра
    public static final String PHOTO_ID = "_photoid";  // id
    public static final String PHOTO_PATH = "path";  // пусть
    public static final String PHOTO_NAME = "name";  // имя файла
    public static final String PHOTO_INSPECTION = "inspectionid";  // ссылка на ID инспекции
    public static final String PHOTO_ISSYNC = "issync";  // признак, что фото залито на сервер

    public DBHelper() {
        super(null, DB_Name, null, DB_VERSION);
    }


    public DBHelper(Context context) {
        super(context, DB_Name, null, DB_VERSION);
        Log.e(TAGDB,"DBHelper Created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.e(TAGDB,"Begin table create.");

        db.beginTransaction();
        try {

//            db.execSQL("drop table if exists " + INSPECTION);
//            db.execSQL("drop table if exists " + PHOTO);

            String sql = "create table IF NOT EXISTS " + INSPECTION + "(" + INSPECTION_ID
                    + " integer primary key AUTOINCREMENT," + INSPECTION_NUMBER + " integer," + INSPECTION_ORDERID + " integer," + INSPECTION_ISSYNC + " integer,"
                    + INSPECTION_DATE + " integer,"+ INSPECTION_MODEL+" text,"+INSPECTION_VIN+" text)";

            Log.e(TAGDB, sql);
            db.execSQL(sql);

            sql = "create table IF NOT EXISTS " + PHOTO + "(" + PHOTO_ID
                    + " integer primary key AUTOINCREMENT," + PHOTO_PATH + " text," + PHOTO_NAME + " text," + PHOTO_ISSYNC + " integer," + PHOTO_INSPECTION + " integer )";

            Log.e(TAGDB, sql);
            db.execSQL(sql);

            db.setTransactionSuccessful();
            Log.e(TAGDB, "Table Created");
        }
        finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists " + INSPECTION);
        db.execSQL("drop table if exists " + PHOTO);

        onCreate(db);

    }


    public ArrayList<Inspection> getInspectionList(){
        Inspection item;
        ArrayList<Inspection> inspectionList = new ArrayList<Inspection>();

        SQLiteDatabase database = this.getReadableDatabase();
        String SQL = "SELECT " + INSPECTION_ID + ", " + INSPECTION_NUMBER + ", " + INSPECTION_ORDERID + ", "
                + INSPECTION_DATE + ", " + INSPECTION_MODEL + ", " + INSPECTION_VIN + ", " + INSPECTION_ISSYNC + ", "
                + " (SELECT count(*) from  " + PHOTO + " where " + PHOTO + "." + PHOTO_INSPECTION + " = " + INSPECTION + "." + INSPECTION_ID + ") as coun"
                + " ,(SELECT " + PHOTO_PATH + " from  " + PHOTO + " where " + PHOTO + "." + PHOTO_INSPECTION + " = " + INSPECTION + "." + INSPECTION_ID + " LIMIT 1) as path"
                + " FROM " + INSPECTION
                + " Order by " + INSPECTION_ID + " desc";
        Cursor cursor = database. rawQuery(SQL, null);
        if (!cursor.isAfterLast()) {
            while (cursor.moveToNext()) {
                Integer InsID = cursor.getInt(cursor.getColumnIndex(INSPECTION_ID));
                Integer num = cursor.getInt(cursor.getColumnIndex(INSPECTION_NUMBER));
                Integer OrdID = cursor.getInt(cursor.getColumnIndex(INSPECTION_ORDERID));
                Date dt = new Date(cursor.getLong(cursor.getColumnIndex(INSPECTION_DATE)));
                String model = cursor.getString(cursor.getColumnIndex(INSPECTION_MODEL));
                String vin = cursor.getString(cursor.getColumnIndex(INSPECTION_VIN));
                Integer isSynced = cursor.getInt(cursor.getColumnIndex(INSPECTION_ISSYNC));
                Integer coun = cursor.getInt(cursor.getColumnIndex("coun"));
                String path = cursor.getString(cursor.getColumnIndex("path"));

                item = new Inspection(InsID, num, OrdID, isSynced, dt, model, vin);
                item.setPhotoCo(coun);
                item.setPath(path);
                inspectionList.add(item);

                Log.e("DB ", "Извлекли INSPECTION_ID: " + InsID);
            }
        }
        cursor.close();
        database.close();

        return inspectionList;
    }

    public Inspection getInspection(int inspectionID) {
        Inspection item;
        item = null;
        if (inspectionID > 0) {
            SQLiteDatabase database = this.getReadableDatabase();
            String SQL = "SELECT " + INSPECTION_ID + ", " + INSPECTION_NUMBER + ", " + INSPECTION_ORDERID + ", "
                    + INSPECTION_DATE + ", " + INSPECTION_MODEL + ", " + INSPECTION_VIN + ", " + INSPECTION_ISSYNC + ", "
                    + " (SELECT count(*) from  " + PHOTO + " where " + PHOTO + "." + PHOTO_INSPECTION + " = " + INSPECTION + "." + INSPECTION_ID + ") as coun"
                    + " FROM " + INSPECTION
                    + " WHERE " + INSPECTION_ID + " = " + Integer.toString(inspectionID);
            Cursor cursor = database.rawQuery(SQL, null);
            if (!cursor.isAfterLast()) {
                cursor.moveToFirst();
                Integer InsID = cursor.getInt(cursor.getColumnIndex(INSPECTION_ID));
                Integer num = cursor.getInt(cursor.getColumnIndex(INSPECTION_NUMBER));
                Integer OrdID = cursor.getInt(cursor.getColumnIndex(INSPECTION_ORDERID));
                Date dt = new Date(cursor.getLong(cursor.getColumnIndex(INSPECTION_DATE)));
                String model = cursor.getString(cursor.getColumnIndex(INSPECTION_MODEL));
                String vin = cursor.getString(cursor.getColumnIndex(INSPECTION_VIN));
                Integer isSynced = cursor.getInt(cursor.getColumnIndex(INSPECTION_ISSYNC));
                Integer Coun = cursor.getInt(cursor.getColumnIndex("coun"));

                item = new Inspection(InsID, num, OrdID, isSynced, dt, model, vin);
                item.setPhotoCo(Coun);

            }
            cursor.close();
            database.close();
        }
        return item;
    }

    public Inspection getInspectionByNumber(int number) {
        Inspection item;
        item = null;
        if (number > 0) {
            SQLiteDatabase database = this.getReadableDatabase();
            String SQL = "SELECT " + INSPECTION_ID + ", " + INSPECTION_NUMBER + ", " + INSPECTION_ORDERID + ", "
                    + INSPECTION_DATE + ", " + INSPECTION_MODEL + ", " + INSPECTION_VIN + ", " + INSPECTION_ISSYNC + ", "
                    + " (SELECT count(*) from  " + PHOTO + " where " + PHOTO + "." + PHOTO_INSPECTION + " = " + INSPECTION + "." + INSPECTION_ID + ") as coun"
                    + " FROM " + INSPECTION
                    + " WHERE " + INSPECTION_NUMBER + " = " + Integer.toString(number);
            Cursor cursor = database.rawQuery(SQL, null);
            if (!cursor.isAfterLast()) {
                cursor.moveToFirst();
                Integer InsID = cursor.getInt(cursor.getColumnIndex(INSPECTION_ID));
                Integer num = cursor.getInt(cursor.getColumnIndex(INSPECTION_NUMBER));
                Integer OrdID = cursor.getInt(cursor.getColumnIndex(INSPECTION_ORDERID));
                Date dt = new Date(cursor.getLong(cursor.getColumnIndex(INSPECTION_DATE)));
                String model = cursor.getString(cursor.getColumnIndex(INSPECTION_MODEL));
                String vin = cursor.getString(cursor.getColumnIndex(INSPECTION_VIN));
                Integer isSynced = cursor.getInt(cursor.getColumnIndex(INSPECTION_ISSYNC));
                Integer Coun = cursor.getInt(cursor.getColumnIndex("coun"));

                item = new Inspection(InsID, num, OrdID, isSynced, dt, model, vin);
                item.setPhotoCo(Coun);

            }
            cursor.close();
            database.close();
        }
        return item;
    }

    public Inspection insInspection(Inspection inspection) {
        int inspectionID;

        ContentValues contentValues = new ContentValues();
        contentValues.put(INSPECTION_NUMBER, inspection.getNumber());
        if (inspection.getOrderid() > 0) { contentValues.put(INSPECTION_ORDERID, inspection.getOrderid()); }
        contentValues.put(INSPECTION_ISSYNC, inspection.getIssync());
        if (inspection.getDate() != null) { contentValues.put(INSPECTION_DATE, inspection.getDate().getTime()); }
        if (inspection.getModel() != null) { contentValues.put(INSPECTION_MODEL, inspection.getModel()); }
        if (inspection.getVin() != null) { contentValues.put(INSPECTION_VIN, inspection.getVin()); }

        SQLiteDatabase database = this.getWritableDatabase();
        Long Inspect = database.insert(INSPECTION, null, contentValues);
        database.close();

        inspectionID =  Inspect != null ? Inspect.intValue() : 0;

        if (inspectionID > 0) {
            return getInspection(inspectionID);
        } else {
            return null;
        }
    }

    public Photo insPhoto(Photo photo) {
        int photoID;

        ContentValues contentValues = new ContentValues();
        contentValues.put(PHOTO_PATH, photo.getPath());
        contentValues.put(PHOTO_NAME, photo.getName());
        contentValues.put(PHOTO_INSPECTION, photo.getInspectionid());
        contentValues.put(PHOTO_ISSYNC, photo.getIssync());

        SQLiteDatabase database = this.getWritableDatabase();
        Long photoID_db = database.insert(PHOTO, null, contentValues);
        database.close();

        photoID =  photoID_db != null ? photoID_db.intValue() : null;

        if (photoID > 0) {
            return getPhoto(photoID);
        } else {
            return null;
        }
    }

    private Photo getPhoto(int photoID) {
        Photo item;
        item = null;
        if (photoID > 0) {
            SQLiteDatabase database = this.getReadableDatabase();
            String SQL = "SELECT " + PHOTO_ID + ", " + PHOTO_PATH + ", " + PHOTO_NAME + ", "
                    + PHOTO_INSPECTION + ", " + PHOTO_ISSYNC
                    + " FROM " + PHOTO
                    + " WHERE " + PHOTO_ID + " = " + Integer.toString(photoID);
            Cursor cursor = database.rawQuery(SQL, null);
            if (!cursor.isAfterLast()) {
                cursor.moveToFirst();
                Integer _id = cursor.getInt(cursor.getColumnIndex(PHOTO_ID));
                String path = cursor.getString(cursor.getColumnIndex(PHOTO_PATH));
                String name = cursor.getString(cursor.getColumnIndex(PHOTO_NAME));
                Integer isSynced = cursor.getInt(cursor.getColumnIndex(PHOTO_ISSYNC));
                Integer inspectionId = cursor.getInt(cursor.getColumnIndex(PHOTO_INSPECTION));

                item = new Photo(_id, path, name, isSynced, inspectionId);
            }
            cursor.close();
            database.close();
        }
        return item;

    }


    public ArrayList<Photo> getPhotoList(int inspectionID)  {
        Photo item;
        ArrayList<Photo> photoList = new ArrayList<Photo>();

        SQLiteDatabase database = this.getReadableDatabase();
        String SQL = "SELECT " + PHOTO_ID + ", " + PHOTO_PATH + ", " + PHOTO_NAME + ", "
                + PHOTO_INSPECTION + ", " + PHOTO_ISSYNC
                + " FROM " + PHOTO
                + " WHERE " + PHOTO_INSPECTION + " = " + Integer.toString(inspectionID);
        Cursor cursor = database.rawQuery(SQL, null);
        if (!cursor.isAfterLast()) {
            while (cursor.moveToNext()) {
                Integer _id = cursor.getInt(cursor.getColumnIndex(PHOTO_ID));
                String path = cursor.getString(cursor.getColumnIndex(PHOTO_PATH));
                String name = cursor.getString(cursor.getColumnIndex(PHOTO_NAME));
                Integer isSynced = cursor.getInt(cursor.getColumnIndex(PHOTO_ISSYNC));
                Integer inspectionId = cursor.getInt(cursor.getColumnIndex(PHOTO_INSPECTION));

                item = new Photo(_id, path, name, isSynced, inspectionId);
                photoList.add(item);
            }
        }
        cursor.close();
        database.close();

        return photoList;
    }

    public Inspection updInspectionOrder(Order order) {
        ContentValues contentValues = new ContentValues();

        if (order.getOrderid() > 0) { contentValues.put(INSPECTION_ORDERID, order.getOrderid()); } else { contentValues.putNull(INSPECTION_ORDERID); }
        if (order.getDate() != null) { contentValues.put(INSPECTION_DATE, order.getDate().getTime()); } else { contentValues.putNull(INSPECTION_DATE); }
        if (order.getModel() != null) { contentValues.put(INSPECTION_MODEL, order.getModel()); } else { contentValues.putNull(INSPECTION_MODEL); }
        if (order.getVin() != null) { contentValues.put(INSPECTION_VIN, order.getVin()); } else { contentValues.putNull(INSPECTION_VIN); }
        String where = INSPECTION_ID + "=" + String.valueOf(order.getInspectionID());

        try {
            SQLiteDatabase database = this.getWritableDatabase();
            database.update(INSPECTION, contentValues, where, null);
            database.close();
            return getInspection(order.getInspectionID());
        } catch(Exception e) {
            return null;
        }
    }

    public Inspection updInspection(Inspection inspection) {
        ContentValues contentValues = new ContentValues();
        if (inspection.getNumber() > 0) { contentValues.put(INSPECTION_NUMBER, inspection.getNumber()); } else { contentValues.putNull(INSPECTION_NUMBER); }
        if (inspection.getOrderid() > 0) { contentValues.put(INSPECTION_ORDERID, inspection.getOrderid()); } else { contentValues.putNull(INSPECTION_ORDERID); }
        if (inspection.getDate() != null) { contentValues.put(INSPECTION_DATE, inspection.getDate().getTime()); } else { contentValues.putNull(INSPECTION_DATE); }
        if (inspection.getModel() != null) { contentValues.put(INSPECTION_MODEL, inspection.getModel()); } else { contentValues.putNull(INSPECTION_MODEL); }
        if (inspection.getVin() != null) { contentValues.put(INSPECTION_VIN, inspection.getVin()); } else { contentValues.putNull(INSPECTION_VIN); }
        String where = INSPECTION_ID + "=" + String.valueOf(inspection.get_inspectionid());

        try {
            SQLiteDatabase database = this.getWritableDatabase();
            database.update(INSPECTION, contentValues, where, null);
            database.close();
            return getInspection(inspection.get_inspectionid());
        } catch(Exception e) {
            return null;
        }
    }

    public void updPhotoSync(String[] strings) {
        Photo item = null;

        ContentValues contentValues = new ContentValues();
        contentValues.put(PHOTO_ISSYNC, 1);

        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(INSPECTION_ISSYNC, 1);

        try {
            SQLiteDatabase database = this.getWritableDatabase();
            String SQL = "SELECT " + PHOTO_ID + ", " + PHOTO_PATH + ", " + PHOTO_NAME + ", "
                    + PHOTO_INSPECTION + ", " + PHOTO_ISSYNC
                    + " FROM " + PHOTO
                    + " WHERE " + PHOTO_NAME + " = ?";
            Cursor cursor = database.rawQuery(SQL, strings);
            if (!cursor.isAfterLast()) {
                while (cursor.moveToNext()) {
                    Integer _id = cursor.getInt(cursor.getColumnIndex(PHOTO_ID));
                    String path = cursor.getString(cursor.getColumnIndex(PHOTO_PATH));
                    String name = cursor.getString(cursor.getColumnIndex(PHOTO_NAME));
                    Integer isSynced = cursor.getInt(cursor.getColumnIndex(PHOTO_ISSYNC));
                    Integer inspectionId = cursor.getInt(cursor.getColumnIndex(PHOTO_INSPECTION));

                    item = new Photo(_id, path, name, isSynced, inspectionId);
                }
            }

            if (item != null) {
                try {
                    database.beginTransaction();
                    database.update(PHOTO, contentValues, PHOTO_ID + "=" + String.valueOf(item.get_photoid()), null);
                    database.update(INSPECTION, contentValues2, INSPECTION_ID + "=" + String.valueOf(item.getInspectionid()), null);
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
            }
            database.close();
        } catch (Exception e) {
            Log.e(TAGDB,"updPhotoSync error: "+e.getMessage());
        }

    }

    public boolean delPhoto(int photoId) {
        boolean result;
        SQLiteDatabase database = this.getWritableDatabase();
        result = database.delete(PHOTO, PHOTO_ID + "=" + String.valueOf(photoId), null) > 0;
        database.close();
        return result;
    }
}
