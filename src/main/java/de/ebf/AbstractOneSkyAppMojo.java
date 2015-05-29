package de.ebf;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.google.common.hash.Hashing;
import org.apache.maven.plugin.AbstractMojo;

import org.apache.maven.plugins.annotations.Parameter;

import java.nio.charset.StandardCharsets;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal which downloads translations from oneskyapp.com
 */
public abstract class AbstractOneSkyAppMojo extends AbstractMojo {

    protected final String API_ENDPOINT = "https://platform.api.onesky.io/1/";

    @Parameter(property = "publicKey")
    protected String publicKey;

    @Parameter(property = "secretKey")
    protected String secretKey;

    @Parameter(property = "projectId")
    protected String projectId;
    
    @Override
    public void execute() throws MojoExecutionException {
        if (publicKey == null || secretKey == null || projectId == null){
            showHelp();
        }
    }
    
    protected void showHelp() throws MojoExecutionException{
        throw new MojoExecutionException("Missing one ore more required parameters. See https://github.com/dwissk/oneskyapp-maven-plugin");
    }

    
    protected String getAuthParams(){
        final long timestamp = System.currentTimeMillis() / 1000;
        final String devHash = Hashing.md5().hashString(timestamp + secretKey, StandardCharsets.UTF_8).toString();
        return String.format("api_key=%1$s&timestamp=%2$s&dev_hash=%3$s", publicKey, timestamp, devHash);
    }
}
