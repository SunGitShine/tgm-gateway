package com.juma.tgm.gateway.decorator.engine.ext;

import com.giants.decorator.html.Theme;

public class ThemeExt implements Theme {

	private static final long serialVersionUID = 1021949409910946143L;

	private String name;
	
	private String path;
	
	public ThemeExt(){}
	
	public ThemeExt(String name,String path){
		this.name = name;
		this.path = path;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPath() {
		return path;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setPath(String path) {
		this.path = path;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ThemeExt other = (ThemeExt) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

}
