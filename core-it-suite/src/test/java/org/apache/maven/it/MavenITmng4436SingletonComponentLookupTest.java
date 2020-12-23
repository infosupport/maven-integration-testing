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

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

import java.io.File;
import java.util.Properties;

/**
 * This is a test set for <a href="https://issues.apache.org/jira/browse/MNG-4436">MNG-4436</a>.
 *
 * @author Benjamin Bentmann
 */
public class MavenITmng4436SingletonComponentLookupTest
    extends AbstractMavenIntegrationTestCase
{

    public MavenITmng4436SingletonComponentLookupTest()
    {
        super( "[3.0-alpha-4,)" );
    }

    /**
     * Test that lookup of a singleton component works reliably.
     */
    public void testit()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-4436" );

        Verifier verifier = newVerifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "target" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        Properties props = verifier.loadProperties( "target/comp.properties" );

        assertTrue( props.getProperty( "id.0", "" ).length() > 0 );

        assertEquals( props.getProperty( "id.0" ), props.getProperty( "id.1" ) );
        assertEquals( props.getProperty( "id.1" ), props.getProperty( "id.2" ) );
        assertEquals( props.getProperty( "id.2" ), props.getProperty( "id.3" ) );
    }

}
