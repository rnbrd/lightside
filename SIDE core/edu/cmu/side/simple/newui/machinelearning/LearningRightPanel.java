package edu.cmu.side.simple.newui.machinelearning;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

/**
 * This holds all of the panels for analyzing a trained model.
 * @author emayfiel
 *
 */
public class LearningRightPanel extends JPanel {
	private static final long serialVersionUID = 7572152691679941456L;

	private ConfusionMatrixPanel matrixPanel = new ConfusionMatrixPanel();
	private MiniErrorAnalysisPanel errorPanel = new MiniErrorAnalysisPanel();
	private LearningOutputPanel outputPanel = new LearningOutputPanel();
	private DocumentDisplayPanel documentPanel = new DocumentDisplayPanel();

	/**
	 * Everything is in SplitPanes so the user can adjust their relative sizes.
	 */
	public LearningRightPanel(){
		setPreferredSize(new Dimension(800,725));
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		JSplitPane splitTop = new JSplitPane();
		matrixPanel.setPreferredSize(new Dimension(325, 350));
		errorPanel.setPreferredSize(new Dimension(325, 350));
		JScrollPane topLeft = new JScrollPane(matrixPanel);
		JScrollPane topRight = new JScrollPane(errorPanel);
		splitTop.setLeftComponent(topLeft);
		splitTop.setRightComponent(topRight);
		JScrollPane top = new JScrollPane(splitTop);
		top.setPreferredSize(new Dimension(750, 400));
		
		JTabbedPane bottomPane = new JTabbedPane();
		bottomPane.addTab("Learning Output", outputPanel);
		bottomPane.addTab("Selected Documents Display", documentPanel);
		bottomPane.setPreferredSize(new Dimension(750, 300));
		split.setTopComponent(top);
		split.setBottomComponent(bottomPane);
		
		add(split);
	}
	public void refreshPanel(){
		matrixPanel.refreshPanel();
		errorPanel.refreshPanel();
		outputPanel.refreshPanel();
		documentPanel.refreshPanel();
		repaint();
	}
}
