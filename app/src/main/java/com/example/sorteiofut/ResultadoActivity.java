package com.example.sorteiofut;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ResultadoActivity extends AppCompatActivity {

    private LinearLayout llResultadosContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        llResultadosContainer = findViewById(R.id.llResultadosContainer);

        realizarEShowSorteio();
    }

    private void realizarEShowSorteio() {
        String goleirosStr = getIntent().getStringExtra("GOLEIROS");
        ArrayList<String> potesValues = getIntent().getStringArrayListExtra("POTES");

        String juntoA = getIntent().getStringExtra("JUNTO_A");
        String juntoB = getIntent().getStringExtra("JUNTO_B");
        String separadoA = getIntent().getStringExtra("SEPARADO_A");
        String separadoB = getIntent().getStringExtra("SEPARADO_B");

        if (potesValues == null || potesValues.isEmpty()) {
            Toast.makeText(this, "Sem jogadores para sortear.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pote 1 define quantidade de times
        List<String> pote1 = parseNames(potesValues.get(0));
        int qtdTimes = pote1.size();

        if (qtdTimes == 0) {
            Toast.makeText(this, "Adicione jogadores no Pote 1 para definir a quantidade de times.", Toast.LENGTH_LONG).show();
            return;
        }

        List<List<String>> times = new ArrayList<>();
        for (int i = 0; i < qtdTimes; i++) {
            times.add(new ArrayList<>());
        }

        // 1. Sorteia Goleiros (se tiver)
        if (goleirosStr != null && !goleirosStr.trim().isEmpty()) {
            List<String> goleiros = parseNames(goleirosStr);
            Collections.shuffle(goleiros);
            for (int i = 0; i < Math.min(goleiros.size(), qtdTimes); i++) {
                times.get(i).add("[G] " + goleiros.get(i));
            }
        }

        // 2. Aplica as regras de Juntos e Separados (lógica simplificada)
        // Embaralha e distribui os potes
        for (int p = 0; p < potesValues.size(); p++) {
            List<String> jogadoresDoPote = parseNames(potesValues.get(p));
            Collections.shuffle(jogadoresDoPote);

            // Tenta aplicar regras nesse pote antes de distribuir
            if (juntoA != null && juntoB != null && !juntoA.isEmpty() && !juntoB.isEmpty()) {
                if (jogadoresDoPote.contains(juntoA) && jogadoresDoPote.contains(juntoB)) {
                    jogadoresDoPote.remove(juntoA);
                    jogadoresDoPote.remove(juntoB);
                    jogadoresDoPote.add(0, juntoA + " e " + juntoB); // Adiciona juntos na mesma vaga (simplificado para garantir no mesmo time)
                }
            }

            int timeIndex = 0;
            for (String jogador : jogadoresDoPote) {
                // Checa separados (simplificado)
                if (separadoA != null && separadoB != null && !separadoA.isEmpty() && !separadoB.isEmpty()) {
                     if (jogador.equals(separadoA) || jogador.equals(separadoB)) {
                         // Evita cair no mesmo time (se já tem o outro)
                         while(timeIndex < qtdTimes && (times.get(timeIndex).contains(separadoA) || times.get(timeIndex).contains(separadoB))) {
                             timeIndex++;
                         }
                     }
                }

                if (timeIndex < qtdTimes) {
                    if (jogador.contains(" e ")) {
                        String[] pair = jogador.split(" e ");
                        times.get(timeIndex).add(pair[0]);
                        times.get(timeIndex).add(pair[1]);
                    } else {
                        times.get(timeIndex).add(jogador);
                    }
                    timeIndex++;
                } else {
                    // Jogadores sobrando vão pro banco do primeiro time
                    times.get(0).add(jogador + " (Banco)");
                }
            }
        }

        // Exibir na tela
        for (int i = 0; i < qtdTimes; i++) {
            adicionarTimeNaTela("Time " + (i + 1), times.get(i));
        }
    }

    private List<String> parseNames(String input) {
        List<String> result = new ArrayList<>();
        if (input == null || input.trim().isEmpty()) return result;
        String[] lines = input.split("\n");
        for (String line : lines) {
            String name = line.trim();
            if (!name.isEmpty()) {
                result.add(name);
            }
        }
        return result;
    }

    private void adicionarTimeNaTela(String nomeTime, List<String> jogadores) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = 32;
        card.setLayoutParams(params);
        card.setBackgroundColor(getResources().getColor(R.color.surface_dark));
        card.setPadding(32, 32, 32, 32);

        TextView title = new TextView(this);
        title.setText(nomeTime);
        title.setTextColor(getResources().getColor(R.color.red_primary));
        title.setTextSize(20);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(title);

        for (String jogador : jogadores) {
            TextView tv = new TextView(this);
            tv.setText("- " + jogador);
            tv.setTextColor(getResources().getColor(R.color.text_primary));
            tv.setTextSize(16);
            tv.setPadding(0, 8, 0, 8);
            card.addView(tv);
        }

        llResultadosContainer.addView(card);
    }
}
