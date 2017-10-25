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
import com.squareup.okhttp.HttpUrl;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import java.nio.charset.StandardCharsets;

/**
 * Goal which downloads translations from oneskyapp.com
 */
public abstract class AbstractOneSkyAppMojo extends AbstractMojo {

  final String API_ENDPOINT = "https://platform.api.onesky.io/1/";

  @Parameter(property = "publicKey")
  String publicKey;

  @Parameter(property = "secretKey")
  String secretKey;

  @Parameter(property = "projectId")
  String projectId;

  @Parameter
  Boolean failOnError;

  /**
   * @throws MojoExecutionException in case of missing parameters
   */
  @Override
  public void execute() throws MojoExecutionException {
    if (publicKey == null || secretKey == null || projectId == null) {
      showHelp();
    }
  }

  void showHelp() throws MojoExecutionException {
    throw new MojoExecutionException("Missing one ore more required parameters. See https://github.com/dwissk/oneskyapp-maven-plugin");
  }

  public HttpUrl.Builder getHttpBuilder() {
    final Long timestamp = System.currentTimeMillis() / 1000;
    final String devHash = Hashing.md5().hashString(timestamp + secretKey, StandardCharsets.UTF_8).toString();
    HttpUrl.Builder builder = HttpUrl.parse(API_ENDPOINT).newBuilder()
      .addPathSegment("projects")
      .addPathSegment(projectId)
      .addQueryParameter("api_key", publicKey)
      .addQueryParameter("timestamp", timestamp.toString())
      .addQueryParameter("dev_hash", devHash);
    return builder;
  }
}
