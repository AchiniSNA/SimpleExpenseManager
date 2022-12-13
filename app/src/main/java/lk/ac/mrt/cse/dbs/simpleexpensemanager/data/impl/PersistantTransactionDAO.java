package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.SQLiteDB;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistantTransactionDAO implements TransactionDAO  {
    private SQLiteDB sqLiteDB ;
    private DateFormat format;
    public PersistantTransactionDAO (SQLiteDB sqLiteDB ){
        this.sqLiteDB = sqLiteDB ;
        this.format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }

    @Override
    public void logTransaction(Date date, String acc_no, ExpenseType expense_type, double amount) {
        SQLiteDatabase sqLiteDatabase = sqLiteDB.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteDB.DATE, this.format.format(date));
        contentValues.put(SQLiteDB.ACCOUNT_NUMBER, acc_no);
        contentValues.put(SQLiteDB.EXPENSE_TYPE, String.valueOf(expense_type));
        contentValues.put(SQLiteDB.AMOUNT, amount);
        sqLiteDatabase.insert(SQLiteDB.TABLE2, null, contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase sqLiteDatabase  = sqLiteDB.getReadableDatabase();
        String query1 = "SELECT * FROM " + SQLiteDB.TABLE2 + " ORDER BY " + SQLiteDB.DATE+ " DESC ";
        Cursor cursor = sqLiteDatabase.rawQuery(query1,null);
        ArrayList<Transaction> transactions = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                Transaction transaction= new Transaction();
                try {
                    transaction.setDate(this.format.parse(cursor.getString(0)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                transaction.setAccountNo(cursor.getString(1));
                transaction.setExpenseType(ExpenseType.valueOf(cursor.getString(2).toUpperCase()));
                transaction.setAmount(cursor.getDouble(3));
                transactions.add(transaction);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase sqLiteDatabase = sqLiteDB.getReadableDatabase() ;
        Cursor cursor = sqLiteDatabase.rawQuery( "SELECT * FROM " + SQLiteDB.TABLE2 + " ORDER BY " + SQLiteDB.DATE + " DESC " +
                        " LIMIT ?;" , new String[]{Integer.toString(limit)});
        ArrayList<Transaction> transactions = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                Transaction transaction= new Transaction();
                try {
                    transaction.setDate(this.format.parse(cursor.getString(0)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                transaction.setAccountNo(cursor.getString(1));
                transaction.setExpenseType(ExpenseType.valueOf(cursor.getString(2).toUpperCase()));
                transaction.setAmount(cursor.getDouble(3));
                transactions.add(transaction);

            }while (cursor.moveToNext());
        }
        cursor.close();
        return transactions;
    }
}
