package ua.jackshen.editor;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;

class SetOpacity {
    private ImageView chosenImage;
    private Slider opacityLevel;
    private Label opacityValue;

    SetOpacity(ImageView chosenImage, Slider opacityLevel, Label opacityValue){
       this.chosenImage = chosenImage;
       this.opacityLevel = opacityLevel;
       this.opacityValue = opacityValue;
       opacityLevel.valueProperty().addListener((ov, old_val, new_val) -> {
           chosenImage.setOpacity(new_val.doubleValue());
           opacityValue.setText(String.format("%.2f", new_val));
       });
    }
}
