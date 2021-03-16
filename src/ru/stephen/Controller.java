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
                    //System.out.println(arrNeuronHidden[i].prevWeight[j]);
                }
            }
            for (int i = 0; i < OUTPUT_NEURONS; i++) {
                arrNeuronOutput[i] = new NeuronOutput();
                Random rnd = new Random();
                for (int j = 0; j < HIDDEN_NEURONS; j++) {
                    arrNeuronOutput[i].prevWeight[j] = rnd.nextDouble() - 0.5;
                    //System.out.println(arrNeuronOutput[i].prevWeight[j]);
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
                    //for (int a = 0; a < 1024; a++) {
                    //  System.out.print(input[a]);
                    //    if ((a + 1) % 32 == 0) System.out.println();
                    //}
                    recognizeImage();
                    // (int k = 0; k < results.length; k++) {
                    //    System.out.print(results[k] + " ");
                    //}
                    //System.out.println();
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
            //for(int i = 0; i < results.length; i++) {
            //    System.out.print(results[i] + " ");
            //}
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
        if (Character.compare(c, 'a') == 0) trueAnswer[0] = 1;
        else if (Character.compare(c, 'b') == 0) trueAnswer[1] = 1;
        else if (Character.compare(c, 'c') == 0) trueAnswer[2] = 1;
        else if (Character.compare(c, 'd') == 0) trueAnswer[3] = 1;
        else if (Character.compare(c, 'e') == 0) trueAnswer[4] = 1;
        else if (Character.compare(c, 'f') == 0) trueAnswer[5] = 1;
        else if (Character.compare(c, 'g') == 0) trueAnswer[6] = 1;
        else if (Character.compare(c, 'h') == 0) trueAnswer[7] = 1;
        else if (Character.compare(c, 'i') == 0) trueAnswer[8] = 1;
        else if (Character.compare(c, 'j') == 0) trueAnswer[9] = 1;
        else if (Character.compare(c, 'k') == 0) trueAnswer[10] = 1;
        else if (Character.compare(c, 'l') == 0) trueAnswer[11] = 1;
        else if (Character.compare(c, 'm') == 0) trueAnswer[12] = 1;
        else if (Character.compare(c, 'n') == 0) trueAnswer[13] = 1;
        else if (Character.compare(c, 'o') == 0) trueAnswer[14] = 1;
        else if (Character.compare(c, 'p') == 0) trueAnswer[15] = 1;
        else if (Character.compare(c, 'q') == 0) trueAnswer[16] = 1;
        else if (Character.compare(c, 'r') == 0) trueAnswer[17] = 1;
        else if (Character.compare(c, 's') == 0) trueAnswer[18] = 1;
        else if (Character.compare(c, 't') == 0) trueAnswer[19] = 1;
        else if (Character.compare(c, 'u') == 0) trueAnswer[20] = 1;
        else if (Character.compare(c, 'v') == 0) trueAnswer[21] = 1;
        else if (Character.compare(c, 'w') == 0) trueAnswer[22] = 1;
        else if (Character.compare(c, 'x') == 0) trueAnswer[23] = 1;
        else if (Character.compare(c, 'y') == 0) trueAnswer[24] = 1;
        else if (Character.compare(c, 'z') == 0) trueAnswer[25] = 1;
    }

    //Отображение букв на экране результата.
    public void letterDisplay() {
        int largest = 0;
        for (int i = 1; i < results.length; i++ ) {
            if (results[i] > results[largest]) largest = i;
        }
        //System.out.println(largest);
        if (largest == 0) label_figure.setText("БУКВА: a");
        else if (largest == 1) label_figure.setText("БУКВА: b");
        else if (largest == 2) label_figure.setText("БУКВА: c");
        else if (largest == 3) label_figure.setText("БУКВА: d");
        else if (largest == 4) label_figure.setText("БУКВА: e");
        else if (largest == 5) label_figure.setText("БУКВА: f");
        else if (largest == 6) label_figure.setText("БУКВА: g");
        else if (largest == 7) label_figure.setText("БУКВА: h");
        else if (largest == 8) label_figure.setText("БУКВА: i");
        else if (largest == 9) label_figure.setText("БУКВА: j");
        else if (largest == 10) label_figure.setText("БУКВА: k");
        else if (largest == 11) label_figure.setText("БУКВА: l");
        else if (largest == 12) label_figure.setText("БУКВА: m");
        else if (largest == 13) label_figure.setText("БУКВА: n");
        else if (largest == 14) label_figure.setText("БУКВА: o");
        else if (largest == 15) label_figure.setText("БУКВА: p");
        else if (largest == 16) label_figure.setText("БУКВА: q");
        else if (largest == 17) label_figure.setText("БУКВА: r");
        else if (largest == 18) label_figure.setText("БУКВА: s");
        else if (largest == 19) label_figure.setText("БУКВА: t");
        else if (largest == 20) label_figure.setText("БУКВА: u");
        else if (largest == 21) label_figure.setText("БУКВА: y");
        else if (largest == 22) label_figure.setText("БУКВА: w");
        else if (largest == 23) label_figure.setText("БУКВА: x");
        else if (largest == 24) label_figure.setText("БУКВА: y");
        else if (largest == 25) label_figure.setText("БУКВА: z");
    }
}
