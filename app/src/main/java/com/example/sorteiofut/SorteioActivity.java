package com.example.sorteiofut;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class SorteioActivity extends AppCompatActivity {

    private int jogadoresPorTime;
    private SwitchMaterial switchGoleiros;
    private LinearLayout llDynamicPotes;
    private Button btnRealizarSorteio;
    private TextView tvTitle;

    private EditText etGoleiros;
    private List<EditText> etPotesList = new ArrayList<>();

    // Condições especiais
    private String jogarJuntoA = "";
    private String jogarJuntoB = "";
    private String separadosA = "";
    private String separadosB = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorteio);

        jogadoresPorTime = getIntent().getIntExtra("JOGADORES_POR_TIME", 5);

        switchGoleiros = findViewById(R.id.switchGoleiros);
        llDynamicPotes = findViewById(R.id.llDynamicPotes);
        btnRealizarSorteio = findViewById(R.id.btnRealizarSorteio);
        tvTitle = findViewById(R.id.tvTitle);

        setupPotesDinamicamente();

        switchGoleiros.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etGoleiros.setVisibility(View.VISIBLE);
                ((View) etGoleiros.getParent()).setVisibility(View.VISIBLE); // Mostra o titulo
            } else {
                etGoleiros.setVisibility(View.GONE);
                ((View) etGoleiros.getParent()).setVisibility(View.GONE); // Esconde o titulo
            }
        });

        // Setup Segredo
        tvTitle.setOnLongClickListener(v -> {
            abrirSegredoDialog();
            return true;
        });

        btnRealizarSorteio.setOnClickListener(v -> realizarSorteio());
    }

    private void setupPotesDinamicamente() {
        // Goleiros primeiro
        LinearLayout goleiroLayout = createPoteSection("Goleiros");
        etGoleiros = (EditText) goleiroLayout.getChildAt(1);
        goleiroLayout.setVisibility(View.GONE); // Default hidden
        llDynamicPotes.addView(goleiroLayout);

        // Potes de linha
        for (int i = 1; i <= jogadoresPorTime; i++) {
            String title = "Pote " + i;
            if (i == 1) title += " (Craques / Cabeças de chave)";
            else if (i == 2) title += " (Bons de bola)";
            else if (i == jogadoresPorTime) title += " (Completam time)";

            LinearLayout poteLayout = createPoteSection(title);
            EditText et = (EditText) poteLayout.getChildAt(1);
            etPotesList.add(et);
            llDynamicPotes.addView(poteLayout);
        }
    }

    private LinearLayout createPoteSection(String titleStr) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView title = new TextView(this);
        title.setText(titleStr);
        title.setTextColor(getResources().getColor(R.color.text_primary));
        title.setTextSize(16);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.topMargin = dpToPx(24);
        title.setLayoutParams(titleParams);

        EditText editText = new EditText(this);
        LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        etParams.topMargin = dpToPx(8);
        editText.setLayoutParams(etParams);
        editText.setBackgroundTintList(getResources().getColorStateList(R.color.red_primary));
        editText.setGravity(Gravity.TOP | Gravity.START);
        editText.setHint("Um nome por linha...");
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setMinLines(3);
        editText.setTextColor(getResources().getColor(R.color.text_primary));
        editText.setHintTextColor(getResources().getColor(R.color.text_secondary));

        layout.addView(title);
        layout.addView(editText);

        return layout;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void abrirSegredoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Condições Especiais");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText etJuntoA = new EditText(this);
        etJuntoA.setHint("Jogam Junto (Jogador A)");
        etJuntoA.setText(jogarJuntoA);
        final EditText etJuntoB = new EditText(this);
        etJuntoB.setHint("Jogam Junto (Jogador B)");
        etJuntoB.setText(jogarJuntoB);

        final EditText etSeparadoA = new EditText(this);
        etSeparadoA.setHint("Separados (Jogador A)");
        etSeparadoA.setText(separadosA);
        final EditText etSeparadoB = new EditText(this);
        etSeparadoB.setHint("Separados (Jogador B)");
        etSeparadoB.setText(separadosB);

        layout.addView(etJuntoA);
        layout.addView(etJuntoB);
        layout.addView(new TextView(this)); // spacer
        layout.addView(etSeparadoA);
        layout.addView(etSeparadoB);

        builder.setView(layout);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            jogarJuntoA = etJuntoA.getText().toString().trim();
            jogarJuntoB = etJuntoB.getText().toString().trim();
            separadosA = etSeparadoA.getText().toString().trim();
            separadosB = etSeparadoB.getText().toString().trim();
            Toast.makeText(SorteioActivity.this, "Condições salvas!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void realizarSorteio() {
        Intent intent = new Intent(this, ResultadoActivity.class);

        // Passar goleiros
        if (switchGoleiros.isChecked()) {
            intent.putExtra("GOLEIROS", etGoleiros.getText().toString());
        }

        // Passar potes
        ArrayList<String> potesValues = new ArrayList<>();
        for (EditText et : etPotesList) {
            potesValues.add(et.getText().toString());
        }
        intent.putStringArrayListExtra("POTES", potesValues);

        // Passar regras
        intent.putExtra("JUNTO_A", jogarJuntoA);
        intent.putExtra("JUNTO_B", jogarJuntoB);
        intent.putExtra("SEPARADO_A", separadosA);
        intent.putExtra("SEPARADO_B", separadosB);

        startActivity(intent);
    }
}
