package edu.cmu.side.view.predict;

import java.util.Collections;

import javax.swing.JScrollPane;

import edu.cmu.side.Workbench;
import edu.cmu.side.control.GenesisControl;
import edu.cmu.side.control.PredictLabelsControl;
import edu.cmu.side.model.Recipe;
import edu.cmu.side.model.RecipeManager;
import edu.cmu.side.model.RecipeManager.Stage;
import edu.cmu.side.model.data.DocumentList;
import edu.cmu.side.view.generic.GenericLoadCSVPanel;
import edu.cmu.side.view.util.SelectPluginList;

public class PredictNewDataPanel extends GenericLoadCSVPanel
{

	SelectPluginList textColumnsList = new SelectPluginList();
	JScrollPane textColumnsScroll = new JScrollPane(textColumnsList);

	public PredictNewDataPanel()
	{
		super("Unlabeled Data (CSV):");
		GenesisControl.addListenerToMap(RecipeManager.Stage.TRAINED_MODEL, this);
	}

	@Override
	public void setHighlight(Recipe r)
	{
		PredictLabelsControl.setHighlightedUnlabeledData(r);
		Workbench.update(this);
		verifyNewData();
	}

	@Override
	public Recipe getHighlight()
	{
		return PredictLabelsControl.getHighlightedUnlabeledData();
	}

	@Override
	public void refreshPanel()
	{
		refreshPanel(Workbench.getRecipesByPane(Stage.DOCUMENT_LIST));
	}

	@Override
	public void loadNewItem()
	{
		loadNewDocumentsFromCSV();
	}
	
	protected void verifyNewData()
	{
		Recipe trainRecipe = PredictLabelsControl.getHighlightedTrainedModelRecipe();
		Recipe unlabeledRecipe = PredictLabelsControl.getHighlightedUnlabeledData();
		
		if(trainRecipe != null && unlabeledRecipe != null)
		{
			DocumentList trainList = trainRecipe.getDocumentList();
			DocumentList labelList = unlabeledRecipe.getDocumentList();
			if(!Collections.disjoint(trainList.getFilenames(), labelList.getFilenames()))
			{
				setWarning("Unlabeled data set overlaps with training set");
			}
			else
			{
				clearWarning();
			}
		}
		else
		{
			clearWarning();
		}
	}
}