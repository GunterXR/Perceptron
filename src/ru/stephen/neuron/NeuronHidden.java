package ru.stephen.neuron;

public class NeuronHidden extends Neuron {
    public double[] prevWeight = new double[INPUT_NEURONS];
    public double[] prevValue = new double[INPUT_NEURONS];

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