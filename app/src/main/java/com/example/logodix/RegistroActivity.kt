package com.example.logodix

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicializar la base de datos
        dbHelper = DBHelper(this)
         val usuarioDAO= UsuarioDAO(this)

        // Referencias a los EditText y el Botón
        val txtNombre = findViewById<EditText>(R.id.txtRegistroNombre)
        val txtCorreo = findViewById<EditText>(R.id.txtRegistroCorreo)
        val txtPassword = findViewById<EditText>(R.id.txtRegistroPassword)
        val txtConfirmPassword = findViewById<EditText>(R.id.txtConfirmPassword)
        val btnCrearCuenta = findViewById<Button>(R.id.btnCreaCuenta)

        // Acción del botón para crear cuenta
        btnCrearCuenta.setOnClickListener {
            val nombre = txtNombre.text.toString().trim()
            val correo = txtCorreo.text.toString().trim()
            val password = txtPassword.text.toString().trim()
            val confirmPassword = txtConfirmPassword.text.toString().trim()

            // Validaciones
            if (correo.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                // Insertar usuario en la base de datos SQLite
                val success = usuarioDAO.insertarUsuario(nombre, correo, password)
                if (success) {
                    Toast.makeText(this, "Cuenta creada para: $nombre", Toast.LENGTH_SHORT).show()
                    //obtenemos el id de usuario
                    val idUsuario= usuarioDAO.obtenerIdPorCorreo(correo)?:0
                    // Redirigir a LoginActivity
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("ID_USUARIO", idUsuario)
                    startActivity(intent)
                    finish()  // Cierra la actividad de registro
                } else {
                    Toast.makeText(this, "Error al crear cuenta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
