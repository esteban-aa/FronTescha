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

    @Query("DELETE FROM usuario")
    void eliminarTodos();

    // Buscar por identificador
    @Query("SELECT * FROM usuario WHERE identificador = :identificador LIMIT 1")
    Usuario buscarPorIdentificador(String identificador);

    // Buscar por identificador y rol
    @Query("SELECT * FROM usuario WHERE identificador = :identificador AND rol = :rol LIMIT 1")
    Usuario buscarPorIdentificadorYRol(String identificador, String rol);

    // Buscar por ID del backend (opcional)
    @Query("SELECT * FROM usuario WHERE idBackend = :idBackend LIMIT 1")
    Usuario buscarPorIdBackend(String idBackend);

    // Limpiar todos los usuarios
    @Query("DELETE FROM usuario")
    void limpiarUsuarios();
}
