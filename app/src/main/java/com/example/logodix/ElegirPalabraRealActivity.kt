package com.example.logodix

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log

class ElegirPalabraRealActivity : AppCompatActivity() {

    //variable para conexion de palabrasDAO
    private lateinit var palabrasDAO: PalabrasDAO
    private lateinit var puntuacionesDAO: PuntuacionesDAO

    // componentes de la interfaz
    private lateinit var txtOpcion1: TextView
    private lateinit var txtOpcion2: TextView
    private lateinit var btnSiguiente: Button
    private lateinit var txtPuntuacion: TextView
    private lateinit var btnAtras: Button

    // alamacenamos las parejas no usadas
    private var pares: MutableList<PalabrasDAO.ParPalabra> = mutableListOf()
    // Guardamos la pareja de palabra real/falsa

    private var parActual: PalabrasDAO.ParPalabra? = null


    // Para saber si el usuario ya respondió la pregunta actual
    private var haRespondido = false

    //declaracion de la variable puntuacion
    private var puntuacion= 0

    //variable para integrar la puntuacion de BD
    private var registroCreado= false
    //id de la actividad 2
    private val actividadId= 1
    private var idUsuario=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_elegir_palabra_real)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Instanciar DBHelper y DAO
        val dbHelper = DBHelper(this)
        palabrasDAO = PalabrasDAO(dbHelper)
        puntuacionesDAO= PuntuacionesDAO(this)


        // Solo insertar pseudopalabras si la tabla está vacía
        if (palabrasDAO.obtenerTodasPseudopalabras().isEmpty()) {
            palabrasDAO.insertarPseudopalabrasPredefinidas()
        } else {
            palabrasDAO.resetearPseudopalabras()
        }

        // Referencias de los componentes
        txtOpcion1 = findViewById(R.id.txtOpcion1)
        txtOpcion2 = findViewById(R.id.txtOpcion2)
        btnSiguiente = findViewById(R.id.btnSiguiente)
        txtPuntuacion= findViewById(R.id.txtPuntuacion)
        btnAtras = findViewById(R.id.btnAtras)

        //recuperar el idUsuario con un intent, en su defecto el valor sera 0
        idUsuario=intent.getIntExtra("ID_USUARIO",0)



        //mostrar puntuacion inicial
        actualizarPuntuacionUI()

        // Listeners a las opciones
        txtOpcion1.setOnClickListener { verificarOpcion(txtOpcion1.text.toString(), txtOpcion1) }
        txtOpcion2.setOnClickListener { verificarOpcion(txtOpcion2.text.toString(), txtOpcion2) }

        // Botón Siguiente
        btnSiguiente.setOnClickListener {

            onClickSiguiente()
        }

        // Botón Atrás
        btnAtras.setOnClickListener {
            finish()
        }


        // cargar palabras no usadas y barajarlas

        pares= palabrasDAO.obtenerPseudopalabrasSinUsar("facil").toMutableList()
        pares.shuffle()
        // Mostrar la primera pareja
        if (pares.isEmpty()){
            mostrarResultadosFinales()
        }else{
            mostrarNuevoPar()
        }

    }

    /**
     * Obtiene un nuevo par aleatorio del nivel "facil" (ajusta si deseas otro nivel)
     * y lo muestra en pantalla con posiciones mezcladas.
     */
    private fun mostrarNuevoPar() {
        haRespondido = false

        // Restablecer colores
        txtOpcion1.setTextColor(ContextCompat.getColor(this, R.color.negroTexto))
        txtOpcion2.setTextColor(ContextCompat.getColor(this, R.color.negroTexto))

        // Verifica que haya pares disponibles
        if (pares.isEmpty()) {
            mostrarResultadosFinales()
            return
        }

        // Obtiene la siguiente pareja de palabras
        parActual = pares.removeAt(0)

        if (parActual == null) {
            Toast.makeText(this, "Error: No hay más palabras disponibles.", Toast.LENGTH_SHORT).show()
            mostrarResultadosFinales()
            return
        }

        // Mezcla la posición de la palabra real y falsa
        if (Math.random() < 0.5) {
            txtOpcion1.text = parActual!!.palabraReal
            txtOpcion2.text = parActual!!.palabraFalsa
        } else {
            txtOpcion1.text = parActual!!.palabraFalsa
            txtOpcion2.text = parActual!!.palabraReal
        }
    }


    /**
     * Cuando el usuario toca una opción, chequeamos si es la real o no,
     * y coloreamos el TextView en consecuencia.
     */
    private fun verificarOpcion(textoPulsado: String, textViewSeleccionado: TextView) {
        if (haRespondido) {
            Toast.makeText(this, "Ya has respondido. Pulsa Siguiente.", Toast.LENGTH_SHORT).show()
            return
        }

        if (parActual == null) {
            Toast.makeText(this, "Error: No hay palabra activa.", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar si la palabra seleccionada es la correcta
        val acierto = (textoPulsado == parActual!!.palabraReal)
        if (acierto) {
            textViewSeleccionado.setTextColor(ContextCompat.getColor(this, R.color.verdeCorrecto))
            Toast.makeText(this, "¡Correcto!", Toast.LENGTH_SHORT).show()
            puntuacion += 10
        } else {
            textViewSeleccionado.setTextColor(ContextCompat.getColor(this, R.color.rojoError))
            Toast.makeText(this, "Incorrecto, la palabra real era: ${parActual!!.palabraReal}", Toast.LENGTH_LONG).show()
            puntuacion = if (puntuacion >= 5) puntuacion - 5 else 0
        }

        haRespondido = true
        actualizarPuntuacionUI()
        actualizarOInsertarPuntuacion()

        // Marcar la palabra como usada
        parActual?.let { palabrasDAO.marcarPseudopalabraUsada(it.id) }
    }


    private fun actualizarPuntuacionUI(){
        txtPuntuacion.text = "Puntos: $puntuacion"
    }
    /**
     * Al pulsar "Siguiente":
     * - Si no se ha respondido todavía, avisamos.
     * - Si ya se respondió, mostramos un nuevo par.
     */
    private fun onClickSiguiente() {
        if (!haRespondido) {
            Toast.makeText(this, "Responde primero antes de pasar a la siguiente", Toast.LENGTH_SHORT).show()
            return
        }
        // Ya respondió, entonces cargamos una nueva pareja
        mostrarNuevoPar()
    }
    private fun actualizarOInsertarPuntuacion() {
        // Consulta si ya existe una puntuación para este usuario y actividad
        val puntuacionExistente = puntuacionesDAO.obtenerPuntuacionUsuarioActividad(idUsuario, actividadId)
        Log.d("sonia elegir palabra", idUsuario.toString())

        if (puntuacionExistente == null) {
            // No existe registro: lo insertamos
            val insercion = puntuacionesDAO.insertarPuntuacion(idUsuario, actividadId, puntuacion)
            if (!insercion) {
                Toast.makeText(this, "Error al guardar la puntuación", Toast.LENGTH_SHORT).show()
            }
        } else {
            //lo actualizamos la puntuacion reemplazandola por la nueva puntuacion
            val nuevaPuntuacion= puntuacionExistente + puntuacion
            puntuacionesDAO.actualizarPuntuacion(idUsuario, actividadId,puntuacion)
        }
    }
    private fun mostrarResultadosFinales() {
        // Aquí quiero insertar puntucaion final
        // Ocultar elementos de juego
        txtOpcion1.visibility = View.GONE
        txtOpcion2.visibility = View.GONE
        btnSiguiente.visibility = View.GONE

        // Mostrar la puntuación final (se puede usar txtPuntuacion)
        txtPuntuacion.text = "Puntuación final: $puntuacion"

        // Cambiar el texto y funcionalidad del botón "Atras" para que regrese a Home
        btnAtras.text = "Volver a menu principal"
        btnAtras.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}
