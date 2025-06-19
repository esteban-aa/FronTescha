package com.example.teschachatbot_f.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;


import com.example.teschachatbot_f.models.Usuario;

import java.util.List;

@Dao
public interface UsuarioDao {

    @Query("SELECT * FROM usuario")
    List<Usuario> obtenerTodos();

    @Insert
    void insertar(Usuario usuario);

    @Update
    void actualizar(Usuario usuario);

    @Delete
    void eliminar(Usuario usuario);

    // 🔽 Este es el que te falta
    @Query("DELETE FROM usuario")
    void eliminarTodos();
}
