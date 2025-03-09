package com.example.logodix

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar la base de datos
        val usuarioDAO=UsuarioDAO(this)
        dbHelper = DBHelper(this)

        // Referencias a de los datos y el botón
        val txtCorreo: EditText = findViewById(R.id.txtCorreo)
        val txtPassword: EditText = findViewById(R.id.txtPassword)
        val btnIniciaSesion: Button = findViewById(R.id.btnIniciaSesion)
        val btnRegistro: Button = findViewById(R.id.btnRegistro)

        // Evento para iniciar sesión
        btnIniciaSesion.setOnClickListener {
            val correo = txtCorreo.text.toString().trim()
            val password = txtPassword.text.toString().trim()
            Log.d("sonia LOGIN", correo)

            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Verificar usuario en la base de datos SQLite
                val isValid = usuarioDAO.verificarUsuario(correo, password)
                if (isValid) {
                    val nombre = usuarioDAO.obtenerNombrePorCorreo(correo)
                    val idUsuario= usuarioDAO.obtenerIdPorCorreo(correo)?: 0

                    val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    with(sharedPreferences.edit()){
                        putString("correoUsuario", correo)
                        putString("nombreUsuario", nombre)
                        putInt("ID_USUARIO", idUsuario)
                        apply()
                    }


                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("correoUsuario", correo)
                    intent.putExtra("nombreUsuario", nombre)


                    intent.putExtra("ID_USUARIO", idUsuario)
                    startActivity(intent)
                    finish()  // Cierra la actividad de login
                } else {
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Acción para ir al Registro
        btnRegistro.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }
}
