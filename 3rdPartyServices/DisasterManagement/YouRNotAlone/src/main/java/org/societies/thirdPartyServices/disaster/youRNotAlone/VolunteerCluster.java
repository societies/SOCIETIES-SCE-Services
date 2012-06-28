/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.thirdPartyServices.disaster.youRNotAlone;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.societies.thirdPartyServices.disaster.youRNotAlone.model.Volunteer;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

class VolunteerCluster{

	private int numberOfClusters;
	private Instances instances;
	private ArrayList<Attribute> attributes;
	private ArrayList<String> attributeName;

	public VolunteerCluster(int numberOfClusters, ArrayList<String> allLanguages, ArrayList<String> allSkills ){
		this.numberOfClusters = numberOfClusters;

		this.attributes = new ArrayList<Attribute>();
		this.attributeName = new ArrayList<String>();
		//		System.out.println(allLanguages.size());
		for(int i=0;i<allLanguages.size();i++){
			//			System.out.println(allLanguages.get(i));
			attributes.add(new Attribute(allLanguages.get(i)));
			attributeName.add(allLanguages.get(i));
		}
		for(int i=0;i<allSkills.size();i++){
			attributes.add(new Attribute(allSkills.get(i)));
			attributeName.add(allSkills.get(i));
		}
		attributes.add(new Attribute("institute"));
		attributes.add(new Attribute("country"));
		attributeName.add("institute");
		attributeName.add("country");
		this.instances = new Instances("volunteers",attributes,1000);
	}

	public void loadInstanceFromVolunteers(ArrayList<Volunteer> volunteersList){
		for(int i=0;i<volunteersList.size();i++){
			instances.add(convertToInstance(volunteersList.get(i)));
		}
		if (this.numberOfClusters>=(volunteersList.size()/3))
			this.numberOfClusters = (volunteersList.size()/3);
	}

	private Instance convertToInstance(Volunteer v){
		Instance in = new DenseInstance(this.attributes.size());
		//		System.out.println(in.numAttributes());
		Set<String> setLangs = v.getSpokenLanguages().keySet();
		Iterator<String> iter1 = setLangs.iterator();
		while (iter1.hasNext()) {
			String keyL = iter1.next();
			//			System.out.println(keyL);
			//			System.out.println(this.attributes.indexOf(keyL));
			//			for(int i=1;i<this.attributes.size();i++)
			//				System.out.println(this.attributes.get(i));
			in.setValue(this.attributeName.indexOf(keyL), v.getSpokenLanguages().get(keyL));
		}

		Set<String> setSkills = v.getExpertiseSkills().keySet();
		Iterator<String> iter2 = setSkills.iterator();
		while (iter2.hasNext()) {
			String keyS = iter2.next();
			in.setValue(this.attributeName.indexOf(keyS), v.getExpertiseSkills().get(keyS));
		}

		//		in.setValue(this.attributes.indexOf("institute"), v.getInstitut());
		//		in.setValue(this.attributes.indexOf("country"), v.getCountry());
		double[] array = new double[attributes.size()];
		for(int i=0;i<array.length;i++)
			array[i] = 0;
			
		in.replaceMissingValues(array);
		System.out.println(in);
		return in;
	}

	public void update(){
		System.out.println("numberOfClusters is "+numberOfClusters);
		SimpleKMeans kmeans = new SimpleKMeans();

		kmeans.setSeed(100);

		// This is the important parameter to set
		kmeans.setPreserveInstancesOrder(true);
		int[] assignments = null;
//		ClusterEvaluation eval = null;
		try {
			kmeans.setNumClusters(numberOfClusters);
			kmeans.setDisplayStdDevs(false);
			kmeans.setDontReplaceMissingValues(true);
			kmeans.buildClusterer(instances);
			System.out.println(kmeans.toString());
			// This array returns the cluster number (starting with 0) for each instance
			// The array has as many elements as the number of instances
			assignments = kmeans.getAssignments();
//			eval = new ClusterEvaluation();
//			eval.setClusterer(kmeans);
//
//			eval.evaluateClusterer(instances);
//			System.out.println("# of clusters: " + eval.getNumClusters());
//			String a = eval.clusterResultsToString();
//			System.out.println(a);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int i=0;
		for(int clusterNum : assignments) {
			System.out.printf("Instance %d -> Cluster %d\n", i, clusterNum);
			i++;
		}
	}
}