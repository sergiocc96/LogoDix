package com.example.logodix

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Definir la estructura de la base de datos y conexión
class DBHelper(context: Context) : SQLiteOpenHelper(context, "logodix.db", null, 3) {

    // Creación de las tablas: usuarios, actividades , puntuaciones y palabras
    override fun onCreate(db: SQLiteDatabase?) {
        // Crear la tabla de usuarios
        val creaTablaUsuarios = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                correo TEXT UNIQUE,
                password TEXT
            )
        """.trimIndent()
        db?.execSQL(creaTablaUsuarios)

        // Crear la tabla de actividades
        val creaTablaActividades = """
            CREATE TABLE IF NOT EXISTS actividades (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_actividad TEXT,
                nivel TEXT CHECK(nivel IN ('facil', 'medio', 'dificil'))
            )
        """.trimIndent()
        db?.execSQL(creaTablaActividades)

        // Crear la tabla de puntuaciones
        val creaTablaPuntuaciones = """
            CREATE TABLE IF NOT EXISTS puntuaciones (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER,
                id_actividad INTEGER,
                puntuacion INTEGER,
                fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(id_usuario) REFERENCES usuarios(id),
                FOREIGN KEY(id_actividad) REFERENCES actividades(id)
            )
        """.trimIndent()
        db?.execSQL(creaTablaPuntuaciones)

        // Crear la tabla de palabras
        val creaTablaPalabras = """
            CREATE TABLE IF NOT EXISTS palabras (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                palabra_original TEXT,
                nivel TEXT CHECK(nivel IN ('facil', 'medio', 'dificil')),
                usada INTEGER DEFAULT 0
            )
        """.trimIndent()
        db?.execSQL(creaTablaPalabras)
        //crear tabla de pseudopalabras
        val creaTablaPseudopalabras = """
    CREATE TABLE IF NOT EXISTS pseudopalabras (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        palabra_real TEXT,
        palabra_falsa TEXT,
        nivel TEXT CHECK(nivel IN ('facil', 'medio', 'dificil')),
        usada INTEGER DEFAULT 0
    )
""".trimIndent()
        db?.execSQL(creaTablaPseudopalabras)

    }


    // Si se realiza una actualización en la base de datos (update)
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Eliminar las tablas existentes si la base de datos se actualiza
        if(oldVersion<2){
            //se agrega columna usada si no existe
            db?.execSQL("ALTER TABLE pseudopalabras ADD COLUMN usada INTEGER DEFAULT 0")
        }
        db?.execSQL("DROP TABLE IF EXISTS usuarios")
        db?.execSQL("DROP TABLE IF EXISTS actividades")
        db?.execSQL("DROP TABLE IF EXISTS puntuaciones")
        db?.execSQL("DROP TABLE IF EXISTS palabras")
        db?.execSQL("DROP TABLE IF EXISTS pseudopalabras")

        // Crear las tablas de nuevo
        onCreate(db)
    }

}














