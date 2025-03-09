package com.example.logodix

import android.content.ContentValues

import android.database.Cursor

class PalabrasDAO(private val dbHelper: DBHelper) {


    // Método para insertar una palabra en la base de datos
    fun insertarPalabra(palabraOriginal: String, nivel: String): Boolean {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("palabra_original", palabraOriginal)
            put("nivel", nivel)
        }

        return try {
            db.insert("palabras", null, valores) > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    // Método para obtener una palabra aleatoria de un nivel
    fun obtenerPalabraAleatoria(nivel: String): String? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT palabra_original FROM palabras WHERE nivel = ? ORDER BY RANDOM() LIMIT 1",
            arrayOf(nivel)
        )
        val palabra= if(cursor.moveToFirst()) cursor.getString(0) else null
        cursor.close()
        db.close()
        return palabra
    }

    // Método para desordenar una palabra
    fun desordenarPalabra(palabra: String): String {
        val listaLetras = palabra.toCharArray().toList()
        val letrasDesordenadas = listaLetras.shuffled()
        return String(letrasDesordenadas.toCharArray())
    }

    // Método para insertar palabras predefinidas en la base de datos
    fun insertarPalabrasPredefinidas() {
        val db = dbHelper.writableDatabase
        val palabras = listOf(
            Pair("cama", "facil"),
            Pair("gato", "facil"),
            Pair("rata", "facil"),
            Pair("mesa", "facil"),
            Pair("luna", "facil"),
            Pair("gafas", "medio"),
            Pair("perro", "medio"),
            Pair("poder", "medio"),
            Pair("mandar", "medio"),
            Pair("saltar", "medio"),
            Pair("trabajo", "dificil"),
            Pair("segundo", "dificil"),
            Pair("primera", "dificil"),
            Pair("informar", "dificil"),
            Pair("partido", "dificil"),

        )
        db.beginTransaction()

        try {
            for (palabra in palabras) {
                val valores = ContentValues().apply {
                    put("palabra_original", palabra.first)
                    put("nivel", palabra.second)
                }
                db.insert("palabras", null, valores)
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    // Actualizar una palabra
    fun actualizarPalabra(id: Int, nuevaPalabra: String, nuevoNivel: String): Boolean {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("palabra_original", nuevaPalabra)
            put("nivel", nuevoNivel)
        }
        val resultado = db.update("palabras", valores, "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado > 0
    }

    // Eliminar una palabra
    fun eliminarPalabra(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val resultado = db.delete("palabras", "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado > 0
    }

    // funcion para usar solo las parejas que no se han usado
    fun obtenerPalabrasSinUsar(): List<Pair<Int, String>> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT id, palabra_original FROM palabras WHERE usada = 0",
            null
        )
        val lista = mutableListOf<Pair<Int, String>>()
        while (cursor.moveToNext()) {
            // Se guarda el id junto con la palabra
            lista.add(Pair(cursor.getInt(0), cursor.getString(1)))
        }
        cursor.close()
        db.close()
        return lista
    }
    fun marcarPalabraUsada(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("usada", 1)
        }
        val resultado = db.update("palabras", valores, "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado > 0
    }
    //una vez usadas todas las palabras aqui se resetean para que puedan volver a usar la actividad
    fun resetearPalabras() {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("usada", 0)
        }
        db.update("palabras", valores, null, null)
        db.close()
    }





    //2 actividad
    fun insertarPseudopalabra(palabraReal: String, palabraFalsa: String, nivel: String): Boolean {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("palabra_real", palabraReal)
            put("palabra_falsa", palabraFalsa)
            put("nivel", nivel)
        }
        return try {
            db.insert("pseudopalabras", null, valores) > 0
        } finally {
            db.close()
        }
    }

    // Obtener una palabra real junto con su pseudopalabra

    data class ParPalabra(val id: Int, val palabraReal: String, val palabraFalsa: String)

    fun insertarPseudopalabrasPredefinidas() {
        val db = dbHelper.writableDatabase
        val pseudopalabras = listOf(
            Pair("cama", "cuma"),
            Pair("gato", "guto"),
            Pair("rata", "raca"),
            Pair("mesa", "mefa"),
            Pair("luna", "lura"),
            Pair("perro", "perto"),
            Pair("barco", "bargo"),
            Pair("trabajo", "travajo"),
            Pair("deporte", "deborte"),
            Pair("elefante", "elifando"),
            Pair("grande", "granbe"),
            Pair("almohada", "almoada"),
            Pair("segundo", "sequnbo"),
            Pair("primera", "qrimera"),
            Pair("informar", "infonmar"),
            Pair("partido", "partibo"),
            Pair("revista", "rebista"),
            Pair("mejores", "megores"),
            Pair("entregar", "entrejar"),
            Pair("baloncesto", "valoncesto"),
            Pair("estudiar", "estubiar"),
            Pair("estuche", "estucle"),
        )


        db.beginTransaction()
        try {
            for (par in pseudopalabras) {
                val valores = ContentValues().apply {
                    put("palabra_real", par.first)
                    put("palabra_falsa", par.second)
                    put("nivel", "facil") // Puedes cambiar los niveles según las palabras
                }
                db.insert("pseudopalabras", null, valores)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }
    // Actualizar una pseudopalabra
    fun actualizarPseudopalabra(id: Int, nuevaPalabraFalsa: String): Boolean {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("palabra_falsa", nuevaPalabraFalsa)
        }
        val resultado = db.update("pseudopalabras", valores, "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado > 0
    }



    // Eliminar una pseudopalabra
    fun eliminarPseudopalabra(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val resultado = db.delete("pseudopalabras", "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado > 0
    }

    fun obtenerTodasPalabras(): List<String> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT palabra_original FROM palabras ",null)
        val lista= mutableListOf<String>()
        while (cursor.moveToNext()){
            lista.add(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return lista
    }

    fun marcarPseudopalabraUsada(id:Int): Boolean{
        val db = dbHelper.writableDatabase
        val valores= ContentValues().apply {
            put("usada",1)
        }
        val resultado= db.update("pseudopalabras", valores, "id=?", arrayOf(id.toString()))
        db.close()
        return resultado >0
    }

    fun obtenerTodasPseudopalabras(): List<ParPalabra> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT id, palabra_real, palabra_falsa FROM pseudopalabras WHERE usada=0", null)
        val lista = mutableListOf<ParPalabra>()
        while (cursor.moveToNext()) {
            lista.add(
                ParPalabra(
                    id = cursor.getInt(0),
                    palabraReal = cursor.getString(1),
                    palabraFalsa = cursor.getString(2)
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }
    //comentario
    fun obtenerPseudopalabrasSinUsar(nivel: String): List<ParPalabra> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT id, palabra_real, palabra_falsa FROM pseudopalabras WHERE nivel = ? AND usada = 0 limit 2",
            arrayOf(nivel)
        )

        val lista = mutableListOf<ParPalabra>()
        while (cursor.moveToNext()) {
            lista.add(
                ParPalabra(
                    id = cursor.getInt(0),
                    palabraReal = cursor.getString(1),
                    palabraFalsa = cursor.getString(2)
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }

    //resetear pseudopalabras
    fun resetearPseudopalabras() {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("usada", 0)
        }
        db.update("pseudopalabras", valores, null, null)
        db.close()
    }





}









