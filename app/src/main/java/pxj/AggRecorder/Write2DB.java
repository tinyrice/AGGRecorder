package pxj.AggRecorder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rice on 4/14/2015.
 */
public class Write2DB extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    public Write2DB (Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }

    public  Write2DB(Context context, String name,int version) {
        this(context, name, null, VERSION);
    }

    public  Write2DB(Context context, String name){
        this(context,name,VERSION);

    }



    @Override
    public void onCreate (SQLiteDatabase db){

    }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion){

    }
}
