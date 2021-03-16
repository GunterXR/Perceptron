package ru.stephen.neuron;

public class Backpropagation {
    public void BackProp(NeuronHidden[] arrHidden, NeuronOutput[] arrOutput, byte[] trueAnswer) {
        //Корректировка весов между выходными и скрытыми нейронами.
        for (int i = 0; i < arrOutput.length; i++) {
            for (int j = 0; j < arrOutput[i].prevWeight.length; j++) {
                if (arrOutput[i].result != trueAnswer[i]) {
                    arrOutput[i].delta = arrOutput[i].result * (1 - arrOutput[i].result) * (trueAnswer[i] - arrOutput[i].result);
                    arrOutput[i].prevWeight[j] = arrOutput[i].prevWeight[j] + arrOutput[i].NRATIO * arrOutput[i].delta * arrOutput[i].prevValue[j];
                }
            }
        }

        //Корректировка весов между скрытыми и входными нейронами.
        for (int i = 0; i < arrHidden.length; i++) {
            for (int j = 0; j < arrHidden[i].prevWeight.length; j++) {
                arrHidden[i].backSum = 0;
                for (int k = 0; k < arrOutput.length; k++) {
                    arrHidden[i].backSum += arrOutput[k].delta * arrOutput[k].prevWeight[i];
                }
                arrHidden[i].delta = arrHidden[i].result * (1 - arrHidden[i].result) * arrHidden[i].backSum;
                arrHidden[i].prevWeight[j] = arrHidden[i].prevWeight[j] + arrHidden[i].NRATIO * arrHidden[i].delta * arrHidden[i].prevValue[j];
            }
        }
    }
}
