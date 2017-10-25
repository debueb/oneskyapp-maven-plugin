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
import com.squareup.okhttp.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Goal which uploads files to oneskyapp.com
 */
@Mojo(name = "upload-files", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class UploadFileOneSkyAppMojo extends AbstractOneSkyAppMojo {

  @Parameter(property = "outputDir", required = true)
  private List<File> files;

  @Parameter(property = "locale")
  private String locale;

  @Parameter(property = "fileFormat")
  private String fileFormat;

  @Parameter(property = "isKeepingAllStrings", required = false)
  private String isKeepingAllStrings;

  /**
   * upload translation files to oneskyapp
   *
   * @throws MojoExecutionException in case of missing parameters or runtime exception
   */
  @Override
  public void execute() throws MojoExecutionException {
    super.execute();
    if (files == null || files.isEmpty() || locale == null || fileFormat == null) {
      showHelp();
    }
    uploadFiles();
  }

  private void uploadFiles() throws MojoExecutionException {
    try {
      for (File file : files) {
        System.out.println(String.format("Uploading %1$s", file.getName()));
        OkHttpClient okHttpClient = new OkHttpClient();

        final Long timestamp = System.currentTimeMillis() / 1000;
        final String devHash = Hashing.md5().hashString(timestamp + secretKey, StandardCharsets.UTF_8).toString();

        HttpUrl.Builder httpUrlBuilder = getHttpBuilder();
        httpUrlBuilder.addPathSegment("files")
          .addQueryParameter("file_format", fileFormat)
          .addQueryParameter("locale", locale);
        //https://github.com/debueb/oneskyapp-maven-plugin/issues/2
        if (!StringUtils.isEmpty(isKeepingAllStrings)) {
          httpUrlBuilder.addQueryParameter("is_keeping_all_strings", isKeepingAllStrings);
        }
        RequestBody requestBody = new MultipartBuilder()
          .type(MultipartBuilder.FORM)
          .addPart(
            Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"" + file.getName() + "\""),
            RequestBody.create(MediaType.parse("text/plain"), file))
          .build();

        final Request request = new Request.Builder().post(requestBody).url(httpUrlBuilder.build()).build();
        final Response response = okHttpClient.newCall(request).execute();

        if (response.code() == 201) {
          System.out.println(String.format("Successfully uploaded %1$s", file.getName()));
        } else {
          throw new MojoExecutionException(String.format("OneSkyApp API returned %1$s: %2s, %3$s", response.code(), response.message(), response.body().string()));
        }
      }
    } catch (IOException | MojoExecutionException ex) {
      if (failOnError == null || failOnError) {
        throw new MojoExecutionException(ex.getMessage(), ex);
      } else {
        System.out.println("Caught exception: " + ex.getMessage());
      }
    }
  }
}
