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

package opennlp.tools.authorage;

import java.util.Iterator;
import java.util.Arrays;

import opennlp.tools.ml.model.Event;
import opennlp.tools.util.AbstractEventStream;
import opennlp.tools.util.ObjectStream;

/**
 * TODO: Documentation
 */
public class AgeClassifyEventStream extends AbstractEventStream<AuthorAgeSample> {
    private AgeClassifyContextGenerator contextGenerator;
    
    AgeClassifyEventStream(ObjectStream<AuthorAgeSample> samples, 
			   AgeClassifyContextGenerator createContextGenerator) {
	super(samples);
	contextGenerator = createContextGenerator;
    }
    
    @Override
    protected Iterator<Event> createEvents(final AuthorAgeSample sample) {

	return new Iterator<Event>(){

	    private boolean isVirgin = true;

	    public boolean hasNext() {
		return isVirgin;
	    }

	    public Event next() {

		isVirgin = false;
		
		//System.out.println(Arrays.toString(contextGenerator.getContext(sample.getText())));
		return new Event(sample.getCategory(),
				 contextGenerator.getContext(sample.getText()));
	    }

	    public void remove() {
		throw new UnsupportedOperationException();
	    }};
    }

}
					   