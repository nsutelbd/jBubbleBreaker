/*
 * Copyright 2008 Sven Strickroth <email@cs-ware.de>
 * 
 * This file is part of JBubbleBreaker.                                  
 * 
 * JBubbleBreaker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as     
 * published by the Free Software Foundation.                            
 * 
 * JBubbleBreaker is distributed in the hope that it will be useful,     
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         
 * GNU General Public License for more details.                          
 * 
 * You should have received a copy of the GNU General Public License     
 * along with JBubbleBreaker. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jbubblebreaker;

import java.awt.Toolkit;
import javax.swing.JFrame;

/**
 * JFrame Implementation which allows you to set icon, center-state and size more flexible 
 * @author Sven Strickroth
 */
@SuppressWarnings("serial")
public abstract class MyJFrame extends JFrame {
	/**
	 * Creates a new JFrame
	 */
	public MyJFrame() {
		super();
	}

	/**
	 * Creates a JFrame with certain parameters
	 * @param title the window-title
	 * @param iconFileName the icon filename, null for no icon
	 * @param width width of the JFrame
	 * @param height height of the JFrame
	 * @param resizeAble create resizebale JFrame?
	 * @param centered center the JFrame?
	 */
	public MyJFrame(String title, String iconFileName, int width, int height, boolean resizeAble, boolean centered) {
		super(title);
		if (iconFileName != null) {
			setIcon(iconFileName);
		}
		setSize(width,height);
		setResizable(resizeAble);
		if (centered==true) {
			setCentered();
		}
	}

	/**
	 * Sets the icon of the JFrame
	 * @param filename the path and name of the image to use as the icon for the JFrame
	 */
	protected void setIcon(String filename) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/"+filename)));
	}
	
	/**
	 * Centers the window on the screen 
	 */
	protected void setCentered() {
		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width-getWidth())/2,(Toolkit.getDefaultToolkit().getScreenSize().height-getHeight())/2);
	}
}