/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.liu.ida.rdfstar.tools.sparqlstar.sys;

import org.apache.jena.sys.JenaSubsystemLifecycle;
import se.liu.ida.rdfstar.tools.parser.lang.LangTurtleStar;
import se.liu.ida.rdfstar.tools.sparqlstar.lang.ParserSPARQLStar;
import se.liu.ida.rdfstar.tools.sparqlstar.lang.SPARQLStar;
import se.liu.ida.rdfstar.tools.sparqlstar.resultset.ResultSetWritersSPARQLStar;

public class InitRDFStar implements JenaSubsystemLifecycle {

    @Override
    public void start() {
        LangTurtleStar.init();
        SPARQLStar.init();
        ResultSetWritersSPARQLStar.init();
    }

    @Override
    public void stop() {}
    
    @Override
    public int level() { return 100 ; }

}
