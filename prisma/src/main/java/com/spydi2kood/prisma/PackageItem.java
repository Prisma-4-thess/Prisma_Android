package com.spydi2kood.prisma;

/**
 * Created by jim on 24/2/2014.
 */

import android.graphics.drawable.Drawable;

public class PackageItem {

	private Drawable icon;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
}