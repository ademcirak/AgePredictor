/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gov.nasa.jpl.ml.cmdline.authorage;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;

import opennlp.tools.cmdline.AbstractTrainerTool;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.TerminateToolException;
import opennlp.tools.cmdline.ObjectStreamFactory;
import opennlp.tools.cmdline.StreamFactoryRegistry;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ext.ExtensionLoader;
import opennlp.tools.util.model.ModelUtil;

import opennlp.tools.authorage.AgeClassifyFactory;
import opennlp.tools.authorage.AgeClassifyME;
import opennlp.tools.authorage.AgeClassifyModel;
import opennlp.tools.authorage.AuthorAgeSample;
import opennlp.tools.authorage.AuthorAgeSampleStream;
import opennlp.tools.cmdline.ArgumentParser;

import opennlp.tools.ml.AgeClassifyTrainerFactory;
import opennlp.tools.util.TrainingParameters;

import gov.nasa.jpl.ml.cmdline.params.ClassifyTrainingToolParams;

import opennlp.tools.util.featuregen.FeatureGenerator;
import opennlp.tools.util.featuregen.BagOfWordsFeatureGenerator;

import gov.nasa.jpl.ml.cmdline.CLI;

/**
 * TODO: Documentation
 */
public class AgeClassifyTrainerTool 
    extends AbstractTrainerTool<AuthorAgeSample, ClassifyTrainingToolParams> {
    
    public AgeClassifyTrainerTool() {
	super(AuthorAgeSample.class, ClassifyTrainingToolParams.class);
    }
     
    @Override
    public String getShortDescription() {
	return "trainer for the author age classifier";
    }
    
    @Override
    @SuppressWarnings({"unchecked"})
    public String getHelp(String format) {
	if ("".equals(format) || StreamFactoryRegistry.DEFAULT_FORMAT.equals(format)) {
	    return getBasicHelp(paramsClass,
				StreamFactoryRegistry.getFactory(type, StreamFactoryRegistry.DEFAULT_FORMAT)
				.<ClassifyTrainingToolParams>getParameters());
	} else {
	    ObjectStreamFactory<AuthorAgeSample> factory = StreamFactoryRegistry.getFactory(type, format);
	    if (null == factory) {
		throw new TerminateToolException(1, "Format " + format + " is not found.\n" + getHelp());
	    }
	    return "Usage: " + CLI.CMD + " " + getName() + " " +
		ArgumentParser.createUsage(paramsClass, factory.<ClassifyTrainingToolParams>getParameters());
	}
    }
    
    @Override
    public void run(String format, String[] args) {
	super.run(format, args);
	
	TrainingParameters mlParams = null;
	
	// load training parameters
	String paramFile = params.getParams();
	if (paramFile != null) {
	    CmdLineUtil.checkInputFile("Training Parameter", new File(paramFile));
	    
	    InputStream paramsIn = null;
	    try {
		paramsIn = new FileInputStream(new File(paramFile));
		
		mlParams = new TrainingParameters(paramsIn);
	    } catch(IOException e) {
		throw new TerminateToolException(-1, "Error during parameters loading: " + e.getMessage(), e);
	    }
	    finally {
		try {
		    if (paramsIn != null)
			paramsIn.close();
		} catch (IOException e) {
		    //handle error?
		}
	    }
	    
	    if (!AgeClassifyTrainerFactory.isValid(mlParams.getSettings())) {
		throw new TerminateToolException(1, "Training parameters file '" + paramFile + "' is invalid!");    
	    }
	}
	if (mlParams == null) {
	    mlParams = ModelUtil.createDefaultTrainingParameters();
	}
	
	// load output model file
	File modelOutFile = params.getModel();
	
	CmdLineUtil.checkOutputFile("age classifier model", modelOutFile);
	
	System.out.println("Feature Generators: " + params.getFeatureGenerators());
	FeatureGenerator[] featureGenerators = createFeatureGenerators(params
            .getFeatureGenerators());
	
	System.out.println("Tokenizer: " + params.getTokenizer());
	Tokenizer tokenizer = createTokenizer(params.getTokenizer());
	
	AgeClassifyModel model;
	try {
	    AgeClassifyFactory factory = AgeClassifyFactory.create("AgeClassifyFactory", tokenizer, featureGenerators);
	    model = AgeClassifyME.train(params.getLang(), sampleStream, mlParams,
					factory);
	} catch (IOException e) {
	    throw new TerminateToolException(-1,
	        "IO error while reading training data or indexing data: " + e.getMessage(), e);
	} finally {
	    try {
		sampleStream.close();
	    } catch (IOException e) {
		// handle error
	    }
	}
	
	CmdLineUtil.writeModel("age classifier", modelOutFile, model);
    }
    

    private static Tokenizer createTokenizer(String tokenizer) {
	if(tokenizer != null) {
	    return ExtensionLoader.instantiateExtension(Tokenizer.class, tokenizer);
	}
	return WhitespaceTokenizer.INSTANCE;
    }


    private static FeatureGenerator[] createFeatureGenerators(String featureGeneratorsNames) {
	if(featureGeneratorsNames == null) {
	    FeatureGenerator[] def = { new BagOfWordsFeatureGenerator()};
	    return def;
	}
	String[] classes = featureGeneratorsNames.split(",");
	FeatureGenerator[] featureGenerators = new FeatureGenerator[classes.length];
	for (int i = 0; i < featureGenerators.length; i++) {
	    featureGenerators[i] = ExtensionLoader.instantiateExtension(
				       FeatureGenerator.class, classes[i]);
	}
	return featureGenerators;
    }
    
   
}
	    