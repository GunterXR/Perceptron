package ru.stephen.neuron;

import ru.stephen.Const;

public abstract class Neuron extends Const  {
    double sum;
    double result;
    double delta;
    double backSum;

    public abstract double functionActivation();
    public abstract void neuronAdder();
}
