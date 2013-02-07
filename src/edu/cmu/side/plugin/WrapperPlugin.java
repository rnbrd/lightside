package edu.cmu.side.plugin;

import java.awt.Color;
import java.util.Map;

import javax.swing.JPanel;

import se.datadosen.component.RiverLayout;
import edu.cmu.side.model.StatusUpdater;
import edu.cmu.side.model.data.FeatureTable;
import edu.cmu.side.model.data.PredictionResult;

public abstract class WrapperPlugin extends SIDEPlugin{


	protected JPanel panel = new JPanel(new RiverLayout());
	
	public WrapperPlugin(){
		panel.setBackground(Color.white);
	}
	@Override
	public String getType() {
		return "learning_wrapper";
	}
	
	public abstract void learnFromTrainingData(FeatureTable train, int fold, Map<Integer, Integer> foldsMap, StatusUpdater update);

	public FeatureTable wrapTableBefore(FeatureTable table, int fold, Map<Integer, Integer> foldsMap, StatusUpdater update){
		return wrapTableForSubclass(table, fold, foldsMap, update);
	}
	
	public PredictionResult wrapResultAfter(PredictionResult predict, int fold, Map<Integer, Integer> foldsMap, StatusUpdater update){
		return wrapResultForSubclass(predict, fold, foldsMap, update);
	}
	
	public abstract FeatureTable wrapTableForSubclass(FeatureTable table, int fold, Map<Integer, Integer> foldsMap, StatusUpdater update);
	
	public abstract PredictionResult wrapResultForSubclass(PredictionResult result, int fold, Map<Integer, Integer> foldsMap, StatusUpdater update);
}
