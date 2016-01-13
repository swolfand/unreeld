package com.samwolfand.unreeld.ui.adapter.spinner;

public class SortBySpinnerItem {
    boolean isHeader;
    String mode, title;
    boolean indented;

    SortBySpinnerItem(boolean isHeader, String mode, String title, boolean indented) {
        this.isHeader = isHeader;
        this.mode = mode;
        this.title = title;
        this.indented = indented;
    }
}
