package com.application.frontend_;

import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Objects;

public class Validation {
    public static Border validBorder = new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, new CornerRadii(4), BorderWidths.DEFAULT));
    public static Border invalidBorder = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(4), BorderWidths.DEFAULT));

    public static void validateText(TextField field, String pattern) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (pattern.isEmpty()) {
                if (newValue.isEmpty()) {
                    field.setBorder(invalidBorder);
                } else {
                    field.setBorder(validBorder);
                }
            } else {
                if (newValue.matches(pattern)) {
                    field.setBorder(validBorder);
                } else {
                    field.setBorder(invalidBorder);
                }
            }
        });
    }

    public static void passwordMatch (TextField pass1, TextField pass2) {
        pass2.textProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(pass1.getText(), newValue)) {
                pass2.setBorder(validBorder);
            } else {
                pass2.setBorder(invalidBorder);
            }
        });
    }
}