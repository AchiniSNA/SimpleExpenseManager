package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


public class SQLiteDB extends SQLiteOpenHelper {
    public static final String DATABASE = "200004U";
    public static final int VERSION = 1;
    public static final String TABLE1 = "Accounts";
    public static final String TABLE2 = "Transactions";
    public static final String ACCOUNT_NUMBER="acc_no";
    public static final String BANK_NAME="bank_name";
    public static final String ACCOUNT_HOLDER_NAME="account_holder_name";
    public static final String BALANCE= "balance";
    public static final String DATE="date";
    public static final String EXPENSE_TYPE="expense_type";
    public static final String AMOUNT="amount";


    public SQLiteDB(@Nullable Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase ) {
        String query1 = "CREATE TABLE "+TABLE1+" " +
                "("
                +ACCOUNT_NUMBER +" TEXT PRIMARY KEY, "
                +BANK_NAME + " TEXT NOT NULL, "
                +ACCOUNT_HOLDER_NAME  + " TEXT NOT NULL, "
                +BALANCE+" DOUBLE NOT NULL CHECK ("+ BALANCE+" > 0)"+
                ");";
        sqLiteDatabase.execSQL(query1);



        String query2 = "CREATE TABLE "+TABLE2+" " +
                "("
                +DATE + " TEXT NOT NULL, "
                +ACCOUNT_NUMBER +" TEXT NOT NULL, "
                +EXPENSE_TYPE + " TEXT NOT NULL, "
                +AMOUNT+" DOUBLE NOT NULL CHECK ("+ AMOUNT+" > 0),"
                + " FOREIGN KEY ("+ACCOUNT_NUMBER+") REFERENCES "+TABLE1+"("+ACCOUNT_NUMBER+")ON DELETE CASCADE\n" +
                "ON UPDATE CASCADE);";
        sqLiteDatabase.execSQL(query2);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase , int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + TABLE1 + "'");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + TABLE2 + "'");
        onCreate(sqLiteDatabase );
    }


}
