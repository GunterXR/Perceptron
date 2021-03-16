package ru.stephen.image;

import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import ru.stephen.Const;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ImageImport extends Const {
    public ArrayList<Image> imageList = new ArrayList<>();
    public ArrayList<String> pathList = new ArrayList<>();
    private File[] fileList;
    private final FileChooser fileChooser = new FileChooser();
    private final DirectoryChooser directoryChooser = new DirectoryChooser();

    //Импорт изображения/ий.
    public void ImgImport(CheckBox cb) throws FileNotFoundException {
        //Импорт изображения для распознавания.
        File file;
        Image imageTemp;
        if (!cb.isSelected()) {
            fileChooser.setTitle("Выберите изображение");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().addAll(//
                    new FileChooser.ExtensionFilter("All Files", "*.*"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png"));
            file = fileChooser.showOpenDialog(null);
            if (file != null) pathList.add(file.getPath());
            else {
                IOException e = new IOException();
                e.printStackTrace();
            }
            for (int i = 0; i < pathList.size(); i++) {
                assert file != null;
                imageTemp = new Image(new FileInputStream(file));
                if (imageTemp.getWidth() >= WIDTH && imageTemp.getHeight() >= HEIGHT) imageList.add(new Image(new FileInputStream(file), WIDTH, HEIGHT, true, true));
                else pathList.remove(i);
            }

        }
        //Импорт изображениий для обучения.
        else {
            directoryChooser.setTitle("Выберите папку с изображениями");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            file = directoryChooser.showDialog(null);
            if (file != null) {
                fileList = file.listFiles();
                assert fileList != null;
                for (File value : fileList) {
                    pathList.add(value.getPath());
                }
            }
            else {
                IOException e = new IOException();
                e.printStackTrace();
            }
            for (int i = 0; i < pathList.size(); i++) {
                imageTemp = new Image(new FileInputStream(fileList[i]));
                if (imageTemp.getWidth() >= WIDTH && imageTemp.getHeight() >= HEIGHT) imageList.add(new Image(new FileInputStream(fileList[i]), WIDTH, HEIGHT, true, true));
                else pathList.remove(i);
            }
        }
    }
}
