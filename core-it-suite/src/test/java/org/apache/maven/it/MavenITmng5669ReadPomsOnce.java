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

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.it.util.ResourceExtractor;

/**
 * An integration test to ensure any pomfile is only read once.
 * This is confirmed by adding a Java Agent to the DefaultModelReader and output the options, including the source location
 *
 * <a href="https://issues.apache.org/jira/browse/MNG-5669">MNG-5669</a>.
 *
 */
public class MavenITmng5669ReadPomsOnce
    extends AbstractMavenIntegrationTestCase
{

    public MavenITmng5669ReadPomsOnce()
    {
        super( "[3.7.0,)" );
    }

    public void test()
        throws Exception
    {
        // prepare JAvaAgent
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-5669-read-poms-once" );

        Verifier verifier = newVerifier( testDir.getAbsolutePath(), false );
        verifier.setForkJvm( true ); // pick up agent
        verifier.setMavenDebug( false );
        verifier.setAutoclean( false );
        verifier.addCliOption( "-q" );
        verifier.addCliOption( "-U" );
        verifier.executeGoals( Arrays.asList( "verify" ) );
        verifier.resetStreams();
        
        List<String> logTxt = verifier.loadLines( "log.txt", "utf-8" );
        assertEquals( 168, logTxt.size() );
        
        // analyze lines. It is a Hashmap, so we can't rely on the order
        Set<String> uniqueBuildingSources = new HashSet<>( 168 );
        final String buildSourceKey = "org.apache.maven.model.building.source=";
        final int keyLength = buildSourceKey.length();
        for( String line : logTxt )
        {
            int start = line.indexOf( buildSourceKey );
            if ( start < 0 )
            {
                continue;
            }
            
            int end = line.indexOf(", ", start);
            if ( end < 0) 
            {
                end = line.length() - 1; // is the }
            }
            uniqueBuildingSources.add( line.substring( start + keyLength, end ) );
        }
        assertEquals( uniqueBuildingSources.size(), 167 /* is 168 minus superpom */ );
    }
}
