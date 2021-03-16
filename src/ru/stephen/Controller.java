package ru.stephen;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import ru.stephen.image.ImageImport;
import ru.stephen.neuron.Backpropagation;
import ru.stephen.neuron.NeuronHidden;
import ru.stephen.neuron.NeuronInput;
import ru.stephen.neuron.NeuronOutput;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

public class Controller extends Const {
    @FXML
    Label label_figure;
    @FXML
    CheckBox checkbox_learn;
    @FXML
    Button button_recognize;
    @FXML
    ImageView image;

    Backpropagation backPropagation = new Backpropagation();
    private final NeuronInput[] arrNeuronInput = new NeuronInput[INPUT_NEURONS];
    private final NeuronHidden[] arrNeuronHidden = new NeuronHidden[HIDDEN_NEURONS];
    private final NeuronOutput[] arrNeuronOutput = new NeuronOutput[OUTPUT_NEURONS];
    private final char[] Letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private byte[] trueAnswer = new byte[OUTPUT_NEURONS];
    private final int[] input = new int[INPUT_NEURONS];
    private final double[] results = new double[OUTPUT_NEURONS];
    private final Color[][] color = new Color[WIDTH][HEIGHT];
    private ArrayList<Image> imageList = new ArrayList<>();
    private ArrayList<String> pathList = new ArrayList<>();
    private boolean isNeuronsExist = false;

    //Нажатие кнопки импорта изображения/ий.
    public void onClickImport() throws FileNotFoundException {
        ImageImport class_img = new ImageImport();
        class_img.ImgImport(checkbox_learn);
        imageList = class_img.imageList;
        pathList = class_img.pathList;
        button_recognize.setDisable(false);
        label_figure.setText("");
        printImage(imageList.get(0));
    }

    //Нажатие кнопки распознавания изображения или обучения нейронной сети.
    public void onClickRecognize() {
        //Создание экземпляров нейронов. Занесение в них случайных весов (-1, 1)
        if (!isNeuronsExist) {
            for (int i = 0; i < INPUT_NEURONS; i++) {
                arrNeuronInput[i] = new NeuronInput();
            }
            for (int i = 0; i < HIDDEN_NEURONS; i++) {
                arrNeuronHidden[i] = new NeuronHidden();
                Random rnd = new Random();
                for (int j = 0; j < INPUT_NEURONS; j++) {
                    arrNeuronHidden[i].prevWeight[j] = rnd.nextDouble() - 0.5;
                }
            }
            for (int i = 0; i < OUTPUT_NEURONS; i++) {
                arrNeuronOutput[i] = new NeuronOutput();
                Random rnd = new Random();
                for (int j = 0; j < HIDDEN_NEURONS; j++) {
                    arrNeuronOutput[i].prevWeight[j] = rnd.nextDouble() - 0.5;
                }
            }
            isNeuronsExist = true;
        }
        //Занесение во входные нейроны массива чисел, полученный от изображения.
        for (int i = 0; i < INPUT_NEURONS; i++) {
            arrNeuronInput[i].prevValue[0] = input[i];
        }
        //Обучение нейронной сети.
        if (checkbox_learn.isSelected()) {
            for (int j = 0; j < EON; j++) {
                for (int i = 0; i < imageList.size(); i++) {
                    String path = pathList.get(i);
                    char c = path.charAt(path.length() - 5);
                    toLetter(c);
                    printImage(imageList.get(i));
                    binaryImage();
                    recognizeImage();
                    backPropagation.BackProp(arrNeuronHidden, arrNeuronOutput, trueAnswer);
                }
                System.out.println("ЭПОХА №" + j + " успешно!");
            }
            label_figure.setText("НЕЙРОСЕТЬ ОБУЧЕНА");
        }
        //Работа нейронной сети.
        else {
            binaryImage();
            recognizeImage();
            System.out.println();
            letterDisplay();
        }
    }
    //Флажок, включающий режим обучения.
    public void onClickCheck() {
        if (checkbox_learn.isSelected()) button_recognize.setText("Обучить");
        else button_recognize.setText("Распознать");
    }
    //Печать иззображения на экране. Создание массива цветов изображения.
    public void printImage(Image img) {
        PixelReader pixelReader = img.getPixelReader();
        WritableImage writableImage = new WritableImage(WIDTH * RATIO, HEIGHT * RATIO);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int y = 0; y < HEIGHT; y++) {
            for (int i = 0; i < RATIO; i++) {
                for (int x = 0; x < WIDTH; x++) {
                    color[y][x] = pixelReader.getColor(x, y);
                    for (int j = 0; j < RATIO; j++) {
                        pixelWriter.setColor(x * RATIO + j, y * RATIO + i, color[y][x]);
                    }
                }
            }
        }
        image.setImage(writableImage);
    }
    //Преобразование изображения в массив чисел {0, 1}.
    public void binaryImage() {
        double min = 1;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if ((color[y][x].getBlue() + color[y][x].getGreen() + color[y][x].getRed()) / 3 <= min)
                    min = (color[y][x].getBlue() + color[y][x].getGreen() + color[y][x].getRed()) / 3;
            }
        }
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                input[x + HEIGHT * y] = (1 - ((color[y][x].getBlue() + color[y][x].getGreen() + color[y][x].getRed())) / 3 < (1 - min) / 2) ? 0 : 1;
            }
        }
    }
    //Распознование изображения.
    public void recognizeImage() {
        for (int i = 0; i < INPUT_NEURONS; i++) {
            arrNeuronInput[i].prevValue[0] = input[i];
            arrNeuronInput[i].neuronAdder();
            for (int j = 0; j < HIDDEN_NEURONS; j++) {
                arrNeuronHidden[j].prevValue[i] = arrNeuronInput[i].functionActivation();
            }
        }
        for (int i = 0; i < HIDDEN_NEURONS; i++) {
            arrNeuronHidden[i].neuronAdder();
            for (int j = 0; j < OUTPUT_NEURONS; j++) {
                arrNeuronOutput[j].prevValue[i] = arrNeuronHidden[i].functionActivation();
            }
        }
        for (int i = 0; i < OUTPUT_NEURONS; i++) {
            arrNeuronOutput[i].neuronAdder();
            for (int j = 0; j < OUTPUT_NEURONS; j++) {
                results[j] = arrNeuronOutput[j].functionActivation();
            }
        }
    }

    //Перевод буквы в код.
    public void toLetter(char c) {
        for (int i = 0; i < trueAnswer.length; i++) {
            trueAnswer[i] = 0;
        }
        for (int i = 0; i < Letters.length; i++) {
            if (c == Letters[i]) {
                trueAnswer[i] = 1;
                break;
            }
        }
    }

    //Отображение букв на экране результата.
    public void letterDisplay() {
        int largest = 0;
        for (int i = 1; i < results.length; i++ ) {
            if (results[i] > results[largest]) largest = i;
        }
        for (int i = 0; i < Letters.length; i++) {
            if (largest == i) {
                label_figure.setText("БУКВА: " + Letters[largest]);
                break;
            }
        }
    }
}
