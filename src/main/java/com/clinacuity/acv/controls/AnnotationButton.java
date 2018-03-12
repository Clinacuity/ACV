package com.clinacuity.acv.controls;

import com.clinacuity.acv.context.AcvContext;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import java.util.ArrayList;
import java.util.List;

public class AnnotationButton extends Button {
    private static final String NORMAL_STYLE = "button-annotation";
    private static final String HIGHLIGHTED_STYLE = "button-annotation-selected";

    private JsonObject annotation;
    private int begin;
    private int end;
    private String matchTypeStyle = "";
    private MatchType matchType;
    private Label categoryLabel;
    public AnnotationButton previousButton = null;
    public AnnotationButton nextButton = null;

    public List<AnnotationButton> matchingButtons = new ArrayList<>();
    public List<AnnotationButton> sameAnnotationButtons = new ArrayList<>();
    public TextArea targetTextArea;
    public AnchorPane parent;
    public MatchType getMatchType() { return matchType; }

    public int getBegin() { return begin; }

    public int getEnd() { return end; }

    public AnnotationButton(JsonObject json) {
        int beginValue = 0;
        int endValue = 0;

        if (json.has("begin_pos") && json.has("end_pos")) {
            beginValue = json.get("begin_pos").getAsInt();
            endValue = json.get("end_pos").getAsInt();
        }

        initialize(json, beginValue, endValue);
    }

    public void setSelected() {
        setStyle(matchTypeStyle);
        getStyleClass().remove(NORMAL_STYLE);
        getStyleClass().add(HIGHLIGHTED_STYLE);
        addCategoryLabel();
    }

    public void clearSelected() {
        setStyle(matchTypeStyle);
        getStyleClass().remove(HIGHLIGHTED_STYLE);
        getStyleClass().add(NORMAL_STYLE);
        removeCategoryLabel();
    }

    public void checkMatchTypes(MatchType noMatchType) {
        if (matchingButtons.size() == 0) {
            setMatchType(noMatchType);
        } else {
            setMatchType(MatchType.TRUE_POS);
        }
    }

    public void removeFromParent() {
        parent.getChildren().remove(this);
    }

    public void addToParent() {
        parent.getChildren().add(this);
    }

    private void initialize(JsonObject json, int beginValue, int endValue) {
        getStyleClass().add(NORMAL_STYLE);
        getStyleClass().add("no-focus");
        annotation = json;
        begin = beginValue;
        end = endValue;

        setOnAction(onButtonAction);
        setOnMouseEntered(setHover);
        setOnMouseExited(unsetHover);
    }

    private void addCategoryLabel() {
        if (categoryLabel == null) {
            categoryLabel = new Label(annotation.get("type").getAsString());
            categoryLabel.getStyleClass().add("label-annotation-category");
            categoryLabel.setMaxWidth(getWidth());
            categoryLabel.setMinWidth(getWidth());
            AnchorPane.setTopAnchor(categoryLabel, (double) getProperties().get("pane-top-anchor") - getHeight());
            AnchorPane.setLeftAnchor(categoryLabel, (double) getProperties().get("pane-left-anchor"));
        }
        parent.getChildren().add(categoryLabel);
    }

    private void removeCategoryLabel() {
        if (categoryLabel != null) {
            parent.getChildren().remove(categoryLabel);
        }
    }

    private String getAnnotationFeatureTree() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(AcvContext.getInstance().selectedAnnotationTypeProperty.getValueSafe());
        buffer.append(":\n");

        for (String key: annotation.keySet()) {
            buffer.append("\u2014> ");
            buffer.append(key);

            if (annotation.get(key).equals(JsonNull.INSTANCE)) {
                buffer.append(":  null");
            } else {
                buffer.append(":  ");
                buffer.append(annotation.get(key).getAsString());
            }
            buffer.append("\n");
        }

        return buffer.toString();
    }

    private void setMatchType(MatchType match) {
        switch(match) {
            case TRUE_POS:
                matchTypeStyle = "-fx-background-color: DodgerBlue;";
                break;

            case FALSE_POS:
                matchTypeStyle = "-fx-background-color: DarkOrchid;";
                break;

            case FALSE_NEG:
                matchTypeStyle = "-fx-background-color: OrangeRed;";
                break;

            default:
                matchTypeStyle = "-fx-background-color: DarkGray;";
        }

        matchType = match;
        setStyle(matchTypeStyle);
    }

    /* ******************************
     *                              *
     * Event Handlers               *
     *                              *
     *******************************/

    private EventHandler<ActionEvent> onButtonAction = event -> targetTextArea.setText(getAnnotationFeatureTree());

    private EventHandler<MouseEvent> setHover = event -> {
        matchingButtons.forEach(button -> button.setHover(true));
        sameAnnotationButtons.forEach(button -> button.setHover(true));
    };

    private EventHandler<MouseEvent> unsetHover = event -> {
        matchingButtons.forEach(button -> button.setHover(false));
        sameAnnotationButtons.forEach(button -> button.setHover(false));
    };

    /**
     * Exact matches -- identical category and span
     * Partial matches -- spans overlap; categories don't match
     * Partial matches -- spans exact; categories don't match
     * Partial matches -- spans don't overlap; categories match
     * No match
     */
    public enum MatchType {
        TRUE_POS,
        FALSE_POS,
        FALSE_NEG,
    }
}
