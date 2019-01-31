package dev.furtor.contastudenti;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ElementsStructure {
    private final ToggleButton toggleButton;
    private final TextView textView;
    private  final ProgressBar progressBar;
    private int maxStudenti = 100; //capienza aula

    public int getMaxStudenti() {
        return maxStudenti;
    }

    public void setMaxStudenti(int maxStudenti) {
        this.maxStudenti = maxStudenti;
    }

    public ElementsStructure(ToggleButton toggleButton, TextView textView, ProgressBar progressBar) {
        this.toggleButton = toggleButton;
        this.textView = textView;
        this.progressBar = progressBar;
    }

    public ToggleButton getToggleButton() {
        return toggleButton;
    }

    public TextView getTextView() {
        return textView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
