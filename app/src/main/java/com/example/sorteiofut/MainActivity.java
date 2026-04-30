package com.example.sorteiofut;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnSorteador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSorteador = findViewById(R.id.btnSorteador);

        btnSorteador.setOnClickListener(v -> mostrarDialogoJogadores());
    }

    private void mostrarDialogoJogadores() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Jogadores por Time");
        builder.setMessage("Quantos jogadores de linha por time?");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Ex: 5");

        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(50, 20, 50, 0);
        layout.addView(input, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String qtdStr = input.getText().toString().trim();
            if (qtdStr.isEmpty()) {
                Toast.makeText(MainActivity.this, "Informe a quantidade!", Toast.LENGTH_SHORT).show();
            } else {
                int jogadoresPorTime = Integer.parseInt(qtdStr);
                Intent intent = new Intent(MainActivity.this, SorteioActivity.class);
                intent.putExtra("JOGADORES_POR_TIME", jogadoresPorTime);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
