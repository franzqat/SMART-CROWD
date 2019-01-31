package dev.furtor.contastudenti;

import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

public class ElementsStructure {
    private final Switch aSwitch;
    private final TextView textView;
    private  final ProgressBar progressBar;
    private int maxStudenti = 100; //capienza aula

    public int getMaxStudenti() {
        return maxStudenti;
    }

    public void setMaxStudenti(int maxStudenti) {
        this.maxStudenti = maxStudenti;
    }

    public ElementsStructure(Switch aSwitch, TextView textView, ProgressBar progressBar) {
        this.aSwitch = aSwitch;
        this.textView = textView;
        this.progressBar = progressBar;
    }

    public Switch getaSwitch() {
        return aSwitch;
    }

    public TextView getTextView() {
        return textView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
