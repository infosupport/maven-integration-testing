package org.apache.maven.it;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.it.util.ResourceExtractor;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * An integration test to check that --also-make in combination with --resume-from
 *   correctly includes needed dependencies in the reactor.
 *
 * <a href="https://issues.apache.org/jira/browse/MNG-6863">MNG-6863</a>.
 *
 */
public class MavenITmng6863AlsoMakeWithResumeFromTest
    extends AbstractMavenIntegrationTestCase
{

    public MavenITmng6863AlsoMakeWithResumeFromTest()
    {
        super( "[3.7.0,)" );
    }

    public void testItShouldIncludeRequiredModulesInReactor()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-6863-also-make-is-ignored-with-resume-from" );

        // 1. Sanity check, check what the full project reactor would be
        Verifier verifier = newVerifier( testDir.getAbsolutePath() );
        verifier.executeGoal( "validate" );
        verifier.verifyTextInLog( "Building independent-module" );
        verifier.verifyTextInLog( "Building module-a" );
        verifier.verifyTextInLog( "Building module-b" );
        verifier.verifyTextInLog( "Building module-c" );
        verifier.verifyTextInLog( "Building parent" );

        // 2. Actual test; start building from module-c, but also build all needed dependencies.
        verifier = newVerifier( testDir.getAbsolutePath() );

        verifier.addCliOption( "-rf" );
        verifier.addCliOption( ":module-c" );
        verifier.addCliOption( "-am" );
        verifier.executeGoals( Collections.singletonList( "install" ) );

        verifier.setLogFileName( "log.txt" );
        List<String> logLines = verifier.loadLines( "log.txt", "UTF-8" );
        for ( String logLine : logLines )
        {
            if ( logLine.contains( "Building independent-module") || logLine.contains( "Building parent" ) )
            {
                fail( "Did not expect independent-module or parent to be built" );
            }
        }
        verifier.verifyTextInLog( "Building module-a" );
        verifier.verifyTextInLog( "Building module-b" );
        verifier.verifyTextInLog( "Building module-c" );
    }
}
