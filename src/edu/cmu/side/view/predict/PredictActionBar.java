package edu.cmu.side.view.predict;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import edu.cmu.side.Workbench;
import edu.cmu.side.control.PredictLabelsControl;
import edu.cmu.side.model.RecipeManager.Stage;
import edu.cmu.side.model.StatusUpdater;
import edu.cmu.side.view.generic.ActionBar;
import edu.cmu.side.view.util.WarningButton;

public class PredictActionBar extends ActionBar
{
	JCheckBox showMaxScoreBox = new JCheckBox("Show Predicted Label's Score");
	JCheckBox showDistsBox = new JCheckBox("Show All Scores");
	JCheckBox overwriteBox = new JCheckBox("Overwrite Columns");
	WarningButton warn = new WarningButton();

	public PredictActionBar(StatusUpdater update)
	{
		super("predicted",Stage.PREDICTION_RESULT, update);
		actionButton.setIcon(new ImageIcon("toolkits/icons/application_form_edit.png"));
		actionButton.setText("Predict");
		actionButton.setEnabled(false);
		nameLabel.setText("New Column Name:");
		name.setColumns(10);
		name.setText("predicted");
		name.addCaretListener(new CaretListener()
		{
			@Override
			public void caretUpdate(CaretEvent arg0)
			{
				checkColumnName();
			}
		});
		
		overwriteBox.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				checkColumnName();
			}
		});
		
		settings.add("left", warn);
		settings.add("left", showMaxScoreBox);
		settings.add("left", showDistsBox);
		settings.add("left", overwriteBox);
		
		
		actionButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				PredictLabelsControl.executePredictTask(PredictActionBar.this, name.getText(), showMaxScoreBox.isSelected(), showDistsBox.isSelected(), overwriteBox.isSelected());
			}
		});
		
	}

	@Override
	public void startedTask()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void endedTask()
	{
		Workbench.update(this);
	}
	
	@Override
	public void refreshPanel()
	{
		checkColumnName();
	}

	protected boolean isPredictionPossible()
	{
		return PredictLabelsControl.hasHighlightedTrainedModelRecipe() && PredictLabelsControl.hasHighlightedUnlabeledData();
	}

	protected void checkColumnName()
	{
		if(name.getText().isEmpty())
		{
			warn.setWarning("You need to provide a name for the predicted label.");
			actionButton.setEnabled(false);
		}
		
		else if(isPredictionPossible() && 
				PredictLabelsControl.getHighlightedUnlabeledData().getDocumentList().allAnnotations().containsKey(name.getText()))
		{
			warn.setWarning("This will over-write an existing column.");
			actionButton.setEnabled(overwriteBox.isSelected());
		}
		else
		{
			warn.clearWarning();
			actionButton.setEnabled(isPredictionPossible());
		}
	}

}