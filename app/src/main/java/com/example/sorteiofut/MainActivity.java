package com.example.sorteiofut;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText etJogadoresPorTime;
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

        etJogadoresPorTime = findViewById(R.id.etJogadoresPorTime);
        btnSorteador = findViewById(R.id.btnSorteador);

        btnSorteador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qtdStr = etJogadoresPorTime.getText().toString().trim();
                if (qtdStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, insira a quantidade de jogadores de linha por time.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int jogadoresPorTime = Integer.parseInt(qtdStr);

                Intent intent = new Intent(MainActivity.this, SorteioActivity.class);
                intent.putExtra("JOGADORES_POR_TIME", jogadoresPorTime);
                startActivity(intent);
            }
        });
    }
}
