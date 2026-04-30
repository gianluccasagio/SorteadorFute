package com.example.sorteiofut;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
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

        // Pré-processamento: Se "juntoA" e "juntoB" existem, removemos eles dos potes
        // e inserimos os dois como uma entidade única "juntoA && juntoB" no pote onde "juntoA" estava.
        boolean isJuntoValido = juntoA != null && !juntoA.trim().isEmpty() && juntoB != null && !juntoB.trim().isEmpty();

        List<List<String>> listaDePotes = new ArrayList<>();
        for (String p : potesValues) {
            listaDePotes.add(parseNames(p));
        }

        if (isJuntoValido) {
            int poteIndexA = -1;
            boolean foundA = false;
            boolean foundB = false;

            // Encontrar onde eles estao
            for (int i = 0; i < listaDePotes.size(); i++) {
                if (!foundA && listaDePotes.get(i).contains(juntoA)) {
                    poteIndexA = i;
                    foundA = true;
                }
                if (!foundB && listaDePotes.get(i).contains(juntoB)) {
                    foundB = true;
                }
            }

            // Se ambos foram encontrados, une-os no pote do A
            if (foundA && foundB) {
                for (List<String> pote : listaDePotes) {
                    pote.remove(juntoA);
                    pote.remove(juntoB);
                }
                listaDePotes.get(poteIndexA).add(0, juntoA + " && " + juntoB);
            }
        }

        boolean isSeparadoValido = separadoA != null && !separadoA.trim().isEmpty() && separadoB != null && !separadoB.trim().isEmpty();

        // Distribuição dos potes
        for (int p = 0; p < listaDePotes.size(); p++) {
            List<String> jogadoresDoPote = listaDePotes.get(p);
            Collections.shuffle(jogadoresDoPote);

            int timeIndex = 0;
            for (String jogador : jogadoresDoPote) {

                // Se o jogador atual é um dos "separados", precisamos achar um time que não tenha o outro.
                if (isSeparadoValido && (jogador.equals(separadoA) || jogador.equals(separadoB))) {
                    while (timeIndex < qtdTimes &&
                          (times.get(timeIndex).contains(separadoA) || times.get(timeIndex).contains(separadoB))) {
                        timeIndex++;
                    }
                }

                if (timeIndex < qtdTimes) {
                    if (jogador.contains(" && ")) {
                        String[] pair = jogador.split(" && ");
                        times.get(timeIndex).add(pair[0]);
                        times.get(timeIndex).add(pair[1]);
                    } else {
                        times.get(timeIndex).add(jogador);
                    }
                    timeIndex++;
                } else {
                    // Jogadores sobrando vão pro banco do primeiro time
                    if (jogador.contains(" && ")) {
                        String[] pair = jogador.split(" && ");
                        times.get(0).add(pair[0] + " (Banco)");
                        times.get(0).add(pair[1] + " (Banco)");
                    } else {
                        times.get(0).add(jogador + " (Banco)");
                    }
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
