package com.nfa.capapp.service;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

import com.nfa.capapp.service.messaging.serial.GestureMessage;
import com.nfa.capapp.service.messaging.serial.SerialEvent;
import com.nfa.capapp.service.messaging.serial.SerialMessage;
import com.nfa.capapp.service.messaging.serial.SerialMessage.MessageType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class SvmController {
    private static final Logger log = LoggerFactory
        .getLogger(SvmController.class);

    private List<List<Integer>> trainingData = null;
    private List<List<Double>> scaledData = null;
    private List<List<Integer>> historyData = null;
    private svm_model model;
    private svm_parameter parameter;
    private svm_problem problem;
    private boolean crossValidate;
    private double upper = 1;
    private double lower = -1;
    private double featureMin[];
    private double featureMax[];
    private int currentGesture;
    private int iterations;
    private ApplicationEventPublisher publisher;
    private enum Mode { TRAIN, START, STOP };
    private Mode mode;

    @Autowired
    public SvmController(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
        trainingData = new ArrayList<List<Integer>>();
    }

    /**
     * @param trainingData
     * <br>
     *            column 0 - class identifier <br>
     *            column 1+ - observed data
     */
    public void train(List<List<Integer>> trainingData) {
        this.trainingData = trainingData;
        this.crossValidate = true;
        this.train();
    }

    public void train() {
        if (trainingData == null) {
            throw new IllegalStateException("trainingData is null");
        }

        if (parameter == null) {
            this.setDefaultParameters();
        }

        if (parameter.kernel_type == svm_parameter.PRECOMPUTED) {
            throw new IllegalStateException(
                                            "Precomputed kernel type is unsupported");
        }

        mode = Mode.START;

        // TODO: scale the data? See practical tips: https://github.com/cjlin1/libsvm
        scaleTrainingData();

        problem = this.makeProblem();

        String parameterError = svm.svm_check_parameter(problem, parameter);
        if (parameterError != null) {
            throw new IllegalStateException(parameterError);
        }

        if (crossValidate) {
            double acc = crossValidate(5, -5, 15, 2, 3, -15, -2);
            System.out.println("Accuracy: " + acc + " C: " + parameter.C + " gamma: " + parameter.gamma);

        }

        model = svm.svm_train(problem, parameter);

        try {
            svm.svm_save_model("svmModel.txt", model);
        } catch (IOException e) {
            // swallower
        }

    }

    /**
     *
     * @param nFold - number of folds to separate data
     * @param cBegin - begin C param at 2^cBegin
     * @param cEnd - end C param at 2^cEnd
     * @param cStep - step distance for C
     * @param gBegin - begin gamma param at 2^gBegin
     * @param gEnd - end gamma param at 2^gEnd
     * @param gStep - step distance for gamma
     * @return maximum accuracy achieved over grid search, also sets member parameters to the best values found
     *
     */
    public double crossValidate(int nFold, int cBegin, int cEnd, int cStep, int gBegin, int gEnd, int gStep) {
        if (problem == null) {
            throw new IllegalStateException("problem is null");
        }

        if (parameter == null) {
            this.setDefaultParameters();
        }

        double[] target = new double[problem.l];
        double accuracy, bestAccuracy = 0;
        int cBest = 0, gBest = 0, totalCorrect;

        for (int c = cBegin; (cStep > 0 && c < cEnd) || (cStep < 0 && c > cEnd); c += cStep) {
            parameter.C = Math.pow(2, c);

            for (int g = gBegin; (gStep > 0 && g < gEnd) || (gStep < 0 && g > gEnd); g += gStep) {
                parameter.gamma = Math.pow(2, g);

                svm.svm_cross_validation(problem, parameter, nFold, target);
                totalCorrect = 0;
                for (int i = 0; i < problem.l; ++i) {
                    if (target[i] == problem.y[i]) ++totalCorrect;
                }

                accuracy = totalCorrect / problem.l;

                if (accuracy > bestAccuracy) {
                    bestAccuracy = accuracy;
                    cBest = c;
                    gBest = g;
                }
            }
        }

        parameter.C = Math.pow(2, cBest);
        parameter.gamma = Math.pow(2, gBest);
        return bestAccuracy;
    }

    // TODO: Implement prediction with probability estimates

    public double predict(List<Integer> data) {
        // TODO: scale the data? See practical tips: https://github.com/cjlin1/libsvm

        if (historyData == null) historyData = new ArrayList<List<Integer>>();

        historyData.add(data);

        if (historyData.size() == 500) {
            adjustScaling(historyData);
            historyData.clear();
        }

        List<Double> scaled = scaleData(data);

        svm_node[] nodes = new svm_node[scaled.size()];

        for (int i = 0; i < scaled.size(); ++i) {
            svm_node node = new svm_node();
            node.index = i;
            node.value = scaled.get(i);
            nodes[i] = node;
        }

        return this.predict(nodes);
    }

    public double predict(svm_node[] nodes) {
        return svm.svm_predict(model, nodes);
    }

    private void doCrossValidation() {
        // See https://github.com/cjlin1/libsvm/blob/master/java/svm_train.java
        // method do_cross_validation(), do we need?
    }

    private void setDefaultParameters() {
        parameter = new svm_parameter();
        // default values
        parameter.svm_type = svm_parameter.C_SVC;
        parameter.kernel_type = svm_parameter.RBF;
        parameter.degree = 3;
        parameter.gamma = 0; // 1/num_features
        parameter.coef0 = 0;
        parameter.nu = 0.5;
        parameter.cache_size = 100;
        parameter.C = 1;
        parameter.eps = 1e-3;
        parameter.p = 0.1;
        parameter.shrinking = 1;
        parameter.probability = 0;
        parameter.nr_weight = 0;
        parameter.weight_label = new int[0];
        parameter.weight = new double[0];

    }

    private svm_problem makeProblem() {
        svm_problem problem = new svm_problem();

        problem.l = scaledData.size();
        problem.x = new svm_node[problem.l][];
        problem.y = new double[problem.l];

        for (int i = 0; i < problem.l; ++i) {
            problem.y[i] = scaledData.get(i).get(0);
            // System.out.println("Problem Index: " + problem.y[i]);

            int innerSize = scaledData.get(i).size();

            // Take one off since zero is the gesture
            problem.x[i] = new svm_node[innerSize - 1];
            for (int j = 1; j < innerSize; ++j) {

                svm_node node = new svm_node();

                // Observed data starts at index 1 in scaledData
                // but they are stored starting at zero in nodes, consider revision
                // Predict must mirror behavior
                node.index = j - 1;

                node.value = scaledData.get(i).get(j);
                problem.x[i][j - 1] = node;
            }
        }

        if (parameter.gamma == 0 && scaledData.get(0).size() > 1) {
            parameter.gamma = 1.0 / (scaledData.get(0).size() - 1);
        }
        return problem;
    }

    public void saveTrainingData() {
        this.saveTrainingData("trainingData.txt");
    }

    public void saveTrainingData(String outFile) {
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                                                               new FileOutputStream(outFile), "utf-8"));

            // Format: <label> <index1>:<value1> <index2>:<value2> ...
            for (int i = 0; i < trainingData.size(); ++i) {
                writer.write(trainingData.get(i).get(0) + "");

                for (int j = 1; j < trainingData.get(i).size(); ++j) {
                    writer.write(" " + (j - 1) + ":" + trainingData.get(i).get(j));
                }

                writer.write('\n');
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
                /*ignore*/
            }
        }
    }

    public List<List<Integer>> getTrainingData() {
        return trainingData;
    }

    /**
     * @param trainingData
     * <br>
     *            column 0 - class identifier <br>
     *            column 1+ - observed data
     */
    public void setTrainingData(List<List<Integer>> trainingData) {
        this.trainingData = trainingData;
    }

    public svm_parameter getParameter() {
        return parameter;
    }

    public void setParameter(svm_parameter parameter) {
        this.parameter = parameter;
    }

    public boolean isCrossValidate() {
        return crossValidate;
    }

    public void setCrossValidate(boolean crossValidate) {
        this.crossValidate = crossValidate;
    }

    private void adjustScaling(List<List<Integer>> list) {
        int innerSize = list.get(0).size();

        featureMin = new double[innerSize];
        featureMax = new double[innerSize];

        // Find min/max
        for (int i = 0; i < list.size(); ++i) {
            List<Integer> innerList = list.get(i);

            // Find feature min/max
            for (int j = 0; j < innerList.size(); ++j) {
                if (i == 0) {
                    featureMin[j] = innerList.get(j);
                    featureMax[j] = innerList.get(j);
                } else {
                    featureMin[j] = Math.min(featureMin[j], innerList.get(j));
                    featureMax[j] = Math.max(featureMax[j], innerList.get(j));
                }
            }
        }

        System.out.println("New min");
        System.out.println(Arrays.toString(featureMin));
        System.out.println("New max");
        System.out.println(Arrays.toString(featureMax));

    }

    private void scaleTrainingData() {
        int innerSize = trainingData.get(0).size();

        featureMin = new double[innerSize - 1];
        featureMax = new double[innerSize - 1];

        // Find min/max
        for (int i = 0; i < trainingData.size(); ++i) {
            List<Integer> innerList = trainingData.get(i);

            // Find feature min/max
            for (int j = 1; j < innerList.size(); ++j) {
                if (i == 0) {
                    featureMin[j - 1] = innerList.get(j);
                    featureMax[j - 1] = innerList.get(j);
                } else {
                    featureMin[j - 1] = Math.min(featureMin[j - 1], innerList.get(j));
                    featureMax[j - 1] = Math.max(featureMax[j - 1], innerList.get(j));
                }
            }
        }

        // Scale

        scaledData = new ArrayList<List<Double>>();

        for (int i = 0; i < trainingData.size(); ++i) {
            List<Double> scaledList = new ArrayList<Double>();
            List<Integer> innerList = trainingData.get(i);

            scaledList.add(innerList.get(0).doubleValue());

            for (int j = 1; j < innerList.size(); ++j) {
                double val = innerList.get(j);

                if (val == featureMin[j - 1]) {
                    val = lower;
                } else if (val == featureMax[j - 1]) {
                    val = upper;
                } else {
                    val = lower + (upper - lower) *
                        (val - featureMin[j - 1]) /
                        (featureMax[j - 1] - featureMin[j - 1]);
                }

                scaledList.add(val);
            }

            scaledData.add(scaledList);
        }
    }

    private List<Double> scaleData(List<Integer> data) {
        List<Double> out = new ArrayList<Double>(data.size());

        for (int i = 0; i < data.size(); ++i) {
            double val = data.get(i);

            if (val > featureMax[i]) {
                featureMax[i] = val;
                val = upper;
            } else if (val < featureMin[i]) {
                featureMin[i] = val;
                val = lower;
            } else {
                val = lower + (upper - lower) *
                    (val - featureMin[i]) /
                    (featureMax[i] - featureMin[i]);
            }


            out.add(val);
        }

        return out;
    }

    public void setIterationCount(int count) {
        this.iterations = count;
    }

    public void setCurrentGesture(int gesture) {
        this.currentGesture = gesture;
    }

    @EventListener
    public void processSerialEvent(SerialEvent event) {
        if (event.getSource().getClass() == SerialMessage.class) {
    	
	    	SerialMessage eventMessage = (SerialMessage) event.getSource();
	
	        if (eventMessage.getMessageType().equals(MessageType.DATA)) {
	            List<Integer> data = eventMessage.getData();
	
	            switch (mode) {
	            case START:
	            	double prediction = predict(convertDataFrame(data, false));
	                log.debug("Prediction " + prediction);
	                GestureMessage gestureMessage = new GestureMessage((int) prediction);
	                publisher.publishEvent(new SerialEvent(gestureMessage));
	            	break;
	            case TRAIN:
	            	trainingData.add(convertDataFrame(data, true));
	            	
	                if (--iterations == 0) {
	                    SerialMessage serialMessage = new SerialMessage();
	                    serialMessage.setMessageType(MessageType.TRAINING_COMPLETE);
	
	                    publisher.publishEvent(new SerialEvent(serialMessage));
	                }
	            	break;
	            case STOP:
	            	// do nothing on STOP, let remaining frames drop
	            }
	        }
        }
    }

    private List<Integer> convertDataFrame(List<Integer> dataFrame, boolean addGesture) {
        List<Integer> out = new ArrayList<Integer>();

        if (addGesture) {
            out.add(currentGesture);
        }

        for (int i = 1; i < dataFrame.size(); i = i + 2) {
            out.add(dataFrame.get(i));
        }

        return out;
    }

	public void clearModel() {
	    if (trainingData != null) trainingData.clear();
	    if (scaledData != null) scaledData.clear();
	    if (historyData != null) historyData.clear();
	    setStopMode();
	}

	public void setStopMode() {
		mode = Mode.STOP;
	}

	public void setTrainMode() {
		mode = Mode.TRAIN;
	}
}
