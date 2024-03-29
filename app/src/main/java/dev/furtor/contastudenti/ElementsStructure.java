package dev.furtor.contastudenti;

import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

//Struttura per la gestione dei componenti di un elemento
public class ElementsStructure {
    private final Switch aSwitch;
    private final TextView textView;
    private  final ProgressBar progressBar;
    private int maxStudenti; //capienza aula
    private int accessPoints;


    public ElementsStructure(Switch aSwitch, TextView textView, ProgressBar progressBar, int maxStudenti, int accessPoints) {
        this.aSwitch = aSwitch;
        this.textView = textView;
        this.progressBar = progressBar;
        this.maxStudenti = maxStudenti;
        this.accessPoints = accessPoints;
    }

    public int getMaxStudenti() {
        return maxStudenti;
    }

    public void setMaxStudenti(int maxStudenti) {
        this.maxStudenti = maxStudenti;
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

    public int getAccessPoints() {
        return accessPoints;
    }
}
