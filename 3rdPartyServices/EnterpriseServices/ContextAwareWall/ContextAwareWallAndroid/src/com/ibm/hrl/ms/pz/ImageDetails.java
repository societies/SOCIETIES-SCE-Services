package com.ibm.hrl.ms.pz;

public class ImageDetails {
	public String imageName;
	public String imagePath;
	public boolean downloaded;
	
	public ImageDetails(String imageName, String imagePath,boolean downloaded){
		this.imagePath = imagePath;
		this.imageName = imageName;
		this.downloaded = downloaded;
	}

}
