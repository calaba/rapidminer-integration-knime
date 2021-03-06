/*  RapidMiner Integration for KNIME
 *  Copyright (C) 2012 Mind Eratosthenes Kft.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mind_era.knime_rapidminer.knime.nodes.internal;

import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JPanel;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.mind_era.knime_rapidminer.knime.nodes.RapidMinerInit;
import com.rapidminer.gui.AbstractUIState;
import com.rapidminer.gui.RapidMinerGUI;

/**
 * This is the eclipse bundle activator. Note: KNIME node developers probably
 * won't have to do anything in here, as this class is only needed by the
 * eclipse platform/plugin mechanism. If you want to move/rename this file, make
 * sure to change the plugin.xml file in the project root directory accordingly.
 * 
 * @author Gabor Bakos
 */
public class RapidMinerNodePlugin extends AbstractUIPlugin {
	// The shared instance.
	private static RapidMinerNodePlugin plugin;

	/**
	 * The constructor.
	 */
	public RapidMinerNodePlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation.
	 * 
	 * @param context
	 *            The OSGI bundle context
	 * @throws Exception
	 *             If this plugin could not be started
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		//Start a new thread to avoid errors reported because of class loading.
		new Thread() {
			public void run() {
				try {
					//Wait a bit to make sure the bundle is properly initialized
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				RapidMinerInit.init(false);
				AbstractUIState state = new AbstractUIState("design", null,
						new JPanel()) {
					
					@Override
					public void exit(final boolean relaunch) {
						// Do nothing, we do not exit
					}
					
					@Override
					public boolean close() {
						metaDataUpdateQueue.shutdown();
						return true;
					}
					
					@Override
					public void updateRecentFileList() {
						// Do noting
					}
					
					@Override
					protected void setTitle() {
						// Do nothing
					}
				};
				RapidMinerGUI.setMainFrame(state);
				state.getValidateAutomaticallyAction().setSelected(true);
				state.close();
				RapidMinerInit.setPreferences();
			};
		}.start();
	}

	/**
	 * This method is called when the plug-in is stopped.
	 * 
	 * @param context
	 *            The OSGI bundle context
	 * @throws Exception
	 *             If this plugin could not be stopped
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return Singleton instance of the Plugin
	 */
	public static RapidMinerNodePlugin getDefault() {
		return plugin;
	}

}
