package org.dieschnittstelle.mobile.android.skeleton.util;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;

import java.util.function.Consumer;
import java.util.function.Supplier;


public class MADAsyncOperationRunner {
    private final Activity owner;
    private final ProgressBar progressBar;

    public MADAsyncOperationRunner(Activity owner, ProgressBar progressBar) {
        this.owner = owner;
        this.progressBar = progressBar;
    }

    public <T> void run(Supplier<T> operation, Consumer<T> onOperationResult) {
        if (progressBar != null) {
            progressBar.setVisibility(VISIBLE);
        }
        new Thread(() -> {
            T operationResult = operation.get();
            owner.runOnUiThread(() -> {
                onOperationResult.accept(operationResult);
                if (progressBar != null) {
                    progressBar.setVisibility(GONE);
                }
            });

        }).start();
    }
}
