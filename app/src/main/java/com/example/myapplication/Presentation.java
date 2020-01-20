package com.example.myapplication;

import androidx.annotation.NonNull;

public interface Presentation {
    void addText(@NonNull String text);
    void addPingText(String text, int index);
    void clearPingText();
}
