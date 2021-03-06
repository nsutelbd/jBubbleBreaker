/*
 * Copyright 2008 - 2010 Sven Strickroth <email@cs-ware.de>
 * 
 * This file is part of jBubbleBreaker.
 * 
 * jBubbleBreaker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation.
 * 
 * jBubbleBreaker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jBubbleBreaker. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jbubblebreaker;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * Statistics handling and Statistics JFrame
 * @author Sven Strickroth
 */
@SuppressWarnings("serial")
public class Statistics extends MyJDialog implements ActionListener {
	final JTable table;
	private List<StatisticData> myData;

	/**
	 * Create the frame
	 */
	public Statistics() {
		super(Localization.getI18n().tr("Statistics"), "jbubblebreaker.png", 677, 175, true);
		setModal(true);

		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

		JButton resetStatisticsButton = new JButton();
		Localization.setMemnoricText(resetStatisticsButton, Localization.getI18n().tr("&Reset statistics"));
		buttonsPanel.add(resetStatisticsButton);
		resetStatisticsButton.addActionListener(this);

		JButton closeButton = new JButton();
		Localization.setMemnoricText(closeButton, Localization.getI18n().tr("&Close"));
		buttonsPanel.add(closeButton);
		closeButton.addActionListener(this);

		final JScrollPane scrollPane_1 = new JScrollPane();
		getContentPane().add(scrollPane_1, BorderLayout.CENTER);

		table = new JTable();
		scrollPane_1.setViewportView(table);
		table.setModel(new TableTableModel());
		if (!System.getProperty("java.version").substring(0, 3).equals("1.5")) {
			// setAutoCreateRowSorter is Java 1.6 specific and doesn't work with 1.5
			table.setAutoCreateRowSorter(true);
		}

		setVisible(true);
	}

	private class TableTableModel extends AbstractTableModel {
		private final String[] COLUMN_NAMES = new String[] { Localization.getI18n().tr("Mode"), Localization.getI18n().tr("Colors"), Localization.getI18n().tr("Rows"), Localization.getI18n().tr("Columns"), Localization.getI18n().tr("Games played"), Localization.getI18n().tr("Max. Points"), Localization.getI18n().tr("Avg. Points") };

		public TableTableModel() {
			super();
			myData = getStatistics();
		}

		public int getRowCount() {
			return myData.size();
		}

		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		public String getColumnName(int columnIndex) {
			return COLUMN_NAMES[columnIndex];
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return myData.get(rowIndex).getMode();
			} else if (columnIndex == 1) {
				return myData.get(rowIndex).getColors();
			} else if (columnIndex == 2) {
				return myData.get(rowIndex).getRows();
			} else if (columnIndex == 3) {
				return myData.get(rowIndex).getCols();
			} else if (columnIndex == 4) {
				return myData.get(rowIndex).getCountOfGames();
			} else if (columnIndex == 5) {
				return myData.get(rowIndex).getMaxPoints();
			} else {
				return myData.get(rowIndex).getAveragePoints();
			}
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		@SuppressWarnings("unchecked")
		public Class getColumnClass(int c) {
			if (c == 0) {
				return String.class;
			} else {
				return Integer.class;
			}
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		if (((JButton) arg0.getSource()).getText().equals(Localization.getI18n().tr("&Reset statistics"))) {
			if (JOptionPane.showConfirmDialog(null, Localization.getI18n().tr("Do you really want to reset the statistics?"), Localization.getI18n().tr("Reset statistics"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				if (new File(JBubbleBreaker.getPreferencesDirFile(), "statistics").delete() == false) {
					JOptionPane.showMessageDialog(null, Localization.getI18n().tr("An error occoured. The statistics were not reset."), "jBubbleBreaker", JOptionPane.INFORMATION_MESSAGE);
				} else {
					myData = new LinkedList<StatisticData>();
					table.repaint();
				}
			}
		} else {
			dispose();
		}
	}

	/**
	 * Update the statistics
	 * @param mode the game mode name
	 * @param colors count of different colors
	 * @param rows count of rows
	 * @param cols count of columns
	 * @param points points
	 * @return the string that can be used for the message box for the user
	 */
	static String updateStatistics(String mode, int colors, int rows, int cols, int points) {
		String returnString = "";
		if (JBubbleBreaker.isApplicationMode() == true && !JBubbleBreaker.getUserProperty("enableGuestMode", "false").equalsIgnoreCase("true")) {
			ObjectOutputStream out = null;
			try {
				Iterator<StatisticData> myIterator = getStatistics().iterator();
				out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(JBubbleBreaker.getPreferencesDirFile(), "statistics"))));
				boolean found = false;
				while (myIterator.hasNext()) {
					StatisticData myItem = myIterator.next();
					if (myItem.getMode().equals(mode) && myItem.getColors() == colors && myItem.getRows() == rows && myItem.getCols() == cols) {
						myItem.newGame(points);
						returnString = "\n" + Localization.getI18n().tr("Games played") + ": " + myItem.getCountOfGames() + "\n" + Localization.getI18n().tr("Avg. Points") + ": " + myItem.getAveragePoints() + "\n" + Localization.getI18n().tr("Max. Points") + ": " + myItem.getMaxPoints();
						found = true;
					}
					out.writeObject(myItem);
				}
				if (found == false) {
					out.writeObject(new StatisticData(mode, colors, rows, cols, points));
					returnString = "\n" + Localization.getI18n().tr("First time you played this mode-, size-combination.");
				}
				out.close();
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}
		return returnString;
	}

	/**
	 * Opens and returns the stored statistics from the datafile
	 * @return statistics
	 */
	public static List<StatisticData> getStatistics() {
		List<StatisticData> myList = new LinkedList<StatisticData>();
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(JBubbleBreaker.getPreferencesDirFile(), "statistics"))));
		} catch (FileNotFoundException e) {
			return myList;
		} catch (IOException e) {
			return myList;
		}
		try {
			StatisticData statisticData;
			while ((statisticData = (StatisticData) in.readObject()) != null) {
				myList.add(statisticData);
			}
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}
		try {
			in.close();
		} catch (IOException e) {
		}
		return myList;
	}
}
