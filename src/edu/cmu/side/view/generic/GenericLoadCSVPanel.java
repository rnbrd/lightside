package edu.cmu.side.view.generic;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.cmu.side.Workbench;
import edu.cmu.side.control.ExtractFeaturesControl;
import edu.cmu.side.model.Recipe;
import edu.cmu.side.model.RecipeManager.Stage;

public abstract class GenericLoadCSVPanel extends GenericLoadPanel
{
	public GenericLoadCSVPanel(String title)
	{
		super(title);
		configureLoadCSVPanel();
	}

	protected void configureLoadCSVPanel()
	{
		try
		{
			//Test to see if the magic is there.
			Class.forName("fr.emse.tatiana.corpus.COMMSDBWriter");
			
			JButton bonus = new JButton("Load from DB", new ImageIcon("toolkits/icons/folder_database.png"));
			buttons.remove(warn);
			buttons.remove(load);
			buttons.add("right", warn);
			buttons.add("right", bonus);
			buttons.add("right", new JPanel());
			buttons.add("right", load);
			
			bonus.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
//					COMMSDBWriter.loadDocumentsFromDB(ExtractFeaturesControl.getUpdater());
				}
			});

		}
		catch (ClassNotFoundException cnf)
		{
			//no worries.
		}
		
		this.remove(save);
		ImageIcon iconLoad = new ImageIcon("toolkits/icons/folder_page.png");
		load.setIcon(iconLoad);
		
	}
	
	public GenericLoadCSVPanel(String title, boolean showLoad, boolean showDelete, boolean showSave, boolean showDescription)
	{
		super(title, showLoad, showDelete, showSave, showDescription);
		configureLoadCSVPanel();
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

	@Override
	public void checkChooser()
	{
		if(chooser == null)
		{
			super.checkChooser();
			chooser.setCurrentDirectory(new File("data"));
		}
	}

}