/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Fast River Technologies Inc. Irvine, CA, USA 
 * All Rights Reserved.
 * 
 * $Id:					$: Id of last commit                
 * $Revision:			$: Revision of last commit 
 * $Author: cye			$: Author of last commit       
 * $Date:	10-10-2018	$: Date of last commit
 */
package com.frt.util.logging;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.MissingResourceException;

public class Localization {

	private static final String NAME = "messages";
	private static final Pattern DEFAULT_PATTERN = Pattern.compile("([A-Z][A-Z][A-Z]_[A-Z]\\d\\d\\d)(: )(.*)");
	private static final String MESSAGE_ID_PREFIX = "FRT ";

	private final PropertyResourceBundle bundle;
	private final Pattern idPattern;
	private final String prefix;

	public static Localization getInstance() {
		return new Localization(DEFAULT_PATTERN, MESSAGE_ID_PREFIX);
	}

	public static Localization getInstance(String packageName) {
		return new Localization(DEFAULT_PATTERN, MESSAGE_ID_PREFIX, packageName);
	}

	protected Localization(Pattern idPattern, String prefix) {
		this.idPattern = idPattern;
		this.prefix = prefix;
		// Strip off the class to obtain the package name
		String packagename = this.getClass().getName();
		int lastdot = packagename.lastIndexOf('.');
		packagename = packagename.substring(0, lastdot);
		bundle = (PropertyResourceBundle) ResourceBundle.getBundle(packagename + "." + NAME, Locale.getDefault(),
				getClass().getClassLoader());
	}

	protected Localization(Pattern idPattern, String prefix, String packageName) {
		this.idPattern = idPattern;
		this.prefix = prefix;
		bundle = (PropertyResourceBundle) ResourceBundle.getBundle(packageName + "." + NAME, Locale.getDefault(),
				getClass().getClassLoader());
	}

	public String x(String msg, Object... args) {
		try {
			Matcher matcher = this.idPattern.matcher(msg);
			if (matcher.matches() && matcher.groupCount() > 1) {
				String msgid = matcher.group(1);
				StringBuilder strBuf = new StringBuilder();
				strBuf.append(this.prefix);
				strBuf.append(msgid);
				String localizedmsg = this.bundle.getString(strBuf.toString());
				strBuf.append(": ");
				strBuf.append(MessageFormat.format(localizedmsg, args));
				return strBuf.toString();
			}
			StringBuilder strBuf = new StringBuilder();
			strBuf.append(this.prefix);
			strBuf.append(MessageFormat.format(msg, args));
			return strBuf.toString();
		} catch (MissingResourceException | IllegalArgumentException ex) {
			StringBuilder strBuf = new StringBuilder();
			strBuf.append(this.prefix);
			strBuf.append("<message id unknown due to ");
			strBuf.append(ex.getMessage());
			strBuf.append(">\n");
			strBuf.append("actual message: " + msg);
			strBuf.append("message args: " + args);
			return strBuf.toString();
		}
	}

	public String x(String msg) {
		return this.x(msg, new Object[0]);
	}
}
