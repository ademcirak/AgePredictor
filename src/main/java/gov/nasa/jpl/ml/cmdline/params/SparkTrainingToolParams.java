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

package gov.nasa.jpl.ml.cmdline.params;

import opennlp.tools.cmdline.ArgumentParser.ParameterDescription;
import opennlp.tools.cmdline.params.TrainingToolParams;
import opennlp.tools.cmdline.ArgumentParser.OptionalParameter;

/**
 * TODO: Documentation
 */
public interface SparkTrainingToolParams extends TrainingToolParams {
    @ParameterDescription(valueName = "featuregens", 
        description = "Comma separated feature generator classes. Bag of words default.")
    @OptionalParameter
    String getFeatureGenerators();
    
    @ParameterDescription(valueName = "tokenizer", 
        description = "Tokenizer implementation. WhitespaceTokenizer is used if not specified.")
    @OptionalParameter
    String getTokenizer();

    @ParameterDescription(valueName = "sampleData", description = "data to be used, usually a file name.")
    String getData();
}
