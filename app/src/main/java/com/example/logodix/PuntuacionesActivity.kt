package com.example.logodix

import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class PuntuacionesActivity : AppCompatActivity() {

    private lateinit var tablaGlobal: TableLayout
    private lateinit var txtActividadGlobal: TextView
    private lateinit var btnAtras: Button
    private lateinit var btnRefrescar: Button
    private lateinit var puntuaciones: PuntuacionesDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_puntuaciones)

        // Vincular componentes del layout global
        tablaGlobal = findViewById(R.id.tablaGlobal)
        txtActividadGlobal = findViewById(R.id.txtActividadGlobal)
        btnAtras = findViewById(R.id.btnAtras)
        btnRefrescar = findViewById(R.id.btnRefrescar)

        // Inicializar DAO
        puntuaciones = PuntuacionesDAO(this)

        // Cargar la tabla global de puntuaciones
        refrescarPuntuacionesGlobales()

        // Botón Refrescar
        btnRefrescar.setOnClickListener {
            refrescarPuntuacionesGlobales()
        }

        // Botón Atras
        btnAtras.setOnClickListener {
            finish()
        }
    }

    private fun refrescarPuntuacionesGlobales() {
        // Limpiar filas existentes (dejando el encabezado, que está en la fila 0)
        while (tablaGlobal.childCount > 1) {
            tablaGlobal.removeViewAt(1)
        }
        val listaGlobal = puntuaciones.obtenerPuntuacionesGlobales()
        if (listaGlobal.isEmpty()) {
            txtActividadGlobal.text = "Sin puntuaciones globales"
        } else {
            txtActividadGlobal.text = "Puntuaciones globales"
            // Agrupar las puntuaciones por usuario
            val puntuacionesPorUsuario = listaGlobal.groupBy { it.usuario }
            // Para cada usuario, concatenar los puntajes de cada actividad
            for ((usuario, lista) in puntuacionesPorUsuario) {
                val detalleActividades = lista.joinToString(" | ") { "${it.actividad}: ${it.puntos}" }
                val fila = TableRow(this)
                val vUsuario = TextView(this).apply { text = usuario }
                val vDetalle = TextView(this).apply { text = detalleActividades }
                fila.addView(vUsuario)
                fila.addView(vDetalle)
                tablaGlobal.addView(fila)
            }
        }
    }
}












