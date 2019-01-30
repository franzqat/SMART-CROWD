package dev.furtor.contastudenti;

import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UIUpdater implements Runnable {
    private Handler handler = new Handler();
    ProgressBar progressBar;
    int progressStatus=0;
    TextView testo;
    public UIUpdater(TextView testo, ProgressBar progressBar, int progressStatus){
        this.testo = testo;
        this.progressBar = progressBar;
        this.progressStatus = progressStatus;
    }

    @Override
    public void run() {

        // Update the progress bar
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progressStatus);
                // Show the progress on TextView
                testo.setText(progressStatus + "/100");
            }
        });
    }
}
