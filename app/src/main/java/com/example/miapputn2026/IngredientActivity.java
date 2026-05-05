package com.example.miapputn2026;

import android.os.Bundle;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.miapputn2026.api.ApiClient;
import com.example.miapputn2026.models.Ingredient;

public class IngredientActivity extends AppCompatActivity {

    EditText txtIngID, txtIngName, txtIngCalories;
    Switch swIngEstado;
    TextView txtResultadoIng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingredient);

        // ajustar pantalla
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // conectar elementos
        txtIngID = findViewById(R.id.txtIngID);
        txtIngName = findViewById(R.id.txtIngName);
        txtIngCalories = findViewById(R.id.txtIngCalories);
        swIngEstado = findViewById(R.id.swIngEstado);
        txtResultadoIng = findViewById(R.id.txtResultadoIng);

        findViewById(R.id.btnAtras).setOnClickListener(v -> finish());

        // cambiar texto del switch
        swIngEstado.setText(swIngEstado.isChecked() ? "Activo" : "Inactivo");
        swIngEstado.setOnCheckedChangeListener((b, c) ->
                swIngEstado.setText(c ? "Activo" : "Inactivo")
        );

        // conectar botones
        findViewById(R.id.btnCrearIng).setOnClickListener(v -> crear());
        findViewById(R.id.btnLeerIng).setOnClickListener(v -> leer());
        findViewById(R.id.btnActualizarIng).setOnClickListener(v -> actualizar());
        findViewById(R.id.btnEliminarIng).setOnClickListener(v -> eliminar());
    }

    // CREAR
    private void crear() {
        if (txtIngName.getText().toString().isEmpty() ||
                txtIngCalories.getText().toString().isEmpty()) {
            txtResultadoIng.setText("INGRESE NOMBRE Y CALORIAS");
            return;
        }

        Ingredient i = obtenerIngrediente();

        ejecutar(() -> {
            Ingredient nuevo = ApiClient.createIngredient(i);

            runOnUiThread(() -> {
                txtResultadoIng.setText(
                        "SE CREO EL INGREDIENTE\n\n" +
                                "ID: " + nuevo.id + "\n" +
                                "NOMBRE: " + nuevo.nombre + "\n" +
                                "CALORIAS: " + nuevo.calorias + "\n" +
                                "ESTADO: " + (nuevo.estado ? "ACTIVO" : "INACTIVO")
                );

                txtIngID.setText(String.valueOf(nuevo.id));
            });
        });
    }

    // LEER
    private void leer() {
        if (txtIngID.getText().toString().isEmpty()) {
            txtResultadoIng.setText("INGRESE ID");
            return;
        }

        int id = Integer.parseInt(txtIngID.getText().toString());

        ejecutar(() -> {
            Ingredient i = ApiClient.getIngredient(id);

            runOnUiThread(() -> {
                txtIngName.setText(i.nombre);
                txtIngCalories.setText(String.valueOf(i.calorias));
                swIngEstado.setChecked(i.estado);

                txtResultadoIng.setText(
                        "INGREDIENTE ENCONTRADO\n\n" +
                                "ID: " + i.id + "\n" +
                                "NOMBRE: " + i.nombre + "\n" +
                                "CALORIAS: " + i.calorias + "\n" +
                                "ESTADO: " + (i.estado ? "ACTIVO" : "INACTIVO")
                );
            });
        });
    }

    // ACTUALIZAR
    private void actualizar() {
        if (txtIngID.getText().toString().isEmpty()) {
            txtResultadoIng.setText("INGRESE ID");
            return;
        }

        int id = Integer.parseInt(txtIngID.getText().toString());
        Ingredient i = obtenerIngrediente();
        i.id = id;

        ejecutar(() -> {
            ApiClient.updateIngredient(id, i);
            runOnUiThread(() -> txtResultadoIng.setText("SE ACTUALIZO EL INGREDIENTE"));
        });
    }

    // ELIMINAR
    private void eliminar() {
        if (txtIngID.getText().toString().isEmpty()) {
            txtResultadoIng.setText("INGRESE ID");
            return;
        }

        int id = Integer.parseInt(txtIngID.getText().toString());

        ejecutar(() -> {
            ApiClient.deleteIngredient(id);
            runOnUiThread(() -> {
                txtResultadoIng.setText("SE ELIMINO EL INGREDIENTE");
                limpiar();
            });
        });
    }

    // tomar datos del formulario
    private Ingredient obtenerIngrediente() {
        Ingredient i = new Ingredient();
        i.nombre = txtIngName.getText().toString();
        i.calorias = Double.parseDouble(txtIngCalories.getText().toString());
        i.estado = swIngEstado.isChecked();
        return i;
    }

    // hilo simple para consumir la API
    private interface TareaApi {
        void run() throws Exception;
    }

    private void ejecutar(TareaApi tarea) {
        new Thread(() -> {
            try {
                tarea.run();
            } catch (Exception e) {
                runOnUiThread(() -> txtResultadoIng.setText("ERROR: " + e.getMessage()));
            }
        }).start();
    }

    // limpiar campos
    private void limpiar() {
        txtIngID.setText("");
        txtIngName.setText("");
        txtIngCalories.setText("");
        swIngEstado.setChecked(true);
    }
}