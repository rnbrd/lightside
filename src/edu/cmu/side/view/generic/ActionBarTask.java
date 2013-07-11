package edu.cmu.side.view.generic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import edu.cmu.side.Workbench;

public abstract class ActionBarTask extends SwingWorker<Void, Void> implements PropertyChangeListener
{
	protected List<JProgressBar> progressBarList;

	protected ActionBar actionBar;
	protected ActionListener stopListener ;
	protected Icon originalIcon ;
	
	protected abstract void doTask();
	protected boolean halt = false;
	
	protected static Icon dangerIcon = new ImageIcon("toolkits/icons/exclamation.png");
	

	public abstract void requestCancel();
	public void forceCancel()
	{
		boolean cancelled = this.cancel(true);
		finishTask();
	}
	
	protected void finishTask()
	{
		if(actionBar != null)
		{
			actionBar.update.reset();
			actionBar.progressBar.setVisible(false);
			actionBar.actionButton.setEnabled(true);
			actionBar.cancel.setEnabled(false);
			actionBar.cancel.removeActionListener(stopListener);
			actionBar.cancel.setIcon(originalIcon);
	
			actionBar.endedTask();
			Workbench.update(actionBar); //this is the only SIDE-specific code in this class...
		}
	}
	
	@Override
	public Void doInBackground()
	{	
		beginTask(); //FIXME: this should be done in the Event thread, not the background thread. wrap execute()?

		doTask();
		
		return null;
	}

	@Override
	public void done()
	{
		finishTask();
	}
	
	protected void beginTask()
	{
		halt = false;
		if(actionBar != null)
		{
			originalIcon = actionBar.cancel.getIcon();
			actionBar.cancel.addActionListener(stopListener);
			actionBar.cancel.setEnabled(true);
			actionBar.progressBar.setVisible(true);
			actionBar.actionButton.setEnabled(false);
			actionBar.startedTask();
		}
	}
		

	public ActionBarTask(ActionBar action)
	{
		this.progressBarList = new ArrayList<JProgressBar>();
		this.addPropertyChangeListener(this);

		if(action != null)
		{
			this.actionBar = action;
			progressBarList.add(actionBar.progressBar);
		}
		
		stopListener = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(halt)
				{
					forceCancel();
				}
				else
				{
					halt = true;
					if(actionBar != null) actionBar.cancel.setIcon(dangerIcon);
					requestCancel();
				}
			}};
	}

	
	//from OnPanelSwingTask
	public void propertyChange(PropertyChangeEvent evt)
	{
		evt.getSource();
		if ("state" != evt.getPropertyName()) { return; }

		SwingWorker.StateValue stateValue = (SwingWorker.StateValue) evt.getNewValue();
		for (JProgressBar progressBar : progressBarList)
		{
			if (stateValue == SwingWorker.StateValue.DONE)
			{
				progressBar.setIndeterminate(false);
				progressBar.setString(null);
				
			}
			else
			{
				progressBar.setIndeterminate(true);
				progressBar.setString("working...");
			}
		}
	}

	public void addProgressBar(JProgressBar progressBar)
	{
		this.progressBarList.add(progressBar);
	}


}