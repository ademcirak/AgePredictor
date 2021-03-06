/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opennlp.tools.util.featuregen;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.List;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import opennlp.tools.util.InvalidFormatException;

/**
 * Generates a feature for the POS of each word in a document.
 */
public class POSFeatureGenerator implements FeatureGenerator {
    
    public static final POSFeatureGenerator INSTANCE = new POSFeatureGenerator();
    
    private final String MODEL_FILE = "model/opennlp/en-pos-maxent.bin";
	
    public POSFeatureGenerator() {
    }
    
    @Override
    public Collection<String> extractFeatures(String[] text) {
	InputStream modelIn = null;
	
	Collection<String> pos = new ArrayList<String>();
	try {
	    modelIn = new FileInputStream(MODEL_FILE);
	    POSModel model = new POSModel(modelIn);
	    
	    POSTaggerME tagger = new POSTaggerME(model);
	    
	    List<String> sentence = new ArrayList<String>();
	    for (String token : text) {
		if (token == "<SENTENCE>") {
		    String tags[] = tagger.tag(sentence.toArray(new String[sentence.size()]));
		    
		    for (int i = 0; i < tags.length; i++) {
			pos.add("pos=" + tags[i]);
			if (i < tags.length - 1) {
			    pos.add("posng=" + tags[i] + ":" + tags[i + 1]);
			}
		    }
		    sentence.clear();
		}
		else {
		    sentence.add(token);
		}
	    }   
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
	finally {
	    if (modelIn != null) {
                try {
                    modelIn.close();
                }
                catch (IOException e) {
                }
            }
	}
	
	return pos;
    }

    
}
