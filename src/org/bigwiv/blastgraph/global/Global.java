/*
 * BlastGraph: a comparative genomics tool
 * Copyright (C) 2013  Yanbo Ye (yeyanbo289@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.bigwiv.blastgraph.global;

import java.util.HashMap;
import java.util.prefs.Preferences;

import org.bigwiv.blastgraph.BlastGraph;
import org.bigwiv.blastgraph.HitVertex;
import org.bigwiv.blastgraph.ValueEdge;
import org.bigwiv.blastgraph.command.CommandManager;
import org.bigwiv.blastgraph.workstatus.WorkStatus;


public class Global {
	public static final BlastGraph<HitVertex, ValueEdge> graph = new BlastGraph<HitVertex, ValueEdge>();
	public static final String APP_VERSION = "BlastGraph v1.0beta";
	public static final Preferences PREFERENCES = Preferences.userRoot().node("BlastGraph" + Global.APP_VERSION); 
	private static final String SEPARATOR = "::!!::";
	public static final WorkStatus WORK_STATUS = new WorkStatus();
	public static final HashMap<String, String> SETTING = new HashMap<String, String>();
	public static final FrameProxy APP_FRAME_PROXY = new FrameProxy();

	public static final CommandManager COMMAND_MANAGER = new CommandManager();

	public static String getSeparator(char separator) {
		return SEPARATOR + separator;
	}

	public static String getSeparator() {
		return SEPARATOR;
	}

	/**
	 * This method return the path of current running jar file or class file
	 * 
	 * @param cls type
	 * @return app path
	 */
	public static String getAppPath(Class cls) {
		if (cls == null)
			throw new java.lang.IllegalArgumentException("Illegal parameter！");
		ClassLoader loader = cls.getClassLoader();
		String clsName = cls.getName() + ".class";
		Package pack = cls.getPackage();
		String path = "";
		if (pack != null) {
			String packName = pack.getName();
			if (packName.startsWith("java.") || packName.startsWith("javax."))
				throw new java.lang.IllegalArgumentException("System Class found！");
			clsName = clsName.substring(packName.length() + 1);
			if (packName.indexOf(".") < 0)
				path = packName + "/";
			else {
				int start = 0, end = 0;
				end = packName.indexOf(".");
				while (end != -1) {
					path = path + packName.substring(start, end) + "/";
					start = end + 1;
					end = packName.indexOf(".", start);
				}
				path = path + packName.substring(start) + "/";
			}
		}
		java.net.URL url = loader.getResource(path + clsName);
		String realPath = url.getPath();
		int pos = realPath.indexOf("file:");
		if (pos > -1)
			realPath = realPath.substring(pos + 5);
		pos = realPath.indexOf(path + clsName);
		realPath = realPath.substring(0, pos - 1);
		if (realPath.endsWith("!"))
			realPath = realPath.substring(0, realPath.lastIndexOf("/"));
		try {
			realPath = java.net.URLDecoder.decode(realPath, "utf-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return realPath;
	}
}
