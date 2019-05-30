package mondo.orderingsystem;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class OrderDataBase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "mondo.db";

    public static final String TABLE_NAME = "Currency";
    public static final String TABLE_NAME_order = "OrderTB";
    public static final String order_id = "order_id";
    public static final String employee_id = "employee_id";
    public static final String main_menu_id = "main_menu_id";
    public static final String main_menu_name = "main_menu_name";
    public static final String item_id = "item_id";
    public static final String item_name = "item_name";
    public static final String item_size = "item_size";
    public static final String item_size_name = "item_size_name";
    public static final String quantity = "quantity";
    public static final String instructions = "instructions";
    public static final String item_price = "item_price";
    public static final String restaurant_name = "restaurant_name";
    public static final String TAX = "TAX";


    public static final String TABLE_NAME_Addons = "Addons";
    public static final String addon_id = "addon_id";
    public static final String addon_name = "addon_name";
    public static final String addon_quantity = "addon_quantity";
    public static final String addon_price = "addon_price";
    public static final String order_id_fk = "order_id";
    public static final String addons_name = "addons_name";


    public OrderDataBase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME_order + " (order_id INTEGER PRIMARY KEY AUTOINCREMENT,employee_id INTEGER," +
                "main_menu_id INTEGER,main_menu_name TEXT,item_id INTEGER,item_name TEXT, item_size INTEGER, item_size_name TEXT," +
                " quantity INTEGER," +
                "instructions TEXT, item_price DOUBLE, restaurant_name TEXT, TAX DOUBLE)");

      //  db.execSQL("create table " + TABLE_NAME_order + " (id INTEGER ,currency_id INTEGER,currencies_name TEXT,sell DOUBLE,buy DOUBLE,isactive INTEGER,sort INTEGER)");
        db.execSQL("create table " + TABLE_NAME_Addons+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,order_id INTEGER,employee_id INTEGER,addon_id INTEGER,addon_name TEXT," +
                "addon_quantity INTEGER,addon_price DOUBLE,addons_name TEXT)");

        // db.execSQL("create table " + TABLE_NAME1 + " (phoneid INTEGER ,massage TEXT,date TEXT,send_rec INTEGER,FOREIGN KEY (phoneid) REFERENCES " + TABLE_NAME + " (phone))");
}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_order);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_Addons);

        onCreate(db);
    }

    public void deleteOrder(int order_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_Addons,"order_id="+order_id, null);
        db.delete(TABLE_NAME_order,"order_id="+order_id, null);
        db.close();
    }

    public void deleteAllOrders(int employee_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_Addons,"employee_id="+employee_id, null);
        db.delete(TABLE_NAME_order,"employee_id="+employee_id, null);
        db.close();
    }


    public long insert_Order(int employee_id, int main_menu_id, String main_menu_name,int item_id,
         String item_name, int item_size,  String item_size_name,int quantity, String instructions,
                             Double item_price, String restaurant_name, Double TAX){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues =new ContentValues();

        contentValues.put(this.employee_id, employee_id);
        contentValues.put(this.main_menu_id, main_menu_id);
        contentValues.put(this.main_menu_name, main_menu_name);
        contentValues.put(this.item_id, item_id);
        contentValues.put(this.item_name, item_name);
        contentValues.put(this.item_size, item_size);
        contentValues.put(this.item_size_name, item_size_name);
        contentValues.put(this.quantity, quantity);
        contentValues.put(this.instructions, instructions);
        contentValues.put(this.item_price, item_price);
        contentValues.put(this.restaurant_name, restaurant_name);
        contentValues.put(this.TAX, TAX);

        return db.insert(TABLE_NAME_order, null, contentValues);

    }

    public int getOrderCount(int employee_id) {
        String countQuery = "SELECT  * FROM " + TABLE_NAME_order+" WHERE employee_id="+employee_id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public void insert_Addons(int order_id_fk,int employee_id,int addon_id, String addon_name,int addon_quantity, Double addon_price, String addons_name){

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put(this.order_id_fk, order_id_fk);
        contentValues.put(this.employee_id, employee_id);
        contentValues.put(this.addon_id, addon_id);
        contentValues.put(this.addon_name, addon_name);
        contentValues.put(this.addon_quantity, addon_quantity);
        contentValues.put(this.addon_price, addon_price);
        contentValues.put(this.addons_name, addons_name);
        db.insert(TABLE_NAME_Addons, null, contentValues);

    }
    public Cursor get_All_Orders(int employee_id){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT * from "+TABLE_NAME_order +" WHERE employee_id =" + employee_id+" order by order_id desc",null);
        return res;
    }

    public Cursor get_All_Addons(int order_id){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT * from "+TABLE_NAME_Addons +" WHERE order_id =" + order_id,null);
        return res;
    }

}


