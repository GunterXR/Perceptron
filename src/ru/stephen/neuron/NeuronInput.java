package ru.stephen.neuron;

public class NeuronInput extends Neuron {
    public double[] prevValue = new double[1];

    //Функция активации (y = x).
    @Override
    public double functionActivation() {
        result = sum;
        return result;
    }
    //Сумматор нейрона (не суммируется, просто передается).
    @Override
    public void neuronAdder() {
        sum = 0;
        sum = prevValue[0];
    }
}
