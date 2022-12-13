package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.SQLiteDB;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistantAccountDAO implements AccountDAO {
    private SQLiteDB sqLiteDB ;

    public PersistantAccountDAO (SQLiteDB sqLiteDB ) {
        this.sqLiteDB = sqLiteDB ;
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase sqLiteDatabase = sqLiteDB.getReadableDatabase();
        String query = "SELECT "+ SQLiteDB.ACCOUNT_NUMBER + " FROM " + SQLiteDB.TABLE1 ;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        ArrayList<String> acc_numbers = new ArrayList<>() ;

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            acc_numbers.add(cursor.getString(0));
        }
        cursor.close();

        return acc_numbers;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = sqLiteDB.getReadableDatabase();
        String query = "SELECT * FROM "+SQLiteDB.TABLE1;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);

        if(cursor.moveToFirst()){

            do {
                Account account = new Account() ;
                account.setAccountNo(cursor.getString(0));
                account.setBankName(cursor.getString(1));
                account.setAccountHolderName(cursor.getString(2));
                account.setBalance(cursor.getDouble(3));
                accounts.add(account);
            }
            while (cursor.moveToNext());
        }
        cursor.close();

        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = sqLiteDB.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + SQLiteDB.TABLE1 + " WHERE " + SQLiteDB.ACCOUNT_NUMBER + "=?", new String[] {accountNo});
        Account account ;

        if (cursor != null && cursor.moveToFirst()) {
            account = new Account();
            account.setAccountNo(cursor.getString(0));
            account.setBankName(cursor.getString(1));
            account.setAccountHolderName(cursor.getString(2));
            account.setBalance(cursor.getDouble(3));

        }
        else {
            throw new InvalidAccountException("Account "+accountNo+" is Invalid");
        }
        cursor.close();

        return account;
    }



    @Override
    public void addAccount(Account account) {
        SQLiteDatabase sqLiteDatabase = sqLiteDB.getWritableDatabase() ;
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteDB.ACCOUNT_NUMBER,account.getAccountNo());
        contentValues.put(SQLiteDB.BANK_NAME,account.getBankName());
        contentValues.put(SQLiteDB.ACCOUNT_HOLDER_NAME,account.getAccountHolderName());
        contentValues.put(SQLiteDB.BALANCE,account.getBalance());
        sqLiteDatabase.insert(SQLiteDB.TABLE1 , null, contentValues );
        sqLiteDatabase.close();


    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = sqLiteDB.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + SQLiteDB.TABLE1 + " WHERE " + SQLiteDB .ACCOUNT_NUMBER + "=?;"
                , new String[]{accountNo});

        if (cursor.moveToFirst()) {
            sqLiteDatabase.delete( SQLiteDB.TABLE1, SQLiteDB.ACCOUNT_NUMBER + " = ?", new String[]{accountNo});
        }
        else {
            throw new InvalidAccountException("Account "+accountNo+" is Invalid");
        }
        cursor.close();
    }


    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = sqLiteDB .getWritableDatabase();
        Account account = this.getAccount(accountNo);

        if (account != null) {

            switch (expenseType) {
                case EXPENSE:
                    account.setBalance(account.getBalance() - amount);
                    break;
                case INCOME:
                    account.setBalance(account.getBalance() + amount);
                    break;
            }

            sqLiteDatabase.execSQL(
                    "UPDATE " + SQLiteDB.TABLE1 +
                            " SET " + SQLiteDB.BALANCE + " = ?" +
                            " WHERE " + SQLiteDB.ACCOUNT_NUMBER + " = ?",
                    new String[]{Double.toString(account.getBalance()), accountNo});

        } else {
            throw new InvalidAccountException("Account "+accountNo+" is Invalid");
        }

    }

}
