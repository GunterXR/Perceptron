package ru.stephen.neuron;

public class NeuronOutput extends Neuron {
    public double[] prevWeight = new double[HIDDEN_NEURONS];
    public double[] prevValue = new double[HIDDEN_NEURONS];

    //Функция активации (y = 1 / (1 + e^(-x))).
    @Override
    public double functionActivation() {
        result = 1 / (1 + Math.exp(-sum));
        return result;
    }
    //Сумматор нейрона.
    @Override
    public void neuronAdder() {
        sum = 0;
        for (int i = 0; i < prevValue.length; i++) {
            sum = sum + prevValue[i] * prevWeight[i];
        }
    }
}
