package com.example.teschachatbot_f.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.teschachatbot_f.models.Usuario;

@Database(entities = {Usuario.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    // ...
    public abstract UsuarioDao usuarioDao();
}
