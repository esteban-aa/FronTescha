package com.example.teschachatbot_f.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "usuarios.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Solo se usa si necesitas crear la tabla desde cero
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "role TEXT, " +
                "identifier TEXT, " +
                "password TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        onCreate(db);
    }

    // Método para buscar la contraseña
    public String obtenerContrasena(String rol, String identificador) {
        SQLiteDatabase db = this.getReadableDatabase();
        String contrasena = null;

        Cursor cursor = db.rawQuery(
                "SELECT password FROM usuarios WHERE role = ? AND identifier = ? LIMIT 1",
                new String[]{rol, identificador}
        );

        if (cursor.moveToFirst()) {
            contrasena = cursor.getString(0);
        }

        cursor.close();
        db.close();

        return contrasena;
    }
}
