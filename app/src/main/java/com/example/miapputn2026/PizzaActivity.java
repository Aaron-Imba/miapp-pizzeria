package com.example.miapputn2026;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.miapputn2026.api.ApiClient;
import com.example.miapputn2026.models.Ingredient;
import com.example.miapputn2026.models.Pizza;

import java.util.List;

public class PizzaActivity extends AppCompatActivity {

    EditText txtPizID, txtPizName, txtPizOrigin;
    Switch swPizEstado;
    TextView txtResultado;
    Spinner spIngredientes;

    int selectedIngredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pizza);

        // ajustar pantalla
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // conectar elementos
        txtPizID = findViewById(R.id.txtPizID);
        txtPizName = findViewById(R.id.txtPizName);
        txtPizOrigin = findViewById(R.id.txtPizOrigin);
        swPizEstado = findViewById(R.id.swPizEstado);
        txtResultado = findViewById(R.id.txtResultado);
        spIngredientes = findViewById(R.id.spIngredientes);

        findViewById(R.id.btnAtras).setOnClickListener(v -> finish());

        // cambiar texto del switch
        swPizEstado.setOnCheckedChangeListener((b, c) ->
                swPizEstado.setText(c ? "Activo" : "Inactivo")
        );

        // cargar ingredientes en spinner
        cargarIngredientes();

        // guardar ingrediente seleccionado
        spIngredientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                selectedIngredientId = ((Ingredient) p.getItemAtPosition(pos)).id;
            }
            public void onNothingSelected(AdapterView<?> p) {}
        });

        findViewById(R.id.btnAgregarIngrediente).setOnClickListener(v -> agregarIngrediente());
    }

    // CREAR
    public void cmdCrear_onClick(View v) {
        if (txtPizName.getText().toString().isEmpty()) {
            txtResultado.setText("INGRESE NOMBRE");
            return;
        }

        Pizza p = new Pizza();
        p.nombre = txtPizName.getText().toString();
        p.origen = txtPizOrigin.getText().toString();
        p.estado = swPizEstado.isChecked();

        ejecutar(() -> {
            Pizza nueva = ApiClient.createPizza(p);
            runOnUiThread(() -> {
                txtResultado.setText("SE CREO LA PIZZA");
                if (nueva != null) txtPizID.setText(String.valueOf(nueva.id));
            });
        });
    }

    // LEER
    public void cmdLeer_onClick(View v) {
        if (txtPizID.getText().toString().isEmpty()) {
            txtResultado.setText("INGRESE ID");
            return;
        }

        int id = Integer.parseInt(txtPizID.getText().toString());

        ejecutar(() -> {
            Pizza p = ApiClient.getPizza(id);
            String detalle = ApiClient.getIngredientsByPizza(id);

            runOnUiThread(() -> {
                txtPizName.setText(p.nombre);
                txtPizOrigin.setText(p.origen);
                swPizEstado.setChecked(p.estado);

                txtResultado.setText(
                        "PIZZA ENCONTRADA\n\n" +
                                "ID: " + p.id + "\n" +
                                "NOMBRE: " + p.nombre + "\n" +
                                "ORIGEN: " + p.origen + "\n" +
                                "ESTADO: " + (p.estado ? "ACTIVO" : "INACTIVO") + "\n\n" +
                                "INGREDIENTES:\n" +
                                detalle
                                        .replace("[", "")
                                        .replace("]", "")
                                        .replace("{", "")
                                        .replace("}", "")
                                        .replace("\"", "")
                                        .replace("pizId:", "\nPizza ID: ")
                                        .replace("ingId:", "Ingrediente ID: ")
                                        .replace("nombre:", "Nombre: ")
                                        .replace("calorias:", "Calorías: ")
                                        .replace("estado:", "Estado: ")
                                        .replace(",", "\n")
                );
            });
        });
    }

    // ACTUALIZAR
    public void cmdActualizar_onClick(View v) {
        if (txtPizID.getText().toString().isEmpty()) {
            txtResultado.setText("INGRESE ID");
            return;
        }

        Pizza p = new Pizza();
        p.id = Integer.parseInt(txtPizID.getText().toString());
        p.nombre = txtPizName.getText().toString();
        p.origen = txtPizOrigin.getText().toString();
        p.estado = swPizEstado.isChecked();

        ejecutar(() -> {
            ApiClient.updatePizza(p.id, p);
            runOnUiThread(() -> txtResultado.setText("SE ACTUALIZO"));
        });
    }

    // ELIMINAR
    public void cmdEliminar_onClick(View v) {
        if (txtPizID.getText().toString().isEmpty()) {
            txtResultado.setText("INGRESE ID");
            return;
        }

        int id = Integer.parseInt(txtPizID.getText().toString());

        ejecutar(() -> {
            ApiClient.deletePizza(id);
            runOnUiThread(() -> {
                txtResultado.setText("SE ELIMINO");
                limpiar();
            });
        });
    }

    // AGREGAR INGREDIENTE
    private void agregarIngrediente() {
        if (txtPizID.getText().toString().isEmpty()) {
            txtResultado.setText("INGRESE ID");
            return;
        }

        int id = Integer.parseInt(txtPizID.getText().toString());

        ejecutar(() -> {
            ApiClient.addIngredientToPizza(id, selectedIngredientId);
            String detalle = ApiClient.getIngredientsByPizza(id);

            runOnUiThread(() ->
                    txtResultado.setText("INGREDIENTE AGREGADO\n\n" + detalle)
            );
        });
    }

    // CARGAR SPINNER
    private void cargarIngredientes() {
        ejecutar(() -> {
            List<Ingredient> lista = ApiClient.getIngredients();

            runOnUiThread(() -> {
                ArrayAdapter<Ingredient> adapter =
                        new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lista);
                spIngredientes.setAdapter(adapter);
            });
        });
    }

    // HILO SIMPLE PARA NO REPETIR
    private interface TareaApi {
        void run() throws Exception;
    }

    private void ejecutar(TareaApi tarea) {
        new Thread(() -> {
            try {
                tarea.run();
            } catch (Exception e) {
                runOnUiThread(() -> txtResultado.setText("ERROR: " + e.getMessage()));
            }
        }).start();
    }

    // LIMPIAR CAMPOS
    private void limpiar() {
        txtPizID.setText("");
        txtPizName.setText("");
        txtPizOrigin.setText("");
        swPizEstado.setChecked(true);
    }
}