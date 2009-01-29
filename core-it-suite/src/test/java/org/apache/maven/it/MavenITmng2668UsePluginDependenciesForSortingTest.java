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

public class MavenITmng2668UsePluginDependenciesForSortingTest
    extends AbstractMavenIntegrationTestCase
{
    public MavenITmng2668UsePluginDependenciesForSortingTest()
    {
        // TODO: estimated fix by 3.0-alpha-4
        super( "(2.1.0-M1,3.0-alpha-1),(3.0-alpha-3,)" ); // 2.1.0-M2+
    }

    public void testmng2668()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-2668" );
        Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteArtifacts( "org.apache.maven.its.mng2668" );
        verifier.executeGoal( "install" );
        verifier.assertArtifactPresent( "org.apache.maven.its.mng2668", "project", "1.0-SNAPSHOT", "jar" );
        verifier.assertArtifactPresent( "org.apache.maven.its.mng2668", "tools", "1.0-SNAPSHOT", "jar" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
    }

}
