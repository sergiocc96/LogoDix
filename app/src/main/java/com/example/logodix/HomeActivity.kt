package com.example.logodix

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Inicializar la base de datos
        dbHelper = DBHelper(this)

        // Obtener los datos pasados desde el login
        val correo = intent.getStringExtra("correoUsuario")
        val nombre = intent.getStringExtra("nombreUsuario")
        val idUsuario = intent.getIntExtra("ID_USUARIO", 0)


        // Mostrar mensaje de bienvenida
        val txtBienvenida = findViewById<TextView>(R.id.txtBienvenida)

        if (!nombre.isNullOrEmpty()) {
            // Establecer el texto del TextView con el nombre de usuario
            txtBienvenida.text = "Bienvenid@, $nombre"
        } else {
            txtBienvenida.text = "Bienvenido, Usuario"
        }
        // Mostrar mensaje de bienvenida
        if (!nombre.isNullOrEmpty()){
            Toast.makeText(this, "Bienvenido, $nombre", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Bienvenido, Usuario", Toast.LENGTH_LONG).show()
        }

        // Botón de cerrar  sesión
        val cerrar = findViewById<Button>(R.id.btnClosed)

        // Si el correo no es nulo, lo mostramos en un toast
        if (correo != null) {
            Toast.makeText(this, "Correo: $correo", Toast.LENGTH_LONG).show()
        }

        cerrar.setOnClickListener {
            cerrarSesion()
        }

        //botones de las actividades
        val formarPalabras = findViewById<Button>(R.id.btnFormPalabras)
        val elegirPalabra = findViewById<Button>(R.id.btnRealDiferente)
        val puntuaciones= findViewById<Button>(R.id.btnPuntuacion)

        // Acción del botón "Formar palabras"
        formarPalabras.setOnClickListener {
            val intent = Intent(this, FormarPalabrasActivity::class.java)
            intent.putExtra("ID_USUARIO", idUsuario)
            startActivity(intent)
        }

        // Acción del botón "Elige la palabra real"
        elegirPalabra.setOnClickListener {
            val intent = Intent(this, ElegirPalabraRealActivity::class.java)
            intent.putExtra("ID_USUARIO", idUsuario)
            startActivity(intent)
        }

        // Acción del botón "Puntuaciones"
        puntuaciones.setOnClickListener {
            val intent = Intent(this, PuntuacionesActivity::class.java)
            startActivity(intent)
        }
    }


    private fun cerrarSesion() {

        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Borra todos los datos guardados de sesión
        editor.apply()

        // Redirige a la pantalla de login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Permite volver atrás
        startActivity(intent)
        finish() // Cierra la actividad de home
    }
}
